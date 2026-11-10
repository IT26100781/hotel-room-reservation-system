<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Add or edit a staff member (GET /admin/staff/form, or ?id=S02 to edit). Posts to POST /admin/staff/save.
  Request attributes:
    staff - the Staff being edited; leave it out (null) to show the "Add" form
    error - message to show if saving failed (optional)
  Form fields:
    add  : name, email, password, role (STAFF | ADMIN)
    edit : id (hidden), name, email          (role is changed from the list page, password is not shown)
  After a failed save, forward back here: the typed values are refilled from the request parameters.
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="isEdit" value="${not empty staff}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit staff member' : 'Add staff member'}"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="staff"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-4xl">
      <a href="${ctx}/admin/staff" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to staff</a>
      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">${isEdit ? 'Edit staff member' : 'Add a staff member'}</h1>
      <p class="text-muted mt-2">
        <c:choose>
          <c:when test="${isEdit}">Staff ID <span class="mono"><c:out value="${staff.id}"/></span> · role and status are changed from the staff list.</c:when>
          <c:otherwise>They'll use these details to sign in to the staff portal. The ID is generated automatically.</c:otherwise>
        </c:choose>
      </p>

      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <form method="post" action="${ctx}/admin/staff/save" class="card card-pad mt-8 space-y-8">
        <c:if test="${isEdit}"><input type="hidden" name="id" value="<c:out value='${staff.id}'/>"></c:if>

        <div class="grid sm:grid-cols-2 gap-5">
          <div>
            <label class="label" for="name">Full name</label>
            <div class="input-icon"><i data-lucide="user"></i>
              <input id="name" name="name" class="input" required maxlength="50"
                     value="<c:out value='${not empty param.name ? param.name : staff.name}'/>">
            </div>
          </div>
          <div>
            <label class="label" for="email">Work email</label>
            <div class="input-icon"><i data-lucide="mail"></i>
              <input id="email" name="email" type="email" class="input" placeholder="name@orchidestate.lk" required
                     value="<c:out value='${not empty param.email ? param.email : staff.email}'/>">
            </div>
          </div>
          <c:if test="${not isEdit}">
            <div class="sm:col-span-2">
              <label class="label" for="password">Temporary password</label>
              <div class="input-icon"><i data-lucide="lock"></i><input id="password" name="password" type="password" class="input" minlength="8" required autocomplete="new-password"></div>
              <p class="hint">At least 8 characters, no spaces. Ask them to change it after their first sign-in.</p>
            </div>
          </c:if>
        </div>

        <c:if test="${not isEdit}">
          <c:set var="chosenRole" value="${empty param.role ? 'STAFF' : param.role}"/>
          <fieldset>
            <legend class="label">Role</legend>
            <div class="grid sm:grid-cols-2 gap-3">
              <label class="choice">
                <input type="radio" name="role" value="STAFF" ${chosenRole == 'STAFF' ? 'checked' : ''}>
                <span class="choice-body block">
                  <span class="badge badge-staff no-dot"><i data-lucide="concierge-bell"></i>Staff</span>
                  <span class="block text-sm text-ink/70 mt-3">Front desk: check-in, payments, bookings and guests.</span>
                </span>
              </label>
              <c:if test="${me.canManageAdminUsers()}">
                <label class="choice">
                  <input type="radio" name="role" value="ADMIN" ${chosenRole == 'ADMIN' ? 'checked' : ''}>
                  <span class="choice-body block">
                    <span class="badge badge-admin no-dot"><i data-lucide="shield-check"></i>Admin</span>
                    <span class="block text-sm text-ink/70 mt-3">Everything Staff can do, plus rooms, staff accounts and reviews.</span>
                  </span>
                </label>
              </c:if>
            </div>
          </fieldset>
        </c:if>

        <div class="flex justify-end gap-2 pt-2 border-t border-line">
          <a href="${ctx}/admin/staff" class="btn btn-ghost">Cancel</a>
          <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>${isEdit ? 'Save changes' : 'Add staff member'}</button>
        </div>
      </form>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
