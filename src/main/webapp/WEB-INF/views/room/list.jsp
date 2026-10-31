<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Public rooms listing (GET /rooms).
  Request attributes:
    rooms - List<Room> active rooms available for guests
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Rooms & Suites"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12 pb-16">
    <!-- Page heading -->
    <div class="grid lg:grid-cols-2 gap-6 items-end">
      <div>
        <span class="eyebrow">Accommodation</span>
        <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-4">Rooms &amp; Suites</h1>
      </div>
      <p class="text-ink/65 lg:text-right lg:max-w-md lg:ml-auto">
        Discover peaceful sanctuaries above the mist. Each category is designed with natural woods, local brass, and expansive hill country views.
      </p>
    </div>

    <!-- Search / filter form -->
    <form action="${ctx}/rooms" method="get" class="card mt-10 p-3 sm:p-4 grid gap-3 md:grid-cols-[1fr_1fr_.8fr_auto] items-end">
      <label class="block px-3 py-2">
        <span class="label">Room Category</span>
        <select name="type" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
          <option value="">All Categories</option>
          <option value="StandardRoom" <c:if test="${param.type == 'StandardRoom'}">selected</c:if>>Standard</option>
          <option value="DeluxeRoom" <c:if test="${param.type == 'DeluxeRoom'}">selected</c:if>>Deluxe</option>
          <option value="SuiteRoom" <c:if test="${param.type == 'SuiteRoom'}">selected</c:if>>Estate Suite</option>
        </select>
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Guests</span>
        <select name="capacity" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
          <option value="">Any occupancy</option>
          <option value="1" <c:if test="${param.capacity == '1'}">selected</c:if>>1 guest</option>
          <option value="2" <c:if test="${param.capacity == '2'}">selected</c:if>>2 guests</option>
          <option value="3" <c:if test="${param.capacity == '3'}">selected</c:if>>3 guests</option>
          <option value="4" <c:if test="${param.capacity == '4'}">selected</c:if>>4+ guests</option>
        </select>
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Max Rate / night</span>
        <input type="number" name="maxPrice" step="1000" placeholder="e.g. 35000"
               value="<c:out value='${param.maxPrice}'/>"
               class="w-full bg-transparent font-semibold text-forest focus:outline-none">
      </label>
      <button type="submit" class="btn btn-primary btn-lg min-h-[3.5rem]"><i data-lucide="search"></i>Filter rooms</button>
    </form>

    <!-- Toolbar -->
    <div class="mt-10 flex flex-col md:flex-row md:items-center gap-4 justify-between">
      <div class="flex flex-wrap gap-2" data-chip-filter="#room-grid" data-chip-attr="type">
        <button type="button" class="chip active" data-chip="all">All rooms</button>
        <button type="button" class="chip" data-chip="standard"><i data-lucide="bed-single" class="w-4 h-4"></i>Standard</button>
        <button type="button" class="chip" data-chip="deluxe"><i data-lucide="bed-double" class="w-4 h-4"></i>Deluxe</button>
        <button type="button" class="chip" data-chip="suite"><i data-lucide="sofa" class="w-4 h-4"></i>Suite</button>
      </div>
      <p class="text-sm text-muted">
        <span class="font-semibold text-forest"><c:out value="${rooms != null ? rooms.size() : 0}"/></span> rooms available
      </p>
    </div>

    <!-- Room list -->
    <div id="room-grid" class="mt-6 grid gap-6">
      <c:choose>
        <c:when test="${not empty rooms}">
          <c:forEach var="room" items="${rooms}">
            <article data-item data-type="${fn:toLowerCase(room.typeDisplayName)}" class="card overflow-hidden grid md:grid-cols-[320px_1fr] card-hover">
              <a href="${ctx}/rooms/detail?id=<c:out value='${room.roomId}'/>" class="block h-60 md:h-full img-zoom">
                <img class="img-cover" src="<c:out value='${room.imageUrl}'/>" alt="<c:out value='${room.roomName}'/>">
              </a>
              <div class="p-6 sm:p-7 flex flex-col">
                <div class="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <p class="text-xs tracking-[.2em] uppercase text-brass font-bold"><c:out value="${room.typeDisplayName}"/> · Room <c:out value="${room.roomNumber}"/></p>
                    <h2 class="font-display text-3xl font-semibold text-forest mt-1"><c:out value="${room.roomName}"/></h2>
                  </div>
                  <span class="badge badge-active">Available</span>
                </div>
                <p class="mt-3 text-sm text-ink/65 max-w-xl"><c:out value="${room.description}"/></p>
                <ul class="mt-4 flex flex-wrap gap-x-5 gap-y-2 text-sm text-ink/70">
                  <li class="flex items-center gap-1.5"><i data-lucide="users" class="w-4 h-4 text-brass"></i>Up to <c:out value="${room.capacity}"/> guests</li>
                  <li class="flex items-center gap-1.5"><i data-lucide="ruler" class="w-4 h-4 text-brass"></i><c:out value="${room.roomSize}"/> m²</li>
                  <c:forEach var="amenity" items="${room.amenities}" begin="0" end="1">
                    <li class="flex items-center gap-1.5"><i data-lucide="check" class="w-4 h-4 text-brass"></i><c:out value="${amenity}"/></li>
                  </c:forEach>
                </ul>
                <div class="mt-auto pt-6 flex flex-wrap items-end justify-between gap-4">
                  <p><span class="font-display text-3xl font-semibold text-forest">LKR <c:out value="${room.formattedRate}"/></span> <span class="text-sm text-muted">/ night</span></p>
                  <div class="flex gap-2">
                    <a href="${ctx}/rooms/detail?id=<c:out value='${room.roomId}'/>" class="btn btn-outline btn-sm">View details</a>
                    <a href="${ctx}/reservation/book?roomId=<c:out value='${room.roomId}'/>" class="btn btn-primary btn-sm">Book now</a>
                  </div>
                </div>
              </div>
            </article>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <div class="card p-12 text-center text-muted">
            <i data-lucide="bed" class="w-8 h-8 mx-auto text-muted mb-2"></i>
            <p>No rooms match these filters. Try expanding your search criteria.</p>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
