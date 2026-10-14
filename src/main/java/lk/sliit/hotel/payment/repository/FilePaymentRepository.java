package lk.sliit.hotel.payment.repository;

import lk.sliit.hotel.payment.exception.PaymentStorageException;
import lk.sliit.hotel.payment.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Hand-written file I/O. PaymentService serializes read-modify-write operations. */
@Repository
public class FilePaymentRepository implements PaymentRepository {
    private final Path file;
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    public FilePaymentRepository(@Value("${payment.file}") String filename) {
        file = Path.of(filename).toAbsolutePath().normalize();
        try {
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) Files.createFile(file);
            findAll();
        } catch (IOException ex) { throw new PaymentStorageException("Cannot initialize payments.txt", ex); }
    }

    @Override public synchronized List<Payment> findAll() {
        try {
            List<Payment> result = new ArrayList<>();
            Set<String> ids = new HashSet<>();
            int number = 0;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                number++;
                if (line.isBlank()) continue;
                try {
                    Payment p = decode(line);
                    if (!ids.add(p.getId())) throw new IllegalArgumentException("Duplicate ID");
                    result.add(p);
                } catch (RuntimeException ex) {
                    throw new PaymentStorageException("Invalid payments.txt line " + number, ex);
                }
            }
            return result;
        } catch (IOException ex) { throw new PaymentStorageException("Cannot read payments.txt", ex); }
    }

    private Payment decode(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 10 && p.length != 11) throw new IllegalArgumentException("Expected ten or eleven fields");
        BillingDetails billing = null;
        if (p.length == 11 && !p[10].isEmpty()) {
            try { billing = json.readValue(Base64.getDecoder().decode(p[10]), BillingDetails.class); }
            catch (IOException ex) { throw new IllegalArgumentException("Invalid billing snapshot", ex); }
        }
        BigDecimal amount = new BigDecimal(p[2]);
        PaymentMethod method = PaymentMethod.valueOf(p[3]);
        PaymentStatus status = PaymentStatus.valueOf(p[4]);
        LocalDate date = LocalDate.parse(p[5]);
        BigDecimal rate = new BigDecimal(p[6]);
        Instant created = Instant.parse(p[7]);
        if (method == PaymentMethod.CASH) {
            if (!p[8].isEmpty() || !p[9].isEmpty()) throw new IllegalArgumentException("Cash has card details");
            return new CashPayment(p[0], p[1], amount, status, date, created, rate, billing);
        }
        if (rate.signum() != 0) throw new IllegalArgumentException("Card discount must be zero");
        return new CardPayment(p[0], p[1], amount, status, date, created, p[8], p[9], billing);
    }

    private String encode(Payment p) {
        String snapshot = "";
        if (p.getBilling() != null) {
            try { snapshot = Base64.getEncoder().encodeToString(json.writeValueAsBytes(p.getBilling())); }
            catch (IOException ex) { throw new PaymentStorageException("Cannot encode billing snapshot", ex); }
        }
        return String.join("|", p.getId(), p.getBookingId(), p.getAmount().toPlainString(),
                p.getMethod().name(), p.getStatus().name(), p.getPaymentDate().toString(),
                p.getDiscountRate().toPlainString(), p.getCreatedAt().toString(),
                p.getCardBrand(), p.getLastFour(), snapshot);
    }

    @Override public synchronized void saveAll(List<Payment> payments) {
        Path temp = null;
        try {
            temp = Files.createTempFile(file.getParent(), "payments-", ".tmp");
            Files.write(temp, payments.stream().map(this::encode).toList(), StandardCharsets.UTF_8);
            try { Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException ex) { throw new PaymentStorageException("Cannot save payments.txt", ex); }
        finally {
            if (temp != null) try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
        }
    }
}
