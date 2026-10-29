<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Guest home after sign-in (GET /account). Only for logged-in guests.
  Reads the logged-in guest from the session (${me}, set in init.jspf) and uses:
    me.name, me.guestType ("VIP" or "REGULAR"), me.discount (e.g. 0.10 for 10%, shown as a whole percent)
  Bookings and reviews belong to other components, so this page only links to them.
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="My account"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <c:set var="firstName" value="${fn:contains(me.name, ' ') ? fn:substringBefore(me.name, ' ') : me.name}"/>
  <c:set var="isVip" value="${me.guestType == 'VIP'}"/>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12 pb-20">
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-6">
      <div>
        <span class="eyebrow">Your account</span>
        <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-4">Welcome back, <em class="italic text-brass"><c:out value="${firstName}"/>.</em></h1>
      </div>
      <c:choose>
        <c:when test="${isVip}">
          <div class="flex items-center gap-3 rounded-2xl border border-brass/40 bg-brass/10 px-5 py-3">
            <span class="badge badge-vip no-dot"><i data-lucide="crown"></i>VIP</span>
            <span class="text-sm text-forest">${Math.round(me.discount * 100)}% off every stay, applied automatically</span>
          </div>
        </c:when>
        <c:otherwise>
          <div class="flex items-center gap-3 rounded-2xl border border-line px-5 py-3">
            <span class="badge badge-outline no-dot">Regular guest</span>
            <span class="text-sm text-muted">Returning guests can be upgraded to VIP by our front desk.</span>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- Quick actions -->
    <section class="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-5">
      <a href="${ctx}/rooms" class="card card-pad card-hover group">
        <span class="stat-icon"><i data-lucide="bed-double"></i></span>
        <h3 class="font-display text-2xl font-semibold text-forest mt-5">Book a room</h3>
        <p class="text-sm text-muted mt-1">Check dates and availability.</p>
        <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-4 transition group-hover:translate-x-1"></i>
      </a>
      <a href="${ctx}/bookings" class="card card-pad card-hover group">
        <span class="stat-icon"><i data-lucide="calendar-days"></i></span>
        <h3 class="font-display text-2xl font-semibold text-forest mt-5">My bookings</h3>
        <p class="text-sm text-muted mt-1">Upcoming and past stays.</p>
        <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-4 transition group-hover:translate-x-1"></i>
      </a>
      <a href="${ctx}/reviews" class="card card-pad card-hover group">
        <span class="stat-icon"><i data-lucide="star"></i></span>
        <h3 class="font-display text-2xl font-semibold text-forest mt-5">Guest stories</h3>
        <p class="text-sm text-muted mt-1">Read reviews or share your own.</p>
        <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-4 transition group-hover:translate-x-1"></i>
      </a>
      <a href="${ctx}/profile" class="card card-pad card-hover group">
        <span class="stat-icon"><i data-lucide="user"></i></span>
        <h3 class="font-display text-2xl font-semibold text-forest mt-5">Profile</h3>
        <p class="text-sm text-muted mt-1">Name, email and password.</p>
        <i data-lucide="arrow-right" class="w-5 h-5 text-brass mt-4 transition group-hover:translate-x-1"></i>
      </a>
    </section>

    <!-- Featured stay -->
    <section class="mt-10 card overflow-hidden grid lg:grid-cols-[1.1fr_1fr]">
      <div class="h-64 lg:h-auto relative">
        <img class="img-cover" src="https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1000&q=80" alt="Estate Suite">
      </div>
      <div class="p-8 sm:p-10">
        <p class="text-xs tracking-[.22em] uppercase text-brass font-bold">Plan your next stay</p>
        <h2 class="font-display text-4xl font-semibold text-forest mt-2">Wake up above the clouds</h2>
        <p class="text-muted mt-3">From garden-view standards to our top-floor suites with afternoon tea on the terrace.</p>
        <div class="mt-8 flex flex-wrap gap-3">
          <a href="${ctx}/rooms" class="btn btn-primary"><i data-lucide="search"></i>Browse rooms</a>
          <a href="${ctx}/bookings" class="btn btn-outline">View my bookings</a>
        </div>
      </div>
    </section>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
