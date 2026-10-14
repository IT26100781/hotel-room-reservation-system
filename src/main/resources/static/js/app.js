const money = value => new Intl.NumberFormat('en-LK', {style:'currency',currency:'LKR'}).format(value);
function message(text, error=false) {
  const target = document.querySelector('#message');
  target.textContent = text;
  target.className = 'message' + (error ? ' error' : '');
}
function methodLabel(p) {
  if (p.method === 'CASH') return 'Cash';
  const brands = {VISA:'Visa', MASTERCARD:'Mastercard', AMEX:'Amex', OTHER:'Card'};
  return (brands[p.cardBrand] || 'Card') + (p.lastFour ? ' •••• ' + p.lastFour : '');
}
function set(id, value) { document.getElementById(id).textContent = value; }
function today() {
  const parts = new Intl.DateTimeFormat('en-GB', {timeZone:'Asia/Colombo',year:'numeric',month:'2-digit',day:'2-digit'}).formatToParts(new Date());
  const get = type => parts.find(p => p.type === type).value;
  return get('year') + '-' + get('month') + '-' + get('day');
}

async function historyPage() {
  let records = [], method = 'all', selectedId, returnFocus;
  const body = document.querySelector('#payments-table tbody');
  const search = document.querySelector('[data-filter-target]');
  const modal = document.querySelector('#delete-modal');
  const confirmDelete = document.querySelector('#confirm-delete');
  function closeModal() { modal.hidden = true; selectedId = undefined; if (returnFocus?.isConnected) returnFocus.focus(); }
  document.querySelectorAll('[data-modal-close]').forEach(b => b.addEventListener('click', closeModal));
  modal.addEventListener('click', e => { if (e.target === modal && !confirmDelete.disabled) closeModal(); });
  modal.addEventListener('keydown', e => {
    if (e.key === 'Escape' && !confirmDelete.disabled) closeModal();
    if (e.key === 'Tab') {
      const buttons = [...modal.querySelectorAll('button:not(:disabled)')];
      const first = buttons[0], last = buttons.at(-1);
      if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    }
  });
  confirmDelete.addEventListener('click', async () => {
    if (!selectedId) return;
    confirmDelete.disabled = true;
    try { await PaymentAPI.remove(selectedId); closeModal(); await load(); message('Payment deleted.'); }
    catch (error) { closeModal(); message(error.message, true); }
    finally { confirmDelete.disabled = false; }
  });
  function button(parent, text, style, action) {
    const b = document.createElement('button'); b.type = 'button'; b.className = 'btn btn-sm ' + style; b.textContent = text;
    b.addEventListener('click', async () => {
      b.disabled = true;
      try { await action(b); } catch (error) { message(error.message, true); }
      finally { b.disabled = false; }
    });
    parent.append(b);
  }
  function render() {
    const q = search.value.trim().toLowerCase();
    const filtered = records.filter(p => (method === 'all' || p.method.toLowerCase() === method)
      && (p.id + ' ' + p.bookingId + ' ' + p.guestName).toLowerCase().includes(q));
    body.replaceChildren();
    document.querySelector('[data-empty-for]').classList.toggle('hidden', filtered.length > 0);
    for (const p of filtered) {
      const row = body.insertRow(); row.dataset.item = ''; row.dataset.method = p.method.toLowerCase();
      const id = row.insertCell(); id.className = 'mono'; id.textContent = p.id.slice(0, 12) + '…'; id.title = p.id;
      const booking = row.insertCell(); booking.className = 'mono'; booking.textContent = p.bookingId;
      row.insertCell().textContent = p.guestName;
      row.insertCell().textContent = methodLabel(p);
      const amount = row.insertCell(); amount.className = 'text-right font-semibold'; amount.textContent = money(p.total);
      if (p.status === 'REFUNDED') amount.classList.add('line-through');
      const badge = document.createElement('span'); badge.className = 'badge badge-' + p.status.toLowerCase();
      badge.textContent = p.status[0] + p.status.slice(1).toLowerCase(); row.insertCell().append(badge);
      const actions = row.insertCell(); actions.className = 'table-actions';
      const invoice = document.createElement('a'); invoice.className = 'btn btn-ghost btn-sm'; invoice.textContent = 'Invoice';
      invoice.href = 'invoice.html?id=' + encodeURIComponent(p.id); actions.append(invoice);
      if (p.status !== 'REFUNDED') button(actions, p.status === 'PENDING' ? 'Mark paid' : 'Refund', 'btn-ghost', async () => {
        const next = p.status === 'PENDING' ? 'PAID' : 'REFUNDED';
        if (next === 'REFUNDED' && !confirm('Confirm the money has been refunded at the desk. Record the refund?')) return;
        await PaymentAPI.update(p.id, next); await load(); message('Payment status updated.');
      });
      button(actions, 'Delete', 'text-clay btn-ghost', async b => {
        selectedId = p.id; returnFocus = b; modal.querySelector('[data-fill="payment"]').textContent = p.id;
        modal.hidden = false; modal.querySelector('[data-modal-close]').focus();
      });
    }
  }
  async function load() {
    const [payments, stats] = await Promise.all([PaymentAPI.list(), PaymentAPI.summary()]);
    records = payments; render();
    set('collected', money(stats.collectedAmount)); set('collected-meta', stats.paidCount + ' paid payments');
    set('awaiting', stats.awaitingCount); set('awaiting-meta', 'active bookings, est. ' + money(stats.awaitingEstimate));
    set('refunded', money(stats.refundedAmount)); set('refunded-meta', stats.refundedCount + ' refunded transactions');
    set('refund-count', stats.refundNeeded.length ? stats.refundNeeded.length + ' need refund' : 'All clear');
    const list = document.querySelector('#refund-list'); list.replaceChildren();
    stats.refundNeeded.forEach(p => {
      const row = document.createElement('div'); row.className = 'refund-row';
      const label = document.createElement('span'); label.textContent = p.bookingId + ' · ' + p.guestName + ' · ' + money(p.total);
      row.append(label);
      button(row, 'Record refund', 'btn-danger', async () => {
        if (!confirm('Confirm that the money has already been refunded.')) return;
        await PaymentAPI.update(p.id, 'REFUNDED'); await load(); message('Refund recorded.');
      });
      list.append(row);
    });
  }
  search.addEventListener('input', render);
  document.querySelectorAll('[data-chip]').forEach(b => {
    b.setAttribute('aria-pressed', b.dataset.chip === method);
    b.addEventListener('click', () => {
      method = b.dataset.chip;
      document.querySelectorAll('[data-chip]').forEach(c => {
        c.classList.toggle('active', c === b); c.setAttribute('aria-pressed', c === b);
      });
      render();
    });
  });
  await load();
}

