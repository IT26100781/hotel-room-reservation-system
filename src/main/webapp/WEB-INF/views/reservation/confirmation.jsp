<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Booking confirmed (GET /reservation/confirmation?id=BK01). Shown by BookingConfirmationServlet after a booking is saved.
  Request attributes:
    b - BookingDetails of the new booking (reservation, room, guest and the estimated price)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Booking confirmed"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <c:set var="r" value="${b.reservation}"/>

  <main class="mx-auto max-w-3xl px-4 sm:px-6 pt-10 pb-16">
    <div class="steps justify-center">
      <div class="step done"><span class="step-dot"><i data-lucide="check"></i></span>Choose room</div>
      <span class="step-line"></span>
      <div class="step done"><span class="step-dot"><i data-lucide="check"></i></span>Review &amp; confirm</div>
      <span class="step-line"></span>
      <div class="step current"><span class="step-dot"><i data-lucide="check"></i></span>Booked</div>
    </div>

    <div class="card mt-10 overflow-hidden">
      <div class="panel-dark px-8 py-10 text-center relative overflow-hidden">
        <div class="absolute inset-0 opacity-25"><img class="img-cover" src="<c:out value='${b.room.imageUrl}'/>" alt=""></div>
        <div class="relative">
          <span class="mx-auto grid place-items-center w-16 h-16 rounded-full bg-brass text-forest-900"><i data-lucide="check" class="w-8 h-8"></i></span>
          <h1 class="font-display text-5xl font-semibold mt-5">Your stay is reserved</h1>
          <p class="text-ivory/70 mt-2">We've saved your room. See you in the hills, <c:out value="${fn:substringBefore(b.guest.name.concat(' '), ' ')}"/>.</p>
        </div>
      </div>

      <div class="p-8">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p class="label !mb-1">Booking reference</p>
            <p class="font-display text-4xl font-semibold text-forest tracking-wide"><c:out value="${r.id}"/></p>
          </div>
          <div class="flex gap-2">
            <span class="badge ${r.state.badge}"><c:out value="${r.state.label}"/></span>
            <span class="badge badge-${r.paymentStatus.value}">Payment <c:out value="${fn:toLowerCase(r.paymentStatus.label)}"/></span>
          </div>
        </div>

        <dl class="mt-8 grid sm:grid-cols-4 gap-6 text-sm">
          <div><dt class="label !mb-1">Room</dt><dd class="font-semibold"><c:out value="${b.room.roomName}"/> · <c:out value="${b.room.roomNumber}"/></dd></div>
          <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold"><c:out value="${r.checkInLabel}"/></dd></div>
          <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold"><c:out value="${r.checkOutLabel}"/></dd></div>
          <div><dt class="label !mb-1">Estimated total</dt><dd class="font-semibold">LKR <fmt:formatNumber value="${b.estimatedTotal}" pattern="#,##0"/></dd></div>
        </dl>
        <p class="mt-4 text-sm text-muted"><c:out value="${r.nights}"/> ${r.nights == 1 ? 'night' : 'nights'} · <c:out value="${r.guests}"/> ${r.guests == 1 ? 'guest' : 'guests'} · <c:out value="${r.cancellationPolicy}"/></p>

        <div class="ornament my-8"><span></span></div>

        <h2 class="font-display text-2xl font-semibold text-forest">What happens next</h2>
        <ol class="mt-5 relative border-l border-line ml-3 space-y-6">
          <li class="pl-6 relative"><span class="absolute -left-[9px] top-0.5 w-4 h-4 rounded-full bg-brass ring-4 ring-paper"></span><p class="font-semibold">Arrive from 2:00 pm on <c:out value="${r.checkInLabel}"/></p><p class="text-sm text-muted">Show your booking reference at the front desk.</p></li>
          <li class="pl-6 relative"><span class="absolute -left-[9px] top-0.5 w-4 h-4 rounded-full bg-line ring-4 ring-paper"></span><p class="font-semibold">Check in and pay</p><p class="text-sm text-muted">Pay by cash (extra 5% off) or card. Staff record it for you.</p></li>
          <li class="pl-6 relative"><span class="absolute -left-[9px] top-0.5 w-4 h-4 rounded-full bg-line ring-4 ring-paper"></span><p class="font-semibold">After your stay</p><p class="text-sm text-muted">Leave a verified review from My bookings.</p></li>
        </ol>

        <div class="mt-10 flex flex-wrap gap-3">
          <a href="${ctx}/bookings" class="btn btn-primary"><i data-lucide="calendar-days"></i>View my bookings</a>
          <a href="${ctx}/" class="btn btn-outline">Back to home</a>
        </div>
      </div>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
