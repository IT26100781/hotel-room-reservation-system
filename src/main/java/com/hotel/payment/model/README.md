# model

This folder holds the classes that describe a payment, such as `Payment`, `CashPayment` and `CardPayment`.
Each class keeps its own data in private fields and has the behaviour that belongs to it, like overridden methods in subclasses.
Models only describe the object. They do not run SQL or handle web requests.
