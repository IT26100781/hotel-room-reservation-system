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

      <div class="mt-8 grid sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <div class="stat">
          <div class="flex items-center justify-between"><p class="stat-label">Arrivals today</p><span class="stat-icon"><i data-lucide="door-open"></i></span></div>
          <p class="stat-value mt-2">${empty arrivalsToday ? '–' : arrivalsToday}</p>
        </div>
        <div class="stat">
          <div class="flex items-center justify-between"><p class="stat-label">Awaiting payment</p><span class="stat-icon"><i data-lucide="wallet"></i></span></div>
          <p class="stat-value mt-2">${empty pendingPayments ? '–' : pendingPayments}</p>
        </div>
        <div class="stat">
          <div class="flex items-center justify-between"><p class="stat-label">Active rooms</p><span class="stat-icon"><i data-lucide="bed-double"></i></span></div>
          <p class="stat-value mt-2">${empty activeRoomCount ? '–' : activeRoomCount}</p>
          <c:if test="${not empty roomCount}"><p class="text-xs text-muted">of ${roomCount} rooms</p></c:if>
        </div>
        <div class="stat">
          <div class="flex items-center justify-between"><p class="stat-label">Active staff</p><span class="stat-icon"><i data-lucide="users"></i></span></div>
          <p class="stat-value mt-2">${empty activeStaffCount ? '–' : activeStaffCount}</p>
          <c:if test="${not empty staffCount}"><p class="text-xs text-muted">of ${staffCount} accounts</p></c:if>
        </div>
      </div>

      <section class="mt-8">
        <h2 class="font-display text-2xl font-semibold text-forest">Quick actions</h2>
        <div class="mt-4 grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <c:if test="${me.canManageAnyBooking()}">
            <a href="${ctx}/admin/bookings" class="card card-pad card-hover group">
              <span class="stat-icon"><i data-lucide="calendar-check"></i></span>
              <h3 class="font-display text-xl font-semibold text-forest mt-4">Bookings &amp; check-in</h3>
              <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-3 transition group-hover:translate-x-1"></i>
            </a>
          </c:if>
          <c:if test="${me.canRecordPayment()}">
            <a href="${ctx}/admin/payments" class="card card-pad card-hover group">
              <span class="stat-icon"><i data-lucide="wallet"></i></span>
              <h3 class="font-display text-xl font-semibold text-forest mt-4">Payments</h3>
              <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-3 transition group-hover:translate-x-1"></i>
            </a>
          </c:if>
          <c:if test="${me.canManageRooms()}">
            <a href="${ctx}/admin/rooms" class="card card-pad card-hover group">
              <span class="stat-icon"><i data-lucide="bed-double"></i></span>
              <h3 class="font-display text-xl font-semibold text-forest mt-4">Rooms</h3>
              <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-3 transition group-hover:translate-x-1"></i>
            </a>
          </c:if>
          <c:if test="${me.canViewStaffUsers()}">
            <a href="${ctx}/admin/staff" class="card card-pad card-hover group">
              <span class="stat-icon"><i data-lucide="user-cog"></i></span>
              <h3 class="font-display text-xl font-semibold text-forest mt-4">Staff accounts</h3>
              <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-3 transition group-hover:translate-x-1"></i>
            </a>
          </c:if>
        </div>
      </section>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
