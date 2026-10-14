package lk.sliit.hotel.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.sliit.hotel.payment.integration.*;
import org.junit.jupiter.api.*;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="demo.password=TestOnlyPayment42!")
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@WithMockUser(username="staff", roles="STAFF")
class PaymentApiTest {
    static final Path FILE;
    static {
        try { FILE = Files.createTempDirectory("payment-test-").resolve("payments.txt"); }
        catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        registry.add("payment.file", FILE::toString);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean(answer=Answers.CALLS_REAL_METHODS) BookingGateway bookings;

    private List<BookingInfo> fixtures(boolean cancelled) {
        return List.of(
            new BookingInfo("BK1001","Alice Example","101",cancelled ? "CANCELLED" : "CONFIRMED",new BigDecimal("10000")),
            new BookingInfo("BK1002","Bob Example","102","CONFIRMED",new BigDecimal("20000")),
            new BookingInfo("BK-CANCELLED","Cancelled Guest","103","CANCELLED",new BigDecimal("5000")));
    }
    @BeforeEach void reset() throws Exception {
        Files.writeString(FILE, "");
        when(bookings.findAll()).thenReturn(fixtures(false));
    }
    private String body(String booking, String method, String status, String amount) throws Exception {
        return json.writeValueAsString(Map.of("bookingId",booking, "method",method,
            "status",status,"amount",amount,"paymentDate","2026-01-10"));
    }
    private JsonNode create(String booking, String method, String status) throws Exception {
        return json.readTree(mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body(booking,method,status,booking.equals("BK1002") ? "20000" : "10000"))).andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString());
    }
    private ResultActions update(String id, String status) throws Exception {
        return mvc.perform(patch("/api/payments/{id}/status",id).with(csrf())
            .contentType("application/json").content(json.writeValueAsString(Map.of("status",status))));
    }

    @Test void cashDiscountIsCalculatedOnServerAndSaved() throws Exception {
        JsonNode p=create("BK1001","CASH","PAID");
        assertThat(p.get("total").decimalValue()).isEqualByComparingTo("9500");
        assertThat(p.get("discountAmount").decimalValue()).isEqualByComparingTo("500");
        assertThat(Files.readString(FILE)).contains(p.get("id").asText(), "|CASH|PAID|2026-01-10|0.05|");
        mvc.perform(get("/api/payments/summary")).andExpect(jsonPath("$.collectedAmount").value(9500));
    }
    @Test void cardHasNoDiscountAndStoresOnlyLastFour() throws Exception {
        Map<String,Object> fields=json.readValue(body("BK1001","CARD","PAID","10000"),Map.class);
        fields.put("cardBrand","VISA"); fields.put("lastFour","4242");
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json").content(json.writeValueAsString(fields)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(10000))
            .andExpect(jsonPath("$.discountAmount").value(0)).andExpect(jsonPath("$.lastFour").value("4242"));
    }
    @Test void lifecycleIsForwardOnly() throws Exception {
        String id=create("BK1001","CASH","PENDING").get("id").asText();
        update(id,"PAID").andExpect(status().isOk());
        update(id,"REFUNDED").andExpect(status().isOk());
        update(id,"PAID").andExpect(status().isConflict());
        assertThat(Files.readString(FILE)).contains("|REFUNDED|");
    }
    @Test void pendingCannotBeRefunded() throws Exception {
        String id=create("BK1001","CASH","PENDING").get("id").asText();
        update(id,"REFUNDED").andExpect(status().isConflict());
    }
    @Test void anonymousAndGuestsCannotUsePaymentEndpoints() throws Exception {
        mvc.perform(get("/api/payments").with(anonymous())).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/payments").with(user("guest").roles("GUEST"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/payments").with(user("guest").roles("GUEST")).with(csrf())
            .contentType("application/json").content(body("BK1001","CASH","PAID","100")))
            .andExpect(status().isForbidden());
        assertThat(Files.readString(FILE)).isEmpty();
    }
    @Test void csrfIsRequiredForWrites() throws Exception {
        mvc.perform(post("/api/payments").contentType("application/json")
            .content(body("BK1001","CASH","PAID","100"))).andExpect(status().isForbidden());
    }
    @Test void adminAlsoHasStaffPaymentAccess() throws Exception {
        mvc.perform(post("/api/payments").with(user("admin").roles("ADMIN")).with(csrf())
            .contentType("application/json").content(body("BK1001","CASH","PAID","10000")))
            .andExpect(status().isCreated());
    }
    @Test void invalidAmountsAreRejectedWithoutWriting() throws Exception {
        for (String amount: List.of("0","-1","1.234","1000000000")) {
            mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
                .content(body("BK1001","CASH","PAID",amount))).andExpect(status().isBadRequest());
        }
        assertThat(Files.readString(FILE)).isEmpty();
    }
    @Test void missingAndCancelledBookingsAreRejected() throws Exception {
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("MISSING","CASH","PAID","100"))).andExpect(status().isNotFound());
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK-CANCELLED","CASH","PAID","100"))).andExpect(status().isConflict());
    }
    @Test void creationCannotStartRefundedOrInFuture() throws Exception {
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK1001","CASH","REFUNDED","100"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK1001","CASH","PAID","100").replace("2026-01-10","2999-01-01")))
            .andExpect(status().isBadRequest());
    }
    @Test void searchAndMethodFiltersUseGuestAndBookingData() throws Exception {
        create("BK1001","CASH","PAID"); create("BK1002","CARD","PAID");
        mvc.perform(get("/api/payments").param("q","Alice").param("method","CASH"))
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].bookingId").value("BK1001"));
        mvc.perform(get("/api/payments").param("method","CARD"))
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].guestName").value("Bob Example"));
    }
    @Test void hardDeleteRemovesRecordAndDerivedPaidState() throws Exception {
        String id=create("BK1001","CASH","PAID").get("id").asText();
        mvc.perform(get("/api/payments/booking/BK1001")).andExpect(jsonPath("$.paid").value(true));
        mvc.perform(delete("/api/payments/{id}",id).with(csrf())).andExpect(status().isNoContent());
        assertThat(Files.readString(FILE)).isEmpty();
        mvc.perform(get("/api/payments/{id}",id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/payments/booking/BK1001")).andExpect(jsonPath("$.paid").value(false));
    }
    @Test void invoiceDistinguishesUnpaidPaidAndRefunded() throws Exception {
        String id=create("BK1001","CARD","PENDING").get("id").asText();
        mvc.perform(get("/api/payments/{id}/invoice",id)).andExpect(jsonPath("$.documentType").value("INVOICE"));
        update(id,"PAID").andExpect(status().isOk());
        mvc.perform(get("/api/payments/{id}/invoice",id)).andExpect(jsonPath("$.documentType").value("RECEIPT"));
        update(id,"REFUNDED").andExpect(status().isOk());
        mvc.perform(get("/api/payments/{id}/invoice",id)).andExpect(jsonPath("$.documentType").value("REFUND_RECORD"));
    }
    @Test void duplicateActivePaymentIsRejected() throws Exception {
        create("BK1001","CASH","PENDING");
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK1001","CARD","PAID","100"))).andExpect(status().isConflict());
        assertThat(Files.readAllLines(FILE)).hasSize(1);
    }
    @Test void cancelledPaidBookingAppearsInRefundQueueAndCanBeRefunded() throws Exception {
        String id=create("BK1001","CARD","PAID").get("id").asText();
        when(bookings.findAll()).thenReturn(fixtures(true));
        mvc.perform(get("/api/payments/summary")).andExpect(jsonPath("$.refundNeeded.length()").value(1));
        update(id,"REFUNDED").andExpect(status().isOk());
        mvc.perform(get("/api/payments/summary")).andExpect(jsonPath("$.refundNeeded.length()").value(0))
            .andExpect(jsonPath("$.refundedAmount").value(10000));
    }
    @Test void suppliedVipExampleStacksDiscountsAndPreservesInvoiceSnapshot() throws Exception {
        var billing = new lk.sliit.hotel.payment.model.BillingDetails("Nethmi Perera", "nethmi@example.com",
            "Estate Suite · Room 301", java.time.LocalDate.of(2026,10,12), java.time.LocalDate.of(2026,10,15),
            new BigDecimal("138000"), new BigDecimal("0.10"));
        when(bookings.findAll()).thenReturn(List.of(new BookingInfo("BK1024", "Nethmi Perera", billing.room(),
            "CONFIRMED", billing.amountDue(), billing)));
        JsonNode payment = json.readTree(mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK1024", "CASH", "PAID", "124200")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(117990))
            .andExpect(jsonPath("$.discountAmount").value(6210)).andReturn().getResponse().getContentAsString());
        // Each request reloads payments.txt. A changed or removed booking cannot change the saved invoice.
        when(bookings.findAll()).thenReturn(List.of());
        mvc.perform(get("/api/payments/{id}/invoice", payment.get("id").asText()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.payment.guestName").value("Nethmi Perera"))
            .andExpect(jsonPath("$.payment.billing.roomTotal").value(138000))
            .andExpect(jsonPath("$.payment.billing.checkIn").value("2026-10-12"))
            .andExpect(jsonPath("$.payment.total").value(117990));
    }
    @Test void omittedAmountUsesBookingPriceAndTamperedAmountIsRejected() throws Exception {
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json")
            .content(body("BK1001","CASH","PAID","1"))).andExpect(status().isBadRequest());
        assertThat(Files.readString(FILE)).isEmpty();
        Map<String,Object> fields=json.readValue(body("BK1001","CARD","PAID","10000"),Map.class);
        fields.remove("amount");
        mvc.perform(post("/api/payments").with(csrf()).contentType("application/json").content(json.writeValueAsString(fields)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(10000));
    }
    @Test @WithAnonymousUser void realSessionLoginAndCsrfTokenRotationWork() throws Exception {
        // Use the real saved session instead of injecting a synthetic test principal on each request.
        org.springframework.security.test.context.TestSecurityContextHolder.clearContext();
        MvcResult initial=mvc.perform(get("/api/csrf")).andExpect(status().isOk()).andReturn();
        MockHttpSession session=(MockHttpSession)initial.getRequest().getSession(false);
        JsonNode token=json.readTree(initial.getResponse().getContentAsString());
        mvc.perform(post("/api/login").session(session).param("username","staff").param("password","TestOnlyPayment42!")
            .header(token.get("headerName").asText(),token.get("token").asText())).andExpect(status().isOk());
        mvc.perform(get("/api/payments").session(session)).andExpect(status().isOk());
        MvcResult fresh=mvc.perform(get("/api/csrf").session(session)).andReturn();
        token=json.readTree(fresh.getResponse().getContentAsString());
        mvc.perform(post("/api/logout").session(session)
            .header(token.get("headerName").asText(),token.get("token").asText())).andExpect(status().isOk());
    }
}
