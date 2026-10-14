package lk.sliit.hotel.payment;

import lk.sliit.hotel.payment.exception.PaymentStorageException;
import lk.sliit.hotel.payment.model.*;
import lk.sliit.hotel.payment.repository.FilePaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class FileAndModelTest {
    @TempDir Path directory;
    private CashPayment cash(String amount, String rate) {
        return new CashPayment("P-test","BK1001",new BigDecimal(amount),PaymentStatus.PAID,
            LocalDate.of(2026,1,10),Instant.parse("2026-01-10T10:00:00Z"),new BigDecimal(rate));
    }
    @Test void reloadPreservesDiscountAndSubclass() {
        Path file=directory.resolve("payments.txt");
        FilePaymentRepository first=new FilePaymentRepository(file.toString());
        first.saveAll(List.of(cash("123.45","0.05")));
        Payment saved=new FilePaymentRepository(file.toString()).findAll().get(0);
        assertThat(saved).isInstanceOf(CashPayment.class);
        assertThat(saved.calculateTotal()).isEqualByComparingTo("117.28");
        assertThat(saved.getDiscountRate()).isEqualByComparingTo("0.05");
    }
    @Test void originalTenFieldFileRemainsReadable() throws Exception {
        Path file=directory.resolve("payments.txt");
        Files.writeString(file,"P-old|BK1001|100.00|CASH|PAID|2026-01-10|0.05|2026-01-10T10:00:00Z||\n");
        Payment saved=new FilePaymentRepository(file.toString()).findAll().get(0);
        assertThat(saved.calculateTotal()).isEqualByComparingTo("95.00");
        assertThat(saved.getBilling()).isNull();
    }
    @Test void corruptedFileFailsClearlyAndIsNotOverwritten() throws Exception {
        Path file=directory.resolve("payments.txt");
        Files.writeString(file,"broken|record\n");
        assertThatThrownBy(() -> new FilePaymentRepository(file.toString())).isInstanceOf(PaymentStorageException.class);
        assertThat(Files.readString(file)).isEqualTo("broken|record\n");
    }
    @Test void invalidDiscountAndPrecisionAreRejected() {
        assertThatThrownBy(() -> cash("100","1")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cash("100","-0.1")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cash("1.234","0.05")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void identifiersCannotInjectAdditionalFileRecords() {
        assertThatThrownBy(() -> new CardPayment("P-x","BK1|injected",BigDecimal.ONE,
            PaymentStatus.PAID,LocalDate.now(),Instant.now(),"","")).isInstanceOf(IllegalArgumentException.class);
    }
}
