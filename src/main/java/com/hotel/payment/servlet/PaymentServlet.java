package com.hotel.payment.servlet;

import com.hotel.payment.model.*;
import com.hotel.payment.service.PaymentService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@WebServlet(urlPatterns = {"/admin/payments", "/admin/payments/record", "/admin/payments/invoice", "/admin/payments/paid", "/admin/payments/refund", "/admin/payments/delete"})
public class PaymentServlet extends HttpServlet {
    @Autowired
    private PaymentService service;

    private User user(HttpServletRequest r) {
        return SessionUtil.getCurrentUser(r.getSession(false));
    }

    private void token(HttpServletRequest r) {
        if (r.getSession().getAttribute("paymentCsrf") == null)
            r.getSession().setAttribute("paymentCsrf", UUID.randomUUID().toString());
    }

    private void form(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        r.setAttribute("unpaid", service.unpaid(user(r)));
        r.setAttribute("cashRate", CashPayment.DISCOUNT);
        r.setAttribute("today", LocalDate.now());
        view(r, s, "record");
    }

    private void view(HttpServletRequest r, HttpServletResponse s, String name) throws ServletException, IOException {
        r.getRequestDispatcher("/WEB-INF/views/payment/" + name + ".jsp").forward(r, s);
    }

    @Override
    protected void doGet(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        s.setHeader("Cache-Control", "no-store");
        try {
            service.authorize(user(r));
            token(r);
            switch (r.getServletPath()) {
                case "/admin/payments/record" -> form(r, s);
                case "/admin/payments/invoice" -> {
                    r.setAttribute("d", service.invoice(user(r), r.getParameter("id"), r.getParameter("bookingId")));
                    view(r, s, "invoice");
                }
                case "/admin/payments" -> {
                    var list = service.list(user(r));
                    r.setAttribute("payments", list);
                    r.setAttribute("metrics", service.metrics(user(r), list));
                    r.setAttribute("refundsDue", list.stream().filter(d -> d.getPayment().getStatus() == PaymentStatus.PAID && "cancelled".equals(d.getBooking().getStatus()) && !d.getBooking().isCheckedIn()).toList());
                    view(r, s, "list");
                }
                default -> s.sendError(405);
            }
        } catch (SecurityException e) {
            s.sendError(403);
        } catch (IllegalArgumentException e) {
            s.sendError(404, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        r.setCharacterEncoding("UTF-8");
        s.setHeader("Cache-Control", "no-store");
        try {
            service.authorize(user(r));
            Object expected = r.getSession().getAttribute("paymentCsrf");
            if (expected == null || !expected.equals(r.getParameter("csrf"))) {
                s.sendError(403, "Reload the payment page and try again.");
                return;
            }
            if (r.getServletPath().equals("/admin/payments/record")) {
                String id = service.record(user(r), r.getParameter("bookingId"), r.getParameter("method"), r.getParameter("status"), r.getParameter("paidOn"), r.getParameter("cardholder"), r.getParameter("cardNumber"), r.getParameter("expiry"), r.getParameter("cvv"));
                s.sendRedirect(r.getContextPath() + "/admin/payments/invoice?id=" + id + "&done=recorded");
            } else {
                String action = switch (r.getServletPath()) {
                    case "/admin/payments/paid" -> "paid";
                    case "/admin/payments/refund" -> "refund";
                    case "/admin/payments/delete" -> "delete";
                    default -> "";
                };
                if (action.isEmpty()) {
                    s.sendError(405);
                    return;
                }
                service.change(user(r), r.getParameter("id"), action);
                s.sendRedirect(r.getContextPath() + "/admin/payments?done=" + (action.equals("delete") ? "deleted" : action.equals("refund") ? "refunded" : "paid"));
            }
        } catch (SecurityException e) {
            s.sendError(403);
        } catch (IllegalArgumentException | DataAccessException e) {
            String message = e instanceof DataAccessException ? "The payment could not be saved. Reload the page and check whether it was already recorded." : e.getMessage();
            if (r.getServletPath().equals("/admin/payments/record")) {
                r.setAttribute("error", message);
                form(r, s);
            } else
                s.sendRedirect(r.getContextPath() + "/admin/payments?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
        }
    }
}
