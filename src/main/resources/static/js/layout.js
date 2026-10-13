/*
 * Shared page chrome for the HTML prototype: header, footer, staff sidebar.
 *
 * Each page sets attributes on <body>:
 *   data-root   path from the page back to /prototype/  ("" or "../")
 *   data-layout "public" (top header + footer) or "staff" (sidebar + top bar)
 *   data-active which nav link is highlighted (e.g. "rooms", "bookings")
 *   data-user   who is "logged in" for the mock: none | guest | staff | admin
 *
 * and leaves empty slots: <div data-slot="header|footer|sidebar|topbar"></div>
 *
 * WHEN CONVERTING TO JSP: copy the markup below into
 * WEB-INF/views/common/header.jsp, footer.jsp and sidebar.jsp, replace the
 * hard-coded user with the session user, and <%@ include %> them instead.
 */
(function () {
  const body = document.body;
  const root = body.dataset.root || '';
  const active = body.dataset.active || '';
  const user = body.dataset.user || 'none';

  const url = (path) => root + path;
  const isActive = (key) => (key === active ? ' active' : '');

  const LOGO = `
    <svg viewBox="0 0 40 40" class="w-9 h-9 text-brass" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      <path d="M8 36V18a12 12 0 0 1 24 0v18"/>
      <path d="M4 36h32"/>
      <path d="M20 31V19"/>
      <path d="M20 19c-4.2-.6-6.6-3.6-6.6-8 4.2.3 6.6 3.3 6.6 8z"/>
      <path d="M20 19c4.2-.6 6.6-3.6 6.6-8-4.2.3-6.6 3.3-6.6 8z"/>
      <path d="M20 17.5c-1.8-2.6-1.8-5.6 0-8.5 1.8 2.9 1.8 5.9 0 8.5z"/>
      <path d="M20 27c-2.2 0-3.8-1-4.6-2.8M20 27c2.2 0 3.8-1 4.6-2.8"/>
    </svg>`;

  const brand = (dark) => `
    <a href="${url('index.html')}" class="flex items-center gap-3 shrink-0" aria-label="The Orchid Estate home">
      ${LOGO}
      <span class="leading-none">
        <span class="font-display text-[1.25rem] sm:text-[1.45rem] font-semibold block ${dark ? 'text-ivory' : 'text-forest'}">The Orchid Estate</span>
        <span class="text-[9.5px] tracking-[.3em] uppercase ${dark ? 'text-ivory/50' : 'text-muted'} ${dark ? 'block' : 'hidden sm:block'} mt-1">Hill Country · Sri Lanka</span>
      </span>
    </a>`;

  /* ------------------------------------------------------------------ */
  /* Public header                                                       */
  /* ------------------------------------------------------------------ */
  function publicHeader() {
    const links = [
      ['home', 'Home', 'index.html'],
      ['rooms', 'Rooms & Suites', 'room/list.html'],
      ['reviews', 'Guest Stories', 'review/list.html'],
    ];
    if (user === 'guest') links.push(['bookings', 'My Bookings', 'reservation/my-bookings.html']);

    const navLinks = links.map(([key, label, href]) =>
      `<a href="${url(href)}" class="nav-link${isActive(key)}">${label}</a>`).join('');

    const mobileLinks = links.map(([key, label, href]) =>
      `<a href="${url(href)}" class="block py-3 border-b border-line/60 font-medium ${key === active ? 'text-forest' : 'text-ink/75'}">${label}</a>`).join('');

    const right = user === 'guest' ? `
      <div class="dropdown" data-dropdown>
        <button class="flex items-center gap-2.5 rounded-full pl-1 pr-3 py-1 hover:bg-forest/5 transition" data-dropdown-toggle aria-haspopup="true">
          <span class="avatar avatar-brass">NP</span>
          <span class="hidden sm:block text-left leading-tight">
            <span class="block text-sm font-semibold">Nethmi</span>
            <span class="block text-[10px] tracking-widest uppercase text-brass font-bold">VIP Guest</span>
          </span>
          <i data-lucide="chevron-down" class="w-4 h-4 text-muted"></i>
        </button>
        <div class="dropdown-menu">
          <a class="dropdown-item" href="${url('guest/dashboard.html')}"><i data-lucide="layout-dashboard"></i>My account</a>
          <a class="dropdown-item" href="${url('reservation/my-bookings.html')}"><i data-lucide="calendar-days"></i>My bookings</a>
          <a class="dropdown-item" href="${url('guest/profile.html')}"><i data-lucide="user"></i>Profile &amp; password</a>
          <div class="my-1 border-t border-line"></div>
          <a class="dropdown-item" href="${url('index.html')}"><i data-lucide="log-out"></i>Sign out</a>
        </div>
      </div>` : `
      <a href="${url('guest/login.html')}" class="btn btn-ghost btn-sm hidden sm:inline-flex">Sign in</a>
      <a href="${url('room/list.html')}" class="btn btn-primary btn-sm hidden sm:inline-flex">Book a stay</a>`;

    return `
      <header class="no-print header-glass sticky top-0 z-50">
        <div class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 h-[76px] flex items-center gap-3 sm:gap-6">
          ${brand(false)}
          <nav class="hidden md:flex items-center gap-8 ml-6" aria-label="Main">${navLinks}</nav>
          <div class="ml-auto flex items-center gap-2">
            ${right}
            <button class="md:hidden btn btn-ghost btn-icon" data-toggle="#mobile-nav" aria-label="Open menu">
              <i data-lucide="menu" class="w-5 h-5"></i>
            </button>
          </div>
        </div>
        <div id="mobile-nav" class="md:hidden hidden border-t border-line/70 px-4 pb-4">
          ${mobileLinks}
          ${user === 'guest' ? '' : `<a href="${url('guest/login.html')}" class="block py-3 border-b border-line/60 font-medium text-ink/75">Sign in</a>
          <a href="${url('room/list.html')}" class="btn btn-primary btn-block mt-4">Book a stay</a>`}
        </div>
      </header>`;
  }

  /* ------------------------------------------------------------------ */
  /* Public footer                                                       */
  /* ------------------------------------------------------------------ */
  function publicFooter() {
    return `
      <footer class="no-print panel-dark mt-24">
        <div class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-16 pb-10">
          <div class="grid gap-12 md:grid-cols-12">
            <div class="md:col-span-5">
              ${brand(true)}
              <p class="mt-6 text-ivory/60 text-sm leading-relaxed max-w-sm">
                A twenty-four room boutique estate among the tea hills, where slow mornings,
                mountain air and quiet service are the whole point.
              </p>
              <div class="mt-6 flex gap-3 text-ivory/70">
                <span class="inline-flex items-center gap-2 text-sm"><i data-lucide="map-pin" class="w-4 h-4 text-brass"></i>12 Estate Road, Nuwara Eliya</span>
              </div>
            </div>
            <div class="md:col-span-2">
              <h4 class="text-xs tracking-[.22em] uppercase text-brass font-bold mb-4">Explore</h4>
              <ul class="space-y-2.5 text-sm text-ivory/70">
                <li><a class="hover:text-ivory" href="${url('room/list.html')}">Rooms &amp; Suites</a></li>
                <li><a class="hover:text-ivory" href="${url('review/list.html')}">Guest stories</a></li>
                <li><a class="hover:text-ivory" href="${url('index.html')}#estate">The estate</a></li>
              </ul>
            </div>
            <div class="md:col-span-2">
              <h4 class="text-xs tracking-[.22em] uppercase text-brass font-bold mb-4">Guests</h4>
              <ul class="space-y-2.5 text-sm text-ivory/70">
                <li><a class="hover:text-ivory" href="${url('guest/login.html')}">Sign in</a></li>
                <li><a class="hover:text-ivory" href="${url('guest/register.html')}">Create account</a></li>
                <li><a class="hover:text-ivory" href="${url('reservation/my-bookings.html')}">My bookings</a></li>
              </ul>
            </div>
            <div class="md:col-span-3">
              <h4 class="text-xs tracking-[.22em] uppercase text-brass font-bold mb-4">Front desk</h4>
              <ul class="space-y-2.5 text-sm text-ivory/70">
                <li class="flex items-center gap-2"><i data-lucide="phone" class="w-4 h-4 text-brass"></i>+94 52 222 0198</li>
                <li class="flex items-center gap-2"><i data-lucide="mail" class="w-4 h-4 text-brass"></i>stay@orchidestate.lk</li>
                <li class="flex items-center gap-2"><i data-lucide="clock" class="w-4 h-4 text-brass"></i>Open 24 hours</li>
              </ul>
            </div>
          </div>
          <div class="ornament mt-14 mb-6 opacity-60"><span></span></div>
          <div class="flex flex-col sm:flex-row gap-3 justify-between text-xs text-ivory/45">
            <p>&copy; <span data-year></span> The Orchid Estate · SE1020 OOP project</p>
            <a href="${url('admin/login.html')}" class="inline-flex items-center gap-1.5 hover:text-ivory"><i data-lucide="key-round" class="w-3.5 h-3.5"></i>Staff portal</a>
          </div>
        </div>
      </footer>`;
  }

  /* ------------------------------------------------------------------ */
  /* Staff sidebar + top bar                                             */
  /* ------------------------------------------------------------------ */
  function staffSidebar() {
    const isAdmin = user !== 'staff';
    // [key, label, icon, href, adminOnly]
    const groups = [
      ['Overview', [
        ['dashboard', 'Dashboard', 'layout-dashboard', 'admin/dashboard.html', false],
      ]],
      ['Front desk', [
        ['bookings', 'Bookings', 'calendar-check', 'reservation/manage.html', false],
        ['payments', 'Payments', 'wallet', 'payment/list.html', false],
        ['guests', 'Guests', 'users', 'guest/manage.html', false],
      ]],
      ['Property', [
        ['rooms', 'Rooms', 'bed-double', 'room/manage.html', true],
        ['reviews', 'Reviews', 'message-square-quote', 'review/moderate.html', true],
      ]],
      ['Administration', [
        ['staff', 'Staff accounts', 'user-cog', 'admin/staff.html', true],
      ]],
    ];

    const nav = groups.map(([title, items]) => {
      const visible = items.filter((i) => isAdmin || !i[4]);
      if (!visible.length) return '';
      return `<p class="side-group">${title}</p>` + visible.map(([key, label, icon, href, adminOnly]) =>
        `<a href="${url(href)}" class="side-link${isActive(key)}"><i data-lucide="${icon}"></i>${label}${adminOnly ? '<span class="side-tag">ADMIN</span>' : ''}</a>`
      ).join('');
    }).join('');

    const who = isAdmin
      ? { initials: 'KP', name: 'Kasun Perera', role: 'Administrator' }
      : { initials: 'IS', name: 'Ishara Silva', role: 'Front desk staff' };

    return `
      <div id="side-overlay" class="no-print fixed inset-0 z-40 bg-forest-900/50 hidden lg:hidden" data-toggle="#side-overlay, #sidebar"></div>
      <aside id="sidebar" data-toggle-class="-translate-x-full" class="no-print panel-dark fixed inset-y-0 left-0 z-50 w-72 flex flex-col px-5 py-6 -translate-x-full lg:translate-x-0 transition-transform duration-300">
        <div class="px-1">${brand(true)}</div>
        <p class="mt-6 mx-1 text-[10px] tracking-[.28em] uppercase text-brass font-bold">Staff portal</p>
        <nav class="mt-2 flex-1 overflow-y-auto pl-3 -ml-3 pr-1" aria-label="Staff">${nav}</nav>
        <div class="mt-4 rounded-2xl bg-ivory/[.06] p-3 flex items-center gap-3">
          <span class="avatar avatar-brass">${who.initials}</span>
          <div class="min-w-0 leading-tight">
            <p class="text-sm font-semibold text-ivory truncate">${who.name}</p>
            <p class="text-[11px] text-ivory/50">${who.role}</p>
          </div>
          <a href="${url('admin/login.html')}" class="ml-auto p-2 rounded-lg text-ivory/60 hover:text-ivory hover:bg-ivory/10" title="Sign out" aria-label="Sign out"><i data-lucide="log-out" class="w-4 h-4"></i></a>
        </div>
      </aside>`;
  }

  function staffTopbar() {
    const today = new Date().toLocaleDateString('en-GB', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
    return `
      <div class="no-print header-glass sticky top-0 z-30">
        <div class="h-16 px-4 sm:px-8 flex items-center gap-4">
          <button class="lg:hidden btn btn-ghost btn-icon" data-toggle="#side-overlay, #sidebar" aria-label="Open menu"><i data-lucide="menu" class="w-5 h-5"></i></button>
          <p class="hidden sm:flex items-center gap-2 text-sm text-muted"><i data-lucide="calendar" class="w-4 h-4 text-brass"></i>${today}</p>
          <div class="ml-auto flex items-center gap-2">
            <a href="${url('index.html')}" class="btn btn-ghost btn-sm"><i data-lucide="hotel"></i><span class="hidden sm:inline">Guest site</span></a>
            <button class="btn btn-ghost btn-icon relative" aria-label="Notifications">
              <i data-lucide="bell" class="w-5 h-5"></i>
              <span class="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-brass"></span>
            </button>
          </div>
        </div>
      </div>`;
  }

  /* ------------------------------------------------------------------ */
  const fill = (slot, html) => {
    const el = document.querySelector(`[data-slot="${slot}"]`);
    if (el) el.outerHTML = html;
  };

  if (body.dataset.layout === 'public') {
    fill('header', publicHeader());
    fill('footer', publicFooter());
  } else if (body.dataset.layout === 'staff') {
    fill('sidebar', staffSidebar());
    fill('topbar', staffTopbar());
  }
})();
