<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff accounts list (GET /admin/staff). Shown by the staff list servlet.
  Request attributes:
    staffList - List<Staff> to show (required)
    error     - message to show if an action failed (optional)
  URL parameter ?done=added|updated|status|role shows a success message after a redirect.
  Actions on this page post to:
    POST /admin/staff/status  (id, active=true|false)  - activate / deactivate
    POST /admin/staff/role    (id)                     - switch between Staff and Admin
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Staff accounts"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="staff"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Administration</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Staff accounts</h1>
          <p class="text-muted mt-2">Who can sign in to the portal, and what they're allowed to do.</p>
        </div>
        <c:if test="${me.canManageStaffUsers()}">
          <a href="${ctx}/admin/staff/form" class="btn btn-primary"><i data-lucide="user-plus"></i>Add staff member</a>
        </c:if>
      </div>

      <c:choose>
        <c:when test="${param.done == 'added'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Staff member added.</span></div></c:when>
        <c:when test="${param.done == 'updated'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Details updated.</span></div></c:when>
        <c:when test="${param.done == 'status'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Account status changed.</span></div></c:when>
        <c:when test="${param.done == 'role'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Role changed.</span></div></c:when>
      </c:choose>
      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <!-- Role explainer -->
      <div class="mt-8 grid md:grid-cols-2 gap-4">
        <div class="card card-pad">
          <span class="badge badge-staff no-dot"><i data-lucide="concierge-bell"></i>Staff</span>
          <ul class="mt-4 grid grid-cols-2 gap-2 text-sm text-ink/75">
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-sage"></i>Check guests in</li>
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-sage"></i>Record payments</li>
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-sage"></i>Manage any booking</li>
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-sage"></i>Manage guests</li>
          </ul>
        </div>
        <div class="card card-pad">
          <div class="flex items-center gap-3"><span class="badge badge-admin no-dot"><i data-lucide="shield-check"></i>Admin</span><span class="text-xs text-muted">Everything Staff can do, plus</span></div>
          <ul class="mt-4 grid grid-cols-2 gap-2 text-sm text-ink/75">
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-brass"></i>Manage rooms</li>
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-brass"></i>Manage staff</li>
            <li class="flex items-center gap-2"><i data-lucide="check" class="w-4 h-4 text-brass"></i>Moderate reviews</li>
          </ul>
        </div>
      </div>

      <div class="card mt-8">
        <div class="p-4 sm:p-5 flex flex-col md:flex-row gap-3 md:items-center justify-between border-b border-line">
          <div class="flex flex-wrap gap-2" data-chip-filter="#staff-table" data-chip-attr="role">
            <button type="button" class="chip active" data-chip="all">Everyone</button>
            <button type="button" class="chip" data-chip="admin">Admins</button>
            <button type="button" class="chip" data-chip="staff">Staff</button>
            <button type="button" class="chip" data-chip="inactive">Inactive</button>
          </div>
          <div class="input-icon md:w-72"><i data-lucide="search"></i><input class="input !py-2.5" placeholder="Search name, email or ID…" data-filter-target="#staff-table"></div>
        </div>
        <div class="table-wrap">
          <table class="table" id="staff-table">
            <thead><tr><th>Name</th><th>ID</th><th>Role</th><th>Status</th><th class="text-right">Actions</th></tr></thead>
            <tbody>
              <c:forEach var="s" items="${staffList}">
                <c:set var="isMe" value="${s.id == me.id}"/>
                <c:set var="initials" value="${fn:toUpperCase(fn:substring(s.name, 0, 1))}${fn:toUpperCase(fn:substring(fn:substringAfter(s.name, ' '), 0, 1))}"/>
                <tr data-item data-role="${fn:toLowerCase(s.role)}${s.active ? '' : ' inactive'}" class="${s.active ? '' : 'text-ink/55'}">
                  <td>
                    <div class="flex items-center gap-3">
                      <span class="avatar ${s.role == 'ADMIN' ? 'avatar-brass' : ''} ${s.active ? '' : 'opacity-60'}"><c:out value="${initials}"/></span>
                      <div>
                        <p class="font-semibold"><c:out value="${s.name}"/><c:if test="${isMe}"> <span class="text-xs text-muted font-normal">(you)</span></c:if></p>
                        <p class="text-xs text-muted"><c:out value="${s.email}"/></p>
                      </div>
                    </div>
                  </td>
                  <td class="mono"><c:out value="${s.id}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${s.role == 'ADMIN'}"><span class="badge badge-admin no-dot">Admin</span></c:when>
                      <c:otherwise><span class="badge badge-staff no-dot">Staff</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${s.active}"><span class="badge badge-active">Active</span></c:when>
                      <c:otherwise><span class="badge badge-retired">Inactive</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-right whitespace-nowrap">
                    <c:set var="canManageRow" value="${s.role == 'ADMIN' ? me.canManageAdminUsers() : me.canManageStaffUsers()}"/>
                    <c:if test="${canManageRow}">
                      <a href="${ctx}/admin/staff/form?id=${fn:escapeXml(s.id)}" class="btn btn-ghost btn-sm"><i data-lucide="pencil"></i>Edit</a>
                      <c:if test="${me.canManageAdminUsers()}">
                        <button type="button" class="btn btn-ghost btn-sm" data-modal-open="role-modal"
                                data-staff-id="${fn:escapeXml(s.id)}" data-staff-name="${fn:escapeXml(s.name)}"
                                data-new-role="${s.role == 'ADMIN' ? 'Staff' : 'Admin'}">
                          <i data-lucide="${s.role == 'ADMIN' ? 'arrow-down' : 'arrow-up'}"></i>${s.role == 'ADMIN' ? 'Make staff' : 'Make admin'}
                        </button>
                      </c:if>
                      <c:if test="${not isMe}">
                        <c:choose>
                          <c:when test="${s.active}">
                            <button type="button" class="btn btn-ghost btn-sm text-clay" data-modal-open="status-modal"
                                    data-staff-id="${fn:escapeXml(s.id)}" data-staff-name="${fn:escapeXml(s.name)}">
                              <i data-lucide="ban"></i>Deactivate
                            </button>
                          </c:when>
                          <c:otherwise>
                            <form method="post" action="${ctx}/admin/staff/status" class="inline">
                              <input type="hidden" name="id" value="${fn:escapeXml(s.id)}">
                              <input type="hidden" name="active" value="true">
                              <button type="submit" class="btn btn-ghost btn-sm"><i data-lucide="rotate-ccw"></i>Activate</button>
                            </form>
                          </c:otherwise>
                        </c:choose>
                      </c:if>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <c:if test="${empty staffList}">
          <div class="p-10 text-center text-muted">No staff accounts yet.</div>
        </c:if>
        <div class="hidden p-10 text-center text-muted" data-empty-for="#staff-table">No staff match these filters.</div>
      </div>
    </main>
  </div>

  <!-- Deactivate confirmation -->
  <div class="modal" id="status-modal" role="dialog" aria-modal="true" aria-labelledby="status-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="ban"></i></span>
      <h2 id="status-title" class="font-display text-3xl font-semibold text-forest mt-4">Deactivate <span data-fill="staffName">this account</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">They won't be able to sign in to the staff portal until the account is activated again.</p>
      <form method="post" action="${ctx}/admin/staff/status" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="id" data-fill="staffId">
        <input type="hidden" name="active" value="false">
        <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-danger">Deactivate</button>
      </form>
    </div>
  </div>

  <!-- Role change confirmation -->
  <div class="modal" id="role-modal" role="dialog" aria-modal="true" aria-labelledby="role-title">
    <div class="modal-box">
      <span class="stat-icon"><i data-lucide="user-cog"></i></span>
      <h2 id="role-title" class="font-display text-3xl font-semibold text-forest mt-4">Change role?</h2>
      <p class="mt-2 text-sm text-ink/70"><span class="font-semibold" data-fill="staffName"></span> will become <span class="font-semibold" data-fill="newRole"></span>. Their ID, email and password stay the same.</p>
      <form method="post" action="${ctx}/admin/staff/role" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="id" data-fill="staffId">
        <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Change role</button>
      </form>
    </div>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
