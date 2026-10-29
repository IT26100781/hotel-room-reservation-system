<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Shared error page. Spring Boot shows the view named "error" for every error (404, 403, 500 ...),
  which with our view settings is this file. Spring passes in:
    status  - the HTTP status code, e.g. 404
    error   - the standard reason, e.g. "Not Found"
    message - the text given to response.sendError(code, "...") (needs server.error.include-message=always)
--%>
<!DOCTYPE html>
<html lang="en">
<c:choose>
  <c:when test="${status == 403}">
    <c:set var="pageTitle" value="Access denied"/>
    <c:set var="heading" value="This door is locked for you"/>
    <c:set var="text" value="Your account doesn't have permission to open this page. If you think that's a mistake, ask an administrator or sign in with a different account."/>
    <c:set var="icon" value="key-round"/>
  </c:when>
  <c:when test="${status == 404}">
    <c:set var="pageTitle" value="Page not found"/>
    <c:set var="heading" value="This page has checked out"/>
    <c:set var="text" value="We couldn't find the page you were looking for. It may have moved, or the link may be wrong."/>
    <c:set var="icon" value="door-open"/>
  </c:when>
  <c:otherwise>
    <c:set var="pageTitle" value="Something went wrong"/>
    <c:set var="heading" value="Something went wrong"/>
    <c:set var="text" value="An unexpected error happened on our side. Please try again in a moment."/>
    <c:set var="icon" value="shield-alert"/>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <%@ include file="/WEB-INF/views/common/public-header.jspf" %>

  <main class="mx-auto max-w-3xl px-4 sm:px-6 py-20 text-center">
    <div class="relative inline-block">
      <p class="font-display text-[9rem] sm:text-[12rem] font-semibold leading-none text-forest/10 select-none"><c:out value="${empty status ? 'Error' : status}"/></p>
      <span class="absolute inset-0 grid place-items-center"><span class="grid place-items-center w-20 h-20 rounded-full bg-brass text-forest-900 shadow-xl shadow-brass/30"><i data-lucide="${icon}" class="w-9 h-9"></i></span></span>
    </div>
    <h1 class="font-display text-5xl font-semibold text-forest mt-4"><c:out value="${heading}"/></h1>
    <p class="mt-4 text-ink/65 max-w-md mx-auto"><c:out value="${text}"/></p>
    <c:if test="${status == 403 and not empty message and message != 'No message available'}">
      <p class="mt-3 text-sm text-muted"><c:out value="${message}"/></p>
    </c:if>
    <div class="mt-10 flex flex-wrap justify-center gap-3">
      <a href="${ctx}/" class="btn btn-primary"><i data-lucide="hotel"></i>Back to home</a>
      <c:if test="${status == 403}">
        <a href="${ctx}/login" class="btn btn-outline">Sign in</a>
      </c:if>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/common/footer.jspf" %>
  <script src="${ctx}/js/app.js"></script>
</body>
</html>
