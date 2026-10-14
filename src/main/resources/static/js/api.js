/* Same-origin session API. No passwords or role claims are stored in localStorage. */
window.PaymentAPI = (() => {
  let csrf;
  async function request(path, options = {}) {
    const method = options.method || 'GET';
    const headers = { Accept: 'application/json', ...options.headers };
    if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
      if (!csrf) csrf = await request('/api/csrf');
      headers[csrf.headerName] = csrf.token;
    }
    const response = await fetch(path, { ...options, method, headers, credentials: 'same-origin' });
    if (response.status === 204) return null;
    const data = await response.json().catch(() => ({ message: 'Unexpected server response' }));
    if (!response.ok) {
      const extra = data.errors ? ' ' + Object.entries(data.errors).map(([k,v]) => k + ': ' + v).join('; ') : '';
      const error = new Error((data.message || 'Request failed') + extra);
      error.status = response.status;
      if ([401, 403].includes(response.status)) csrf = undefined;
      throw error; // Do not automatically retry payment writes.
    }
    return data;
  }
  const json = (method, body) => ({ method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(body) });
  return {
    async login(username, password) {
      await request('/api/login', { method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'},
        body:new URLSearchParams({username, password}) });
      csrf = await request('/api/csrf');
    },
    async logout() { await request('/api/logout', { method:'POST' }); csrf = undefined; },
    session: () => request('/api/session'),
    settings: () => request('/api/payments/settings'),
    summary: () => request('/api/payments/summary'),
    bookings: () => request('/api/payments/bookings'),
    list: (filters={}) => request('/api/payments?' + new URLSearchParams(Object.entries(filters).filter(([,v]) => v))),
    create: payment => request('/api/payments', json('POST', payment)),
    update: (id, status) => request('/api/payments/' + encodeURIComponent(id) + '/status', json('PATCH', {status})),
    remove: id => request('/api/payments/' + encodeURIComponent(id), {method:'DELETE'}),
    invoice: id => request('/api/payments/' + encodeURIComponent(id) + '/invoice')
  };
})();
