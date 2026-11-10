<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="editing" value="${not empty review}"/>
<c:set var="pageTitle" value="${editing ? 'Edit Review' : 'Write a Review'}"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="reviews"/>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>
  <%@ include file="/WEB-INF/views/common/botanical-art.jspf" %>

  <c:set var="bookingRef" value="${editing ? review.bookingId : (empty bookingId ? param.bookingId : bookingId)}"/>
  <c:set var="currentRating">${not empty reviewRating ? reviewRating : (editing ? review.rating : '')}</c:set>
  <c:set var="currentText" value="${not empty reviewText ? reviewText : (editing ? review.text : '')}"/>
  <c:set var="firstName" value="${empty me ? '' : fn:split(fn:trim(me.name), ' ')[0]}"/>
  <c:set var="ratingWords" value="${fn:split('-,Poor,Fair,Good,Very good,Exceptional', ',')}"/>
  <c:set var="ratingWord" value=""/>

  <main class="art-clip">
    <div class="has-art relative mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 pt-12 pb-20">
      <svg class="art art-flip hidden lg:block absolute -left-40 top-24 w-72 h-80" style="--tilt:-8deg" aria-hidden="true"><use href="#art-orchid"/></svg>

      <a href="${ctx}/reviews" class="text-sm text-muted hover:text-forest inline-flex items-center gap-1.5"><i data-lucide="arrow-left" class="w-4 h-4"></i>Back to guest stories</a>

      <div class="mt-6 grid lg:grid-cols-[320px_1fr] gap-10 items-start">
        <aside class="card card-pad lg:sticky lg:top-28">
          <c:choose>
            <c:when test="${not empty bookingRef}">
              <span class="stat-icon"><i data-lucide="badge-check"></i></span>
              <p class="mono text-xs text-muted mt-4"><c:out value="${bookingRef}"/></p>
              <h2 class="font-display text-2xl font-semibold text-forest">Your stay with us</h2>
              <p class="text-sm text-ink/70 mt-2">${editing ? 'This review is linked to your booking.' : 'Reviews for a completed stay are shown with a verified badge.'}</p>
              <span class="badge badge-verified no-dot mt-4"><i data-lucide="badge-check"></i>${editing ? 'Verified stay' : 'Will be posted as verified'}</span>
            </c:when>
            <c:otherwise>
              <span class="stat-icon"><i data-lucide="message-square-quote"></i></span>
              <h2 class="font-display text-2xl font-semibold text-forest mt-4">${editing ? 'Public review' : 'Share your experience'}</h2>
              <p class="text-sm text-ink/70 mt-2">Visited for tea or dinner? Post a public review. Stayed with us? Add your booking ID to get the verified badge.</p>
              <span class="badge badge-outline mt-4">Public review</span>
            </c:otherwise>
          </c:choose>
          <ul class="mt-6 pt-5 border-t border-line space-y-3 text-sm text-ink/70">
            <li class="flex gap-3"><i data-lucide="sparkles" class="w-4 h-4 text-brass shrink-0 mt-0.5"></i>Be kind and specific. It helps other travellers.</li>
            <li class="flex gap-3"><i data-lucide="pencil" class="w-4 h-4 text-brass shrink-0 mt-0.5"></i>You can edit or delete your review later.</li>
          </ul>
        </aside>

        <form action="${ctx}/reviews/save" method="post" class="card card-pad">
          <c:if test="${editing}"><input type="hidden" name="id" value="<c:out value='${review.id}'/>"></c:if>
          <span class="eyebrow">${editing ? 'Edit your review' : 'Your review'}</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">
            <c:choose>
              <c:when test="${editing}">Update your review</c:when>
              <c:when test="${not empty firstName}">How was your stay, <c:out value="${firstName}"/>?</c:when>
              <c:otherwise>How was your stay?</c:otherwise>
            </c:choose>
          </h1>

          <c:if test="${not empty error}">
            <div class="alert alert-error mt-6"><i data-lucide="alert-circle"></i><span><c:out value="${error}"/></span></div>
          </c:if>

          <c:if test="${not editing}">
            <div class="mt-8">
              <label class="label" for="bookingId">Booking ID <span class="text-muted font-normal">(optional)</span></label>
              <div class="input-icon"><i data-lucide="ticket"></i><input class="input mono" id="bookingId" name="bookingId" maxlength="20" value="<c:out value='${bookingRef}'/>" placeholder="e.g. BK01"></div>
              <p class="hint">Use a completed booking to post a verified review. Leave blank for a public review.</p>
            </div>
          </c:if>

          <fieldset class="mt-8">
            <legend class="label">Overall rating</legend>
            <div class="flex flex-wrap items-center gap-4">
              <div class="star-input">
                <c:forEach begin="1" end="5" var="i">
                  <c:set var="v" value="${6 - i}"/>
                  <c:set var="vText">${v}</c:set>
                  <c:if test="${currentRating == vText}"><c:set var="ratingWord" value="${ratingWords[v]}"/></c:if>
                  <input type="radio" id="r${v}" name="rating" value="${v}" ${v == 5 ? 'required' : ''} ${currentRating == vText ? 'checked' : ''}>
                  <label for="r${v}" title="${ratingWords[v]}"><i data-star></i></label>
                </c:forEach>
              </div>
              <span class="font-display text-2xl italic text-brass" data-rating-text>${ratingWord}</span>
            </div>
          </fieldset>

          <div class="mt-8">
            <label class="label" for="text">Tell other guests about it</label>
            <textarea id="text" name="text" class="textarea" rows="6" maxlength="500" required data-counter="#text-count"
                      placeholder="What did you love? What could be better?"><c:out value="${currentText}"/></textarea>
            <div class="flex justify-between hint"><span>Between 1 and 500 characters.</span><span id="text-count"></span></div>
          </div>

          <div class="mt-8 pt-6 border-t border-line flex flex-wrap justify-end gap-2">
            <a href="${ctx}/reviews" class="btn btn-ghost">Cancel</a>
            <button type="submit" class="btn btn-primary"><i data-lucide="send"></i>${editing ? 'Save changes' : 'Post review'}</button>
          </div>
        </form>
      </div>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
