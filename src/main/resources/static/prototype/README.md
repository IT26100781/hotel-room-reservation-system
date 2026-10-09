# UI prototype (The Orchid Estate)

These are static HTML designs for every page, filled with sample data. They show what each page
should look like before the Java backend exists. Buttons and forms only pretend to work: they show a
small message and move to the next page.

## How to open them

- **Without running anything:** double-click `prototype/pages.html`, which links to every page.
- **With the app running:** start Spring Boot and go to http://localhost:8080/prototype/pages.html.

You need internet: Tailwind, the Google fonts, the icons (Lucide) and the photos (Unsplash) load from the web.

## Shared files (use these in your JSPs too)

| File | What it does |
|------|--------------|
| `css/theme.css` | Colours and ready-made classes: `btn btn-primary`, `card`, `input`, `badge badge-paid`, `table`, `modal` ... |
| `js/tailwind.config.js` | Gives Tailwind our colours (`text-forest`, `bg-brass`, `text-muted`) and fonts (`font-display`). |
| `js/app.js` | Shared behaviour: menus, pop-ups, tabs, filters, price and discount preview, star rating. |
| `js/layout.js` | Draws the header, footer and staff sidebar in the prototype only. Replace it with JSP includes. |

## Converting your page to JSP

1. Copy `prototype/<component>/<page>.html` to `src/main/webapp/WEB-INF/views/<component>/<page>.jsp`.
2. Add `<%@ page contentType="text/html;charset=UTF-8" %>` and
   `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` at the top.
3. Change the asset paths to absolute ones: `../../css/theme.css` becomes
   `${pageContext.request.contextPath}/css/theme.css`, and the same for the js and img files.
4. Replace the sample rows and cards with `<c:forEach>` over the list your servlet puts in the request.
   The `<!-- JSP: ... -->` comments in each page show where.
5. On each `<form>`, remove `data-demo` and `data-next` and set the real `action` and `method="post"`.
   The comment above each form shows the suggested action.
6. Header, footer and sidebar: one person creates `WEB-INF/views/common/header.jsp`, `footer.jsp` and
   `sidebar.jsp` by copying the markup from `js/layout.js`. Every page then uses `<%@ include file="../common/header.jsp" %>`.
   After that, stop loading `layout.js`.

## Rules to keep it consistent

- Use the existing classes before inventing new styles. Badge per status: `badge-active`, `badge-pending`,
  `badge-paid`, `badge-cancelled`, `badge-refunded`, `badge-maintenance`, `badge-retired`, `badge-checked`,
  `badge-noshow`, `badge-completed`, `badge-vip`, `badge-verified`.
- All prices are calculated in Java. The JS price previews are only for showing the guest a preview.
- Hiding a button is not security. Every servlet must still check `canX()` on the session user.
