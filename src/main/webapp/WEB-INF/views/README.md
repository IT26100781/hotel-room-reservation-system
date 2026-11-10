# views

This folder holds the JSP pages (HTML styled with Tailwind) that the browser sees, with one subfolder per component.
Servlets forward to these pages, for example `room/list.jsp`. They live inside `WEB-INF`, so users cannot open them directly by URL.

## Shared layout (`common/`)

| File | Use |
|------|-----|
| `init.jspf` | Include first in every page. Declares the `c`, `fn` and `fmt` tag libraries and sets `${ctx}` (context path) and `${me}` (logged-in user). |
| `head.jspf` | The whole `<head>`. Set `pageTitle` before including it. |
| `public-header.jspf`, `footer.jspf` | Guest website header and footer. Set `activeNav` to highlight a menu item. |
| `staff-sidebar.jspf`, `staff-topbar.jspf` | Staff portal sidebar and top bar. Sidebar links appear based on the user's permissions. |
| `logo.jspf` | The brand mark, used by the headers. |

A new page starts like this:

```jsp
<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/init.jspf" %>
<!DOCTYPE html>
<html lang="en">
<c:set var="pageTitle" value="My page"/>
<%@ include file="/WEB-INF/views/common/head.jspf" %>
```

## Rules

- Every `.jsp` and `.jspf` file starts with `<%@ page pageEncoding="UTF-8" %>` (it is not inherited through includes).
- Print user data with `<c:out value="${...}"/>` so it is escaped.
- Build links with `${ctx}`, for example `href="${ctx}/rooms"`.
- Pages only display data; permission checks must still be done in the servlet, not by hiding buttons.
