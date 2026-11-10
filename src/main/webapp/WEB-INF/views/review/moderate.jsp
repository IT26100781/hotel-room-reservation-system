<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Reviews"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="reviews"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>

  <c:set var="total" value="${fn:length(reviews)}"/>
  <c:set var="verifiedPct" value="${total == 0 ? 0 : (verifiedCount * 100) / total}"/>

  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>

    <main class="px-4 sm:px-8 py-8 max-w-7xl">
      <span class="eyebrow">Property</span>
      <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Reviews</h1>
      <p class="text-muted mt-2">Remove reviews that are abusive, spam or about the wrong hotel.</p>

      <c:if test="${param.done == 'deleted'}">
        <div class="alert alert-success mt-6"><i data-lucide="check-circle-2"></i><span>Review removed.</span></div>
      </c:if>

      <div class="mt-8 grid sm:grid-cols-3 gap-4">
        <div class="stat"><p class="stat-label">Total reviews</p><p class="stat-value mt-2">${total}</p></div>
        <div class="stat"><p class="stat-label">Average rating</p><p class="stat-value mt-2"><fmt:formatNumber value="${averageRating}" minFractionDigits="1" maxFractionDigits="1"/> <span class="text-brass text-2xl">★</span></p></div>
        <div class="stat">
          <p class="stat-label">Verified</p>
          <p class="stat-value mt-2"><fmt:formatNumber value="${verifiedPct}" maxFractionDigits="0"/>%</p>
          <div class="bar mt-2"><span style="width:<fmt:formatNumber value='${verifiedPct}' maxFractionDigits='0'/>%"></span></div>
        </div>
      </div>

      <div class="card mt-8">
        <div class="p-4 sm:p-5 flex flex-col md:flex-row gap-3 md:items-center justify-between border-b border-line">
          <div class="flex flex-wrap gap-2" data-chip-filter="#reviews-table" data-chip-attr="kind">
            <button type="button" class="chip active" data-chip="all">All</button>
            <button type="button" class="chip" data-chip="verified">Verified</button>
            <button type="button" class="chip" data-chip="public">Public</button>
            <button type="button" class="chip" data-chip="low"><i data-lucide="shield-alert" class="w-4 h-4"></i>3 stars or less</button>
          </div>
          <div class="input-icon md:w-72"><i data-lucide="search"></i><input class="input !py-2.5" placeholder="Search text or guest…" data-filter-target="#reviews-table"></div>
        </div>

        <c:choose>
          <c:when test="${total == 0}">
            <div class="p-12 text-center">
              <span class="stat-icon mx-auto"><i data-lucide="message-square-quote"></i></span>
              <p class="font-display text-2xl font-semibold text-forest mt-4">No reviews yet</p>
              <p class="text-sm text-muted mt-1">Guest reviews will appear here once they are posted.</p>
            </div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table" id="reviews-table">
                <thead><tr><th>Review</th><th>Guest</th><th>Booking</th><th>Rating</th><th>Text</th><th>Type</th><th class="text-right">Actions</th></tr></thead>
                <tbody>
                  <c:forEach var="rv" items="${reviews}">
                    <tr data-item data-kind="${rv.verified ? 'verified' : 'public'}${rv.rating <= 3 ? ' low' : ''}" class="${rv.rating <= 3 ? 'bg-amber/[.05]' : ''}">
                      <td class="mono text-xs whitespace-nowrap" title="<c:out value='${rv.id}'/>"><c:out value="${fn:length(rv.id) > 8 ? fn:substring(rv.id, 0, 8).concat('…') : rv.id}"/></td>
                      <td class="font-medium whitespace-nowrap"><c:out value="${rv.guestName}"/></td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty rv.bookingId}"><span class="mono"><c:out value="${rv.bookingId}"/></span></c:when>
                          <c:otherwise><span class="text-muted">—</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="whitespace-nowrap"><span data-stars="${rv.rating}"></span></td>
                      <td class="text-ink/75"><p class="line-clamp-2 min-w-[16rem] max-w-md" title="<c:out value='${rv.text}'/>"><c:out value="${rv.text}"/></p></td>
                      <td>
                        <c:choose>
                          <c:when test="${rv.verified}"><span class="badge badge-verified">Verified</span></c:when>
                          <c:otherwise><span class="badge badge-outline">Public</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="text-right">
                        <button type="button" class="btn btn-ghost btn-sm text-clay" data-modal-open="delete-modal"
                                data-review-id="<c:out value='${rv.id}'/>" data-guest-name="<c:out value='${rv.guestName}'/>"><i data-lucide="trash-2"></i>Delete</button>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="hidden p-10 text-center text-muted" data-empty-for="#reviews-table">No reviews match.</div>
          </c:otherwise>
        </c:choose>
      </div>
    </main>
  </div>

  <div class="modal" id="delete-modal" role="dialog" aria-modal="true" aria-labelledby="delete-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="trash-2"></i></span>
      <h2 id="delete-title" class="font-display text-3xl font-semibold text-forest mt-4">Remove this review?</h2>
      <p class="mt-2 text-sm text-ink/70">The review by <span class="font-semibold text-ink" data-fill="guestName">this guest</span> will be removed from Guest Stories. This can't be undone.</p>
      <form method="post" action="${ctx}/reviews/delete" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="id" data-fill="reviewId">
        <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-danger">Remove review</button>
      </form>
    </div>
  </div>

  <script src="${ctx}/js/app.js"></script>
</body>
</html>
