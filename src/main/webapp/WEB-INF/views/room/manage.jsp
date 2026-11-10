<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff room management list (GET /admin/rooms).
  Request attributes:
    rooms            - List<Room> to show
    totalCount       - total number of rooms
    activeCount      - count of active rooms
    maintenanceCount - count of rooms in maintenance
    retiredCount     - count of retired rooms
    error            - error message if an operation failed
  URL parameter ?done=added|updated|retired displays a success banner.
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Rooms"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>

  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>

    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Property</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Rooms</h1>
          <p class="text-muted mt-2">Add rooms, change rates, or take a room out of service.</p>
        </div>
        <c:if test="${me.canManageRooms()}">
          <a href="${ctx}/admin/rooms/form" class="btn btn-primary"><i data-lucide="plus"></i>Add room</a>
        </c:if>
      </div>

      <c:choose>
        <c:when test="${param.done == 'added'}">
          <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Room added successfully.</span></div>
        </c:when>
        <c:when test="${param.done == 'updated'}">
          <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Room details updated.</span></div>
        </c:when>
        <c:when test="${param.done == 'retired'}">
          <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Room has been retired.</span></div>
        </c:when>
      </c:choose>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>
      <c:if test="${not empty param.error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${param.error}"/></span></div>
      </c:if>

      <!-- Summary metrics calculated in a single pass -->
      <div class="mt-8 grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="stat"><p class="stat-label">Total rooms</p><p class="stat-value mt-2">${totalCount}</p></div>
        <div class="stat"><p class="stat-label">Active</p><p class="stat-value mt-2">${activeCount}</p></div>
        <div class="stat"><p class="stat-label">Maintenance</p><p class="stat-value mt-2 !text-amber">${maintenanceCount}</p></div>
        <div class="stat"><p class="stat-label">Retired</p><p class="stat-value mt-2 !text-muted">${retiredCount}</p></div>
      </div>

      <!-- Table Card -->
      <div class="card mt-8">
        <div class="p-4 sm:p-5 flex flex-col lg:flex-row gap-3 lg:items-center justify-between border-b border-line">
          <div class="flex flex-wrap gap-2" data-chip-filter="#rooms-table" data-chip-attr="status">
            <button type="button" class="chip active" data-chip="all">All</button>
            <button type="button" class="chip" data-chip="active">Active</button>
            <button type="button" class="chip" data-chip="maintenance">Maintenance</button>
            <button type="button" class="chip" data-chip="retired">Retired</button>
          </div>
          <div class="flex gap-3">
            <div class="input-icon w-full lg:w-64">
              <i data-lucide="search"></i>
              <input class="input !py-2.5" placeholder="Search rooms…" data-filter-target="#rooms-table">
            </div>
            <select class="select !py-2.5 w-40" data-filter-target="#rooms-table" data-filter-attr="type">
              <option value="">All types</option>
              <option value="standard">Standard</option>
              <option value="deluxe">Deluxe</option>
              <option value="suite">Suite</option>
            </select>
          </div>
        </div>

        <div class="table-wrap">
          <table class="table" id="rooms-table">
            <thead>
              <tr>
                <th>Room</th>
                <th>Type</th>
                <th>Rate / night</th>
                <th>Capacity</th>
                <th>Status</th>
                <th class="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="r" items="${rooms}">
                <tr data-item data-status="${r.status.value}" data-type="${fn:toLowerCase(r.typeDisplayName)}" class="${r.retired ? 'opacity-60' : ''}">
                  <td>
                    <div class="flex items-center gap-3">
                      <c:choose>
                        <c:when test="${r.retired}">
                          <div class="w-12 h-12 rounded-xl bg-sand grid place-items-center">
                            <i data-lucide="archive" class="w-5 h-5 text-muted"></i>
                          </div>
                        </c:when>
                        <c:otherwise>
                          <img class="w-12 h-12 rounded-xl object-cover ${r.maintenance ? 'grayscale' : ''}"
                               src="<c:out value='${r.imageUrl}'/>" alt="<c:out value='${r.roomName}'/>">
                        </c:otherwise>
                      </c:choose>
                      <div>
                        <p class="font-semibold"><c:out value="${r.roomName}"/></p>
                        <p class="mono"><c:out value="${r.roomId}"/></p>
                      </div>
                    </div>
                  </td>
                  <td><c:out value="${r.typeDisplayName}"/></td>
                  <td class="font-semibold">LKR <c:out value="${r.formattedRate}"/></td>
                  <td><c:out value="${r.capacity}"/> guests</td>
                  <td>
                    <c:choose>
                      <c:when test="${r.active}">
                        <span class="badge badge-active">Active</span>
                      </c:when>
                      <c:when test="${r.maintenance}">
                        <span class="badge badge-maintenance">Maintenance</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge badge-retired">Retired</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-right whitespace-nowrap">
                    <c:if test="${me.canManageRooms()}">
                      <a href="${ctx}/admin/rooms/form?id=<c:out value='${r.roomId}'/>" class="btn btn-ghost btn-sm">
                        <i data-lucide="pencil"></i>Edit
                      </a>
                      <c:if test="${!r.retired}">
                        <button class="btn btn-ghost btn-sm text-clay" type="button"
                                onclick="openRetireModal('<c:out value="${r.roomId}"/>', '<c:out value="${r.roomName}"/>')">
                          <i data-lucide="archive"></i>Retire
                        </button>
                      </c:if>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="hidden p-10 text-center text-muted" data-empty-for="#rooms-table">No rooms match these filters.</div>
      </div>
    </main>
  </div>

  <!-- Retire confirmation modal (soft delete) -->
  <div class="modal" id="retire-modal" role="dialog" aria-modal="true" aria-labelledby="retire-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="archive"></i></span>
      <h2 id="retire-title" class="font-display text-3xl font-semibold text-forest mt-4">Retire <span id="retire-room-name">this room</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">The room will no longer appear in search or accept bookings. Past bookings keep their history.</p>
      <form id="retire-form" method="post" action="${ctx}/admin/rooms/retire">
        <input type="hidden" name="roomId" id="retire-room-id" value="">
        <div class="mt-6 flex justify-end gap-2">
          <button type="button" class="btn btn-ghost" onclick="closeRetireModal()">Keep it</button>
          <button type="submit" class="btn btn-danger">Retire room</button>
        </div>
      </form>
    </div>
  </div>

  <script>
    function openRetireModal(roomId, roomName) {
      document.getElementById('retire-room-id').value = roomId;
      document.getElementById('retire-room-name').textContent = roomName;
      document.getElementById('retire-modal').classList.add('open');
    }
    function closeRetireModal() {
      document.getElementById('retire-modal').classList.remove('open');
    }
  </script>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
