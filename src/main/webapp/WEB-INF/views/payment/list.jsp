<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Payment history for staff (GET /admin/payments). Shown by PaymentListServlet.
  Request attributes:
    payments   - List<PaymentDetails> (payment + its booking)
    refundsDue - List<PaymentDetails> paid bookings that were cancelled before check-in
    metrics    - PaymentMetrics: collected, paidCount, awaitingCount, awaitingEstimate, refunded, refundedCount
  URL parameters: ?done=deleted|refunded, ?error=message
  Posts: /admin/payments/refund (id), /admin/payments/delete (id)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Payments"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="payments"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Front desk</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Payments</h1>
          <p class="text-muted mt-2">Track pending payments, receipts and refunds for every booking.</p>
        </div>
        <a href="${ctx}/admin/payments/record" class="btn btn-primary"><i data-lucide="plus"></i>Record payment</a>
      </div>

      <c:choose>
        <c:when test="${param.done == 'paid'}"><div class="alert alert-success mt-6">Payment marked as paid.</div></c:when>
        <c:when test="${param.done == 'deleted'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Payment deleted. The booking is back to payment pending.</span></div></c:when>
        <c:when test="${param.done == 'refunded'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Payment marked as refunded.</span></div></c:when>
      </c:choose>
      <c:if test="${not empty param.error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${param.error}"/></span></div>
      </c:if>

      <div class="mt-8 grid sm:grid-cols-3 gap-4">
        <div class="stat"><div class="flex items-center justify-between"><p class="stat-label">Collected</p><span class="stat-icon"><i data-lucide="banknote"></i></span></div><p class="stat-value mt-2">LKR <fmt:formatNumber value="${metrics.collected}" pattern="#,##0"/></p><p class="text-xs text-muted">${metrics.paidCount} paid ${metrics.paidCount == 1 ? 'payment' : 'payments'}</p></div>
        <div class="stat"><div class="flex items-center justify-between"><p class="stat-label">Awaiting payment</p><span class="stat-icon"><i data-lucide="clock"></i></span></div><p class="stat-value mt-2">${metrics.awaitingCount}</p><p class="text-xs text-muted">bookings, est. LKR <fmt:formatNumber value="${metrics.awaitingEstimate}" pattern="#,##0"/></p></div>
        <div class="stat"><div class="flex items-center justify-between"><p class="stat-label">Refunded</p><span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="rotate-ccw"></i></span></div><p class="stat-value mt-2 !text-clay">LKR <fmt:formatNumber value="${metrics.refunded}" pattern="#,##0"/></p><p class="text-xs text-muted">${metrics.refundedCount} refunded ${metrics.refundedCount == 1 ? 'booking' : 'bookings'}</p></div>
      </div>

      <div class="card card-pad mt-8 ${empty refundsDue ? '' : 'border-clay/40'}">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div class="flex items-start gap-4">
            <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="shield-alert"></i></span>
            <div>
              <h2 class="font-semibold text-forest">Refund needed</h2>
              <p class="text-sm text-muted mt-0.5">When a paid booking is cancelled before check-in it shows up here. Mark it as refunded after returning the money.</p>
            </div>
          </div>
          <c:if test="${empty refundsDue}"><span class="badge badge-active">All clear</span></c:if>
        </div>
        <c:if test="${not empty refundsDue}">
          <ul class="mt-5 divide-y divide-line border-t border-line">
            <c:forEach var="d" items="${refundsDue}">
              <li class="py-3 flex flex-wrap items-center justify-between gap-3">
                <span class="text-sm"><span class="mono"><c:out value="${d.payment.id}"/></span> · <span class="mono"><c:out value="${d.payment.bookingId}"/></span> · <c:out value="${d.booking.guest.name}"/></span>
                <span class="flex items-center gap-3">
                  <span class="font-semibold">LKR <fmt:formatNumber value="${d.payment.amount}" pattern="#,##0.00"/></span>
                  <button type="button" class="btn btn-danger-outline btn-sm" data-modal-open="refund-modal" data-payment="<c:out value='${d.payment.id}'/>"
                          data-amount="LKR <fmt:formatNumber value='${d.payment.amount}' pattern='#,##0.00'/>"><i data-lucide="rotate-ccw"></i>Mark refunded</button>
                </span>
              </li>
            </c:forEach>
          </ul>
        </c:if>
      </div>

      <div class="card mt-8">
        <div class="p-4 sm:p-5 flex flex-col md:flex-row gap-3 md:items-center justify-between border-b border-line">
          <div class="flex flex-wrap gap-2" data-chip-filter="#payments-table" data-chip-attr="method">
            <button type="button" class="chip active" data-chip="all">All methods</button>
            <button type="button" class="chip" data-chip="cash"><i data-lucide="banknote" class="w-4 h-4"></i>Cash</button>
            <button type="button" class="chip" data-chip="card"><i data-lucide="credit-card" class="w-4 h-4"></i>Card</button>
          </div>
          <select class="select md:w-40" aria-label="Filter status" data-filter-target="#payments-table" data-filter-attr="status"><option value="all">All statuses</option><option value="pending">Pending</option><option value="paid">Paid</option><option value="refunded">Refunded</option></select>
          <div class="input-icon md:w-72"><i data-lucide="search"></i><input class="input !py-2.5" placeholder="Search payment, booking or guest…" data-filter-target="#payments-table"></div>
        </div>
        <div class="table-wrap">
          <table class="table" id="payments-table">
            <thead>
              <tr><th>Payment</th><th>Booking</th><th>Guest</th><th>Method</th><th>Date</th><th class="text-right">Amount</th><th>Status</th><th class="text-right">Actions</th></tr>
            </thead>
            <tbody>
              <c:forEach var="d" items="${payments}">
                <c:set var="p" value="${d.payment}"/>
                <tr data-item data-status="${p.status.value}" data-method="${p.method}" class="${p.refunded ? 'text-ink/60' : ''}">
                  <td class="mono"><c:out value="${p.id}"/></td>
                  <td class="mono"><c:out value="${p.bookingId}"/></td>
                  <td><span class="inline-flex items-center gap-1.5"><c:out value="${d.booking.guest.name}"/><c:if test="${p.guestRate > 0}"><i data-lucide="crown" class="w-3.5 h-3.5 text-brass"></i></c:if></span></td>
                  <td><span class="inline-flex items-center gap-2"><i data-lucide="${p.method == 'cash' ? 'banknote' : 'credit-card'}" class="w-4 h-4 text-muted"></i><c:out value="${p.methodLabel}"/></span></td>
                  <td><c:out value="${p.paidOnLabel}"/></td>
                  <td class="text-right font-semibold ${p.refunded ? 'line-through' : ''}">LKR <fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td>
                  <td><span class="badge badge-${p.status.value}"><c:out value="${p.status.label}"/></span></td>
                  <td class="text-right whitespace-nowrap">
                    <a href="${ctx}/admin/payments/invoice?id=<c:out value='${p.id}'/>" class="btn btn-ghost btn-sm"><i data-lucide="receipt"></i>Invoice</a>
                    <c:if test="${p.pending}">
                      <form class="inline" method="post" action="${ctx}/admin/payments/paid">
                        <input type="hidden" name="csrf" value="${sessionScope.paymentCsrf}"><input type="hidden" name="id" value="${p.id}">
                        <button class="btn btn-outline btn-sm" type="submit">Mark paid</button>
                      </form>
                    </c:if>
                    <c:if test="${p.status.value == 'paid'}">
                      <button type="button" class="btn btn-ghost btn-sm" data-modal-open="refund-modal" data-payment="${p.id}" data-amount="LKR ${p.amount}">Refund</button>
                    </c:if>
                      <button type="button" class="btn btn-ghost btn-sm text-clay" title="Delete" data-modal-open="delete-modal" data-payment="<c:out value='${p.id}'/>"><i data-lucide="trash-2"></i></button>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="${empty payments ? '' : 'hidden'} p-10 text-center text-muted" data-empty-for="#payments-table">No matching payments. Record a payment for an existing booking to get started.</div>
      </div>
    </main>
  </div>

  <div class="modal" id="delete-modal" role="dialog" aria-modal="true" aria-labelledby="delete-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="trash-2"></i></span>
      <h2 id="delete-title" class="font-display text-3xl font-semibold text-forest mt-4">Delete <span data-fill="payment">payment</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">Use this only to remove a payment recorded by mistake. The booking goes back to <strong>payment pending</strong>. This can't be undone.</p>
      <form method="post" action="${ctx}/admin/payments/delete" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="csrf" value="${sessionScope.paymentCsrf}">
        <input type="hidden" name="id" data-fill="payment">
        <button type="button" class="btn btn-ghost" data-modal-close>Keep it</button>
        <button type="submit" class="btn btn-danger">Delete payment</button>
      </form>
    </div>
  </div>

  <div class="modal" id="refund-modal" role="dialog" aria-modal="true" aria-labelledby="refund-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="rotate-ccw"></i></span>
      <h2 id="refund-title" class="font-display text-3xl font-semibold text-forest mt-4">Mark <span data-fill="payment">payment</span> refunded?</h2>
      <p class="mt-2 text-sm text-ink/70">Confirm you have returned <strong data-fill="amount"></strong> to the guest. The payment is kept for the records.</p>
      <form method="post" action="${ctx}/admin/payments/refund" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="csrf" value="${sessionScope.paymentCsrf}">
        <input type="hidden" name="id" data-fill="payment">
        <button type="button" class="btn btn-ghost" data-modal-close>Not yet</button>
        <button type="submit" class="btn btn-danger">Mark refunded</button>
      </form>
    </div>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
