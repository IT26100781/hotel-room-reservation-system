<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<%--
  Edit the guest home page (GET /admin/home). Shown by HomeContentServlet, posts to POST /admin/home.
  Request attributes:
    home  - HomeContent to fill the form with (after a failed save: the values that were typed).
            Also reads home.updatedBy and home.updatedAtText for the "Last edited by" line.
    error - message to show if saving failed (optional)
  URL parameters:
    ?done=saved - changes were saved -> success message
  Form fields:
    heroEyebrow, heroTitle, heroHighlight, heroText, heroImageUrl,
    announcementVisible (checkbox, sent only when ticked), announcementText,
    ctaTitle, ctaHighlight, ctaText
--%>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="Home page"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<body>
  <c:set var="activeNav" value="home"/>
  <%@ include file="/WEB-INF/views/common/staff-sidebar.jspf" %>
  <div class="lg:pl-72 min-h-screen">
    <%@ include file="/WEB-INF/views/common/staff-topbar.jspf" %>
    <main class="px-4 sm:px-8 py-8 max-w-6xl">
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span class="eyebrow">Website</span>
          <h1 class="font-display text-4xl sm:text-5xl font-semibold text-forest mt-3">Home page</h1>
          <p class="text-muted mt-2">
            Change the text guests see on the home page.
            <c:if test="${not empty home.updatedBy}">
              Last edited by <span class="font-semibold text-ink"><c:out value="${home.updatedBy}"/></span><c:if test="${not empty home.updatedAtText}"> on <c:out value="${home.updatedAtText}"/></c:if>.
            </c:if>
          </p>
        </div>
        <a href="${ctx}/" target="_blank" rel="noopener" class="btn btn-outline"><i data-lucide="external-link"></i>View home page</a>
      </div>

      <c:if test="${param.done == 'saved'}">
        <div class="alert alert-success mt-6"><i data-lucide="circle-check"></i><span>Home page updated. Guests will see the changes straight away.</span></div>
      </c:if>
      <c:if test="${not empty error}">
        <div class="alert alert-error mt-6"><i data-lucide="shield-alert"></i><span><c:out value="${error}"/></span></div>
      </c:if>

      <div class="mt-8 grid xl:grid-cols-[1fr_380px] gap-8 items-start">
        <form method="post" action="${ctx}/admin/home" class="space-y-6" id="home-form">

          <%-- Hero --%>
          <section class="card card-pad space-y-5">
            <div class="flex items-center gap-3">
              <span class="stat-icon"><i data-lucide="image"></i></span>
              <div>
                <h2 class="font-display text-2xl font-semibold text-forest">Hero</h2>
                <p class="text-sm text-muted">The first thing guests see.</p>
              </div>
            </div>
            <div>
              <label class="label" for="heroEyebrow">Small heading</label>
              <input id="heroEyebrow" name="heroEyebrow" class="input" required maxlength="50" data-preview="eyebrow"
                     value="<c:out value='${home.heroEyebrow}'/>">
            </div>
            <div class="grid sm:grid-cols-2 gap-5">
              <div>
                <label class="label" for="heroTitle">Headline</label>
                <input id="heroTitle" name="heroTitle" class="input" required maxlength="60" data-preview="title"
                       value="<c:out value='${home.heroTitle}'/>">
              </div>
              <div>
                <label class="label" for="heroHighlight">Highlighted words</label>
                <input id="heroHighlight" name="heroHighlight" class="input" required maxlength="40" data-preview="highlight"
                       value="<c:out value='${home.heroHighlight}'/>">
                <p class="hint">Shown in gold italics after the headline.</p>
              </div>
            </div>
            <div>
              <label class="label" for="heroText">Intro text</label>
              <textarea id="heroText" name="heroText" class="textarea" required maxlength="400" data-preview="text"><c:out value="${home.heroText}"/></textarea>
              <p class="hint">Up to 400 characters.</p>
            </div>
            <div>
              <label class="label" for="heroImageUrl">Hero image URL</label>
              <div class="input-icon"><i data-lucide="link"></i>
                <input id="heroImageUrl" name="heroImageUrl" type="url" class="input" required maxlength="500" placeholder="https://" data-preview="image"
                       value="<c:out value='${home.heroImageUrl}'/>">
              </div>
              <p class="hint">Must start with https://</p>
            </div>
          </section>

          <%-- Announcement --%>
          <section class="card card-pad space-y-5">
            <div class="flex items-center justify-between gap-4">
              <div class="flex items-center gap-3">
                <span class="stat-icon"><i data-lucide="megaphone"></i></span>
                <div>
                  <h2 class="font-display text-2xl font-semibold text-forest">Announcement bar</h2>
                  <p class="text-sm text-muted">A short notice at the top of the home page, e.g. a seasonal offer.</p>
                </div>
              </div>
              <label class="switch" title="Show announcement">
                <input type="checkbox" name="announcementVisible" value="true" id="announcementVisible" ${home.announcementVisible ? 'checked' : ''}>
                <span></span>
              </label>
            </div>
            <div>
              <label class="label" for="announcementText">Announcement text</label>
              <input id="announcementText" name="announcementText" class="input" maxlength="150" placeholder="Monsoon offer: 15% off stays in June"
                     value="<c:out value='${home.announcementText}'/>">
              <p class="hint">Required when the switch is on. Up to 150 characters.</p>
            </div>
          </section>

          <%-- Call to action --%>
          <section class="card card-pad space-y-5">
            <div class="flex items-center gap-3">
              <span class="stat-icon"><i data-lucide="sparkles"></i></span>
              <div>
                <h2 class="font-display text-2xl font-semibold text-forest">Sign-up banner</h2>
                <p class="text-sm text-muted">The dark banner near the bottom that invites guests to register.</p>
              </div>
            </div>
            <div class="grid sm:grid-cols-2 gap-5">
              <div>
                <label class="label" for="ctaTitle">Heading</label>
                <input id="ctaTitle" name="ctaTitle" class="input" required maxlength="60" value="<c:out value='${home.ctaTitle}'/>">
              </div>
              <div>
                <label class="label" for="ctaHighlight">Highlighted words</label>
                <input id="ctaHighlight" name="ctaHighlight" class="input" required maxlength="40" value="<c:out value='${home.ctaHighlight}'/>">
              </div>
            </div>
            <div>
              <label class="label" for="ctaText">Text</label>
              <textarea id="ctaText" name="ctaText" class="textarea" required maxlength="300"><c:out value="${home.ctaText}"/></textarea>
            </div>
          </section>

          <div class="flex justify-end gap-2">
            <a href="${ctx}/admin" class="btn btn-ghost">Cancel</a>
            <button type="submit" class="btn btn-primary"><i data-lucide="check"></i>Save home page</button>
          </div>
        </form>

        <%-- Live preview of the hero (updates while typing, nothing is saved until the form is submitted) --%>
        <aside class="xl:sticky xl:top-24">
          <p class="label">Preview</p>
          <div class="card overflow-hidden">
            <div class="h-44 bg-sand"><img id="pv-image" class="img-cover" src="<c:out value='${home.heroImageUrl}'/>" alt=""></div>
            <div class="p-6">
              <span class="eyebrow" id="pv-eyebrow"><c:out value="${home.heroEyebrow}"/></span>
              <p class="font-display text-3xl leading-tight font-semibold text-forest mt-4">
                <span id="pv-title"><c:out value="${home.heroTitle}"/></span>
                <em class="italic font-medium text-brass" id="pv-highlight"><c:out value="${home.heroHighlight}"/></em>
              </p>
              <p class="mt-3 text-sm text-ink/70 leading-relaxed" id="pv-text"><c:out value="${home.heroText}"/></p>
            </div>
          </div>
          <p class="hint">Only the hero is previewed here.</p>
        </aside>
      </div>
    </main>
  </div>

  <script src="${ctx}/js/app.js"></script>
  <script>
    document.querySelectorAll('[data-preview]').forEach(function (field) {
      field.addEventListener('input', function () {
        var target = document.getElementById('pv-' + field.dataset.preview);
        if (field.dataset.preview === 'image') {
          if (field.value.indexOf('https://') === 0) target.src = field.value;
        } else {
          target.textContent = field.value;
        }
      });
    });
  </script>
</body>
</html>
