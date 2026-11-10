<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Guest Stories"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="reviews"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <%@ include file="/WEB-INF/views/common/botanical-art.jspf" %>

  <c:set var="total" value="${fn:length(reviews)}"/>
  <c:set var="verifiedTotal" value="0"/>
  <c:forEach var="rv" items="${reviews}"><c:if test="${rv.verified}"><c:set var="verifiedTotal" value="${verifiedTotal + 1}"/></c:if></c:forEach>
  <c:set var="canWrite" value="${empty me or me.canAddReviews()}"/>

  <main class="art-clip">
    <section class="has-art relative mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12">
      <svg class="art art-sway hidden md:block absolute -right-24 -top-10 w-72 h-[30rem]" style="--tilt:14deg" aria-hidden="true"><use href="#art-fern"/></svg>

      <div class="grid lg:grid-cols-[1fr_420px] gap-10 items-end">
        <div>
          <span class="eyebrow">Guest stories</span>
          <h1 class="font-display text-5xl sm:text-6xl font-semibold text-forest mt-4">In their own words</h1>
          <p class="mt-4 text-ink/65 max-w-xl">Reviews marked <span class="badge badge-verified">Verified</span> come from guests who stayed with us and reviewed their own booking.</p>
          <c:if test="${canWrite}">
            <a href="${ctx}/reviews/form" class="btn btn-brass mt-6"><i data-lucide="pencil"></i>Write a review</a>
          </c:if>
        </div>

        <div class="card card-pad">
          <div class="flex items-center gap-5">
            <p class="font-display text-7xl font-semibold text-forest leading-none"><fmt:formatNumber value="${averageRating}" minFractionDigits="1" maxFractionDigits="1"/></p>
            <div>
              <span data-stars="${averageRating}"></span>
              <p class="text-sm text-muted mt-1">Based on ${total} review${total == 1 ? '' : 's'}</p>
            </div>
          </div>
          <ul class="mt-6 space-y-2.5 text-sm">
            <c:forEach begin="1" end="5" var="i">
              <c:set var="star" value="${6 - i}"/>
              <c:set var="n" value="0"/>
              <c:forEach var="rv" items="${reviews}"><c:if test="${rv.rating == star}"><c:set var="n" value="${n + 1}"/></c:if></c:forEach>
              <c:set var="pct" value="${total == 0 ? 0 : (n * 100) / total}"/>
              <li class="flex items-center gap-3">
                <span class="w-3 text-muted">${star}</span>
                <div class="bar flex-1"><span style="width:<fmt:formatNumber value='${pct}' maxFractionDigits='0'/>%"></span></div>
                <span class="w-9 text-right text-muted"><fmt:formatNumber value="${pct}" maxFractionDigits="0"/>%</span>
              </li>
            </c:forEach>
          </ul>
        </div>
      </div>
    </section>

    <section class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pb-20">
      <c:if test="${param.done == 'saved'}">
        <div class="alert alert-success mt-10"><i data-lucide="check-circle-2"></i><span>Thank you, your review has been saved.</span></div>
      </c:if>
      <c:if test="${param.done == 'deleted'}">
        <div class="alert alert-success mt-10"><i data-lucide="check-circle-2"></i><span>Your review was deleted.</span></div>
      </c:if>

      <c:choose>
        <c:when test="${total == 0}">
          <div class="card card-pad text-center mt-12 py-16">
            <span class="stat-icon mx-auto"><i data-lucide="message-square-quote"></i></span>
            <h2 class="font-display text-3xl font-semibold text-forest mt-4">No stories yet</h2>
            <p class="text-muted mt-2">Be the first to share your experience of the estate.</p>
            <c:if test="${canWrite}"><a href="${ctx}/reviews/form" class="btn btn-primary mt-6"><i data-lucide="pencil"></i>Write a review</a></c:if>
          </div>
        </c:when>
        <c:otherwise>
          <div class="mt-12 flex flex-wrap gap-2" data-chip-filter="#review-grid" data-chip-attr="kind">
            <button type="button" class="chip active" data-chip="all">All reviews</button>
            <button type="button" class="chip" data-chip="verified"><i data-lucide="badge-check" class="w-4 h-4"></i>Verified stays (${verifiedTotal})</button>
            <button type="button" class="chip" data-chip="public">Public (${total - verifiedTotal})</button>
            <c:if test="${not empty me and me.canAddReviews()}">
              <button type="button" class="chip" data-chip="mine"><i data-lucide="user" class="w-4 h-4"></i>My reviews</button>
            </c:if>
          </div>

          <div id="review-grid" class="mt-8 columns-1 md:columns-2 lg:columns-3 gap-6 [&>*]:mb-6 [&>*]:break-inside-avoid">
            <c:forEach var="rv" items="${reviews}">
              <c:set var="mine" value="${not empty me and me.id == rv.guestId and me.canAddReviews()}"/>
              <c:set var="parts" value="${fn:split(fn:trim(rv.guestName), ' ')}"/>
              <c:set var="initials" value="${fn:toUpperCase(fn:substring(parts[0], 0, 1))}${fn:length(parts) > 1 ? fn:toUpperCase(fn:substring(parts[1], 0, 1)) : ''}"/>
              <figure class="card card-pad" data-item data-kind="${rv.verified ? 'verified' : 'public'}${mine ? ' mine' : ''}">
                <div class="flex items-center justify-between gap-3">
                  <span data-stars="${rv.rating}"></span>
                  <c:choose>
                    <c:when test="${rv.verified}"><span class="badge badge-verified">Verified</span></c:when>
                    <c:otherwise><span class="badge badge-outline">Public</span></c:otherwise>
                  </c:choose>
                </div>
                <c:choose>
                  <c:when test="${fn:length(rv.text) <= 90}">
                    <blockquote class="mt-4 font-display text-2xl leading-snug text-forest">&ldquo;<c:out value="${rv.text}"/>&rdquo;</blockquote>
                  </c:when>
                  <c:otherwise>
                    <blockquote class="mt-4 text-ink/80 leading-relaxed">&ldquo;<c:out value="${rv.text}"/>&rdquo;</blockquote>
                  </c:otherwise>
                </c:choose>
                <figcaption class="mt-5 flex items-center gap-3 text-sm">
                  <span class="avatar ${mine ? 'avatar-brass' : ''}"><c:out value="${initials}"/></span>
                  <span>
                    <span class="font-semibold block"><c:out value="${parts[0]}"/><c:if test="${fn:length(parts) > 1}"> <c:out value="${fn:substring(parts[1], 0, 1)}"/>.</c:if></span>
                    <span class="text-muted">${mine ? 'Your review' : (rv.verified ? 'Stayed with us' : 'Visitor')}</span>
                  </span>
                </figcaption>
                <c:if test="${mine}">
                  <div class="mt-5 pt-4 border-t border-line flex gap-2">
                    <a href="${ctx}/reviews/form?id=<c:out value='${rv.id}'/>" class="btn btn-ghost btn-sm"><i data-lucide="pencil"></i>Edit</a>
                    <button type="button" class="btn btn-ghost btn-sm text-clay" data-modal-open="delete-review-modal" data-review-id="<c:out value='${rv.id}'/>"><i data-lucide="trash-2"></i>Delete</button>
                  </div>
                </c:if>
              </figure>
            </c:forEach>
          </div>
          <div class="hidden card card-pad text-center text-muted" data-empty-for="#review-grid">No reviews in this category yet.</div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>

  <div class="modal" id="delete-review-modal" role="dialog" aria-modal="true" aria-labelledby="delete-review-title">
    <div class="modal-box">
      <span class="stat-icon !bg-clay/10 !text-clay"><i data-lucide="trash-2"></i></span>
      <h2 id="delete-review-title" class="font-display text-3xl font-semibold text-forest mt-4">Delete your review?</h2>
      <p class="mt-2 text-sm text-ink/70">It will be removed from Guest Stories. This can't be undone.</p>
      <form method="post" action="${ctx}/reviews/delete" class="mt-6 flex justify-end gap-2">
        <input type="hidden" name="id" data-fill="reviewId">
        <button type="button" class="btn btn-ghost" data-modal-close>Keep it</button>
        <button type="submit" class="btn btn-danger">Delete review</button>
      </form>
    </div>
  </div>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
