<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Complete your booking"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body data-calc-scope>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-10">
    <div class="steps">
      <div class="step done"><span class="step-dot"><i data-lucide="check"></i></span>Choose room</div>
      <span class="step-line"></span>
      <div class="step current"><span class="step-dot">2</span>Review &amp; confirm</div>
      <span class="step-line"></span>
      <div class="step"><span class="step-dot">3</span>Booked</div>
    </div>

    <h1 class="font-display text-5xl font-semibold text-forest mt-8">Review your stay</h1>
    <p class="text-muted mt-2">Check the details below. Nothing is charged until you pay at the front desk.</p>

    <c:if test="${not empty error}">
      <div class="alert alert-error mt-6"><i data-lucide="alert-circle"></i><span><c:out value="${error}"/></span></div>
    </c:if>

    <div class="mt-10 grid lg:grid-cols-[1fr_400px] gap-10 items-start">
      <form id="book-form" action="${ctx}/reservation/book" method="post" class="space-y-6" data-booking-calc data-default-dates 
            data-rate="${not empty room ? room.baseRate : '0'}" data-discount="${not empty me and me.role == 'GUEST' ? me.discount : '0'}">
        <input type="hidden" name="roomId" value="${room.roomId}">

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">i.</span>Your dates</h2>
          <div class="mt-5 grid sm:grid-cols-3 gap-4">
            <div>
              <label class="label" for="checkIn">Check-in</label>
              <input id="checkIn" type="date" name="checkIn" class="input" required value="${param.checkIn}">
            </div>
            <div>
              <label class="label" for="checkOut">Check-out</label>
              <input id="checkOut" type="date" name="checkOut" class="input" required value="${param.checkOut}">
            </div>
            <div>
              <label class="label" for="guests">Guests</label>
              <select id="guests" name="guests" class="select">
                <c:forEach begin="1" end="${not empty room ? room.capacity : 4}" var="g">
                  <option value="${g}" ${g == 2 ? 'selected' : ''}>${g}</option>
                </c:forEach>
              </select>
            </div>
          </div>
        </section>

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">ii.</span>Guest details</h2>
          <p class="text-sm text-muted mt-1">Taken from your account profile.</p>
          <div class="mt-5 grid sm:grid-cols-2 gap-4">
            <div><label class="label">Full name</label><input class="input" value="<c:out value='${me.name}'/>" readonly></div>
            <div><label class="label">Email</label><input class="input" value="<c:out value='${me.email}'/>" readonly></div>
          </div>
          <c:if test="${not empty me and me.discount > 0}">
            <div class="mt-4 flex items-center gap-3 rounded-2xl bg-brass/10 border border-brass/30 px-4 py-3">
              <span class="badge badge-vip no-dot"><i data-lucide="crown"></i>VIP</span>
              <span class="text-sm text-forest">Your ${Math.round(me.discount * 100)}% VIP discount is applied automatically.</span>
            </div>
          </c:if>
        </section>

        <section class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest flex items-center gap-3"><span class="sec-num text-xl">iii.</span>Payment &amp; policies</h2>
          <ul class="mt-5 space-y-3 text-sm text-ink/75">
            <li class="flex gap-3"><i data-lucide="concierge-bell" class="w-5 h-5 text-brass shrink-0"></i>Pay at the front desk by cash or card when you arrive. No card details are taken online.</li>
            <li class="flex gap-3"><i data-lucide="banknote" class="w-5 h-5 text-brass shrink-0"></i>Paying in cash? You get an extra 5% off, combined with your VIP discount.</li>
            <li class="flex gap-3"><i data-lucide="calendar-x" class="w-5 h-5 text-brass shrink-0"></i>Free cancellation any time before check-in, from <a href="${ctx}/bookings" class="font-semibold text-forest">My bookings</a>.</li>
          </ul>
          <label class="mt-6 flex items-start gap-3 text-sm cursor-pointer">
            <input type="checkbox" required class="mt-1 w-4 h-4 accent-[rgb(31,59,47)]">
            <span>I understand the cancellation and no-show policy.</span>
          </label>
        </section>
      </form>

      <!-- Summary Sidebar -->
      <aside class="lg:sticky lg:top-28">
        <div class="card overflow-hidden">
          <c:if test="${not empty room.imageUrl}">
            <div class="h-48"><img class="img-cover" src="<c:out value='${room.imageUrl}'/>" alt="<c:out value='${room.roomName}'/>"></div>
          </c:if>
          <div class="p-6">
            <p class="text-xs tracking-[.2em] uppercase text-brass font-bold"><c:out value="${room.typeDisplayName}"/> · Room <c:out value="${room.roomNumber}"/></p>
            <h2 class="font-display text-3xl font-semibold text-forest mt-1"><c:out value="${room.roomName}"/></h2>

            <dl class="mt-5 grid grid-cols-2 gap-4 text-sm">
              <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold" data-out="checkin">—</dd></div>
              <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold" data-out="checkout">—</dd></div>
            </dl>

            <dl class="mt-6 space-y-3 text-sm border-t border-line pt-5">
              <div class="flex justify-between"><dt class="text-ink/70" data-out="rate-x-nights">—</dt><dd data-out="subtotal">—</dd></div>
              <c:if test="${not empty me and me.discount > 0}">
                <div class="flex justify-between text-brass"><dt>VIP discount (${Math.round(me.discount * 100)}%)</dt><dd data-out="discount">—</dd></div>
              </c:if>
              <div class="flex justify-between border-t border-line pt-3 text-base font-semibold text-forest"><dt>Estimated total</dt><dd data-out="total">—</dd></div>
            </dl>
            <p class="mt-2 text-xs text-muted">Final amount is settled at the desk. Cash payments save a further 5%.</p>

            <button type="submit" form="book-form" class="btn btn-primary btn-lg btn-block mt-6"><i data-lucide="calendar-check"></i>Confirm booking</button>
            <a href="${ctx}/rooms" class="btn btn-ghost btn-block mt-2">Back to rooms</a>
          </div>
        </div>
      </aside>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
