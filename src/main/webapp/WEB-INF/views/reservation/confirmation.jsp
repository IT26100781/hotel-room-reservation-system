<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Booking Confirmed"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

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
        <div class="relative">
          <span class="mx-auto grid place-items-center w-16 h-16 rounded-full bg-brass text-forest-900"><i data-lucide="check" class="w-8 h-8"></i></span>
          <h1 class="font-display text-5xl font-semibold mt-5">Your stay is reserved</h1>
          <p class="text-ivory/70 mt-2">We've saved your room. See you in the hills, <c:out value="${me.name}"/>.</p>
        </div>
      </div>

      <div class="p-8">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p class="label !mb-1">Booking reference</p>
            <p class="font-display text-4xl font-semibold text-forest tracking-wide"><c:out value="${booking.id}"/></p>
          </div>
          <div class="flex gap-2">
            <span class="badge badge-active">Active</span>
            <span class="badge badge-pending">Payment pending</span>
          </div>
        </div>

        <dl class="mt-8 grid sm:grid-cols-4 gap-6 text-sm">
          <div><dt class="label !mb-1">Room</dt><dd class="font-semibold"><c:out value="${booking.roomName}"/> · <c:out value="${booking.roomNumber}"/></dd></div>
          <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold"><c:out value="${booking.checkIn}"/></dd></div>
          <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold"><c:out value="${booking.checkOut}"/></dd></div>
          <div><dt class="label !mb-1">Total</dt><dd class="font-semibold">LKR <c:out value="${booking.formattedAmount}"/></dd></div>
        </dl>

        <div class="ornament my-8"><span></span></div>

        <h2 class="font-display text-2xl font-semibold text-forest">What happens next</h2>
        <ol class="mt-5 relative border-l border-line ml-3 space-y-6">
          <li class="pl-6 relative"><span class="absolute -left-[9px] top-0.5 w-4 h-4 rounded-full bg-brass ring-4 ring-paper"></span><p class="font-semibold">Arrive from 2:00 pm on <c:out value="${booking.checkIn}"/></p><p class="text-sm text-muted">Show your booking reference at the front desk.</p></li>
          <li class="pl-6 relative"><span class="absolute -left-[9px] top-0.5 w-4 h-4 rounded-full bg-line ring-4 ring-paper"></span><p class="font-semibold">Check in and pay</p><p class="text-sm text-muted">Pay by cash (extra 5% off) or card. Staff will record it for you.</p></li>
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
