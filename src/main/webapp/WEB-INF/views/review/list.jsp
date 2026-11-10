<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html><html lang="en"><c:set var="pageTitle" value="Guest Reviews"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body><%@ include file="/WEB-INF/views/common/public-header.jspf" %>
<main class="mx-auto max-w-site px-4 sm:px-6 lg:px-8 pt-12 pb-16">
  <span class="eyebrow">Guest stories</span>
  <h1 class="font-display text-5xl font-semibold text-forest mt-3">Reviews</h1>
  <p class="text-muted mt-2">${fn:length(reviews)} reviews · Average rating <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/> / 5</p>
  <c:if test="${param.done == 'saved'}"><div class="alert alert-success mt-5">Review saved successfully.</div></c:if>
  <c:if test="${param.done == 'deleted'}"><div class="alert alert-success mt-5">Review deleted.</div></c:if>
  <c:if test="${not empty me and me.canAddReviews()}"><a class="btn btn-primary mt-6" href="${ctx}/reviews/form">Write a review</a></c:if>
  <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-5 mt-8">
    <c:forEach var="rv" items="${reviews}">
      <article class="card card-pad">
        <p class="text-brass text-xl"><c:forEach begin="1" end="${rv.rating}">★</c:forEach><span class="text-muted text-sm"> ${rv.rating}/5</span></p>
        <p class="mt-4 text-ink/80"><c:out value="${rv.text}"/></p>
        <p class="mt-5 font-semibold"><c:out value="${rv.guestName}"/></p>
        <span class="badge ${rv.verified ? 'badge-verified' : 'badge-outline'}">${rv.verified ? 'Verified stay' : 'Public review'}</span>
        <c:if test="${not empty me and me.id == rv.guestId and me.canAddReviews()}">
          <div class="flex gap-3 mt-4"><a href="${ctx}/reviews/form?id=${rv.id}" class="btn btn-ghost btn-sm">Edit</a>
          <form action="${ctx}/reviews/delete" method="post"><input type="hidden" name="id" value="${rv.id}"/>
          <button type="submit" class="btn btn-ghost btn-sm" onclick="return confirm('Delete this review?')">Delete</button></form></div>
        </c:if>
      </article>
    </c:forEach>
  </div>
  <c:if test="${empty reviews}"><p class="text-muted mt-10">No reviews yet. Be the first to share your experience.</p></c:if>
</main><%@ include file="/WEB-INF/views/common/footer.jspf" %>
<script src="${ctx}/js/app.js"></script></body></html>
