# views

This folder holds the JSP pages (HTML styled with Tailwind) that the browser sees, with one subfolder per component.
Servlets forward to these pages, for example `room/list.jsp`. They live inside `WEB-INF`, so users cannot open them directly by URL.
Pages only display data; permission checks must still be done in the servlet, not by hiding buttons.