const percent = rate => new Intl.NumberFormat('en', {style:'percent', maximumFractionDigits:2}).format(rate);
function stayLabel(billing) {
  if (!billing?.checkIn || !billing?.checkOut) return 'Stay details unavailable';
  const format = value => new Intl.DateTimeFormat('en-GB', {day:'numeric',month:'short',year:'numeric',timeZone:'UTC'}).format(new Date(value));
  const nights = Math.round((new Date(billing.checkOut) - new Date(billing.checkIn)) / 86400000);
  return format(billing.checkIn) + ' – ' + format(billing.checkOut) + ' · ' + nights + (nights === 1 ? ' night' : ' nights');
}

async function recordPage() {
  const form = document.querySelector('#payment-form'), fields = document.querySelector('#payment-fields');
  const input = name => form.elements.namedItem(name);
  const out = (name, value) => { form.querySelector('[data-out="' + name + '"]').textContent = value; };
  const [settings, bookings] = await Promise.all([PaymentAPI.settings(), PaymentAPI.bookings()]);
  const picker = input('bookingId');
  picker.replaceChildren(new Option(bookings.length ? 'Select an unpaid booking…' : 'No unpaid active bookings', ''));
  bookings.forEach(b => picker.add(new Option(b.id + ' · ' + b.guestName + ' · ' + b.room, b.id)));
  input('paymentDate').value = today(); input('paymentDate').max = today();
  fields.disabled = !bookings.length;
  set('cash-rate-hint', percent(settings.cashDiscountRate) + ' cash discount');
  set('cash-label', 'Cash discount (' + percent(settings.cashDiscountRate) + ')');
  function preview() {
    const cash = input('method').value === 'cash';
    const b = bookings.find(b => b.id === picker.value);
    const rate = cash ? Number(settings.cashDiscountRate) : 0;
    document.querySelector('[data-show-when="card"]').hidden = cash;
    input('cardType').disabled = cash; input('last4').disabled = cash;
    form.querySelector('[data-vip-badge]').hidden = !(b?.billing?.vipDiscountRate > 0);
    out('guest', b?.guestName || '—'); out('room', b?.room || '—'); out('stay', b ? stayLabel(b.billing) : '—');
    out('vip-label', b?.billing ? 'VIP discount (' + percent(b.billing.vipDiscountRate) + ')' : 'VIP discount');
    if (!b) {
      ['total','combined','subtotal','vip','cash','formula'].forEach(key => out(key, '—'));
      return;
    }
    // Preview only. Java uses authoritative booking data and BigDecimal when saving.
    const subtotal = Number(b.billing?.roomTotal ?? b.amountDue), due = Number(b.amountDue);
    const total = Math.round((due * (1-rate) + Number.EPSILON) * 100) / 100;
    out('subtotal', money(subtotal)); out('vip', b.billing ? '− ' + money(subtotal-due) : 'Already included in booking amount');
    out('cash', '− ' + money(due-total)); out('total', money(total)); out('combined', percent(1-total/subtotal));
    out('formula', b.billing ? money(subtotal) + ' × (1 − ' + percent(b.billing.vipDiscountRate) + ') × (1 − ' + percent(rate) + ')' : money(due) + ' × (1 − ' + percent(rate) + ')');
  }
  form.addEventListener('input', preview); form.addEventListener('change', preview); preview();
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const data = new FormData(form), booking = bookings.find(b => b.id === data.get('bookingId'));
    if (!booking) return;
    fields.disabled = true; message('');
    try {
      const cash = data.get('method') === 'cash';
      const result = await PaymentAPI.create({
        bookingId:booking.id, amount:String(booking.amountDue), method:cash ? 'CASH' : 'CARD',
        status:data.get('status'), paymentDate:data.get('paymentDate'),
        cardBrand:cash ? '' : data.get('cardType'), lastFour:cash ? '' : data.get('last4')
      });
      location.assign('invoice.html?id=' + encodeURIComponent(result.id));
    } catch (error) {
      message(error.message + ' If the connection was interrupted, check payment history before trying again.', true);
      fields.disabled = false; preview();
    }
  });
}

