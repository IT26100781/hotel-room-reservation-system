<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Room search by dates (GET /reservation/search). Shown by RoomSearchServlet.
  Request attributes:
    rooms   - List<Room> free for the chosen dates (only set once dates were sent)
    party   - number of guests that was asked for
    minDate - today, for the date pickers
    error   - message if the dates were not valid
  URL parameters: checkIn, checkOut, guests (or capacity from the home page), type (standard | deluxe | suite)
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Find a room"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="rooms"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <c:set var="partySize" value="${empty party ? '2' : party}"/>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12 pb-16">
    <span class="eyebrow">Plan your stay</span>
    <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-4">Find a room</h1>
    <p class="text-ink/65 mt-3 max-w-xl">Choose your dates and we'll show only the rooms that are free for the whole stay.</p>

    <form action="${ctx}/reservation/search" method="get" data-booking-calc
          class="card mt-8 p-3 sm:p-4 grid gap-3 md:grid-cols-[1fr_1fr_.8fr_1fr_auto] items-end">
      <label class="block px-3 py-2">
        <span class="label">Check-in</span>
        <input type="date" name="checkIn" required min="${minDate}" value="<c:out value='${param.checkIn}'/>" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Check-out</span>
        <input type="date" name="checkOut" required value="<c:out value='${param.checkOut}'/>" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Guests</span>
        <select name="guests" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
          <c:forEach var="n" begin="1" end="4">
            <option value="${n}" ${partySize == n.toString() ? 'selected' : ''}>${n} ${n == 1 ? 'guest' : 'guests'}</option>
          </c:forEach>
        </select>
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Room type</span>
        <select name="type" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
          <option value="">Any room</option>
          <option value="standard" ${param.type == 'standard' ? 'selected' : ''}>Standard</option>
          <option value="deluxe" ${param.type == 'deluxe' ? 'selected' : ''}>Deluxe</option>
          <option value="suite" ${param.type == 'suite' ? 'selected' : ''}>Suite</option>
        </select>
      </label>
      <button type="submit" class="btn btn-brass btn-lg h-full min-h-[3.5rem]"><i data-lucide="search"></i>Check availability</button>
    </form>
    <p class="mt-3 text-center text-xs text-muted"><span data-out="nights">—</span> · payment is taken at the front desk, never online</p>

    <c:if test="${not empty error}">
      <div class="alert alert-error mt-8"><i data-lucide="calendar-x"></i><span><c:out value="${error}"/></span></div>
    </c:if>

    <c:choose>
      <c:when test="${empty param.checkIn or empty param.checkOut or not empty error}">
        <div class="card p-12 mt-8 text-center text-muted">
          <i data-lucide="calendar-search" class="w-8 h-8 mx-auto mb-3 text-brass"></i>
          <p>Pick your check-in and check-out dates to see which rooms are free.</p>
          <a href="${ctx}/rooms" class="btn btn-outline btn-sm mt-5">Or browse all rooms</a>
        </div>
      </c:when>
      <c:when test="${empty rooms}">
        <div class="card p-12 mt-8 text-center text-muted">
          <i data-lucide="bed" class="w-8 h-8 mx-auto mb-3 text-brass"></i>
          <p>No rooms are free for those dates. Try different dates, fewer guests or any room type.</p>
        </div>
      </c:when>
      <c:otherwise>
        <p class="mt-10 text-sm text-muted"><span class="font-semibold text-forest">${fn:length(rooms)}</span> ${fn:length(rooms) == 1 ? 'room is' : 'rooms are'} free for your dates</p>
        <div class="mt-4 grid gap-6">
          <c:forEach var="room" items="${rooms}">
            <c:url var="bookUrl" value="/reservation/book">
              <c:param name="roomId" value="${room.roomId}"/>
              <c:param name="checkIn" value="${param.checkIn}"/>
              <c:param name="checkOut" value="${param.checkOut}"/>
              <c:param name="guests" value="${partySize}"/>
            </c:url>
            <article class="card overflow-hidden grid md:grid-cols-[320px_1fr] card-hover">
              <a href="${ctx}/rooms/detail?id=<c:out value='${room.roomId}'/>" class="block h-60 md:h-full img-zoom">
                <img class="img-cover" src="<c:out value='${room.imageUrl}'/>" alt="<c:out value='${room.roomName}'/>">
              </a>
              <div class="p-6 sm:p-7 flex flex-col">
                <div class="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <p class="text-xs tracking-[.2em] uppercase text-brass font-bold"><c:out value="${room.typeDisplayName}"/> · Room <c:out value="${room.roomNumber}"/></p>
                    <h2 class="font-display text-3xl font-semibold text-forest mt-1"><c:out value="${room.roomName}"/></h2>
                  </div>
                  <span class="badge badge-active">Free for your dates</span>
                </div>
                <p class="mt-3 text-sm text-ink/65 max-w-xl"><c:out value="${room.description}"/></p>
                <ul class="mt-4 flex flex-wrap gap-x-5 gap-y-2 text-sm text-ink/70">
                  <li class="flex items-center gap-1.5"><i data-lucide="users" class="w-4 h-4 text-brass"></i>Up to <c:out value="${room.capacity}"/> guests</li>
                  <li class="flex items-center gap-1.5"><i data-lucide="ruler" class="w-4 h-4 text-brass"></i><c:out value="${room.roomSize}"/> m²</li>
                </ul>
                <div class="mt-auto pt-6 flex flex-wrap items-end justify-between gap-4">
                  <p><span class="font-display text-3xl font-semibold text-forest">LKR <c:out value="${room.formattedRate}"/></span> <span class="text-sm text-muted">/ night</span></p>
                  <a href="${bookUrl}" class="btn btn-primary btn-sm"><i data-lucide="calendar-check"></i>Book this room</a>
                </div>
              </div>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
