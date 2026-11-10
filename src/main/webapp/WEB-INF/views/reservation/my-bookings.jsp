<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="My Bookings"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="bookings"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <main class="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 pt-12 pb-20" data-tab-scope>
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
      <div>
        <span class="eyebrow">Your account</span>
        <h1 class="font-display text-5xl font-semibold text-forest mt-4">My bookings</h1>
      </div>
      <a href="${ctx}/rooms" class="btn btn-primary"><i data-lucide="plus"></i>New booking</a>
    </div>

    <c:if test="${not empty param.done}">
      <div class="alert alert-success mt-6"><i data-lucide="check-circle"></i><span>Booking successfully cancelled.</span></div>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
      <div class="alert alert-error mt-6"><i data-lucide="alert-circle"></i><span><c:out value="${sessionScope.flashError}"/></span></div>
      <c:remove var="flashError" scope="session"/>
    </c:if>

    <div class="tabs mt-8">
      <button type="button" class="tab active" data-tab="upcoming">Upcoming <span class="count">${fn:length(upcomingBookings)}</span></button>
      <button type="button" class="tab" data-tab="past">Completed <span class="count">${fn:length(completedBookings)}</span></button>
      <button type="button" class="tab" data-tab="cancelled">Cancelled <span class="count">${fn:length(cancelledBookings)}</span></button>
    </div>

    <!-- ============ UPCOMING ============ -->
    <div class="mt-8 space-y-6" data-tab-panel="upcoming">
      <c:choose>
        <c:when test="${empty upcomingBookings}">
          <div class="card p-12 text-center text-muted">
            <i data-lucide="calendar" class="w-12 h-12 mx-auto stroke-[1.5] text-forest/40"></i>
            <p class="mt-4 font-semibold text-forest">No upcoming bookings</p>
            <p class="text-sm mt-1">Ready for your mountain getaway?</p>
            <a href="${ctx}/rooms" class="btn btn-primary btn-sm mt-5"><i data-lucide="bed-double"></i>Browse rooms</a>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach items="${upcomingBookings}" var="b">
            <article class="card overflow-hidden p-6 sm:p-7">
              <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <p class="mono text-xs text-muted tracking-wider"><c:out value="${b.id}"/></p>
                  <h2 class="font-display text-3xl font-semibold text-forest"><c:out value="${b.roomName}"/> · Room <c:out value="${b.roomNumber}"/></h2>
                </div>
                <div class="flex flex-wrap gap-2">
                  <span class="badge badge-active">Active</span>
                  <span class="badge ${b.paid ? 'badge-paid' : 'badge-pending'}"><c:out value="${b.paymentStatus.value}"/></span>
                </div>
              </div>
              <dl class="mt-5 grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm">
                <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold"><c:out value="${b.checkIn}"/></dd></div>
                <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold"><c:out value="${b.checkOut}"/></dd></div>
                <div><dt class="label !mb-1">Nights</dt><dd class="font-semibold"><c:out value="${b.nights}"/></dd></div>
                <div><dt class="label !mb-1">Estimated</dt><dd class="font-semibold">LKR <c:out value="${b.formattedAmount}"/></dd></div>
              </dl>
              <div class="mt-6 pt-5 border-t border-line flex flex-wrap items-center justify-between gap-3">
                <p class="text-sm text-muted flex items-center gap-2"><i data-lucide="clock" class="w-4 h-4 text-brass"></i>Reserved stay</p>
                <form method="post" action="${ctx}/reservations/cancel" onsubmit="return confirm('Cancel this booking? The room will be released.');">
                  <input type="hidden" name="bookingId" value="${b.id}">
                  <button type="submit" class="btn btn-danger-outline btn-sm"><i data-lucide="x"></i>Cancel booking</button>
                </form>
              </div>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- ============ COMPLETED ============ -->
    <div class="mt-8 space-y-6 hidden" data-tab-panel="past">
      <c:choose>
        <c:when test="${empty completedBookings}">
          <div class="card p-12 text-center text-muted">
            <p>No completed stays yet.</p>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach items="${completedBookings}" var="b">
            <article class="card overflow-hidden p-6 sm:p-7">
              <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <p class="mono text-xs text-muted tracking-wider"><c:out value="${b.id}"/></p>
                  <h2 class="font-display text-3xl font-semibold text-forest"><c:out value="${b.roomName}"/> · Room <c:out value="${b.roomNumber}"/></h2>
                </div>
                <div class="flex flex-wrap gap-2">
                  <span class="badge badge-completed">Completed</span>
                  <span class="badge badge-paid">Paid</span>
                </div>
              </div>
              <dl class="mt-5 grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm">
                <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold"><c:out value="${b.checkIn}"/></dd></div>
                <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold"><c:out value="${b.checkOut}"/></dd></div>
                <div><dt class="label !mb-1">Nights</dt><dd class="font-semibold"><c:out value="${b.nights}"/></dd></div>
                <div><dt class="label !mb-1">Total Paid</dt><dd class="font-semibold">LKR <c:out value="${b.formattedAmount}"/></dd></div>
              </dl>
              <div class="mt-6 pt-5 border-t border-line flex flex-wrap items-center justify-between gap-3">
                <p class="text-sm text-muted">Hope you enjoyed your stay in the hills!</p>
                <a href="${ctx}/reviews" class="btn btn-brass btn-sm"><i data-lucide="star"></i>Write a review</a>
              </div>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- ============ CANCELLED ============ -->
    <div class="mt-8 space-y-6 hidden" data-tab-panel="cancelled">
      <c:choose>
        <c:when test="${empty cancelledBookings}">
          <div class="card p-12 text-center text-muted">
            <p>No cancelled bookings.</p>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach items="${cancelledBookings}" var="b">
            <article class="card overflow-hidden p-6 sm:p-7 opacity-80">
              <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <p class="mono text-xs text-muted tracking-wider"><c:out value="${b.id}"/></p>
                  <h2 class="font-display text-3xl font-semibold text-forest line-through text-muted"><c:out value="${b.roomName}"/> · Room <c:out value="${b.roomNumber}"/></h2>
                </div>
                <div class="flex flex-wrap gap-2">
                  <span class="badge badge-cancelled">Cancelled</span>
                  <span class="badge ${b.refunded ? 'badge-refunded' : 'badge-outline'}"><c:out value="${b.paymentStatus.value}"/></span>
                </div>
              </div>
              <dl class="mt-5 grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm">
                <div><dt class="label !mb-1">Check-in</dt><dd class="font-semibold line-through text-muted"><c:out value="${b.checkIn}"/></dd></div>
                <div><dt class="label !mb-1">Check-out</dt><dd class="font-semibold line-through text-muted"><c:out value="${b.checkOut}"/></dd></div>
                <div><dt class="label !mb-1">Nights</dt><dd class="font-semibold"><c:out value="${b.nights}"/></dd></div>
                <div><dt class="label !mb-1">Amount</dt><dd class="font-semibold">LKR <c:out value="${b.formattedAmount}"/></dd></div>
              </dl>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
