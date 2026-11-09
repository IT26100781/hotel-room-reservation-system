<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Record a payment taken at the desk (GET/POST /admin/payments/record). Shown by RecordPaymentServlet.
  The amount on this page is only a preview: PaymentService works out the real total when it saves.
  Request attributes:
    unpaid    - List<BookingDetails> that can take a payment
    cardTypes - List<String> of accepted card types
    cashRate  - cash discount rate (e.g. 0.05)
    error     - message if the payment could not be saved
  URL parameter ?bookingId= preselects a booking (sent from the bookings page).
  Form fields: bookingId, method (cash | card), cardType, last4
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Record a payment"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="payments"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-6xl">
      <a href="${ctx}/admin/payments" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to payments</a>
      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Record a payment</h1>
      <p class="text-muted mt-2">Take payment at the desk, then record it here to mark the booking as paid.</p>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <c:set var="chosenMethod" value="${param.method == 'card' ? 'card' : 'cash'}"/>
      <form method="post" action="${ctx}/admin/payments/record" class="mt-8 grid lg:grid-cols-[1fr_380px] gap-8 items-start"
            data-payment-calc data-cash-rate="${cashRate}">
        <div class="space-y-6">
          <section class="card card-pad">
            <label class="label" for="bookingId">Booking</label>
            <select id="bookingId" name="bookingId" class="select" required>
              <option value="">${empty unpaid ? 'No bookings are waiting for payment' : 'Select an unpaid booking…'}</option>
              <c:forEach var="b" items="${unpaid}">
                <c:set var="r" value="${b.reservation}"/>
                <option value="<c:out value='${r.id}'/>" ${param.bookingId == r.id ? 'selected' : ''}
                        data-amount="${b.roomTotal}" data-vip="${b.guestRate}"
                        data-guest="<c:out value='${b.guest.name}'/>"
                        data-room="<c:out value='${b.room.roomName}'/> · Room <c:out value='${b.room.roomNumber}'/>"
                        data-stay="<c:out value='${r.stayLabel}'/> · ${r.nights} ${r.nights == 1 ? 'night' : 'nights'}">
                  <c:out value="${r.id}"/> · <c:out value="${b.guest.name}"/> · <c:out value="${b.room.typeDisplayName}"/> <c:out value="${b.room.roomNumber}"/> · <c:out value="${r.stayLabel}"/>
                </option>
              </c:forEach>
            </select>

            <div class="mt-5 rounded-2xl bg-sand/60 p-4 grid sm:grid-cols-3 gap-4 text-sm hidden" data-booking-summary>
              <div><p class="label !mb-1">Guest</p><p class="font-semibold flex items-center gap-1.5"><span data-out="guest">—</span><span class="badge badge-vip no-dot !px-1.5 !py-0.5" data-vip-badge><i data-lucide="crown"></i>VIP</span></p></div>
              <div><p class="label !mb-1">Room</p><p class="font-semibold" data-out="room">—</p></div>
              <div><p class="label !mb-1">Stay</p><p class="font-semibold" data-out="stay">—</p></div>
            </div>
          </section>

          <section class="card card-pad">
            <p class="label">Payment method</p>
            <div class="grid sm:grid-cols-2 gap-3">
              <label class="choice">
                <input type="radio" name="method" value="cash" ${chosenMethod == 'cash' ? 'checked' : ''}>
                <span class="choice-body flex items-center gap-4">
                  <span class="stat-icon"><i data-lucide="banknote"></i></span>
                  <span><span class="block font-semibold text-forest">Cash</span><span class="block text-xs text-muted"><fmt:formatNumber value="${cashRate}" type="percent" maxFractionDigits="1"/> cash discount</span></span>
                </span>
              </label>
              <label class="choice">
                <input type="radio" name="method" value="card" ${chosenMethod == 'card' ? 'checked' : ''}>
                <span class="choice-body flex items-center gap-4">
                  <span class="stat-icon"><i data-lucide="credit-card"></i></span>
                  <span><span class="block font-semibold text-forest">Card</span><span class="block text-xs text-muted">No extra discount</span></span>
                </span>
              </label>
            </div>

            <div class="mt-5 grid sm:grid-cols-2 gap-4 ${chosenMethod == 'card' ? '' : 'hidden'}" data-show-when="card">
              <div>
                <label class="label" for="cardType">Card type</label>
                <select id="cardType" name="cardType" class="select">
                  <c:forEach var="type" items="${cardTypes}">
                    <option ${param.cardType == type ? 'selected' : ''}><c:out value="${type}"/></option>
                  </c:forEach>
                </select>
              </div>
              <div>
                <label class="label" for="last4">Last 4 digits</label>
                <div class="input-icon"><i data-lucide="lock"></i><input id="last4" name="last4" class="input tracking-[.4em]" inputmode="numeric" maxlength="4" pattern="[0-9]{4}" placeholder="••••" autocomplete="off" value="<c:out value='${param.last4}'/>"></div>
              </div>
              <p class="sm:col-span-2 hint flex items-center gap-1.5"><i data-lucide="shield-check" class="w-4 h-4 text-sage"></i>Never type the full card number. Only the last 4 digits are stored.</p>
            </div>
          </section>
        </div>

        <aside class="lg:sticky lg:top-24 card overflow-hidden">
          <div class="panel-dark p-6">
            <p class="text-xs tracking-[.22em] uppercase text-brass-light font-bold">Amount due</p>
            <p class="font-display text-5xl font-semibold mt-2" data-out="total">—</p>
            <p class="text-ivory/60 text-sm mt-1">Combined discount <span class="text-brass-light font-semibold" data-out="combined">—</span></p>
          </div>
          <div class="p-6">
            <dl class="space-y-3 text-sm">
              <div class="flex justify-between"><dt class="text-ink/70">Room total</dt><dd data-out="subtotal">—</dd></div>
              <div class="flex justify-between text-brass"><dt data-out="vip-label">VIP discount</dt><dd data-out="vip">—</dd></div>
              <div class="flex justify-between text-sage"><dt>Cash discount (<fmt:formatNumber value="${cashRate}" type="percent" maxFractionDigits="1"/>)</dt><dd data-out="cash">—</dd></div>
            </dl>
            <div class="mt-5 rounded-xl bg-sand/60 px-4 py-3">
              <p class="label !mb-1">How it's calculated</p>
              <p class="mono text-xs" data-out="formula">—</p>
              <p class="text-xs text-muted mt-1">Discounts stack, they are not simply added.</p>
            </div>
            <button type="submit" class="btn btn-primary btn-lg btn-block mt-6" ${empty unpaid ? 'disabled' : ''}><i data-lucide="check"></i>Record payment</button>
            <a href="${ctx}/admin/payments" class="btn btn-ghost btn-block mt-2">Cancel</a>
          </div>
        </aside>
      </form>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
