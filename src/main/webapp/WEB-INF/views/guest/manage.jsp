<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Staff view of all guests (GET /admin/guests). Protected by StaffAuthFilter because it is under /admin/*;
  the servlet must still check me.canViewGuestUsers().
  Request attributes:
    guestList - List<Guest> to show (required)
                uses g.id, name, email, guestType ("VIP"/"REGULAR"), active (isActive())
    error     - message to show if an action failed (optional)
  URL parameter ?done=updated|vip|status shows a success message after a redirect.
  Actions (only shown when me.canManageGuestUsers()) post to:
    POST /admin/guests/update  (id, name, email)        - edit details
    POST /admin/guests/vip     (id, vip=true|false)     - make VIP / make regular
    POST /admin/guests/status  (id, active=true|false)  - deactivate / reactivate
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Guests"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="guests"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>

  <%-- Summary counts for the tiles --%>
  <c:set var="vipCount" value="0"/>
  <c:set var="inactiveCount" value="0"/>
  <c:forEach var="g" items="${guestList}">
    <c:if test="${g.guestType == 'VIP'}"><c:set var="vipCount" value="${vipCount + 1}"/></c:if>
    <c:if test="${not g.active}"><c:set var="inactiveCount" value="${inactiveCount + 1}"/></c:if>
  </c:forEach>
  <c:set var="canManage" value="${me.canManageGuestUsers()}"/>

  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <span class="eyebrow">Front desk</span>
      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Guests</h1>
      <p class="text-muted mt-2">Look up guests, update details and grant VIP status.</p>

      <c:choose>
        <c:when test="${param.done == 'updated'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Guest details updated.</span></div></c:when>
        <c:when test="${param.done == 'vip'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>VIP status changed.</span></div></c:when>
        <c:when test="${param.done == 'status'}"><div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Account status changed.</span></div></c:when>
      </c:choose>
      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <div class="mt-8 grid sm:grid-cols-3 gap-4">
        <div class="stat flex items-start justify-between"><div><p class="stat-label">Registered</p><p class="stat-value mt-2">${fn:length(guestList)}</p></div><span class="stat-icon"><i data-lucide="users"></i></span></div>
        <div class="stat flex items-start justify-between"><div><p class="stat-label">VIP guests</p><p class="stat-value mt-2">${vipCount}</p></div><span class="stat-icon"><i data-lucide="crown"></i></span></div>
        <div class="stat flex items-start justify-between"><div><p class="stat-label">Deactivated</p><p class="stat-value mt-2 !text-muted">${inactiveCount}</p></div><span class="stat-icon !bg-sand !text-muted"><i data-lucide="ban"></i></span></div>
      </div>

      <div class="card mt-8">
        <div class="p-4 sm:p-5 flex flex-col md:flex-row gap-3 md:items-center justify-between border-b border-line">
          <div class="flex flex-wrap gap-2" data-chip-filter="#guests-table" data-chip-attr="tier">
            <button type="button" class="chip active" data-chip="all">Everyone</button>
            <button type="button" class="chip" data-chip="vip"><i data-lucide="crown" class="w-4 h-4"></i>VIP</button>
            <button type="button" class="chip" data-chip="regular">Regular</button>
            <button type="button" class="chip" data-chip="inactive">Deactivated</button>
          </div>
          <div class="input-icon md:w-72"><i data-lucide="search"></i><input class="input !py-2.5" placeholder="Search name, email or ID…" data-filter-target="#guests-table"></div>
        </div>
        <div class="table-wrap">
          <table class="table" id="guests-table">
            <thead><tr><th>Guest</th><th>ID</th><th>Type</th><th>Status</th><th class="text-right">Actions</th></tr></thead>
            <tbody>
              <c:forEach var="g" items="${guestList}">
                <c:set var="gVip" value="${g.guestType == 'VIP'}"/>
                <c:set var="initials" value="${fn:toUpperCase(fn:substring(g.name, 0, 1))}${fn:toUpperCase(fn:substring(fn:substringAfter(g.name, ' '), 0, 1))}"/>
                <tr data-item data-tier="${gVip ? 'vip' : 'regular'}${g.active ? '' : ' inactive'}" class="${g.active ? '' : 'text-ink/55'}">
                  <td>
                    <div class="flex items-center gap-3">
                      <span class="avatar ${gVip ? 'avatar-brass' : ''} ${g.active ? '' : 'opacity-60'}"><c:out value="${initials}"/></span>
                      <div><p class="font-semibold"><c:out value="${g.name}"/></p><p class="text-xs text-muted"><c:out value="${g.email}"/></p></div>
                    </div>
                  </td>
                  <td class="mono"><c:out value="${g.id}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${gVip}"><span class="badge badge-vip no-dot"><i data-lucide="crown"></i>VIP</span></c:when>
                      <c:otherwise><span class="badge badge-outline no-dot">Regular</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${g.active}"><span class="badge badge-active">Active</span></c:when>
                      <c:otherwise><span class="badge badge-retired">Deactivated</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-right whitespace-nowrap">
                    <c:if test="${canManage}">
                      <c:choose>
                        <c:when test="${g.active}">
                          <button type="button" class="btn btn-ghost btn-sm" data-modal-open="edit-modal"
                                  data-gid="${fn:escapeXml(g.id)}" data-name="${fn:escapeXml(g.name)}" data-email="${fn:escapeXml(g.email)}">
                            <i data-lucide="pencil"></i>Edit
                          </button>
                          <form method="post" action="${ctx}/admin/guests/vip" class="inline">
                            <input type="hidden" name="id" value="${fn:escapeXml(g.id)}">
                            <input type="hidden" name="vip" value="${not gVip}">
                            <button type="submit" class="btn btn-ghost btn-sm"><i data-lucide="${gVip ? 'arrow-down' : 'crown'}"></i>${gVip ? 'Make regular' : 'Make VIP'}</button>
                          </form>
                          <button type="button" class="btn btn-ghost btn-sm text-clay" data-modal-open="status-modal"
                                  data-gid="${fn:escapeXml(g.id)}" data-name="${fn:escapeXml(g.name)}">
                            <i data-lucide="ban"></i>Deactivate
                          </button>
                        </c:when>
                        <c:otherwise>
                          <form method="post" action="${ctx}/admin/guests/status" class="inline">
                            <input type="hidden" name="id" value="${fn:escapeXml(g.id)}">
                            <input type="hidden" name="active" value="true">
                            <button type="submit" class="btn btn-ghost btn-sm"><i data-lucide="rotate-ccw"></i>Reactivate</button>
                          </form>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <c:if test="${empty guestList}">
          <div class="p-10 text-center text-muted">No guests have registered yet.</div>
        </c:if>
        <div class="hidden p-10 text-center text-muted" data-empty-for="#guests-table">No guests match.</div>
      </div>
    </main>
  </div>

  <!-- Edit guest -->
  <div class="modal" id="edit-modal" role="dialog" aria-modal="true" aria-labelledby="edit-title">
    <div class="modal-box">
      <div class="flex items-start justify-between">
        <div>
          <h2 id="edit-title" class="font-display text-3xl font-semibold text-forest">Edit guest</h2>
          <p class="mono text-sm" data-fill="gid"></p>
        </div>
        <button type="button" class="btn btn-ghost btn-icon" data-modal-close aria-label="Close"><i data-lucide="x" class="w-5 h-5"></i></button>
      </div>
      <form method="post" action="${ctx}/admin/guests/update" class="mt-6 space-y-5">
        <input type="hidden" name="id" data-fill="gid">
        <div><label class="label" for="g-name">Full name</label><input id="g-name" name="name" class="input" maxlength="50" data-fill="name" required></div>
        <div><label class="label" for="g-email">Email</label><input id="g-email" name="email" type="email" class="input" data-fill="email" required></div>
        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
          <button type="submit" class="btn btn-primary">Save</button>
        </div>
      </form>
    </div>
  </div>

  <!-- Deactivate confirmation -->
  <div class="modal" id="status-modal" role="dialog" aria-modal="true" aria-labelledby="status-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="ban"></i></span>
      <h2 id="status-title" class="font-display text-3xl font-semibold text-forest mt-4">Deactivate <span data-fill="name">this guest</span>?</h2>
      <p class="mt-2 text-sm text-ink/70">They won't be able to sign in until the account is reactivated. Their bookings are kept.</p>
      <form method="post" action="${ctx}/admin/guests/status" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="id" data-fill="gid">
        <input type="hidden" name="active" value="false">
        <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-danger">Deactivate</button>
      </form>
    </div>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
