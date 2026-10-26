<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:choose><c:when test="${not empty room}">Edit room ${room.roomNumber}</c:when><c:otherwise>Add room</c:otherwise></c:choose> · Staff portal · The Orchid Estate</title>
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
    <main class="px-4 sm:px-8 py-8 max-w-6xl">
      <a href="${pageContext.request.contextPath}/admin/rooms" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5">
        <i data-lucide="arrow-left" class="w-4 h-4"></i>Back to rooms
      </a>

      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">
        <c:choose>
          <c:when test="${not empty room}">Edit Room ${room.roomNumber}</c:when>
          <c:otherwise>Add a room</c:otherwise>
        </c:choose>
      </h1>
      <p class="text-muted mt-2">
        <c:choose>
          <c:when test="${not empty room}">Update room specifications, pricing, and availability status.</c:when>
          <c:otherwise>The room ID is generated automatically when you save.</c:otherwise>
        </c:choose>
      </p>

      <c:if test="${not empty errorMessage}">
        <div class="mt-4 p-4 rounded-xl bg-clay/10 border border-clay/20 text-clay font-medium text-sm">
          ${errorMessage}
        </div>
      </c:if>

      <div class="mt-8 grid lg:grid-cols-[1fr_340px] gap-8 items-start">
        <form action="${pageContext.request.contextPath}/admin/rooms/save" method="post" class="card card-pad space-y-8">
          <input type="hidden" name="roomId" value="${room != null ? room.roomId : ''}">
          <input type="hidden" name="roomName" value="${room != null ? room.roomName : ''}">

          <fieldset>
            <legend class="label">Room type</legend>
            <div class="grid sm:grid-cols-3 gap-3">
              <label class="choice">
                <input type="radio" name="type" value="StandardRoom" data-bind="type" data-label="Standard Room" data-set="capacity=2;rate=18500"
                       data-img="https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=700&q=80"
                       <c:if test="${empty room || room.type == 'StandardRoom'}">checked</c:if>>
                <span class="choice-body block">
                  <i data-lucide="bed-single" class="w-6 h-6 text-brass"></i>
                  <span class="block font-semibold text-forest mt-2">Standard</span>
                  <span class="block text-xs text-muted mt-1">2 guests · garden view</span>
                </span>
              </label>
              <label class="choice">
                <input type="radio" name="type" value="DeluxeRoom" data-bind="type" data-label="Deluxe Room" data-set="capacity=3;rate=28000"
                       data-img="https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80"
                       <c:if test="${room.type == 'DeluxeRoom'}">checked</c:if>>
                <span class="choice-body block">
                  <i data-lucide="bed-double" class="w-6 h-6 text-brass"></i>
                  <span class="block font-semibold text-forest mt-2">Deluxe</span>
                  <span class="block text-xs text-muted mt-1">3 guests · balcony, tub</span>
                </span>
              </label>
              <label class="choice">
                <input type="radio" name="type" value="SuiteRoom" data-bind="type" data-label="Estate Suite" data-set="capacity=4;rate=46000"
                       data-img="https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=700&q=80"
                       <c:if test="${room.type == 'SuiteRoom'}">checked</c:if>>
                <span class="choice-body block">
                  <i data-lucide="sofa" class="w-6 h-6 text-brass"></i>
                  <span class="block font-semibold text-forest mt-2">Suite</span>
                  <span class="block text-xs text-muted mt-1">4 guests · living area</span>
                </span>
              </label>
            </div>
          </fieldset>

          <div class="grid sm:grid-cols-2 gap-5">
            <div>
              <label class="label" for="roomNo">Room number</label>
              <input id="roomNo" name="roomNo" class="input" placeholder="e.g. 204" required pattern="\d{3}"
                     value="${room != null ? room.roomNumber : ''}" data-bind="roomNo">
              <p class="hint">Three digits. First digit is the floor.</p>
            </div>
            <div>
              <label class="label" for="roomIdDisplay">Room ID</label>
              <input id="roomIdDisplay" class="input" value="${room != null ? room.roomId : 'Generated on save'}" readonly>
            </div>
            <div>
              <label class="label" for="rate">Rate per night (LKR)</label>
              <div class="input-icon">
                <i data-lucide="banknote"></i>
                <input id="rate" name="rate" type="number" min="1000" step="500" class="input"
                       value="${room != null ? room.baseRate : '28000'}" required data-bind="rate">
              </div>
            </div>
            <div>
              <label class="label" for="capacity">Capacity (guests)</label>
              <div class="input-icon">
                <i data-lucide="users"></i>
                <input id="capacity" name="capacity" type="number" min="1" max="6" class="input"
                       value="${room != null ? room.capacity : '3'}" required data-bind="capacity">
              </div>
            </div>
            <div>
              <label class="label" for="roomSize">Room Size (m²)</label>
              <input id="roomSize" name="roomSize" type="number" min="10" max="200" class="input"
                     value="${room != null ? room.roomSize : '44'}">
            </div>
            <div>
              <label class="label" for="status">Status</label>
              <select id="status" name="status" class="select">
                <option value="active" <c:if test="${empty room || room.status == 'active'}">selected</c:if>>Active: bookable</option>
                <option value="maintenance" <c:if test="${room.status == 'maintenance'}">selected</c:if>>Maintenance: temporarily hidden</option>
                <option value="retired" <c:if test="${room.status == 'retired'}">selected</c:if>>Retired: permanently out of service</option>
              </select>
            </div>
            <div class="sm:col-span-2">
              <label class="label" for="imageUrl">Image URL</label>
              <input id="imageUrl" name="imageUrl" class="input" placeholder="https://images.unsplash.com/..."
                     value="${room != null ? room.imageUrl : ''}">
            </div>
          </div>

          <div class="flex justify-end gap-2 pt-2 border-t border-line">
            <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-ghost">Cancel</a>
            <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save room</button>
          </div>
        </form>

        <!-- Live preview -->
        <aside class="lg:sticky lg:top-24">
          <p class="label">Guest preview</p>
          <div class="card overflow-hidden">
            <div class="arch-sm h-56 m-3 mb-0">
              <img class="img-cover" data-bound-img="type"
                   src="${room != null ? room.imageUrl : 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80'}"
                   alt="Room preview">
            </div>
            <div class="p-5">
              <p class="text-xs tracking-[.2em] uppercase text-brass font-bold">Room <span data-bound="roomNo" data-empty="···">${room != null ? room.roomNumber : '···'}</span></p>
              <h3 class="font-display text-3xl font-semibold text-forest mt-1" data-bound="type">${room != null ? room.roomName : 'Deluxe Room'}</h3>
              <p class="text-sm text-muted mt-2 flex items-center gap-1.5">
                <i data-lucide="users" class="w-4 h-4"></i>Up to <span data-bound="capacity">${room != null ? room.capacity : '3'}</span> guests
              </p>
              <p class="mt-4">
                <span class="font-display text-2xl font-semibold text-forest" data-bound="rate" data-format="lkr">LKR ${room != null ? room.formattedRate : '28,000'}</span>
                <span class="text-sm text-muted">/ night</span>
              </p>
            </div>
          </div>
        </aside>
      </div>
    </main>
  </div>

  <script src="${pageContext.request.contextPath}/js/layout.js"></script>
  <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
