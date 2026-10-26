<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Rooms · Staff portal · The Orchid Estate</title>
  <link rel="icon" href="${pageContext.request.contextPath}/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,500;0,600;0,700;1,500;1,600&family=Manrope:wght@400;500;600;700;800&display=swap">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="${pageContext.request.contextPath}/js/tailwind.config.js"></script>
  <script src="https://unpkg.com/lucide@0.469.0/dist/umd/lucide.min.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
</head>
<body data-root="${pageContext.request.contextPath}/prototype/" data-layout="staff" data-active="rooms" data-user="admin">
  <div data-slot="sidebar"></div>
  <div class="lg:pl-72 min-h-screen">
    <div data-slot="topbar"></div>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Property</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Rooms</h1>
          <p class="text-muted mt-2">Add rooms, change rates, or take a room out of service.</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/rooms/form" class="btn btn-primary"><i data-lucide="plus"></i>Add room</a>
      </div>

      <!-- Summary -->
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
                <tr data-item data-status="${r.status.toLowerCase()}" data-type="${r.typeDisplayName.toLowerCase()}" class="${r.retired ? 'opacity-60' : ''}">
                  <td>
                    <div class="flex items-center gap-3">
                      <c:choose>
                        <c:when test="${r.retired}">
                          <div class="w-12 h-12 rounded-xl bg-sand grid place-items-center">
                            <i data-lucide="archive" class="w-5 h-5 text-muted"></i>
                          </div>
                        </c:when>
                        <c:otherwise>
                          <img class="w-12 h-12 rounded-xl object-cover ${r.maintenance ? 'grayscale' : ''}" src="${r.imageUrl}" alt="${r.roomName}">
                        </c:otherwise>
                      </c:choose>
                      <div>
                        <p class="font-semibold">${r.roomName}</p>
                        <p class="mono">${r.roomId}</p>
                      </div>
                    </div>
                  </td>
                  <td>${r.typeDisplayName}</td>
                  <td class="font-semibold">LKR ${r.formattedRate}</td>
                  <td>${r.capacity} guests</td>
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
                    <a href="${pageContext.request.contextPath}/admin/rooms/form?id=${r.roomId}" class="btn btn-ghost btn-sm">
                      <i data-lucide="pencil"></i>Edit
                    </a>
                    <c:if test="${!r.retired}">
                      <button class="btn btn-ghost btn-sm text-clay" type="button" onclick="openRetireModal('${r.roomId}', '${r.roomName}')">
                        <i data-lucide="archive"></i>Retire
                      </button>
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

  <!-- Retire confirmation modal -->
  <div class="modal" id="retire-modal" role="dialog" aria-modal="true" aria-labelledby="retire-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="archive"></i></span>
      <h2 id="retire-title" class="font-display text-3xl font-semibold text-forest mt-4">Retire <span id="retire-room-name">this room</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">The room will no longer appear in search or accept bookings. Past bookings keep their history, and you can restore it later.</p>
      <form id="retire-form" method="post" action="${pageContext.request.contextPath}/admin/rooms/retire">
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
  <script src="${pageContext.request.contextPath}/js/layout.js"></script>
  <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
