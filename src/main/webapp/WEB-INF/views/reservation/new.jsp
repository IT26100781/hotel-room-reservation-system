<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Front-desk booking for a guest (GET/POST /admin/bookings/new). Shown by BookingNewServlet.
  Request attributes:
    guestList - List<Guest> to choose from (inactive guests are shown but cannot be picked)
    rooms     - List<Room> that are active
    minDate   - today, for the date pickers
    error     - message if the booking could not be made
  URL parameter ?roomId= preselects a room (sent from "Book now" on the room pages).
  Form fields: guestId, roomId, checkIn, checkOut, guests
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="New desk booking"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-4xl">
      <a href="${ctx}/admin/bookings" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to bookings</a>
      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">New desk booking</h1>
      <p class="text-muted mt-2">Book a room for a guest at the front desk. Only staff can change or cancel desk bookings.</p>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <form method="post" action="${ctx}/admin/bookings/new" class="card card-pad mt-8 space-y-8" data-booking-calc>
        <div class="grid sm:grid-cols-2 gap-5">
          <div>
            <label class="label" for="guestId">Guest</label>
            <select id="guestId" name="guestId" class="select" required>
              <option value="">Choose a guest…</option>
              <c:forEach var="g" items="${guestList}">
                <option value="<c:out value='${g.id}'/>" ${param.guestId == g.id ? 'selected' : ''} ${g.active ? '' : 'disabled'}>
                  <c:out value="${g.name}"/> · <c:out value="${g.email}"/><c:if test="${g.discount > 0}"> · VIP</c:if><c:if test="${not g.active}"> (inactive)</c:if>
                </option>
              </c:forEach>
            </select>
            <p class="hint">New guest? They can register on the website first, or ask an admin.</p>
          </div>
          <div>
            <label class="label" for="roomId">Room</label>
            <select id="roomId" name="roomId" class="select" required>
              <option value="">Choose a room…</option>
              <c:forEach var="room" items="${rooms}">
                <option value="<c:out value='${room.roomId}'/>" ${param.roomId == room.roomId ? 'selected' : ''}>
                  <c:out value="${room.roomNumber}"/> · <c:out value="${room.roomName}"/> · up to <c:out value="${room.capacity}"/> · LKR <c:out value="${room.formattedRate}"/>
                </option>
              </c:forEach>
            </select>
          </div>
          <div>
            <label class="label" for="checkIn">Check-in</label>
            <input id="checkIn" type="date" name="checkIn" class="input" required min="${minDate}" value="<c:out value='${empty param.checkIn ? minDate : param.checkIn}'/>">
          </div>
          <div>
            <label class="label" for="checkOut">Check-out</label>
            <input id="checkOut" type="date" name="checkOut" class="input" required value="<c:out value='${param.checkOut}'/>">
            <p class="hint"><span data-out="nights">—</span></p>
          </div>
          <div>
            <label class="label" for="guests">Guests</label>
            <c:set var="partySize" value="${empty param.guests ? '2' : param.guests}"/>
            <select id="guests" name="guests" class="select">
              <c:forEach var="n" begin="1" end="6">
                <option value="${n}" ${partySize == n.toString() ? 'selected' : ''}>${n}</option>
              </c:forEach>
            </select>
          </div>
        </div>

        <div class="alert alert-info"><i data-lucide="info"></i><span>Availability, capacity and the price are checked when you save. The guest pays at the desk; record it from Payments.</span></div>

        <div class="flex justify-end gap-2 pt-2 border-t border-line">
          <a href="${ctx}/admin/bookings" class="btn btn-ghost">Cancel</a>
          <button type="submit" class="btn btn-primary"><i data-lucide="calendar-check"></i>Create booking</button>
        </div>
      </form>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
