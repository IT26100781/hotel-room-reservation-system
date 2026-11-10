<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Guest profile (GET /profile). Only for logged-in guests; they can only edit THEIR OWN account
  (the servlet takes the id from the session, never from the form or URL).
  Request attributes:
    guest         - the logged-in Guest, freshly loaded by the servlet (required)
                    uses guest.id, name, email, guestType ("VIP"/"REGULAR"), discount (e.g. 0.10)
    error         - message if saving personal details failed (optional)
    passwordError - message if changing the password failed (optional)
  URL parameter ?done=details|password shows a success message after a redirect.
  Forms on this page post to:
    POST /profile             (name, email)                                    - update details
    POST /profile/password    (currentPassword, newPassword, confirmPassword)  - change password
    POST /profile/deactivate  (no fields)                                      - soft delete own account
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Profile"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <c:set var="isVip" value="${guest.guestType == 'VIP'}"/>
  <c:set var="initials" value="${fn:toUpperCase(fn:substring(guest.name, 0, 1))}${fn:toUpperCase(fn:substring(fn:substringAfter(guest.name, ' '), 0, 1))}"/>

  <main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12 pb-20">
    <span class="eyebrow">Your account</span>
    <h1 class="font-display text-5xl font-semibold text-forest mt-4">Profile &amp; password</h1>

    <c:choose>
      <c:when test="${param.done == 'details'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Your details have been updated.</span></div></c:when>
      <c:when test="${param.done == 'password'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Your password has been changed.</span></div></c:when>
    </c:choose>

    <div class="mt-10 grid lg:grid-cols-[320px_1fr] gap-8 items-start">
      <!-- Identity card -->
      <aside class="card card-pad text-center lg:sticky lg:top-28">
        <span class="avatar avatar-lg avatar-brass mx-auto"><c:out value="${initials}"/></span>
        <h2 class="font-display text-3xl font-semibold text-forest mt-4"><c:out value="${guest.name}"/></h2>
        <p class="text-sm text-muted"><c:out value="${guest.email}"/></p>
        <c:choose>
          <c:when test="${isVip}"><span class="badge badge-vip no-dot mt-4"><i data-lucide="crown"></i>VIP guest</span></c:when>
          <c:otherwise><span class="badge badge-outline no-dot mt-4">Regular guest</span></c:otherwise>
        </c:choose>
        <dl class="mt-6 pt-6 border-t border-line grid grid-cols-2 gap-4 text-sm text-left">
          <div><dt class="label !mb-1">Guest ID</dt><dd class="mono"><c:out value="${guest.id}"/></dd></div>
          <div><dt class="label !mb-1">Discount</dt><dd class="font-semibold ${isVip ? 'text-brass' : ''}">${Math.round(guest.discount * 100)}%</dd></div>
        </dl>
      </aside>

      <div class="space-y-6">
        <!-- Personal details -->
        <form method="post" action="${ctx}/profile" class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest">Personal details</h2>
          <c:if test="${not empty error}">
            <div class="alert alert-error mt-4"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
          </c:if>
          <div class="mt-6 grid sm:grid-cols-2 gap-5">
            <div><label class="label" for="name">Full name</label><input id="name" name="name" class="input" maxlength="50" required value="<c:out value='${guest.name}'/>"></div>
            <div><label class="label" for="email">Email</label><input id="email" name="email" type="email" class="input" required value="<c:out value='${guest.email}'/>"></div>
          </div>
          <p class="hint mt-4 flex items-center gap-1.5"><i data-lucide="info" class="w-4 h-4"></i>VIP status is managed by our front desk team.</p>
          <div class="mt-6 flex justify-end"><button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save changes</button></div>
        </form>

        <!-- Password -->
        <form method="post" action="${ctx}/profile/password" class="card card-pad">
          <h2 class="font-display text-2xl font-semibold text-forest">Change password</h2>
          <c:if test="${not empty passwordError}">
            <div class="alert alert-error mt-4"><i data-lucide="shield-alert"></i><span><c:out value="${passwordError}"/></span></div>
          </c:if>
          <div class="mt-6 grid sm:grid-cols-3 gap-5">
            <div><label class="label" for="current">Current</label><input id="current" name="currentPassword" type="password" class="input" autocomplete="current-password" required></div>
            <div><label class="label" for="newpw">New</label><input id="newpw" name="newPassword" type="password" class="input" minlength="8" autocomplete="new-password" required></div>
            <div><label class="label" for="confirm">Confirm new</label><div><input id="confirm" name="confirmPassword" type="password" class="input" autocomplete="new-password" required data-match="#newpw"></div><p class="error-text hidden" data-match-msg>Passwords don't match.</p></div>
          </div>
          <div class="mt-6 flex justify-end"><button type="submit" class="btn btn-outline"><i data-lucide="lock"></i>Update password</button></div>
        </form>

        <!-- Danger zone -->
        <section class="card card-pad border-clay/30">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 class="font-display text-2xl font-semibold text-clay">Deactivate account</h2>
              <p class="text-sm text-muted mt-1">You won't be able to sign in. Your past bookings are kept for our records.</p>
            </div>
            <button type="button" class="btn btn-danger-outline" data-modal-open="deactivate-modal">Deactivate</button>
          </div>
        </section>
      </div>
    </div>
  </main>

  <div class="modal" id="deactivate-modal" role="dialog" aria-modal="true" aria-labelledby="deact-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="shield-alert"></i></span>
      <h2 id="deact-title" class="font-display text-3xl font-semibold text-forest mt-4">Deactivate your account?</h2>
      <p class="mt-2 text-sm text-ink/70">Upcoming bookings should be cancelled first. You'll be signed out straight away.</p>
      <form method="post" action="${ctx}/profile/deactivate" class="mt-6 flex justify-end gap-2">
        <button type="button" class="btn btn-ghost" data-modal-close>Keep my account</button>
        <button type="submit" class="btn btn-danger">Deactivate</button>
      </form>
    </div>
  </div>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
