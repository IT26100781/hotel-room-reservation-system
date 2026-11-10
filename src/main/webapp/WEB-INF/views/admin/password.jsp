<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Change own password (GET /admin/password). Shown by StaffPasswordServlet, posts to POST /admin/password.
  StaffAuthFilter also sends staff here while they still have a temporary password
  (me.mustChangePassword is true). This page has no sidebar because the other pages are locked until then.
  Form fields: currentPassword, newPassword, confirmPassword
  Request attributes (optional):
    error - message to show if changing the password failed
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Change password"/>
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
        <c:choose>
          <c:when test="${me.mustChangePassword}">
            <h1 class="font-display text-5xl font-semibold mt-3">Set your password</h1>
            <p class="text-ivory/60 mt-2 text-sm">You're using a temporary password from an administrator. Choose your own to continue.</p>
          </c:when>
          <c:otherwise>
            <h1 class="font-display text-5xl font-semibold mt-3">Change password</h1>
            <p class="text-ivory/60 mt-2 text-sm">Enter your current password, then choose a new one.</p>
          </c:otherwise>
        </c:choose>
      </div>

      <form method="post" action="${ctx}/admin/password" class="mt-10 rounded-[22px] bg-paper text-ink p-8 shadow-2xl shadow-black/30 space-y-5">
        <c:if test="${not empty error}">
          <div class="alert alert-error"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
        </c:if>

        <div>
          <label class="label" for="currentPassword">${me.mustChangePassword ? 'Temporary password' : 'Current password'}</label>
          <div class="input-icon"><i data-lucide="lock"></i><input id="currentPassword" name="currentPassword" type="password" class="input" required autocomplete="current-password"></div>
        </div>
        <div>
          <label class="label" for="newPassword">New password</label>
          <div class="input-icon"><i data-lucide="key-round"></i><input id="newPassword" name="newPassword" type="password" class="input" minlength="8" required autocomplete="new-password"></div>
          <p class="hint">At least 8 characters, no spaces.</p>
        </div>
        <div>
          <label class="label" for="confirmPassword">Confirm new password</label>
          <div class="input-icon"><i data-lucide="key-round"></i><input id="confirmPassword" name="confirmPassword" type="password" class="input" required autocomplete="new-password" data-match="#newPassword"></div>
          <p class="error-text hidden" data-match-msg>Passwords don't match.</p>
        </div>
        <button type="submit" class="btn btn-brass btn-lg btn-block"><i data-lucide="check"></i>Save new password</button>
      </form>

      <c:choose>
        <c:when test="${me.mustChangePassword}">
          <form method="post" action="${ctx}/admin/logout" class="mt-8 text-center">
            <button type="submit" class="inline-flex items-center gap-2 text-sm text-ivory/60 hover:text-ivory"><i data-lucide="log-out" class="w-4 h-4"></i>Sign out</button>
          </form>
        </c:when>
        <c:otherwise>
          <a href="${ctx}/admin" class="mt-8 flex items-center justify-center gap-2 text-sm text-ivory/60 hover:text-ivory"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to dashboard</a>
        </c:otherwise>
      </c:choose>
    </div>
  </main>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
