<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Guest sign-in.  Shown by the guest login servlet (GET /login), posts to POST /login.
  Request attributes (all optional):
    error  - message to show after a failed login (e.g. "Invalid email or password")
    email  - the email that was typed, so the field is refilled (never refill the password)
  URL parameters:
    ?created=1     - account was just registered -> success message
    ?loggedOut=1   - guest just signed out        -> info message
    ?deactivated=1 - guest just deactivated their own account -> info message
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Sign in"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <main class="min-h-screen grid lg:grid-cols-2">
    <!-- Image side -->
    <div class="relative hidden lg:block p-6">
      <div class="arch h-full relative">
        <img class="img-cover" src="https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?auto=format&fit=crop&w=1200&q=80" alt="Balcony overlooking the lake and mountains">
        <div class="absolute inset-0 bg-gradient-to-t from-forest-900/80 via-forest-900/10 to-transparent"></div>
        <figure class="absolute bottom-12 left-12 right-12 text-ivory">
          <blockquote class="font-display text-4xl leading-tight">"The kind of quiet you only find above the clouds."</blockquote>
          <figcaption class="mt-4 text-sm text-ivory/70">Sarah C., Deluxe Room</figcaption>
        </figure>
      </div>
    </div>

    <!-- Form side -->
    <div class="flex flex-col px-6 sm:px-12 py-10">
      <a href="${ctx}/" class="inline-flex items-center gap-3 self-start">
        <img src="${ctx}/img/favicon.svg" class="w-10 h-10" alt="">
        <span class="font-display text-2xl font-semibold text-forest">The Orchid Estate</span>
      </a>

      <div class="my-auto w-full max-w-md mx-auto py-12">
        <span class="eyebrow">Welcome back</span>
        <h1 class="font-display text-5xl font-semibold text-forest mt-4">Sign in</h1>
        <p class="text-muted mt-2">Manage your bookings and leave reviews.</p>

        <c:if test="${param.created == '1'}">
          <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Your account is ready. Please sign in.</span></div>
        </c:if>
        <c:if test="${param.loggedOut == '1'}">
          <div class="alert alert-info mt-6"><i data-lucide="log-out"></i><span>You have been signed out.</span></div>
        </c:if>
        <c:if test="${param.deactivated == '1'}">
          <div class="alert alert-info mt-6"><i data-lucide="ban"></i><span>Your account has been deactivated. Contact the front desk to reactivate it.</span></div>
        </c:if>
        <c:if test="${not empty error}">
          <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
        </c:if>

        <form method="post" action="${ctx}/login" class="mt-8 space-y-5">
          <div>
            <label class="label" for="email">Email</label>
            <div class="input-icon"><i data-lucide="mail"></i><input id="email" name="email" type="email" class="input" placeholder="you@example.com" autocomplete="email" required value="<c:out value='${email}'/>"></div>
          </div>
          <div>
            <label class="label" for="password">Password</label>
            <div class="input-icon relative">
              <i data-lucide="lock"></i>
              <input id="password" name="password" type="password" class="input pr-12" placeholder="••••••••" autocomplete="current-password" required>
              <button type="button" class="absolute right-3 top-1/2 -translate-y-1/2 p-1.5 text-muted hover:text-forest" data-password-toggle aria-label="Show password">
                <i data-lucide="eye" data-eye="on" class="w-4 h-4"></i><i data-lucide="eye-off" data-eye="off" class="w-4 h-4 hidden"></i>
              </button>
            </div>
          </div>
          <button type="submit" class="btn btn-primary btn-lg btn-block">Sign in <i data-lucide="arrow-right"></i></button>
        </form>

        <p class="mt-8 text-sm text-center text-muted">New to the Estate? <a href="${ctx}/register" class="font-semibold text-forest hover:text-brass">Create an account</a></p>
        <div class="ornament my-8"><span></span></div>
        <a href="${ctx}/admin/login" class="flex items-center justify-between rounded-2xl border border-line px-5 py-4 hover:border-brass transition">
          <span class="flex items-center gap-3"><span class="stat-icon"><i data-lucide="key-round"></i></span><span><span class="block font-semibold text-forest text-sm">Staff member?</span><span class="block text-xs text-muted">Sign in to the staff portal</span></span></span>
          <i data-lucide="arrow-right" class="w-4 h-4 text-muted"></i>
        </a>
      </div>
    </div>
  </main>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
