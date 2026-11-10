<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Printable invoice (GET /admin/payments/invoice?id=P01 or ?bookingId=BK01). Shown by InvoiceServlet.
  Request attributes:
    d - PaymentDetails: payment (rates copied when it was taken), booking (BookingDetails), fullRateTotal, roomDiscount
  URL parameter ?done=recorded shows a success message after a payment was saved.
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Invoice ${d.payment.id}"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="payments"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <c:set var="p" value="${d.payment}"/>
  <c:set var="r" value="${d.booking.reservation}"/>
  <c:set var="room" value="${d.booking.room}"/>
  <c:set var="guest" value="${d.booking.guest}"/>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-4xl print-full">
      <div class="no-print flex flex-wrap items-center justify-between gap-3">
        <a href="${ctx}/admin/payments" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to payments</a>
        <button type="button" class="btn btn-outline btn-sm" data-print><i data-lucide="printer"></i>Print invoice</button>
      </div>
      <c:if test="${param.done == 'recorded'}">
        <div class="no-print alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Payment recorded. Booking <span class="mono"><c:out value="${p.bookingId}"/></span> is now <c:out value="${p.status.label}"/>.</span></div>
      </c:if>

      <article class="card mt-6 p-8 sm:p-12">
        <header class="flex flex-col sm:flex-row justify-between gap-8">
          <div>
            <div class="flex items-center gap-3">
              <img src="${ctx}/img/favicon.svg" class="w-11 h-11" alt="">
              <div>
                <p class="font-display text-2xl font-semibold text-forest leading-none">The Orchid Estate</p>
                <p class="text-[10px] tracking-[.3em] uppercase text-muted mt-1">Hill Country · Sri Lanka</p>
              </div>
            </div>
            <p class="mt-5 text-sm text-muted leading-relaxed">12 Estate Road, Nuwara Eliya<br>+94 52 222 0198 · stay@orchidestate.lk</p>
          </div>
          <div class="sm:text-right">
            <p class="font-display text-5xl font-semibold text-forest">${p.pending ? 'Invoice' : 'Receipt'}</p>
            <dl class="mt-3 text-sm space-y-1">
              <div><dt class="inline text-muted">Payment </dt><dd class="inline mono"><c:out value="${p.id}"/></dd></div>
              <div><dt class="inline text-muted">Booking </dt><dd class="inline mono"><c:out value="${p.bookingId}"/></dd></div>
              <div><dt class="inline text-muted">Issued </dt><dd class="inline"><c:out value="${p.paidOnLabel}"/></dd></div>
            </dl>
          </div>
        </header>

        <div class="ornament my-10"><span></span></div>

        <div class="grid sm:grid-cols-3 gap-6 text-sm">
          <div>
            <p class="label">Billed to</p>
            <p class="font-semibold"><c:out value="${guest.name}"/></p>
            <p class="text-muted"><c:out value="${guest.email}"/></p>
            <c:if test="${p.guestRate > 0}"><span class="badge badge-vip no-dot mt-2"><i data-lucide="crown"></i>VIP guest</span></c:if>
          </div>
          <div>
            <p class="label">Stay</p>
            <p class="font-semibold"><c:out value="${room.roomName}"/> · Room <c:out value="${room.roomNumber}"/></p>
            <p class="text-muted"><c:out value="${r.checkInLabel}"/> – <c:out value="${r.checkOutLabel}"/> · ${r.nights} ${r.nights == 1 ? 'night' : 'nights'}</p>
          </div>
          <div>
            <p class="label">Payment method</p>
            <p class="font-semibold flex items-center gap-2"><i data-lucide="${p.method == 'cash' ? 'banknote' : 'credit-card'}" class="w-4 h-4 text-brass"></i><c:out value="${p.methodLabel}"/></p>
            <p class="text-muted">${p.pending ? 'Awaiting payment' : 'Recorded at front desk'}</p>
          </div>
        </div>

        <div class="table-wrap mt-10"><table class="table">
          <thead><tr><th>Description</th><th class="text-right">Nights</th><th class="text-right">Rate</th><th class="text-right">Amount</th></tr></thead>
          <tbody>
            <tr>
              <td><c:out value="${room.roomName}"/>, Room <c:out value="${room.roomNumber}"/></td>
              <td class="text-right">${r.nights}</td>
              <td class="text-right">LKR <fmt:formatNumber value="${p.nightlyRate}" pattern="#,##0.00"/></td>
              <td class="text-right">LKR <fmt:formatNumber value="${d.fullRateTotal}" pattern="#,##0.00"/></td>
            </tr>
            <c:if test="${d.roomDiscount > 0.005}">
              <tr class="text-sage"><td colspan="3">Long-stay discount</td><td class="text-right">− LKR <fmt:formatNumber value="${d.roomDiscount}" pattern="#,##0.00"/></td></tr>
            </c:if>
            <c:if test="${p.guestRate > 0}">
              <tr class="text-brass"><td colspan="3">VIP discount (<fmt:formatNumber value="${p.guestRate}" type="percent" maxFractionDigits="1"/>)</td><td class="text-right">− LKR <fmt:formatNumber value="${p.guestDiscount}" pattern="#,##0.00"/></td></tr>
            </c:if>
            <c:if test="${p.methodRate > 0}">
              <tr class="text-sage"><td colspan="3">Cash discount (<fmt:formatNumber value="${p.methodRate}" type="percent" maxFractionDigits="1"/> of LKR <fmt:formatNumber value="${p.roomTotal - p.guestDiscount}" pattern="#,##0.00"/>)</td><td class="text-right">− LKR <fmt:formatNumber value="${p.methodDiscount}" pattern="#,##0.00"/></td></tr>
            </c:if>
          </tbody>
        </table></div>

        <div class="mt-6 flex flex-col sm:flex-row items-start sm:items-end justify-between gap-6 border-t border-line pt-6">
          <span class="paid-stamp ${p.refunded ? '!text-clay !border-clay' : ''}">${fn:toUpperCase(p.status.value)}</span>
          <div class="sm:text-right">
            <p class="text-sm text-muted">Total ${p.pending ? 'due' : (p.refunded ? 'refunded' : 'paid')}<c:if test="${p.combinedRate > 0}"> · combined discount <fmt:formatNumber value="${p.combinedRate}" type="percent" maxFractionDigits="1"/></c:if></p>
            <p class="font-display text-5xl font-semibold text-forest">LKR <fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></p>
          </div>
        </div>

        <c:if test="${p.method == 'card'}"><p class="mt-6 text-sm text-muted">Simulated card payment — no real charge or refund was processed.</p></c:if>
        <p class="mt-12 text-center text-xs text-muted">Thank you for staying with us. We hope to welcome you back to the hills soon.</p>
      </article>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
