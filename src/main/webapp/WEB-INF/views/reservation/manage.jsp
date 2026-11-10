<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff bookings and check-in (GET /admin/bookings). Shown by BookingManageServlet.
  Request attributes:
    bookings - List<BookingDetails> (every booking, newest check-in first)
    metrics  - BookingMetrics: arrivingToday, checkedInToday, inHouse, paymentPending, noShowsThisWeek
  URL parameters: ?done=created|cancelled|dates|checkedin, ?error=message
  Posts: /admin/bookings/checkin (bookingId, next=payment optional),
         /admin/bookings/cancel (bookingId), /admin/bookings/dates (bookingId, checkIn, checkOut)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Bookings"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Front desk</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Bookings</h1>
          <p class="text-muted mt-2">Check guests in, take payments and manage every reservation.</p>
        </div>
        <a href="${ctx}/admin/bookings/new" class="btn btn-primary"><i data-lucide="plus"></i>New desk booking</a>
      </div>

      <c:choose>
        <c:when test="${param.done == 'created'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Booking created.</span></div></c:when>
        <c:when test="${param.done == 'cancelled'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Booking cancelled. If it was paid, mark the refund in Payments.</span></div></c:when>
        <c:when test="${param.done == 'dates'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Dates changed.</span></div></c:when>
        <c:when test="${param.done == 'checkedin'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Guest checked in.</span></div></c:when>
      </c:choose>
      <c:if test="${not empty param.error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${param.error}"/></span></div>
      </c:if>

      <div class="mt-8 grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="stat flex items-start justify-between"><div><p class="stat-label">Arriving today</p><p class="stat-value mt-2">${metrics.arrivingToday}</p><p class="text-xs text-muted">${metrics.checkedInToday} already checked in</p></div><span class="stat-icon"><i data-lucide="door-open"></i></span></div>
        <div class="stat flex items-start justify-between"><div><p class="stat-label">In house</p><p class="stat-value mt-2">${metrics.inHouse}</p><p class="text-xs text-muted">guests staying tonight</p></div><span class="stat-icon"><i data-lucide="bed-double"></i></span></div>
        <div class="stat flex items-start justify-between"><div><p class="stat-label">Payment pending</p><p class="stat-value mt-2">${metrics.paymentPending}</p><p class="text-xs text-muted">bookings to settle</p></div><span class="stat-icon"><i data-lucide="wallet"></i></span></div>
        <div class="stat flex items-start justify-between"><div><p class="stat-label">No-shows</p><p class="stat-value mt-2">${metrics.noShowsThisWeek}</p><p class="text-xs text-muted">this week, room released</p></div><span class="stat-icon"><i data-lucide="calendar-x"></i></span></div>
      </div>

      <div class="card mt-8">
        <div class="p-4 sm:p-5 grid gap-3 md:grid-cols-[1fr_auto_auto] border-b border-line">
          <div class="input-icon"><i data-lucide="search"></i><input class="input !py-2.5" placeholder="Search by booking ID, guest or room…" data-filter-target="#bookings-table"></div>
          <select class="select !py-2.5 md:w-48" data-filter-target="#bookings-table" data-filter-attr="status">
            <option value="">All statuses</option>
            <option value="arriving">Arriving today</option>
            <option value="inhouse">Checked in</option>
            <option value="upcoming">Upcoming</option>
            <option value="completed">Completed</option>
            <option value="noshow">No-show</option>
            <option value="cancelled">Cancelled</option>
          </select>
          <select class="select !py-2.5 md:w-44" data-filter-target="#bookings-table" data-filter-attr="payment">
            <option value="">Any payment</option>
            <option value="pending">Pending</option>
            <option value="paid">Paid</option>
            <option value="refunded">Refunded</option>
          </select>
        </div>
        <div class="table-wrap">
          <table class="table" id="bookings-table">
            <thead>
              <tr><th>Booking</th><th>Guest</th><th>Room</th><th>Stay</th><th>Status</th><th>Payment</th><th class="text-right">Actions</th></tr>
            </thead>
            <tbody>
              <c:forEach var="b" items="${bookings}">
                <c:set var="r" value="${b.reservation}"/>
                <c:set var="faded" value="${r.state.value == 'completed' or r.state.value == 'cancelled' or r.state.value == 'noshow'}"/>
                <tr data-item data-status="${r.state.value}" data-payment="${r.paymentStatus.value}"
                    class="${r.state.value == 'arriving' ? 'bg-brass/[.06]' : ''} ${faded ? 'text-ink/60' : ''}">
                  <td><p class="mono"><c:out value="${r.id}"/></p><p class="text-xs text-muted"><c:out value="${r.sourceLabel}"/></p></td>
                  <td>
                    <div class="flex items-center gap-3">
                      <span class="avatar ${b.guest.discount > 0 ? 'avatar-brass' : ''}"><c:out value="${b.guestInitials}"/></span>
                      <div>
                        <p class="font-semibold flex items-center gap-1.5"><c:out value="${b.guest.name}"/><c:if test="${b.guest.discount > 0}"><i data-lucide="crown" class="w-3.5 h-3.5 text-brass"></i></c:if></p>
                        <p class="text-xs text-muted"><c:out value="${b.guest.id}"/><c:if test="${b.guest.discount > 0}"> · VIP</c:if></p>
                      </div>
                    </div>
                  </td>
                  <td><c:out value="${b.room.typeDisplayName}"/> · <c:out value="${b.room.roomNumber}"/></td>
                  <td><p class="font-medium ${r.state.value == 'cancelled' ? 'line-through' : ''}"><c:out value="${r.stayLabel}"/></p><p class="text-xs text-muted">${r.nights} ${r.nights == 1 ? 'night' : 'nights'} · ${r.guests} ${r.guests == 1 ? 'guest' : 'guests'}</p></td>
                  <td><span class="badge ${r.state.badge}"><c:out value="${r.state.label}"/></span></td>
                  <td>
                    <c:choose>
                      <c:when test="${r.state.value == 'noshow' and r.paymentStatus.value == 'pending'}"><span class="badge badge-outline">Not paid</span></c:when>
                      <c:otherwise><span class="badge badge-${r.paymentStatus.value}"><c:out value="${r.paymentStatus.label}"/></span></c:otherwise>
                    </c:choose>
                    <c:if test="${r.refundPending}"><p class="text-xs text-clay mt-1">Refund due</p></c:if>
                  </td>
                  <td class="text-right whitespace-nowrap">
                    <c:if test="${r.checkInDue and me.canCheckInGuests()}">
                      <button type="button" class="btn btn-primary btn-sm" data-modal-open="checkin-modal"
                              data-booking="<c:out value='${r.id}'/>" data-guest="<c:out value='${b.guest.name}'/>"
                              data-room="<c:out value='${b.room.roomName}'/> · Room <c:out value='${b.room.roomNumber}'/>"
                              data-payable="${r.payable}"><i data-lucide="key-round"></i>Check in</button>
                    </c:if>
                    <c:if test="${r.payable and me.canRecordPayment()}">
                      <a href="${ctx}/admin/payments/record?bookingId=<c:out value='${r.id}'/>" class="btn btn-ghost btn-sm" title="Record payment"><i data-lucide="wallet"></i></a>
                    </c:if>
                    <c:if test="${r.paymentStatus.value != 'pending' and me.canRecordPayment()}">
                      <a href="${ctx}/admin/payments/invoice?bookingId=<c:out value='${r.id}'/>" class="btn btn-ghost btn-sm"><i data-lucide="receipt"></i>Invoice</a>
                    </c:if>
                    <c:if test="${r.staffDatesEditable}">
                      <button type="button" class="btn btn-ghost btn-sm" title="Change dates" data-modal-open="dates-modal"
                              data-booking="<c:out value='${r.id}'/>" data-checkin="${r.checkIn}" data-checkout="${r.checkOut}"><i data-lucide="calendar-range"></i></button>
                    </c:if>
                    <c:if test="${r.staffCancellable}">
                      <button type="button" class="btn btn-ghost btn-sm text-clay" title="Cancel" data-modal-open="cancel-modal"
                              data-booking="<c:out value='${r.id}'/>"><i data-lucide="x"></i></button>
                    </c:if>
                    <c:if test="${r.state.value == 'noshow'}"><span class="text-xs text-muted">Room released</span></c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="${empty bookings ? '' : 'hidden'} p-10 text-center text-muted" data-empty-for="#bookings-table">No bookings match these filters.</div>
        <div class="px-5 py-4 border-t border-line text-sm text-muted">Showing <span data-count-for="#bookings-table" class="font-semibold text-forest">${fn:length(bookings)}</span> bookings</div>
      </div>
    </main>
  </div>

  <div class="modal" id="checkin-modal" role="dialog" aria-modal="true" aria-labelledby="checkin-title">
    <div class="modal-box">
      <span class="stat-icon"><i data-lucide="key-round"></i></span>
      <h2 id="checkin-title" class="font-display text-3xl font-semibold text-forest mt-4">Check in <span data-fill="guest">guest</span></h2>
      <p class="mt-2 text-sm text-ink/70"><span class="mono" data-fill="booking"></span>, <span data-fill="room"></span></p>
      <div class="alert alert-brass mt-5" data-when-payable><i data-lucide="wallet"></i><span>Payment is still pending. You can record it now or later from Payments.</span></div>
      <form method="post" action="${ctx}/admin/bookings/checkin" class="mt-6 flex flex-wrap justify-end gap-2">
        <input type="hidden" name="bookingId" data-fill="booking">
        <button type="button" class="btn btn-ghost" data-modal-close>Not yet</button>
        <c:if test="${me.canRecordPayment()}">
          <button type="submit" name="next" value="payment" class="btn btn-outline" data-when-payable>Check in &amp; take payment</button>
        </c:if>
        <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Check in</button>
      </form>
    </div>
  </div>

  <div class="modal" id="cancel-modal" role="dialog" aria-modal="true" aria-labelledby="cancel-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="calendar-x"></i></span>
      <h2 id="cancel-title" class="font-display text-3xl font-semibold text-forest mt-4">Cancel <span data-fill="booking">booking</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">The room becomes available again. If the booking was already paid, mark the payment as refunded in Payments.</p>
      <form method="post" action="${ctx}/admin/bookings/cancel" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="bookingId" data-fill="booking">
        <button type="button" class="btn btn-ghost" data-modal-close>Keep booking</button>
        <button type="submit" class="btn btn-danger">Cancel booking</button>
      </form>
    </div>
  </div>

  <div class="modal" id="dates-modal" role="dialog" aria-modal="true" aria-labelledby="dates-title">
    <div class="modal-box">
      <span class="stat-icon"><i data-lucide="calendar-range"></i></span>
      <h2 id="dates-title" class="font-display text-3xl font-semibold text-forest mt-4">Change dates for <span data-fill="booking">booking</span></h2>
      <p class="mt-2 text-sm text-ink/70">Extend a stay or move it. The room must still be free for the new dates. Paid bookings are locked.</p>
      <form method="post" action="${ctx}/admin/bookings/dates" class="mt-6">
        <input type="hidden" name="bookingId" data-fill="booking">
        <div class="grid sm:grid-cols-2 gap-4">
          <div><label class="label" for="newCheckIn">Check-in</label><input id="newCheckIn" type="date" name="checkIn" class="input" required data-fill="checkin"></div>
          <div><label class="label" for="newCheckOut">Check-out</label><input id="newCheckOut" type="date" name="checkOut" class="input" required data-fill="checkout"></div>
        </div>
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn btn-ghost" data-modal-close>Keep dates</button>
          <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save new dates</button>
        </div>
      </form>
    </div>
  </div>

  <script src="${ctx}/js/app.js"></script>
  <script>
    // Hide the payment prompt in the check-in box when the booking is already paid.
    document.querySelectorAll('[data-modal-open="checkin-modal"]').forEach(function (button) {
      button.addEventListener('click', function () {
        var payable = button.dataset.payable === 'true';
        document.querySelectorAll('#checkin-modal [data-when-payable]').forEach(function (el) { el.classList.toggle('hidden', !payable); });
      });
    });
  </script>
</body>
</html>
