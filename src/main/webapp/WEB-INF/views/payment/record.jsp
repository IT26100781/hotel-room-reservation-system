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
      <p class="text-muted mt-2">Record cash or simulate a card payment. Save as pending, or mark it paid now.</p>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <c:set var="chosenMethod" value="${param.method == 'card' ? 'card' : 'cash'}"/>
      <form method="post" action="${ctx}/admin/payments/record" class="mt-8 grid xl:grid-cols-[1fr_380px] gap-8 items-start"
            data-payment-calc data-cash-rate="${cashRate}">
        <input type="hidden" name="csrf" value="${sessionScope.paymentCsrf}">
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

            <fieldset class="mt-5 grid sm:grid-cols-2 gap-4 ${chosenMethod == 'card' ? '' : 'hidden'}" data-show-when="card" data-card-fields>
              <legend class="label mb-3">Simulated card details</legend>
              <div class="sm:col-span-2 alert alert-info">
                <i data-lucide="info"></i>
                <div class="w-full">
                  <p>Demo only. No money is charged. Click a test card to fill it in:</p>
                  <div class="mt-3 grid gap-2">
                    <button type="button" class="test-card" data-test-card="4242 4242 4242 4242"><span>Visa · approved</span><span class="mono">4242 4242 4242 4242</span></button>
                    <button type="button" class="test-card" data-test-card="5555 5555 5555 4444"><span>Mastercard · approved</span><span class="mono">5555 5555 5555 4444</span></button>
                    <button type="button" class="test-card" data-test-card="4000 0000 0000 0002"><span>Declined card</span><span class="mono">4000 0000 0000 0002</span></button>
                  </div>
                  <p class="mt-3 text-xs">Use any current or future expiry and any 3-digit CVV.</p>
                </div>
              </div>
              <div class="sm:col-span-2"><label class="label" for="cardholder">Cardholder name</label><input class="input" id="cardholder" name="cardholder" minlength="2" maxlength="100" autocomplete="off"></div>
              <div class="sm:col-span-2"><label class="label" for="cardNumber">Test card number</label><input class="input mono" id="cardNumber" name="cardNumber" inputmode="numeric" maxlength="19" placeholder="4242 4242 4242 4242" autocomplete="off"></div>
              <div><label class="label" for="expiry">Expiry (MM/YY)</label><input class="input" type="text" id="expiry" name="expiry" inputmode="numeric" placeholder="MM/YY" maxlength="5" pattern="(0[1-9]|1[0-2])/[0-9]{2}" title="Enter the expiry as MM/YY, for example 12/30" autocomplete="off"></div>
              <div><label class="label" for="cvv">Test CVV</label><input class="input" type="password" id="cvv" name="cvv" inputmode="numeric" pattern="[0-9]{3}" maxlength="3" autocomplete="off"></div>
              <p class="sm:col-span-2 hint">Only the card type and last four digits are saved. Card details are cleared after submission.</p>
            </fieldset>
          </section>
          <section class="card card-pad grid sm:grid-cols-2 gap-4">
            <div><label class="label" for="status">Status</label><select class="select" id="status" name="status"><option value="paid">Paid</option><option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Pending</option></select></div>
            <div><label class="label" for="paidOn">Transaction date</label><input class="input" id="paidOn" name="paidOn" type="date" required max="${requestScope.today}" value="<c:out value='${empty param.paidOn ? requestScope.today : param.paidOn}'/>"></div>
            <p class="hint sm:col-span-2">The amount is calculated from the booking. Pending entries can be marked paid from payment history.</p>
          </section>
        </div>

        <aside class="xl:sticky xl:top-24 card overflow-hidden">
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
  <script src="${ctx}/js/payments.js"></script>
  <script>
    document.querySelectorAll('[data-test-card]').forEach((btn) => btn.addEventListener('click', () => {
      const number = document.getElementById('cardNumber');
      number.value = btn.dataset.testCard;
      number.dispatchEvent(new Event('input', { bubbles: true }));
      document.getElementById('cardholder').focus();
    }));
  </script>
</body>
</html>
