<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff portal home (GET /admin). Shown by AdminDashboardServlet.
  Request attributes (all optional; a tile shows "-" when its value is missing):
    staffCount, activeStaffCount            - from StaffService
    roomCount, activeRoomCount               - from RoomService   (when the room module is merged)
    arrivalsToday, pendingPayments           - from the reservation / payment modules (later)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Dashboard"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="dashboard"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div>
        <span class="eyebrow">Overview</span>
        <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Welcome, <em class="italic text-brass"><c:out value="${fn:substringBefore(me.name.concat(' '), ' ')}"/>.</em></h1>
        <p class="text-muted mt-2">Here's how the estate looks today.</p>
      </div>

      <c:if test="${param.done == 'password'}">
        <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Your password has been changed.</span></div>
      </c:if>

      <div class="mt-8 grid sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <a href="${ctx}/admin/bookings" class="stat stat-link">
          <div class="flex items-center justify-between gap-3"><p class="stat-label truncate">Arrivals today</p><span class="stat-icon shrink-0"><i data-lucide="door-open"></i></span></div>
          <p class="stat-value mt-2">${empty arrivalsToday ? '–' : arrivalsToday}</p>
          <p class="text-xs text-muted">guests checking in</p>
        </a>
        <a href="${ctx}/admin/payments" class="stat stat-link">
          <div class="flex items-center justify-between gap-3"><p class="stat-label truncate">Unpaid</p><span class="stat-icon shrink-0"><i data-lucide="wallet"></i></span></div>
          <p class="stat-value mt-2">${empty pendingPayments ? '–' : pendingPayments}</p>
          <p class="text-xs text-muted">payments pending</p>
        </a>
        <a href="${ctx}${me.canManageRooms() ? '/admin/rooms' : '/rooms'}" class="stat stat-link">
          <div class="flex items-center justify-between gap-3"><p class="stat-label truncate">Active rooms</p><span class="stat-icon shrink-0"><i data-lucide="bed-double"></i></span></div>
          <p class="stat-value mt-2">${empty activeRoomCount ? '–' : activeRoomCount}</p>
          <p class="text-xs text-muted"><c:choose><c:when test="${not empty roomCount}">of ${roomCount} rooms</c:when><c:otherwise>open for booking</c:otherwise></c:choose></p>
        </a>
        <c:choose>
          <c:when test="${me.canViewStaffUsers()}">
            <a href="${ctx}/admin/staff" class="stat stat-link">
              <div class="flex items-center justify-between gap-3"><p class="stat-label truncate">Active staff</p><span class="stat-icon shrink-0"><i data-lucide="users"></i></span></div>
              <p class="stat-value mt-2">${empty activeStaffCount ? '–' : activeStaffCount}</p>
              <p class="text-xs text-muted"><c:choose><c:when test="${not empty staffCount}">of ${staffCount} accounts</c:when><c:otherwise>accounts</c:otherwise></c:choose></p>
            </a>
          </c:when>
          <c:otherwise>
            <div class="stat">
              <div class="flex items-center justify-between gap-3"><p class="stat-label truncate">Signed in as</p><span class="stat-icon shrink-0"><i data-lucide="badge-check"></i></span></div>
              <p class="font-display text-2xl font-semibold text-forest mt-3 truncate"><c:out value="${me.name}"/></p>
              <p class="text-xs text-muted">Front desk staff</p>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <section class="mt-10">
        <h2 class="font-display text-2xl font-semibold text-forest">Quick actions</h2>
        <div class="mt-4 grid sm:grid-cols-2 xl:grid-cols-4 gap-4">
          <c:if test="${me.canManageAnyBooking()}">
            <a href="${ctx}/admin/bookings" class="action-card">
              <span class="stat-icon"><i data-lucide="calendar-check"></i></span>
              <span class="min-w-0"><span class="action-title">Bookings &amp; check-in</span><span class="action-text">Today's arrivals, date changes and check-in</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
            <a href="${ctx}/admin/bookings/new" class="action-card">
              <span class="stat-icon"><i data-lucide="calendar-plus"></i></span>
              <span class="min-w-0"><span class="action-title">New desk booking</span><span class="action-text">Book a room for a guest at the front desk</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canRecordPayment()}">
            <a href="${ctx}/admin/payments" class="action-card">
              <span class="stat-icon"><i data-lucide="wallet"></i></span>
              <span class="min-w-0"><span class="action-title">Payments</span><span class="action-text">Record, mark paid and refund</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canViewGuestUsers()}">
            <a href="${ctx}/admin/guests" class="action-card">
              <span class="stat-icon"><i data-lucide="users"></i></span>
              <span class="min-w-0"><span class="action-title">Guests</span><span class="action-text">Find guests and their accounts</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canManageRooms()}">
            <a href="${ctx}/admin/rooms" class="action-card">
              <span class="stat-icon"><i data-lucide="bed-double"></i></span>
              <span class="min-w-0"><span class="action-title">Rooms</span><span class="action-text">Add rooms, set rates and status</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canModerateReviews()}">
            <a href="${ctx}/admin/reviews" class="action-card">
              <span class="stat-icon"><i data-lucide="message-square-quote"></i></span>
              <span class="min-w-0"><span class="action-title">Reviews</span><span class="action-text">Read and remove guest reviews</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canViewStaffUsers()}">
            <a href="${ctx}/admin/staff" class="action-card">
              <span class="stat-icon"><i data-lucide="user-cog"></i></span>
              <span class="min-w-0"><span class="action-title">Staff accounts</span><span class="action-text">Add staff, set roles and access</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
          <c:if test="${me.canEditSiteContent()}">
            <a href="${ctx}/admin/home" class="action-card">
              <span class="stat-icon"><i data-lucide="house"></i></span>
              <span class="min-w-0"><span class="action-title">Home page</span><span class="action-text">Edit the hero text and announcement</span></span>
              <i data-lucide="arrow-right" class="action-arrow"></i>
            </a>
          </c:if>
        </div>
      </section>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
