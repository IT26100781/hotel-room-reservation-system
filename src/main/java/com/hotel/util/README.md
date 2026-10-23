# util

This folder holds small helper classes that every component can use.
`FileHelper` reads and writes the `.txt` data files safely (with try-with-resources), and splits and joins `|` lines.
`SessionUtil` stores and gets the logged-in user from the HttpSession, and handles logout.
Use these helpers instead of writing your own file or session code.
