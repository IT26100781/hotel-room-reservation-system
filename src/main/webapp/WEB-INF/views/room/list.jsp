<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Rooms &amp; Suites · The Orchid Estate</title>
  <link rel="icon" href="${pageContext.request.contextPath}/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,500;0,600;0,700;1,500;1,600&family=Manrope:wght@400;500;600;700;800&display=swap">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="${pageContext.request.contextPath}/js/tailwind.config.js"></script>
  <script src="https://unpkg.com/lucide@0.469.0/dist/umd/lucide.min.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
</head>
<body data-root="${pageContext.request.contextPath}/prototype/" data-layout="public" data-active="rooms" data-user="none">
  <div data-slot="header"></div>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12">
    <!-- Page heading -->
    <div class="grid lg:grid-cols-2 gap-6 items-end">
      <div>
        <span class="eyebrow">Accommodation</span>
        <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-4">Rooms &amp; Suites</h1>
      </div>
      <p class="text-ink/65 lg:text-right lg:max-w-md lg:ml-auto">Choose your dates to see which rooms are free. Rooms under maintenance are hidden automatically.</p>
    </div>

    <!-- Search / dates -->
    <form action="${pageContext.request.contextPath}/rooms" method="get" data-fill-query data-booking-calc data-default-dates
          class="card mt-10 p-3 sm:p-4 grid gap-3 md:grid-cols-[1fr_1fr_.8fr_auto] items-end">
      <label class="block px-3 py-2">
        <span class="label">Check-in</span>
        <input type="date" name="checkIn" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Check-out</span>
        <input type="date" name="checkOut" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
      </label>
      <label class="block px-3 py-2 md:border-l md:border-line">
        <span class="label">Guests</span>
        <select name="capacity" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
          <option value="1">1 guest</option>
          <option value="2" selected>2 guests</option>
          <option value="3">3 guests</option>
          <option value="4">4+ guests</option>
        </select>
      </label>
      <button type="submit" class="btn btn-primary btn-lg min-h-[3.5rem]"><i data-lucide="search"></i>Update search</button>
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
        <span class="font-semibold text-forest" data-count-for="#room-grid">${rooms != null ? rooms.size() : 0}</span> rooms
      </p>
    </div>

    <!-- Room list -->
    <div id="room-grid" class="mt-6 grid gap-6">
      <c:forEach var="room" items="${rooms}">
        <article data-item data-type="${room.typeDisplayName.toLowerCase()}" class="card overflow-hidden grid md:grid-cols-[320px_1fr] card-hover">
          <a href="${pageContext.request.contextPath}/rooms/detail?id=${room.roomId}" class="block h-60 md:h-full img-zoom">
            <img class="img-cover" src="${room.imageUrl}" alt="${room.roomName}">
          </a>
          <div class="p-6 sm:p-7 flex flex-col">
            <div class="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p class="text-xs tracking-[.2em] uppercase text-brass font-bold">${room.typeDisplayName} · Room ${room.roomNumber}</p>
                <h2 class="font-display text-3xl font-semibold text-forest mt-1">${room.roomName}</h2>
              </div>
              <span class="badge badge-active">Available</span>
            </div>
            <p class="mt-3 text-sm text-ink/65 max-w-xl">${room.description}</p>
            <ul class="mt-4 flex flex-wrap gap-x-5 gap-y-2 text-sm text-ink/70">
              <li class="flex items-center gap-1.5"><i data-lucide="users" class="w-4 h-4 text-brass"></i>Up to ${room.capacity}</li>
              <li class="flex items-center gap-1.5"><i data-lucide="ruler" class="w-4 h-4 text-brass"></i>${room.roomSize} m²</li>
              <c:forEach var="amenity" items="${room.amenities}" begin="0" end="1">
                <li class="flex items-center gap-1.5"><i data-lucide="check" class="w-4 h-4 text-brass"></i>${amenity}</li>
              </c:forEach>
            </ul>
            <div class="mt-auto pt-6 flex flex-wrap items-end justify-between gap-4">
              <p><span class="font-display text-3xl font-semibold text-forest">LKR ${room.formattedRate}</span> <span class="text-sm text-muted">/ night</span></p>
              <div class="flex gap-2">
                <a href="${pageContext.request.contextPath}/rooms/detail?id=${room.roomId}" class="btn btn-outline btn-sm">View details</a>
                <a href="${pageContext.request.contextPath}/prototype/reservation/book.html?roomId=${room.roomId}" class="btn btn-primary btn-sm">Book now</a>
              </div>
            </div>
          </div>
        </article>
      </c:forEach>
    </div>
  </main>

  <div data-slot="footer"></div>

  <script src="${pageContext.request.contextPath}/js/layout.js"></script>
  <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
