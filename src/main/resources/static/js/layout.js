// Reconstructed shared layout: the original layout.js was not supplied.
document.querySelector('[data-slot="sidebar"]').innerHTML =
  '<aside class="sidebar"><a class="brand" href="/staff/payments/list.html"><span class="brand-mark">O</span><span>The Orchid<br>Estate<small>STAFF PORTAL</small></span></a>' +
  '<div class="sidebar-label">FRONT DESK</div><nav><a href="/staff/payments/list.html">Payment history</a><a href="/staff/payments/record.html">Record payment</a></nav>' +
  '<div class="sidebar-foot">Hotel operations<br><small>Payment &amp; billing</small></div></aside>';
document.querySelector('[data-slot="topbar"]').innerHTML =
  '<header class="topbar"><span>Staff portal <span class="text-muted">/ Payments</span></span><div class="flex items-center gap-4"><span id="current-user"></span><button class="btn btn-ghost btn-sm" id="sign-out">Sign out</button></div></header>';
document.querySelectorAll('.sidebar nav a').forEach(link => {
  if (new URL(link.href).pathname === location.pathname) link.setAttribute('aria-current', 'page');
});
window.staffReady = PaymentAPI.session().then(session => {
  if (!session.roles.some(r => ['ROLE_STAFF','ROLE_ADMIN'].includes(r))) throw new Error('Staff access required.');
  document.querySelector('#current-user').textContent = session.username;
  return session;
});
document.querySelector('#sign-out').addEventListener('click', async event => {
  const button = event.currentTarget;
  button.disabled = true;
  try { await PaymentAPI.logout(); location.assign('/login.html'); }
  catch (error) { button.disabled = false; window.alert(error.message); }
});

// Small local SVG set for the icon hooks in the supplied pages.
const iconPaths = {
  'arrow-left':'M19 12H5m7-7-7 7 7 7', 'plus':'M12 5v14M5 12h14',
  'banknote':'M2 5h20v14H2zM6 9h.01M18 15h.01M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0',
  'credit-card':'M3 4h18v16H3zM3 9h18M7 15h4', 'clock':'M12 8v5l3 2M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0',
  'rotate-ccw':'M3 4v6h6M3 10a9 9 0 1 1 1 8', 'search':'m21 21-5-5M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0',
  'shield-alert':'m12 2 9 4v6c0 5-9 10-9 10S3 17 3 12V6zM12 8v5M12 17h.01',
  'shield-check':'m12 2 9 4v6c0 5-9 10-9 10S3 17 3 12V6zM8 12l3 3 5-6',
  'trash-2':'M3 6h18M9 6V3h6v3M5 6l1 15h12l1-15M10 10v7M14 10v7',
  'printer':'M6 9V3h12v6M6 18H3V9h18v9h-3M6 14h12v8H6z',
  'crown':'m2 6 5 4 5-7 5 7 5-4-3 14H5z', 'lock':'M5 10h14v12H5zM8 10V6a4 4 0 0 1 8 0v4', 'check':'m5 12 4 4L19 6'
};
document.querySelectorAll('[data-lucide]').forEach(element => {
  const path = iconPaths[element.dataset.lucide];
  if (!path) return;
  const svg = document.createElementNS('http://www.w3.org/2000/svg','svg');
  svg.setAttribute('viewBox','0 0 24 24'); svg.setAttribute('class','icon'); svg.setAttribute('aria-hidden','true');
  const shape = document.createElementNS(svg.namespaceURI,'path'); shape.setAttribute('d',path); svg.append(shape); element.replaceWith(svg);
});
