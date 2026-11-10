/* Card inputs are enabled only for the simulated card method. No card data is persisted here. */
(() => {
  const form = document.querySelector('[data-payment-calc]');
  if (!form) return;
  const fields = form.querySelector('[data-card-fields]');
  const expiry = form.querySelector('#expiry');
  expiry.addEventListener('input', () => {
    const digits = expiry.value.replace(/[^0-9]/g, '').slice(0, 4);
    expiry.value = digits.length > 2 ? digits.slice(0, 2) + '/' + digits.slice(2) : digits;
  });
  const update = () => {
    const card = form.querySelector('[name="method"]:checked')?.value === 'card';
    fields.disabled = !card;
    fields.querySelectorAll('input').forEach(input => { input.required = card; });
  };
  form.addEventListener('change', update);
  form.addEventListener('submit', () => { if (form.checkValidity()) form.querySelector('[type="submit"]').disabled = true; });
  window.addEventListener('pageshow', update);
  update();
})();
