document.querySelector('#login-form').addEventListener('submit', async event => {
  event.preventDefault();
  const button = document.querySelector('#sign-in'), message = document.querySelector('#message');
  button.disabled = true; message.textContent = '';
  try {
    await PaymentAPI.login(document.querySelector('#username').value, document.querySelector('#password').value);
    const session = await PaymentAPI.session();
    if (!session.roles.some(r => ['ROLE_STAFF','ROLE_ADMIN'].includes(r))) {
      await PaymentAPI.logout(); throw new Error('Payments are available to staff only.');
    }
    location.assign('/staff/payments/list.html');
  } catch (error) { message.textContent = error.message; message.className = 'message error'; }
  finally { button.disabled = false; }
});
