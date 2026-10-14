package lk.sliit.hotel.payment.controller;

import jakarta.validation.Valid;
import lk.sliit.hotel.payment.dto.*;
import lk.sliit.hotel.payment.integration.BookingInfo;
import lk.sliit.hotel.payment.model.*;
import lk.sliit.hotel.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }
    @PostMapping public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/payments/" + result.id())).body(result);
    }
    @GetMapping public List<PaymentResponse> list(@RequestParam(required=false) String q,
            @RequestParam(required=false) PaymentMethod method, @RequestParam(required=false) PaymentStatus status) {
        return service.list(q, method, status);
    }
    @GetMapping("/summary") public Map<String, Object> summary() { return service.summary(); }
    @GetMapping("/settings") public Map<String, Object> settings() { return service.settings(); }
    @GetMapping("/bookings") public List<BookingInfo> bookings() { return service.availableBookings(); }
    @GetMapping("/booking/{bookingId}") public Map<String, Object> booking(@PathVariable String bookingId) {
        return service.bookingPaymentState(bookingId);
    }
    @GetMapping("/{id}") public PaymentResponse get(@PathVariable String id) { return service.get(id); }
    @GetMapping("/{id}/invoice") public Map<String, Object> invoice(@PathVariable String id) { return service.invoice(id); }
    @PatchMapping("/{id}/status") public PaymentResponse update(@PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) { return service.updateStatus(id, request.status()); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id); return ResponseEntity.noContent().build();
    }
}
