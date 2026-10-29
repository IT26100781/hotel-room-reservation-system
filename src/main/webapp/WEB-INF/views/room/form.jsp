<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Add or edit a room (GET /admin/rooms/form, or ?id=R101 to edit).
  Posts to POST /admin/rooms/save.
  Request attributes:
    room  - the Room being edited (null when adding)
    error - error message if saving failed
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="isEdit" value="${not empty room and not empty room.roomId}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit room' : 'Add room'}"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>

  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>

    <main class="px-4 sm:px-8 py-8 max-w-6xl">
      <a href="${ctx}/admin/rooms" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5">
        <i data-lucide="arrow-left" class="w-4 h-4"></i>Back to rooms
      </a>

      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">
        <c:choose>
          <c:when test="${isEdit}">Edit Room <c:out value="${room.roomNumber}"/></c:when>
          <c:otherwise>Add a room</c:otherwise>
        </c:choose>
      </h1>
      <p class="text-muted mt-2">
        <c:choose>
          <c:when test="${isEdit}">Room ID <span class="mono"><c:out value="${room.roomId}"/></span> · Update pricing, capacity, and operational status.</c:when>
          <c:otherwise>The room ID is generated automatically from the room number when you save.</c:otherwise>
        </c:choose>
      </p>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <div class="mt-8 grid lg:grid-cols-[1fr_340px] gap-8 items-start">
        <form action="${ctx}/admin/rooms/save" method="post" class="card card-pad space-y-8">
          <c:if test="${isEdit}">
            <input type="hidden" name="roomId" value="<c:out value='${room.roomId}'/>">
          </c:if>

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
                     value="<c:out value='${room != null ? room.roomNumber : param.roomNo}'/>" data-bind="roomNo">
              <p class="hint">Three digits. First digit is the floor.</p>
            </div>
            <div>
              <label class="label" for="roomName">Room name</label>
              <input id="roomName" name="roomName" class="input" placeholder="e.g. Valley Deluxe 204" required
                     value="<c:out value='${room != null ? room.roomName : param.roomName}'/>">
            </div>
            <div>
              <label class="label" for="rate">Rate per night (LKR)</label>
              <div class="input-icon">
                <i data-lucide="banknote"></i>
                <input id="rate" name="rate" type="number" min="1000" step="500" class="input"
                       value="<c:out value='${room != null ? room.baseRate : (param.rate != null ? param.rate : 28000)}'/>" required data-bind="rate">
              </div>
            </div>
            <div>
              <label class="label" for="capacity">Capacity (guests)</label>
              <div class="input-icon">
                <i data-lucide="users"></i>
                <input id="capacity" name="capacity" type="number" min="1" max="10" class="input"
                       value="<c:out value='${room != null ? room.capacity : (param.capacity != null ? param.capacity : 3)}'/>" required data-bind="capacity">
              </div>
            </div>
            <div>
              <label class="label" for="roomSize">Room Size (m²)</label>
              <input id="roomSize" name="roomSize" type="number" min="10" max="200" class="input" required
                     value="<c:out value='${room != null ? room.roomSize : (param.roomSize != null ? param.roomSize : 44)}'/>">
            </div>
            <div>
              <label class="label" for="status">Status</label>
              <select id="status" name="status" class="select">
                <option value="active" <c:if test="${empty room || room.status.value == 'active'}">selected</c:if>>Active: bookable</option>
                <option value="maintenance" <c:if test="${room.status.value == 'maintenance'}">selected</c:if>>Maintenance: temporarily hidden</option>
                <option value="retired" <c:if test="${room.status.value == 'retired'}">selected</c:if>>Retired: permanently out of service</option>
              </select>
            </div>
            <div class="sm:col-span-2">
              <label class="label" for="imageUrl">Image URL</label>
              <input id="imageUrl" name="imageUrl" class="input" placeholder="https://images.unsplash.com/..."
                     value="<c:out value='${room != null ? room.imageUrl : param.imageUrl}'/>">
            </div>
          </div>

          <div class="flex justify-end gap-2 pt-2 border-t border-line">
            <a href="${ctx}/admin/rooms" class="btn btn-ghost">Cancel</a>
            <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save room</button>
          </div>
        </form>

        <!-- Live preview -->
        <aside class="lg:sticky lg:top-24">
          <p class="label">Guest preview</p>
          <div class="card overflow-hidden">
            <div class="arch-sm h-56 m-3 mb-0">
              <img class="img-cover" data-bound-img="type"
                   src="<c:out value='${room != null && not empty room.imageUrl ? room.imageUrl : \"https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80\"}'/>"
                   alt="Room preview">
            </div>
            <div class="p-5">
              <p class="text-xs tracking-[.2em] uppercase text-brass font-bold">Room <span data-bound="roomNo" data-empty="···"><c:out value="${room != null ? room.roomNumber : '···'}"/></span></p>
              <h3 class="font-display text-3xl font-semibold text-forest mt-1" data-bound="type"><c:out value="${room != null ? room.roomName : 'Deluxe Room'}"/></h3>
              <p class="text-sm text-muted mt-2 flex items-center gap-1.5">
                <i data-lucide="users" class="w-4 h-4"></i>Up to <span data-bound="capacity"><c:out value="${room != null ? room.capacity : '3'}"/></span> guests
              </p>
              <p class="mt-4">
                <span class="font-display text-2xl font-semibold text-forest" data-bound="rate" data-format="lkr">LKR <c:out value="${room != null ? room.formattedRate : '28,000'}"/></span>
                <span class="text-sm text-muted">/ night</span>
              </p>
            </div>
          </div>
        </aside>
      </div>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