async function invoicePage() {
  const id = new URLSearchParams(location.search).get('id');
  if (!id) throw new Error('Open an invoice from the Payments page.');
  const documentData = await PaymentAPI.invoice(id), p = documentData.payment, b = p.billing;
  const subtotal = Number(b?.roomTotal ?? p.amount);
  const vip = b ? subtotal - Number(p.amount) : 0;
  const nights = b?.checkIn && b?.checkOut ? Math.round((new Date(b.checkOut)-new Date(b.checkIn))/86400000) : 0;
  const type = {INVOICE:'Invoice', RECEIPT:'Receipt', REFUND_RECORD:'Refund record'}[documentData.documentType];
  document.title = type + ' ' + p.id + ' · The Orchid Estate';
  const values = {
    'document-type':type, 'document-number':documentData.documentNumber,
    'invoice-status':p.status, 'invoice-guest':p.guestName, 'invoice-booking':p.bookingId,
    'invoice-payment':p.id, 'invoice-date':new Intl.DateTimeFormat('en-GB', {day:'numeric',month:'short',year:'numeric',timeZone:'UTC'}).format(new Date(p.paymentDate)),
    'invoice-method':methodLabel(p), 'invoice-email':b?.guestEmail || '',
    'invoice-room':b?.room || 'Booking ' + p.bookingId, 'invoice-stay':stayLabel(b),
    'invoice-description':b?.room || 'Booking amount after any booking discounts',
    'invoice-method-note':p.status === 'PAID' ? 'Received at front desk' : p.status === 'PENDING' ? 'Awaiting payment' : 'Refund recorded at front desk',
    'invoice-nights':nights || '—', 'invoice-rate':nights ? money(subtotal/nights) : '—',
    'invoice-amount':money(subtotal), 'invoice-vip-label':'VIP discount (' + percent(b?.vipDiscountRate || 0) + ')',
    'invoice-vip':'− ' + money(vip), 'invoice-cash-label':'Cash discount (' + percent(p.discountRate) + ' of ' + money(p.amount) + ')',
    'invoice-discount':'− ' + money(p.discountAmount),
    'invoice-total':money(p.total), 'invoice-note':documentData.note,
    'invoice-total-label':(p.status === 'PAID' ? 'Total paid' : p.status === 'PENDING' ? 'Amount due' : 'Total refunded') + ' · ' +
      (b ? 'combined discount ' : 'cash discount ') + percent(1 - Number(p.total)/subtotal)
  };
  Object.entries(values).forEach(([id,value]) => set(id,value));
  document.querySelector('#invoice-vip-row').hidden = !b || !vip;
  document.querySelector('#invoice-vip-badge').hidden = !b || !vip;
  document.querySelector('#invoice-cash-row').hidden = p.method !== 'CASH';
  document.querySelector('#invoice-status').className = 'paid-stamp status-' + p.status.toLowerCase();
  document.querySelector('#invoice').hidden = false;
  document.querySelector('#print').disabled = false;
  document.querySelector('#print').addEventListener('click', () => window.print());
}

staffReady.then(async () => {
  if (document.body.dataset.page === 'history') await historyPage();
  if (document.body.dataset.page === 'record') await recordPage();
  if (document.body.dataset.page === 'invoice') await invoicePage();
}).catch(error => {
  if (error.status === 401) location.assign('/login.html');
  else message(error.message, true);
});
