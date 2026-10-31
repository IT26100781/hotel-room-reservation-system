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
2. Replace everything above `<body>` with the shared start (see `WEB-INF/views/README.md`):
   `<%@ page pageEncoding="UTF-8" %>`, then `<%@ include file="/WEB-INF/views/common/init.jspf" %>`,
   set `pageTitle`, and include `common/head.jspf`.
3. Replace the `data-slot` placeholders with the shared includes: `common/public-header.jspf` and
   `common/footer.jspf` for guest pages, or `common/staff-sidebar.jspf` and `common/staff-topbar.jspf` for
   staff pages. Remove the `data-*` attributes from `<body>` and stop loading `layout.js`.
4. Change the asset and link paths to absolute ones with `${ctx}`: `../../css/theme.css` becomes
   `${ctx}/css/theme.css`, and `../room/list.html` becomes `${ctx}/rooms`.
5. Replace the sample rows and cards with `<c:forEach>` over the list your servlet puts in the request,
   printing values with `<c:out>`. The `<!-- JSP: ... -->` comments in each page show where.
6. On each `<form>`, remove `data-demo` and `data-next` and set the real `action` and `method="post"`.
   The comment above each form shows the suggested action.

The admin, guest and room pages in `WEB-INF/views/` are finished examples of this.

## Rules to keep it consistent

- Use the existing classes before inventing new styles. Badge per status: `badge-active`, `badge-pending`,
  `badge-paid`, `badge-cancelled`, `badge-refunded`, `badge-maintenance`, `badge-retired`, `badge-checked`,
  `badge-noshow`, `badge-completed`, `badge-vip`, `badge-verified`.
- All prices are calculated in Java. The JS price previews are only for showing the guest a preview.
- Hiding a button is not security. Every servlet must still check `canX()` on the session user.
