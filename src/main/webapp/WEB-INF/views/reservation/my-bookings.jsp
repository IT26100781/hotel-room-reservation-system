<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  My bookings (GET /bookings). Shown by MyBookingsServlet. GuestBookingFilter makes sure a guest is signed in.
  Request attributes:
    upcoming, completed, cancelled - List<BookingDetails> (cancelled also holds no-shows)
    paidAmounts                    - Map bookingId -> amount actually paid
    minDate                        - today, for the change-dates picker
  URL parameters: ?done=cancelled|dates, ?error=message
  Posts: /bookings/cancel (bookingId), /bookings/dates (bookingId, checkIn, checkOut)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="My bookings"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <main class="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 pt-12 pb-16" data-tab-scope>
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
      <div>
        <span class="eyebrow">Your account</span>
        <h1 class="font-display text-5xl font-semibold text-forest mt-4">My bookings</h1>
      </div>
      <a href="${ctx}/reservation/search" class="btn btn-primary"><i data-lucide="plus"></i>New booking</a>
    </div>

    <c:choose>
      <c:when test="${param.done == 'cancelled'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Booking cancelled. The room has been released.</span></div></c:when>
      <c:when test="${param.done == 'dates'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Your dates have been changed.</span></div></c:when>
    </c:choose>
    <c:if test="${not empty param.error}">
      <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${param.error}"/></span></div>
    </c:if>

    <div class="tabs mt-8">
      <button type="button" class="tab active" data-tab="upcoming">Upcoming <span class="count">${fn:length(upcoming)}</span></button>
      <button type="button" class="tab" data-tab="completed">Completed <span class="count">${fn:length(completed)}</span></button>
      <button type="button" class="tab" data-tab="cancelled">Cancelled <span class="count">${fn:length(cancelled)}</span></button>
    </div>

    <div class="mt-8 space-y-6" data-tab-panel="upcoming">
      <c:set var="section" value="upcoming"/>
      <c:forEach var="b" items="${upcoming}">
        <%@ include file="/WEB-INF/views/reservation/booking-card.jspf" %>
      </c:forEach>
      <c:if test="${empty upcoming}">
        <div class="card p-12 text-center text-muted">
          <i data-lucide="calendar-plus" class="w-8 h-8 mx-auto mb-3 text-brass"></i>
          <p>No upcoming stays yet.</p>
          <a href="${ctx}/reservation/search" class="btn btn-outline btn-sm mt-5">Find a room</a>
        </div>
      </c:if>
    </div>

    <div class="mt-8 space-y-6 hidden" data-tab-panel="completed">
      <c:set var="section" value="completed"/>
      <c:forEach var="b" items="${completed}">
        <%@ include file="/WEB-INF/views/reservation/booking-card.jspf" %>
      </c:forEach>
      <c:if test="${empty completed}"><div class="card p-12 text-center text-muted">Completed stays will appear here after you check out.</div></c:if>
    </div>

    <div class="mt-8 space-y-6 hidden" data-tab-panel="cancelled">
      <c:set var="section" value="cancelled"/>
      <c:forEach var="b" items="${cancelled}">
        <%@ include file="/WEB-INF/views/reservation/booking-card.jspf" %>
      </c:forEach>
      <c:if test="${empty cancelled}"><div class="card p-12 text-center text-muted">No cancelled bookings.</div></c:if>
    </div>
  </main>

  <div class="modal" id="cancel-modal" role="dialog" aria-modal="true" aria-labelledby="cancel-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="calendar-x"></i></span>
      <h2 id="cancel-title" class="font-display text-3xl font-semibold text-forest mt-4">Cancel this booking?</h2>
      <p class="mt-2 text-sm text-ink/70"><span class="font-semibold" data-fill="booking">BK</span>, <span data-fill="room"></span>. The room will be released for other guests. This can't be undone.</p>
      <form method="post" action="${ctx}/bookings/cancel" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="bookingId" data-fill="booking">
        <button type="button" class="btn btn-ghost" data-modal-close>Keep booking</button>
        <button type="submit" class="btn btn-danger">Yes, cancel</button>
      </form>
    </div>
  </div>

  <div class="modal" id="dates-modal" role="dialog" aria-modal="true" aria-labelledby="dates-title">
    <div class="modal-box">
      <span class="stat-icon"><i data-lucide="calendar-range"></i></span>
      <h2 id="dates-title" class="font-display text-3xl font-semibold text-forest mt-4">Change dates for <span data-fill="booking">booking</span></h2>
      <p class="mt-2 text-sm text-ink/70">We'll check the room is still free for your new dates.</p>
      <form method="post" action="${ctx}/bookings/dates" class="mt-6">
        <input type="hidden" name="bookingId" data-fill="booking">
        <div class="grid sm:grid-cols-2 gap-4">
          <div><label class="label" for="newCheckIn">Check-in</label><input id="newCheckIn" type="date" name="checkIn" class="input" required min="${minDate}" data-fill="checkin"></div>
          <div><label class="label" for="newCheckOut">Check-out</label><input id="newCheckOut" type="date" name="checkOut" class="input" required min="${minDate}" data-fill="checkout"></div>
        </div>
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn btn-ghost" data-modal-close>Keep dates</button>
          <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save new dates</button>
        </div>
      </form>
    </div>
  </div>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
