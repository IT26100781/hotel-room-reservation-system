# service

This folder holds `StaffService`, which contains the business rules for the staff member component.
It decides whether an action is valid (for example, checking for duplicates, availability or permissions) before anything is saved.
It never touches the file directly; it asks the DAO to load or save data.
Servlets call the service, so all the important logic lives in one place.
