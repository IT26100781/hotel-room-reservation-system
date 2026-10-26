<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${room.roomName} · The Orchid Estate</title>
  <link rel="icon" href="${pageContext.request.contextPath}/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,500;0,600;0,700;1,500;1,600&family=Manrope:wght@400;500;600;700;800&display=swap">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="${pageContext.request.contextPath}/js/tailwind.config.js"></script>
  <script src="https://unpkg.com/lucide@0.469.0/dist/umd/lucide.min.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
</head>
<body data-root="${pageContext.request.contextPath}/prototype/" data-layout="public" data-active="rooms" data-user="guest">
  <div data-slot="header"></div>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-8">
    <nav class="text-sm text-muted flex items-center gap-2" aria-label="Breadcrumb">
      <a href="${pageContext.request.contextPath}/" class="hover:text-forest">Home</a>
      <i data-lucide="chevron-right" class="w-3.5 h-3.5"></i>
      <a href="${pageContext.request.contextPath}/rooms" class="hover:text-forest">Rooms</a>
      <i data-lucide="chevron-right" class="w-3.5 h-3.5"></i>
      <span class="text-forest font-medium">${room.roomName}</span>
    </nav>

    <!-- Gallery -->
    <div class="mt-6 grid md:grid-cols-[1.4fr_1fr] gap-4">
      <div class="arch h-[420px] md:h-[520px] img-zoom">
        <img class="img-cover" src="${room.imageUrl}" alt="${room.roomName}">
      </div>
      <div class="grid grid-rows-2 gap-4">
        <div class="rounded-[22px] overflow-hidden img-zoom">
          <img class="img-cover" src="https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80" alt="Living area">
        </div>
        <div class="rounded-[22px] overflow-hidden img-zoom relative">
          <img class="img-cover" src="https://images.unsplash.com/photo-1559599238-308793637427?auto=format&fit=crop&w=800&q=80" alt="Bed detail">
          <span class="absolute bottom-4 right-4 badge no-dot bg-paper/90 text-forest"><i data-lucide="sparkles"></i>Spacious</span>
        </div>
      </div>
    </div>

    <div class="mt-12 grid lg:grid-cols-[1fr_380px] gap-12 items-start">
      <!-- Details -->
      <div>
        <p class="text-xs tracking-[.22em] uppercase text-brass font-bold">${room.typeDisplayName} · Room ${room.roomNumber}</p>
        <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-2">${room.roomName}</h1>
        <div class="mt-5 flex flex-wrap gap-2">
          <span class="badge badge-outline no-dot"><i data-lucide="users"></i>Up to ${room.capacity} guests</span>
          <span class="badge badge-outline no-dot"><i data-lucide="ruler"></i>${room.roomSize} m²</span>
          <span class="badge badge-active">${room.status}</span>
        </div>

        <p class="mt-8 text-lg leading-relaxed text-ink/75">
          ${room.description}
        </p>

        <div class="ornament my-10"><span></span></div>

        <h2 class="font-display text-3xl font-semibold text-forest">What's included</h2>
        <ul class="mt-6 grid sm:grid-cols-2 gap-4">
          <c:forEach var="a" items="${room.amenities}">
            <li class="flex items-center gap-3">
              <span class="stat-icon"><i data-lucide="check"></i></span>
              <span class="text-sm font-medium">${a}</span>
            </li>
          </c:forEach>
        </ul>

        <div class="ornament my-10"><span></span></div>

        <h2 class="font-display text-3xl font-semibold text-forest">Good to know</h2>
        <dl class="mt-6 grid sm:grid-cols-3 gap-6 text-sm">
          <div><dt class="label">Check-in</dt><dd class="font-semibold text-forest">From 2:00 pm</dd></div>
          <div><dt class="label">Check-out</dt><dd class="font-semibold text-forest">Until 11:00 am</dd></div>
          <div><dt class="label">Cancellation</dt><dd class="font-semibold text-forest">Free, before check-in</dd></div>
        </dl>
      </div>

      <!-- Booking Card Widget -->
      <aside class="lg:sticky lg:top-8">
        <div class="card card-pad shadow-lift">
          <p class="label">Reservation</p>
          <div class="mt-2 flex items-baseline justify-between">
            <p><span class="font-display text-4xl font-semibold text-forest">LKR ${room.formattedRate}</span> <span class="text-sm text-muted">/ night</span></p>
            <span class="text-xs text-muted">Taxes included</span>
          </div>

          <div class="mt-6 pt-6 border-t border-line space-y-4">
            <div class="flex justify-between text-sm">
              <span class="text-muted">Max capacity:</span>
              <span class="font-semibold text-forest">${room.capacity} guests</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-muted">Room size:</span>
              <span class="font-semibold text-forest">${room.roomSize} m²</span>
            </div>
          </div>

          <div class="mt-8">
            <a href="${pageContext.request.contextPath}/prototype/reservation/book.html?roomId=${room.roomId}" class="btn btn-primary btn-lg w-full">
              Reserve this room
            </a>
          </div>
        </div>
      </aside>
    </div>
  </main>

  <div data-slot="footer"></div>

  <script src="${pageContext.request.contextPath}/js/layout.js"></script>
  <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
