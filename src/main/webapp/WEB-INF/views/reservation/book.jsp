<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Book a room (GET/POST /reservation/book). Shown by BookRoomServlet. GuestBookingFilter makes sure a guest is signed in.
  "Check price" sends GET with the dates; the servlet returns a quote. "Confirm booking" sends POST and saves it.
  Request attributes:
    room    - the Room being booked
    quote   - BookingDetails for the chosen dates (only when the dates were checked and are free)
    minDate - today, for the date pickers
    error   - message if the dates are not valid or the room is taken
  Form fields: roomId (hidden), checkIn, checkOut, guests
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Complete your booking"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <c:set var="partySize" value="${empty param.guests ? '2' : param.guests}"/>
  <c:set var="hasQuote" value="${not empty quote and empty error}"/>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-10 pb-16">
    <div class="steps">
      <div class="step done"><span class="step-dot"><i data-lucide="check"></i></span>Choose room</div>
      <span class="step-line"></span>
      <div class="step current"><span class="step-dot">2</span>Review &amp; confirm</div>
      <span class="step-line"></span>
      <div class="step"><span class="step-dot">3</span>Booked</div>
    </div>

    <h1 class="font-display text-5xl font-semibold text-forest mt-8">Review your stay</h1>
    <p class="text-muted mt-2">Choose your dates and check the price. Nothing is charged until you pay at the front desk.</p>

    <div class="mt-10 grid lg:grid-cols-[1fr_400px] gap-10 items-start">
      <form id="book-form" method="post" action="${ctx}/reservation/book" class="space-y-6" data-booking-calc>
        <input type="hidden" name="roomId" value="<c:out value='${room.roomId}'/>">

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">i.</span>Your dates</h2>
          <div class="mt-5 grid sm:grid-cols-3 gap-4">
            <div>
              <label class="label" for="checkIn">Check-in</label>
              <input id="checkIn" type="date" name="checkIn" class="input" required min="${minDate}" value="<c:out value='${param.checkIn}'/>" data-date-field>
            </div>
            <div>
              <label class="label" for="checkOut">Check-out</label>
              <input id="checkOut" type="date" name="checkOut" class="input" required value="<c:out value='${param.checkOut}'/>" data-date-field>
            </div>
            <div>
              <label class="label" for="guests">Guests</label>
              <select id="guests" name="guests" class="select" data-date-field>
                <c:forEach var="n" begin="1" end="${room.capacity}">
                  <option value="${n}" ${partySize == n.toString() ? 'selected' : ''}>${n}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <c:choose>
            <c:when test="${not empty error}">
              <div class="alert alert-error mt-5"><i data-lucide="calendar-x"></i><span><c:out value="${error}"/> <a href="${ctx}/reservation/search" class="underline">Search other rooms</a></span></div>
            </c:when>
            <c:when test="${hasQuote}">
              <div class="alert alert-success mt-5" data-quote-ok><i data-lucide="circle-check"></i><span>Room <c:out value="${room.roomNumber}"/> is free for these dates.</span></div>
            </c:when>
          </c:choose>
          <div class="alert alert-brass mt-5 ${hasQuote ? 'hidden' : ''}" data-quote-stale><i data-lucide="info"></i><span>Choose your dates, then press <strong>Check price</strong>.</span></div>
          <button type="submit" formmethod="get" formnovalidate class="btn btn-outline mt-5" data-check-price><i data-lucide="refresh-cw"></i>Check price</button>
        </section>

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">ii.</span>Guest details</h2>
          <p class="text-sm text-muted mt-1">Taken from your profile. <a href="${ctx}/profile" class="text-forest font-semibold underline-offset-4 hover:underline">Edit profile</a></p>
          <div class="mt-5 grid sm:grid-cols-2 gap-4">
            <div><label class="label">Full name</label><input class="input" value="<c:out value='${me.name}'/>" readonly></div>
            <div><label class="label">Email</label><input class="input" value="<c:out value='${me.email}'/>" readonly></div>
          </div>
          <c:if test="${me.discount > 0}">
            <div class="mt-4 flex items-center gap-3 rounded-2xl bg-brass/10 border border-brass/30 px-4 py-3">
              <span class="badge badge-vip no-dot"><i data-lucide="crown"></i>VIP</span>
              <span class="text-sm text-forest">Your <fmt:formatNumber value="${me.discount}" type="percent" maxFractionDigits="1"/> VIP discount is applied automatically.</span>
            </div>
          </c:if>
        </section>

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">iii.</span>Payment &amp; policies</h2>
          <ul class="mt-5 space-y-3 text-sm text-ink/75">
            <li class="flex gap-3"><i data-lucide="concierge-bell" class="w-5 h-5 text-brass shrink-0"></i>Pay at the front desk by cash or card when you arrive. No card details are taken online.</li>
            <li class="flex gap-3"><i data-lucide="banknote" class="w-5 h-5 text-brass shrink-0"></i>Paying in cash? You get an extra 5% off<c:if test="${me.discount > 0}">, combined with your VIP discount</c:if>.</li>
            <li class="flex gap-3"><i data-lucide="calendar-x" class="w-5 h-5 text-brass shrink-0"></i>Free cancellation any time before check-in, from <a href="${ctx}/bookings" class="font-semibold text-forest">My bookings</a>.</li>
            <li class="flex gap-3"><i data-lucide="clock" class="w-5 h-5 text-brass shrink-0"></i>If you don't check in on your arrival day, the booking is released.</li>
          </ul>
          <label class="mt-6 flex items-start gap-3 text-sm cursor-pointer">
            <input type="checkbox" required class="mt-1 w-4 h-4 accent-[rgb(31,59,47)]">
            <span>I understand the cancellation and no-show policy.</span>
          </label>
        </section>
      </form>

      <aside class="lg:sticky lg:top-28">
        <div class="card overflow-hidden">
          <div class="h-48"><img class="img-cover" src="<c:out value='${room.imageUrl}'/>" alt="<c:out value='${room.roomName}'/>"></div>
          <div class="p-6">
            <p class="text-xs tracking-[.2em] uppercase text-brass font-bold"><c:out value="${room.typeDisplayName}"/> · Room <c:out value="${room.roomNumber}"/></p>
            <h2 class="font-display text-3xl font-semibold text-forest mt-1"><c:out value="${room.roomName}"/></h2>
            <p class="text-sm text-muted mt-1">LKR <c:out value="${room.formattedRate}"/> / night · up to <c:out value="${room.capacity}"/> guests</p>

            <c:choose>
              <c:when test="${hasQuote}">
                <div data-quote-panel>
                  <dl class="mt-5 grid grid-cols-2 gap-4 text-sm">
                    <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold"><c:out value="${quote.reservation.checkInLabel}"/></dd></div>
                    <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold"><c:out value="${quote.reservation.checkOutLabel}"/></dd></div>
                  </dl>
                  <dl class="mt-6 space-y-3 text-sm border-t border-line pt-5">
                    <div class="flex justify-between"><dt class="text-ink/70">LKR <fmt:formatNumber value="${room.baseRate}" pattern="#,##0"/> × ${quote.reservation.nights} ${quote.reservation.nights == 1 ? 'night' : 'nights'}</dt><dd>LKR <fmt:formatNumber value="${quote.fullRateTotal}" pattern="#,##0"/></dd></div>
                    <c:if test="${quote.roomDiscount > 0}">
                      <div class="flex justify-between text-sage"><dt>Long-stay discount</dt><dd>− LKR <fmt:formatNumber value="${quote.roomDiscount}" pattern="#,##0"/></dd></div>
                    </c:if>
                    <c:if test="${quote.guestRate > 0}">
                      <div class="flex justify-between text-brass"><dt>VIP discount (<fmt:formatNumber value="${quote.guestRate}" type="percent" maxFractionDigits="1"/>)</dt><dd>− LKR <fmt:formatNumber value="${quote.guestDiscount}" pattern="#,##0"/></dd></div>
                    </c:if>
                    <div class="flex justify-between border-t border-line pt-3 text-base font-semibold text-forest"><dt>Estimated total</dt><dd>LKR <fmt:formatNumber value="${quote.estimatedTotal}" pattern="#,##0"/></dd></div>
                  </dl>
                  <p class="mt-2 text-xs text-muted">The final amount is worked out at the desk. Cash payments save a further 5%.</p>
                </div>
                <button type="submit" form="book-form" class="btn btn-primary btn-lg btn-block mt-6" data-confirm><i data-lucide="calendar-check"></i>Confirm booking</button>
              </c:when>
              <c:otherwise>
                <p class="mt-5 text-sm text-muted border-t border-line pt-5">Your price appears here once you check your dates.</p>
                <button type="submit" form="book-form" formmethod="get" formnovalidate class="btn btn-primary btn-lg btn-block mt-6"><i data-lucide="refresh-cw"></i>Check price</button>
              </c:otherwise>
            </c:choose>
            <a href="${ctx}/rooms/detail?id=<c:out value='${room.roomId}'/>" class="btn btn-ghost btn-block mt-2">Back to room</a>
          </div>
        </div>
      </aside>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
  <script>
    // Changing the dates makes the shown price out of date: hide "Confirm" until the price is checked again.
    document.querySelectorAll('[data-date-field]').forEach(function (field) {
      field.addEventListener('change', function () {
        document.querySelectorAll('[data-quote-panel], [data-quote-ok], [data-confirm]').forEach(function (el) { el.classList.add('hidden'); });
        document.querySelectorAll('[data-quote-stale]').forEach(function (el) { el.classList.remove('hidden'); });
      });
    });
  </script>
</body>
</html>
