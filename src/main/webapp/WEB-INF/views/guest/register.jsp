<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Guest registration.  Shown by the guest register servlet (GET /register), posts to POST /register.
  Form fields sent: name, email, password, confirmPassword
  Request attributes (all optional, set when registration fails):
    error - message to show (e.g. "Email already exists")
    name  - the name that was typed, so the field is refilled
    email - the email that was typed, so the field is refilled (never refill passwords)
  On success the servlet should redirect to /login?created=1
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Create account"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <main class="min-h-screen grid lg:grid-cols-2">
    <div class="flex flex-col px-6 sm:px-12 py-10 order-2 lg:order-1">
      <a href="${ctx}/" class="inline-flex items-center gap-3 self-start">
        <img src="${ctx}/img/favicon.svg" class="w-10 h-10" alt="">
        <span class="font-display text-2xl font-semibold text-forest">The Orchid Estate</span>
      </a>

      <div class="my-auto w-full max-w-md mx-auto py-12">
        <span class="eyebrow">Join us</span>
        <h1 class="font-display text-5xl font-semibold text-forest mt-4">Create your account</h1>
        <p class="text-muted mt-2">Book in seconds, manage your stays and leave verified reviews.</p>

        <c:if test="${not empty error}">
          <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
        </c:if>

        <form method="post" action="${ctx}/register" class="mt-8 space-y-5">
          <div>
            <label class="label" for="name">Full name</label>
            <div class="input-icon"><i data-lucide="user"></i><input id="name" name="name" class="input" placeholder="Nethmi Perera" autocomplete="name" maxlength="50" required value="<c:out value='${name}'/>"></div>
          </div>
          <div>
            <label class="label" for="email">Email</label>
            <div class="input-icon"><i data-lucide="mail"></i><input id="email" name="email" type="email" class="input" placeholder="you@example.com" autocomplete="email" required value="<c:out value='${email}'/>"></div>
          </div>
          <div>
            <label class="label" for="password">Password</label>
            <div class="input-icon relative">
              <i data-lucide="lock"></i>
              <input id="password" name="password" type="password" class="input pr-12" minlength="8" autocomplete="new-password" required data-strength="#pw-strength">
              <button type="button" class="absolute right-3 top-1/2 -translate-y-1/2 p-1.5 text-muted hover:text-forest" data-password-toggle aria-label="Show password">
                <i data-lucide="eye" data-eye="on" class="w-4 h-4"></i><i data-lucide="eye-off" data-eye="off" class="w-4 h-4 hidden"></i>
              </button>
            </div>
            <div id="pw-strength" class="mt-2 flex items-center gap-3">
              <div class="bar flex-1"><span data-bar style="width:0"></span></div>
              <span class="text-xs font-semibold text-muted w-16 text-right" data-label></span>
            </div>
            <p class="hint">At least 8 characters, no spaces. Mix letters, numbers and a symbol.</p>
          </div>
          <div>
            <label class="label" for="confirm">Confirm password</label>
            <div class="input-icon"><i data-lucide="lock"></i><input id="confirm" name="confirmPassword" type="password" class="input" autocomplete="new-password" required data-match="#password"></div>
            <p class="error-text hidden" data-match-msg>Passwords don't match.</p>
          </div>
          <button type="submit" class="btn btn-primary btn-lg btn-block">Create account <i data-lucide="arrow-right"></i></button>
        </form>

        <p class="mt-8 text-sm text-center text-muted">Already have an account? <a href="${ctx}/login" class="font-semibold text-forest hover:text-brass">Sign in</a></p>
      </div>
    </div>

    <div class="relative hidden lg:block p-6 order-1 lg:order-2">
      <div class="arch h-full relative">
        <img class="img-cover" src="https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80" alt="Estate pool and bungalows">
        <div class="absolute inset-0 bg-gradient-to-t from-forest-900/85 via-forest-900/10 to-transparent"></div>
        <div class="absolute bottom-12 left-12 right-12 text-ivory">
          <p class="eyebrow !text-brass-light">Member benefits</p>
          <ul class="mt-5 space-y-3 text-lg">
            <li class="flex items-center gap-3"><i data-lucide="calendar-check" class="w-5 h-5 text-brass-light"></i>Book and cancel online</li>
            <li class="flex items-center gap-3"><i data-lucide="badge-check" class="w-5 h-5 text-brass-light"></i>Verified reviews after your stay</li>
            <li class="flex items-center gap-3"><i data-lucide="crown" class="w-5 h-5 text-brass-light"></i>Returning guests can become VIP (10% off)</li>
          </ul>
        </div>
      </div>
    </div>
  </main>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
