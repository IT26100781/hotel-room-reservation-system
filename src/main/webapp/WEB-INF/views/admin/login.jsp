<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff portal sign-in.  Shown by StaffLoginServlet (GET /admin/login), posts to POST /admin/login.
  Request attributes (all optional):
    error  - message to show after a failed login (e.g. "Invalid email or password")
    email  - the email that was typed, so the field is refilled (never refill the password)
  URL parameters:
    ?created=1   - first admin was just created  -> success message
    ?loggedOut=1 - user just signed out           -> info message
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Staff portal"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body class="panel-dark">
  <main class="min-h-screen relative grid place-items-center px-4 py-12">
    <div class="absolute inset-0">
      <img class="img-cover opacity-30" src="https://images.unsplash.com/photo-1625244724120-1fd1d34d00f6?auto=format&fit=crop&w=1600&q=70" alt="">
      <div class="absolute inset-0 bg-gradient-to-b from-forest-900/70 via-forest-900/85 to-forest-900"></div>
    </div>

    <div class="relative w-full max-w-md">
      <div class="text-center">
        <img src="${ctx}/img/favicon.svg" class="w-14 h-14 mx-auto" alt="">
        <p class="mt-5 eyebrow no-line justify-center !text-brass-light">Staff portal</p>
        <h1 class="font-display text-5xl font-semibold mt-3">The Orchid Estate</h1>
        <p class="text-ivory/60 mt-2 text-sm">Front desk and administration</p>
      </div>

      <form method="post" action="${ctx}/admin/login" class="mt-10 rounded-[22px] bg-paper text-ink p-8 shadow-2xl shadow-black/30 space-y-5">
        <c:if test="${param.created == '1'}">
          <div class="alert alert-success"><i data-lucide="circle-check"></i><span>Admin account created. Please sign in.</span></div>
        </c:if>
        <c:if test="${param.loggedOut == '1'}">
          <div class="alert alert-info"><i data-lucide="log-out"></i><span>You have been signed out.</span></div>
        </c:if>
        <c:if test="${not empty error}">
          <div class="alert alert-error"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
        </c:if>

        <div>
          <label class="label" for="email">Staff email</label>
          <div class="input-icon"><i data-lucide="mail"></i><input id="email" name="email" type="email" class="input" placeholder="name@orchidestate.lk" autocomplete="username" required value="<c:out value='${email}'/>"></div>
        </div>
        <div>
          <label class="label" for="password">Password</label>
          <div class="input-icon relative">
            <i data-lucide="lock"></i>
            <input id="password" name="password" type="password" class="input pr-12" autocomplete="current-password" required>
            <button type="button" class="absolute right-3 top-1/2 -translate-y-1/2 p-1.5 text-muted hover:text-forest" data-password-toggle aria-label="Show password">
              <i data-lucide="eye" data-eye="on" class="w-4 h-4"></i><i data-lucide="eye-off" data-eye="off" class="w-4 h-4 hidden"></i>
            </button>
          </div>
        </div>
        <button type="submit" class="btn btn-primary btn-lg btn-block"><i data-lucide="key-round"></i>Sign in to portal</button>
        <p class="text-xs text-muted text-center flex items-center justify-center gap-1.5"><i data-lucide="shield-check" class="w-4 h-4 text-sage"></i>Authorised staff only. Accounts are created by an administrator.</p>
      </form>

      <a href="${ctx}/" class="mt-8 flex items-center justify-center gap-2 text-sm text-ivory/60 hover:text-ivory"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to the guest website</a>
    </div>
  </main>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
