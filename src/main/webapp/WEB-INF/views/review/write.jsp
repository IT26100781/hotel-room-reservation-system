<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html><html lang="en"><c:set var="pageTitle" value="Write Review"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body><%@ include file="/WEB-INF/views/common/public-header.jspf" %>
<main class="mx-auto max-w-3xl px-4 sm:px-6 py-12">
  <a href="${ctx}/reviews" class="text-muted">← Back to reviews</a>
  <div class="card card-pad mt-6">
    <span class="eyebrow">Your feedback</span><h1 class="font-display text-4xl text-forest mt-3">${empty review ? 'Write a review' : 'Edit your review'}</h1>
    <c:if test="${not empty error}"><div class="alert alert-error mt-5"><c:out value="${error}"/></div></c:if>
    <form action="${ctx}/reviews/save" method="post" class="mt-7">
      <c:if test="${not empty review}"><input type="hidden" name="id" value="${review.id}"/></c:if>
      <c:if test="${empty review}">
        <label class="label" for="bookingId">Completed booking ID (optional, for verified review)</label>
        <input class="input mb-5" id="bookingId" name="bookingId" maxlength="20" value="<c:out value='${empty bookingId ? param.bookingId : bookingId}'/>" placeholder="e.g. BK0941"/>
        <p class="hint mb-5">Leave blank to post a public review.</p>
      </c:if>
      <label class="label" for="rating">Overall rating</label>
      <select class="select w-full mb-5" id="rating" name="rating" required>
        <c:forEach begin="1" end="5" var="i"><option value="${i}" ${((not empty review and review.rating == i) or (not empty reviewRating and reviewRating == i)) ? 'selected' : ''}>${i} star${i == 1 ? '' : 's'}</option></c:forEach>
      </select>
      <label class="label" for="text">Your experience</label>
      <textarea class="textarea w-full" id="text" name="text" maxlength="500" required rows="6" placeholder="Tell us about your stay"><c:out value="${not empty reviewText ? reviewText : review.text}"/></textarea>
      <div class="mt-6 flex gap-3"><button class="btn btn-primary" type="submit">Save review</button><a href="${ctx}/reviews" class="btn btn-ghost">Cancel</a></div>
    </form>
  </div>
</main><%@ include file="/WEB-INF/views/common/footer.jspf" %>
<script src="${ctx}/js/app.js"></script></body></html>
