package com.hotel.payment;

import com.hotel.payment.dao.*;
import com.hotel.payment.model.*;
import com.hotel.payment.service.*;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.guest.model.RegularGuest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentTests {
    @Autowired PaymentService service;
    @Autowired PaymentDAO dao;
    @Autowired JdbcTemplate jdbc;
    private final Staff staff=new Staff("test","Test Staff","test@hotel.test","");
    private final String today=LocalDate.now().toString();
    @BeforeEach void fixture() {
        jdbc.update("INSERT INTO guests(id,name,email,password_hash,guest_type) VALUES ('GPAYTEST','Payment Test','payment-test@hotel.test','unused','VIP')");
        jdbc.update("INSERT INTO bookings(id,guest_id,room_id,check_in,check_out,guests,booking_type) VALUES ('BKPAYTEST','GPAYTEST','R101',?,?,1,'DESK')",LocalDate.now(),LocalDate.now().plusDays(2));
    }
    private String record(String method,String status,String card) {
        return service.record(staff,"BKPAYTEST",method,status,today,"Test Guest",card,YearMonth.now().plusYears(1).format(java.time.format.DateTimeFormatter.ofPattern("MM/yy")),"123");
    }
    @Test void polymorphismStacksDiscountsWithMoneyRounding() {
        Payment cash=new CashPayment("P1","BK1",new BigDecimal("100"),new BigDecimal("200"),new BigDecimal("0.10"),null,PaymentStatus.PAID,LocalDate.now());
        Payment card=new CardPayment("P2","BK1",new BigDecimal("100"),new BigDecimal("200"),new BigDecimal("0.10"),null,PaymentStatus.PAID,LocalDate.now(),"Visa","4242");
        assertEquals(new BigDecimal("171.00"),cash.calculateTotal());
        assertEquals(new BigDecimal("180.00"),card.calculateTotal());
    }
    @Test void cashLifecycleAndDuplicateGuardKeepBookingInSync() {
        String id=record("cash","pending",null);
        assertEquals(PaymentStatus.PENDING,dao.find(id).getStatus());
        assertEquals(new BigDecimal("31635.00"),dao.find(id).getAmount());
        assertThrows(IllegalArgumentException.class,()->record("cash","paid",null));
        service.change(staff,id,"paid");
        assertEquals("paid",jdbc.queryForObject("SELECT payment_status FROM bookings WHERE id='BKPAYTEST'",String.class));
        assertThrows(IllegalArgumentException.class,()->service.change(staff,id,"paid"));
        service.change(staff,id,"refund");
        assertEquals("refunded",jdbc.queryForObject("SELECT payment_status FROM bookings WHERE id='BKPAYTEST'",String.class));
        assertThrows(IllegalArgumentException.class,()->service.change(staff,id,"paid"));
        service.change(staff,id,"delete");
        assertNull(dao.forBooking("BKPAYTEST"));
        assertEquals("pending",jdbc.queryForObject("SELECT payment_status FROM bookings WHERE id='BKPAYTEST'",String.class));
    }
    @Test void cardStoresOnlyMaskAndFrozenPrices() {
        String id=record("card","paid","4242 4242 4242 4242");
        assertInstanceOf(CardPayment.class,dao.find(id));
        assertEquals("4242",dao.find(id).getCardLast4());
        assertEquals("Visa",dao.find(id).getCardType());
        jdbc.update("UPDATE rooms SET base_rate=20000 WHERE id='R101'");
        assertEquals(new BigDecimal("33300.00"),service.invoice(staff,id,null).getPayment().getAmount());
    }
    @Test void declineAndInvalidFieldsWriteNothing() {
        assertThrows(IllegalArgumentException.class,()->record("card","paid","4000000000000002"));
        assertThrows(IllegalArgumentException.class,()->record("card","paid","1234567812345678"));
        assertThrows(IllegalArgumentException.class,()->service.record(staff,"BKPAYTEST","card","paid",today,"Test Guest","4242424242424242","01/20","123"));
        assertThrows(IllegalArgumentException.class,()->service.record(staff,"BKPAYTEST","card","paid",today,"Test Guest","4242424242424242",YearMonth.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/yy")),"x"));
        assertNull(dao.forBooking("BKPAYTEST"));
        assertEquals("pending",jdbc.queryForObject("SELECT payment_status FROM bookings WHERE id='BKPAYTEST'",String.class));
    }
    @Test void cancelledBookingAndInvalidStatusRejected() {
        assertThrows(IllegalArgumentException.class,()->record("cash","refunded",null));
        assertThrows(IllegalArgumentException.class,()->record("wire","paid",null));
        jdbc.update("UPDATE bookings SET status='cancelled' WHERE id='BKPAYTEST'");
        assertThrows(IllegalArgumentException.class,()->record("cash","paid",null));
    }
    @Test void guestsAndInactiveStaffCannotUsePayments() {
        var guest=new RegularGuest("G1","Guest","guest@hotel.test","");
        assertThrows(SecurityException.class,()->service.list(guest));
        assertThrows(SecurityException.class,()->service.invoice(guest,"P1",null));
        assertThrows(SecurityException.class,()->service.record(guest,"BKPAYTEST","cash","paid",today,null,null,null,null));
        assertThrows(SecurityException.class,()->service.change(guest,"P1","delete"));
        staff.setActive(false);
        assertThrows(SecurityException.class,()->service.unpaid(staff));
    }
}
