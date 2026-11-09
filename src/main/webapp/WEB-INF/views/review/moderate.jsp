<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html><html lang="en"><c:set var="pageTitle" value="Manage Reviews"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body><c:set var="activeNav" value="reviews"/>
<%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
<div class="lg:pl-72 min-h-screen"><%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
<main class="px-4 sm:px-8 py-8 max-w-7xl"><span class="eyebrow">Property</span>
<h1 class="font-display text-5xl text-forest mt-3">Review moderation</h1>
<div class="grid sm:grid-cols-3 gap-4 mt-7">
<div class="stat"><p class="stat-label">Reviews</p><p class="stat-value">${fn:length(reviews)}</p></div>
<div class="stat"><p class="stat-label">Average rating</p><p class="stat-value"><fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/></p></div>
<div class="stat"><p class="stat-label">Verified</p><p class="stat-value">${verifiedCount}</p></div></div>
<c:if test="${param.done == 'deleted'}"><div class="alert alert-success mt-5">Review deleted.</div></c:if>
<div class="card mt-7"><div class="table-wrap"><table class="table"><thead><tr><th>Review</th><th>Guest</th><th>Booking</th><th>Rating</th><th>Text</th><th>Type</th><th>Action</th></tr></thead><tbody>
<c:forEach var="rv" items="${reviews}"><tr><td class="mono">${rv.id}</td><td><c:out value="${rv.guestName}"/></td><td><c:out value="${rv.bookingId}"/></td><td>${rv.rating} ★</td><td><c:out value="${rv.text}"/></td><td>${rv.verified ? 'Verified' : 'Public'}</td><td><form action="${ctx}/reviews/delete" method="post"><input type="hidden" name="id" value="${rv.id}"/><button class="btn btn-ghost btn-sm" type="submit" onclick="return confirm('Remove review?')">Delete</button></form></td></tr></c:forEach>
</tbody></table></div></div></main></div>
<script src="${ctx}/js/app.js"></script></body></html>
