<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  First-run setup: create the first admin account. Only reachable while no admin exists.
  Shown by StaffSetupServlet (GET /admin/setup), posts to POST /admin/setup.
  Form fields: name, email, password, confirmPassword
  Request attributes (optional):
    error - message to show if creating the admin failed
  The typed name and email are refilled from the submitted form (${param.name}, ${param.email}).
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Set up staff portal"/>
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
        <p class="mt-5 eyebrow no-line justify-center !text-brass-light">First-time setup</p>
        <h1 class="font-display text-5xl font-semibold mt-3">Create the first admin</h1>
        <p class="text-ivory/60 mt-2 text-sm">This account can add other staff and admins afterwards.</p>
      </div>

      <form method="post" action="${ctx}/admin/setup" class="mt-10 rounded-[22px] bg-paper text-ink p-8 shadow-2xl shadow-black/30 space-y-5">
        <c:if test="${not empty error}">
          <div class="alert alert-error"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
        </c:if>

        <div>
          <label class="label" for="name">Full name</label>
          <div class="input-icon"><i data-lucide="user"></i><input id="name" name="name" class="input" required autocomplete="name" value="<c:out value='${param.name}'/>"></div>
        </div>
        <div>
          <label class="label" for="email">Work email</label>
          <div class="input-icon"><i data-lucide="mail"></i><input id="email" name="email" type="email" class="input" placeholder="name@orchidestate.lk" required autocomplete="username" value="<c:out value='${param.email}'/>"></div>
        </div>
        <div>
          <label class="label" for="password">Password</label>
          <div class="input-icon"><i data-lucide="lock"></i><input id="password" name="password" type="password" class="input" minlength="8" required autocomplete="new-password"></div>
          <p class="hint">At least 8 characters, no spaces.</p>
        </div>
        <div>
          <label class="label" for="confirmPassword">Confirm password</label>
          <div class="input-icon"><i data-lucide="lock"></i><input id="confirmPassword" name="confirmPassword" type="password" class="input" required autocomplete="new-password" data-match="#password"></div>
          <p class="error-text hidden" data-match-msg>Passwords don't match.</p>
        </div>
        <button type="submit" class="btn btn-brass btn-lg btn-block"><i data-lucide="user-plus"></i>Create admin account</button>
      </form>
    </div>
  </main>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
