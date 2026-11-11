<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Home page (GET /). Shown by HomeServlet (@WebServlet("")).
  Request attributes:
    home - HomeContent with the admin-editable text (hero, announcement, sign-up banner).
           Each value falls back to the built-in text if home is missing.
  The search form sends type and capacity to the room module's /rooms page.
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Boutique hotel in the hill country"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="home"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <%@ include file="/WEB-INF/views/common/botanical-art.jspf" %>

  <c:set var="stdCount" value="0"/><c:set var="dlxCount" value="0"/><c:set var="steCount" value="0"/>
  <c:forEach var="r" items="${rooms}">
    <c:if test="${r.type == 'StandardRoom'}"><c:set var="stdCount" value="${stdCount + 1}"/></c:if>
    <c:if test="${r.type == 'DeluxeRoom'}"><c:set var="dlxCount" value="${dlxCount + 1}"/></c:if>
    <c:if test="${r.type == 'SuiteRoom'}"><c:set var="steCount" value="${steCount + 1}"/></c:if>
    <c:if test="${empty minRate or r.baseRate < minRate}"><c:set var="minRate" value="${r.baseRate}"/></c:if>
    <c:if test="${r.type == 'StandardRoom' and (empty stdRoom or r.baseRate < stdRoom.baseRate)}"><c:set var="stdRoom" value="${r}"/></c:if>
    <c:if test="${r.type == 'DeluxeRoom' and (empty dlxRoom or r.baseRate < dlxRoom.baseRate)}"><c:set var="dlxRoom" value="${r}"/></c:if>
    <c:if test="${r.type == 'SuiteRoom' and (empty steRoom or r.baseRate < steRoom.baseRate)}"><c:set var="steRoom" value="${r}"/></c:if>
  </c:forEach>
  <c:set var="typeCount" value="${rooms == null ? 3 : (stdCount > 0 ? 1 : 0) + (dlxCount > 0 ? 1 : 0) + (steCount > 0 ? 1 : 0)}"/>
  <c:set var="ratingSum" value="0"/>
  <c:set var="verifiedTotal" value="0"/>
  <c:forEach var="rv" items="${reviews}">
    <c:set var="ratingSum" value="${ratingSum + rv.rating}"/>
    <c:if test="${rv.verified}"><c:set var="verifiedTotal" value="${verifiedTotal + 1}"/></c:if>
  </c:forEach>
  <c:set var="reviewTotal" value="${fn:length(reviews)}"/>
  <c:set var="avgRating" value="${reviewTotal > 0 ? ratingSum / reviewTotal : 4.8}"/>

  <main class="art-clip">
    <c:if test="${home.announcementVisible and not empty home.announcementText}">
      <div class="bg-forest-700 text-ivory text-sm">
        <p class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 py-2.5 flex items-center justify-center gap-2 text-center">
          <i data-lucide="megaphone" class="w-4 h-4 text-brass-light shrink-0"></i><c:out value="${home.announcementText}"/>
        </p>
      </div>
    </c:if>

    <!-- ============ HERO ============ -->
    <section class="has-art mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-10 lg:pt-16">
      <svg class="art art-sway w-[300px] h-[420px] -left-36 top-2 hidden md:block" style="--tilt:-14deg" aria-hidden="true"><use href="#art-tea"/></svg>
      <div class="grid lg:grid-cols-12 gap-12 items-center">
        <div class="lg:col-span-6 reveal">
          <span class="eyebrow"><c:out value="${home.heroEyebrow}" default="Hill Country · Sri Lanka"/></span>
          <h1 class="font-display text-[3.2rem] leading-[1.02] sm:text-7xl lg:text-[5.4rem] font-semibold text-forest mt-6">
            <c:out value="${home.heroTitle}" default="Stay where time"/> <em class="italic font-medium text-brass"><c:out value="${home.heroHighlight}" default="slows down."/></em>
          </h1>
          <p class="mt-6 text-lg text-ink/70 max-w-lg leading-relaxed">
            <c:out value="${home.heroText}" default="Twenty-four rooms and suites above the tea terraces of Nuwara Eliya. Mountain mornings, unhurried dinners and a front desk that remembers your name."/>
          </p>
          <div class="mt-9 flex flex-wrap gap-3">
            <a href="${ctx}/rooms" class="btn btn-primary btn-lg">Explore rooms <i data-lucide="arrow-right"></i></a>
            <a href="${ctx}/reviews" class="btn btn-outline btn-lg">Read guest stories</a>
          </div>
          <dl class="mt-12 grid grid-cols-3 max-w-md divide-x divide-line">
            <div class="pr-4">
              <dt class="text-xs text-muted uppercase tracking-widest">Rooms</dt>
              <dd class="font-display text-4xl text-forest font-semibold">${rooms == null ? 24 : fn:length(rooms)}</dd>
            </div>
            <div class="px-4">
              <dt class="text-xs text-muted uppercase tracking-widest">Rating</dt>
              <dd class="font-display text-4xl text-forest font-semibold"><fmt:formatNumber value="${avgRating}" pattern="0.0"/><span class="text-brass text-2xl">★</span></dd>
            </div>
            <div class="pl-4">
              <dt class="text-xs text-muted uppercase tracking-widest">Altitude</dt>
              <dd class="font-display text-4xl text-forest font-semibold">1,868<span class="text-lg text-muted">m</span></dd>
            </div>
          </dl>
        </div>

        <div class="lg:col-span-6 relative reveal">
          <div class="arch h-[440px] sm:h-[560px] lg:ml-10 shadow-2xl shadow-forest/20 img-zoom">
            <img class="img-cover" src="<c:out value='${home.heroImageUrl}' default='https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&q=80'/>" alt="The Orchid Estate">
          </div>
          <!-- rotating badge -->
          <div class="absolute -left-2 sm:left-0 bottom-16 w-32 h-32 rounded-full bg-paper grid place-items-center shadow-xl shadow-forest/10">
            <svg viewBox="0 0 120 120" class="absolute inset-0 w-full h-full rotating-badge text-forest" aria-hidden="true">
              <defs><path id="badge-circle" d="M60,60 m-44,0 a44,44 0 1,1 88,0 a44,44 0 1,1 -88,0"/></defs>
              <text font-size="9.6" letter-spacing="2.6" font-weight="700" fill="currentColor"><textPath href="#badge-circle">BOUTIQUE ESTATE · SINCE 1998 ·</textPath></text>
            </svg>
            <i data-lucide="flower-2" class="w-7 h-7 text-brass"></i>
          </div>
          <!-- floating price card -->
          <div class="absolute right-3 sm:-right-4 -bottom-6 card px-5 py-4 flex items-center gap-4">
            <span class="stat-icon"><i data-lucide="moon"></i></span>
            <div>
              <p class="text-xs text-muted uppercase tracking-widest">Tonight from</p>
              <p class="font-display text-2xl font-semibold text-forest">LKR <fmt:formatNumber value="${empty minRate ? 18500 : minRate}" pattern="#,##0"/> <span class="text-sm font-sans font-medium text-muted">/ night</span></p>
            </div>
          </div>
        </div>
      </div>

      <!-- Search bar -->
      <form action="${ctx}/reservation/search" method="get" data-booking-calc data-default-dates
            class="card mt-20 p-3 sm:p-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-[1fr_1fr_.8fr_1fr_auto] items-end reveal">
        <label class="block px-3 py-2">
          <span class="label">Check-in</span>
          <input type="date" name="checkIn" required class="w-full bg-transparent font-semibold text-forest focus:outline-none">
        </label>
        <label class="block px-3 py-2 lg:border-l lg:border-line">
          <span class="label">Check-out</span>
          <input type="date" name="checkOut" required class="w-full bg-transparent font-semibold text-forest focus:outline-none">
        </label>
        <label class="block px-3 py-2 lg:border-l lg:border-line">
          <span class="label">Guests</span>
          <select name="guests" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
            <option value="1">1 guest</option>
            <option value="2" selected>2 guests</option>
            <option value="3">3 guests</option>
            <option value="4">4 guests</option>
          </select>
        </label>
        <label class="block px-3 py-2 lg:border-l lg:border-line">
          <span class="label">Room type</span>
          <select name="type" class="w-full bg-transparent font-semibold text-forest focus:outline-none">
            <option value="">Any room</option>
            <option value="standard">Standard</option>
            <option value="deluxe">Deluxe</option>
            <option value="suite">Suite</option>
          </select>
        </label>
        <button type="submit" class="btn btn-brass btn-lg h-full min-h-[3.5rem] sm:col-span-2 lg:col-span-1"><i data-lucide="search"></i>Check availability</button>
      </form>
      <p class="mt-3 text-center text-xs text-muted"><span data-out="nights">—</span> · payment is taken at the front desk, never online</p>
    </section>

    <!-- ============ ROOMS ============ -->
    <section class="has-art mx-auto max-w-site px-4 sm:px-6 lg:px-8 mt-28">
      <svg class="art art-sway w-[260px] h-[450px] -right-28 -top-16" style="--tilt:16deg" aria-hidden="true"><use href="#art-fern"/></svg>
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-6 reveal">
        <div>
          <span class="eyebrow">Accommodation</span>
          <h2 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-4">Rooms &amp; suites</h2>
          <p class="mt-3 text-ink/65 max-w-xl">Every stay comes with breakfast on the terrace and a view of the hills.</p>
        </div>
        <a href="${ctx}/rooms" class="btn btn-outline">See all rooms <i data-lucide="arrow-right"></i></a>
      </div>

      <c:if test="${typeCount == 0}">
        <div class="mt-12 card card-pad text-center text-muted">No rooms are open for booking right now. Please check back soon.</div>
      </c:if>
      <div class="mt-12 grid gap-8 ${typeCount == 3 ? 'md:grid-cols-3' : 'md:grid-cols-2 max-w-4xl'}">
        <c:if test="${rooms == null or stdCount > 0}">
        <article class="group reveal">
          <a href="${ctx}/rooms?type=standard" class="block arch h-[380px] img-zoom">
            <img class="img-cover" src="<c:out value='${not empty stdRoom.imageUrl ? stdRoom.imageUrl : \"https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=800&q=80\"}'/>" alt="Standard room with queen bed">
          </a>
          <div class="mt-5 flex items-start justify-between gap-4">
            <div>
              <h3 class="font-display text-3xl font-semibold text-forest">Standard Room</h3>
              <p class="text-sm text-muted mt-1 flex flex-wrap items-center gap-x-3 gap-y-1"><span class="inline-flex items-center gap-1"><i data-lucide="users" class="w-4 h-4"></i>${empty stdRoom ? 2 : stdRoom.capacity} guests</span><span class="inline-flex items-center gap-1"><i data-lucide="ruler" class="w-4 h-4"></i>${empty stdRoom ? 24 : stdRoom.roomSize} m²</span><c:if test="${rooms != null}"><span class="inline-flex items-center gap-1"><i data-lucide="door-open" class="w-4 h-4"></i>${stdCount} ${stdCount == 1 ? 'room' : 'rooms'}</span></c:if></p>
            </div>
            <p class="text-right"><span class="block text-xs text-muted">from</span><span class="font-semibold text-forest">LKR <fmt:formatNumber value="${empty stdRoom ? 18500 : stdRoom.baseRate}" pattern="#,##0"/></span></p>
          </div>
          <p class="mt-3 text-sm text-ink/65 leading-relaxed">Queen bed, garden view and a rain shower. Calm and simple.</p>
        </article>
        </c:if>

        <c:if test="${rooms == null or dlxCount > 0}">
        <article class="group reveal">
          <a href="${ctx}/rooms?type=deluxe" class="block arch h-[380px] img-zoom">
            <img class="img-cover" src="<c:out value='${not empty dlxRoom.imageUrl ? dlxRoom.imageUrl : \"https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80\"}'/>" alt="Deluxe room with balcony">
          </a>
          <div class="mt-5 flex items-start justify-between gap-4">
            <div>
              <h3 class="font-display text-3xl font-semibold text-forest">Deluxe Room</h3>
              <p class="text-sm text-muted mt-1 flex flex-wrap items-center gap-x-3 gap-y-1"><span class="inline-flex items-center gap-1"><i data-lucide="users" class="w-4 h-4"></i>${empty dlxRoom ? 3 : dlxRoom.capacity} guests</span><span class="inline-flex items-center gap-1"><i data-lucide="ruler" class="w-4 h-4"></i>${empty dlxRoom ? 34 : dlxRoom.roomSize} m²</span><c:if test="${rooms != null}"><span class="inline-flex items-center gap-1"><i data-lucide="door-open" class="w-4 h-4"></i>${dlxCount} ${dlxCount == 1 ? 'room' : 'rooms'}</span></c:if></p>
            </div>
            <p class="text-right"><span class="block text-xs text-muted">from</span><span class="font-semibold text-forest">LKR <fmt:formatNumber value="${empty dlxRoom ? 28000 : dlxRoom.baseRate}" pattern="#,##0"/></span></p>
          </div>
          <p class="mt-3 text-sm text-ink/65 leading-relaxed">King bed, private valley balcony, a deep bathtub and a daybed.</p>
        </article>
        </c:if>

        <c:if test="${rooms == null or steCount > 0}">
        <article class="group reveal">
          <a href="${ctx}/rooms?type=suite" class="block arch h-[380px] img-zoom">
            <img class="img-cover" src="<c:out value='${not empty steRoom.imageUrl ? steRoom.imageUrl : \"https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80\"}'/>" alt="Estate suite with living area">
          </a>
          <div class="mt-5 flex items-start justify-between gap-4">
            <div>
              <h3 class="font-display text-3xl font-semibold text-forest">Estate Suite</h3>
              <p class="text-sm text-muted mt-1 flex flex-wrap items-center gap-x-3 gap-y-1"><span class="inline-flex items-center gap-1"><i data-lucide="users" class="w-4 h-4"></i>${empty steRoom ? 4 : steRoom.capacity} guests</span><span class="inline-flex items-center gap-1"><i data-lucide="ruler" class="w-4 h-4"></i>${empty steRoom ? 62 : steRoom.roomSize} m²</span><c:if test="${rooms != null}"><span class="inline-flex items-center gap-1"><i data-lucide="door-open" class="w-4 h-4"></i>${steCount} ${steCount == 1 ? 'room' : 'rooms'}</span></c:if></p>
            </div>
            <p class="text-right"><span class="block text-xs text-muted">from</span><span class="font-semibold text-forest">LKR <fmt:formatNumber value="${empty steRoom ? 46000 : steRoom.baseRate}" pattern="#,##0"/></span></p>
          </div>
          <p class="mt-3 text-sm text-ink/65 leading-relaxed">A separate living room, panoramic terrace and afternoon tea served daily.</p>
        </article>
        </c:if>
      </div>
    </section>

    <!-- ============ THE ESTATE ============ -->
    <section id="estate" class="has-art mt-32 bg-sand/60 py-24 overflow-hidden">
      <svg class="art art-flip w-[400px] h-[440px] -left-16 -bottom-10" aria-hidden="true"><use href="#art-orchid"/></svg>
      <svg class="art art-sway w-32 h-32 right-6 top-8" style="--tilt:20deg" aria-hidden="true"><use href="#art-bloom"/></svg>
      <div class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 grid lg:grid-cols-12 gap-12 items-center">
        <div class="lg:col-span-5 reveal">
          <span class="eyebrow">The estate</span>
          <h2 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-4">An estate for slow mornings</h2>
          <p class="mt-4 text-ink/65 leading-relaxed">Everything you need is a short walk through the gardens. Nothing is rushed, and nothing is far.</p>

          <ol class="mt-10 space-y-7">
            <li class="flex gap-5">
              <span class="font-display italic text-3xl text-brass w-8">i.</span>
              <div>
                <h3 class="font-semibold text-forest text-lg">The Fern Spa</h3>
                <p class="text-sm text-ink/65 mt-1">Ayurvedic treatments and a steam room fed by mountain spring water.</p>
              </div>
            </li>
            <li class="flex gap-5">
              <span class="font-display italic text-3xl text-brass w-8">ii.</span>
              <div>
                <h3 class="font-semibold text-forest text-lg">Terrace dining</h3>
                <p class="text-sm text-ink/65 mt-1">Hill-country produce, slow-cooked curries and estate-grown tea.</p>
              </div>
            </li>
            <li class="flex gap-5">
              <span class="font-display italic text-3xl text-brass w-8">iii.</span>
              <div>
                <h3 class="font-semibold text-forest text-lg">The heated pool</h3>
                <p class="text-sm text-ink/65 mt-1">Warm water, cool air and the valley falling away below.</p>
              </div>
            </li>
          </ol>
        </div>

        <div class="lg:col-span-7 grid grid-cols-2 gap-5 reveal">
          <div class="arch h-[440px] row-span-2 img-zoom">
            <img class="img-cover" src="https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=800&q=80" alt="Spa treatment">
          </div>
          <div class="rounded-[22px] overflow-hidden h-[210px] img-zoom">
            <img class="img-cover" src="https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&w=800&q=80" alt="Plated dinner">
          </div>
          <div class="rounded-[22px] overflow-hidden h-[210px] img-zoom">
            <img class="img-cover" src="https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?auto=format&fit=crop&w=800&q=80" alt="Infinity pool">
          </div>
        </div>
      </div>
    </section>

    <!-- ============ AMENITIES ============ -->
    <section class="has-art mx-auto max-w-site px-4 sm:px-6 lg:px-8 mt-24">
      <svg class="art art-sway w-28 h-28 -left-6 -top-14 hidden sm:block" style="--tilt:-24deg" aria-hidden="true"><use href="#art-bloom"/></svg>
      <svg class="art art-sway w-28 h-28 -right-6 top-6 hidden sm:block" style="--tilt:28deg" aria-hidden="true"><use href="#art-bloom"/></svg>
      <div class="ornament mb-12"><span></span></div>
      <ul class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-8 text-center reveal">
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="coffee"></i></span><p class="mt-3 text-sm font-semibold text-forest">Breakfast included</p></li>
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="wifi"></i></span><p class="mt-3 text-sm font-semibold text-forest">Fast Wi-Fi</p></li>
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="mountain"></i></span><p class="mt-3 text-sm font-semibold text-forest">Valley views</p></li>
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="car"></i></span><p class="mt-3 text-sm font-semibold text-forest">Station pickup</p></li>
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="leaf"></i></span><p class="mt-3 text-sm font-semibold text-forest">Tea estate walks</p></li>
        <li><span class="stat-icon mx-auto w-14 h-14 rounded-full"><i data-lucide="concierge-bell"></i></span><p class="mt-3 text-sm font-semibold text-forest">24-hour front desk</p></li>
      </ul>
    </section>

    <!-- ============ REVIEWS ============ -->
    <section class="has-art mx-auto max-w-site px-4 sm:px-6 lg:px-8 mt-28">
      <svg class="art art-flip art-sway w-[260px] h-[450px] -left-32 -top-20" style="--tilt:14deg" aria-hidden="true"><use href="#art-fern"/></svg>
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-6 reveal">
        <div>
          <span class="eyebrow">Guest reviews</span>
          <h2 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-4">Guest stories</h2>
        </div>
        <div class="flex items-center gap-4">
          <span class="font-display text-5xl font-semibold text-forest"><fmt:formatNumber value="${avgRating}" pattern="0.0"/></span>
          <div>
            <fmt:formatNumber var="avgStars" value="${avgRating}" maxFractionDigits="0"/>
            <span data-stars="${avgStars}"></span>
            <p class="text-xs text-muted mt-1">
              <c:choose>
                <c:when test="${reviews == null}">from 312 verified stays</c:when>
                <c:otherwise>from ${reviewTotal} ${reviewTotal == 1 ? 'review' : 'reviews'} · ${verifiedTotal} verified</c:otherwise>
              </c:choose>
            </p>
          </div>
        </div>
      </div>

      <c:if test="${reviews != null}">
        <c:set var="shown" value="0"/>
        <div class="mt-10 grid md:grid-cols-3 gap-6">
          <c:forEach var="rv" items="${reviews}">
            <c:if test="${shown < 3 and rv.rating >= 4}">
              <c:set var="shown" value="${shown + 1}"/>
              <c:set var="parts" value="${fn:split(fn:trim(rv.guestName), ' ')}"/>
              <figure class="card card-pad card-hover reveal">
                <div class="flex items-center justify-between gap-3">
                  <i data-lucide="quote" class="w-8 h-8 text-brass/60"></i>
                  <span data-stars="${rv.rating}"></span>
                </div>
                <blockquote class="mt-4 font-display text-2xl leading-snug text-forest line-clamp-5">&ldquo;<c:out value="${rv.text}"/>&rdquo;</blockquote>
                <figcaption class="mt-6 flex items-center gap-3">
                  <span class="avatar"><c:out value="${fn:toUpperCase(fn:substring(parts[0], 0, 1))}${fn:length(parts) > 1 ? fn:toUpperCase(fn:substring(parts[1], 0, 1)) : ''}"/></span>
                  <span class="text-sm"><span class="font-semibold block"><c:out value="${parts[0]}"/><c:if test="${fn:length(parts) > 1}"> <c:out value="${fn:substring(parts[1], 0, 1)}"/>.</c:if></span><span class="text-muted">${rv.verified ? 'Stayed with us' : 'Visitor'}</span></span>
                  <c:if test="${rv.verified}"><span class="badge badge-verified ml-auto">Verified</span></c:if>
                </figcaption>
              </figure>
            </c:if>
          </c:forEach>
        </div>
        <c:if test="${shown == 0}">
          <div class="mt-10 card card-pad text-center text-muted">
            No reviews yet. <a href="${ctx}/reviews" class="text-forest font-semibold underline underline-offset-4">Be the first to share your stay.</a>
          </div>
        </c:if>
      </c:if>

      <c:if test="${reviews == null}">
      <div class="mt-10 grid md:grid-cols-3 gap-6">
        <figure class="card card-pad card-hover reveal">
          <i data-lucide="quote" class="w-8 h-8 text-brass/60"></i>
          <blockquote class="mt-4 font-display text-2xl leading-snug text-forest">"We woke to mist over the tea fields every morning. The suite terrace is the best seat in Sri Lanka."</blockquote>
          <figcaption class="mt-6 flex items-center gap-3">
            <span class="avatar">RB</span>
            <span class="text-sm"><span class="font-semibold block">Ravindu B.</span><span class="text-muted">Estate Suite</span></span>
            <span class="badge badge-verified ml-auto">Verified</span>
          </figcaption>
        </figure>
        <figure class="card card-pad card-hover reveal">
          <i data-lucide="quote" class="w-8 h-8 text-brass/60"></i>
          <blockquote class="mt-4 font-display text-2xl leading-snug text-forest">"Quiet, warm and beautifully run. Check-in took two minutes and they remembered our tea order."</blockquote>
          <figcaption class="mt-6 flex items-center gap-3">
            <span class="avatar">SC</span>
            <span class="text-sm"><span class="font-semibold block">Sarah C.</span><span class="text-muted">Deluxe Room</span></span>
            <span class="badge badge-verified ml-auto">Verified</span>
          </figcaption>
        </figure>
        <figure class="card card-pad card-hover reveal">
          <i data-lucide="quote" class="w-8 h-8 text-brass/60"></i>
          <blockquote class="mt-4 font-display text-2xl leading-snug text-forest">"Simple room, spotless, and the breakfast hoppers were worth the trip on their own."</blockquote>
          <figcaption class="mt-6 flex items-center gap-3">
            <span class="avatar">AF</span>
            <span class="text-sm"><span class="font-semibold block">Ashan F.</span><span class="text-muted">Standard Room</span></span>
            <span class="badge badge-verified ml-auto">Verified</span>
          </figcaption>
        </figure>
      </div>
      </c:if>
    </section>

    <!-- ============ CTA ============ -->
    <section class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 mt-28">
      <div class="has-art panel-dark rounded-[28px] overflow-hidden grid md:grid-cols-2 reveal">
        <svg class="art art-light w-[300px] h-[330px] left-[28%] -bottom-12" aria-hidden="true"><use href="#art-orchid"/></svg>
        <div class="p-10 sm:p-14">
          <span class="eyebrow">Members save more</span>
          <h2 class="font-display text-4xl sm:text-5xl font-semibold mt-5 leading-tight"><c:out value="${home.ctaTitle}" default="Your window seat is"/> <em class="italic text-brass-light"><c:out value="${home.ctaHighlight}" default="waiting."/></em></h2>
          <p class="mt-5 text-ivory/65 max-w-md"><c:out value="${home.ctaText}" default="Create a free account to book in seconds, manage your stays and leave a verified review after you check out. VIP guests enjoy 10% off every stay."/></p>
          <div class="mt-8 flex flex-wrap gap-3">
            <c:choose>
              <c:when test="${empty me}">
                <a href="${ctx}/register" class="btn btn-brass btn-lg">Create an account</a>
                <a href="${ctx}/login" class="btn btn-lg text-ivory border border-ivory/25 hover:bg-ivory/10">Sign in</a>
              </c:when>
              <c:when test="${me.role == 'GUEST'}">
                <a href="${ctx}/reservation/search" class="btn btn-brass btn-lg">Book a stay</a>
                <a href="${ctx}/bookings" class="btn btn-lg text-ivory border border-ivory/25 hover:bg-ivory/10">My bookings</a>
              </c:when>
              <c:otherwise>
                <a href="${ctx}/admin" class="btn btn-brass btn-lg">Staff portal</a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class="min-h-[280px]">
          <img class="img-cover" src="https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&w=1000&q=80" alt="Balcony overlooking the lake and mountains">
        </div>
      </div>
    </section>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
