# model

This folder holds the classes that describe what a payment is, such as `Payment`, `CashPayment` and `CardPayment`.
Each class keeps its own data in private fields and has the behaviour that belongs to it, like overridden methods in subclasses.
Each model also has `toFileString()` to turn the object into one line of `payments.txt`, and `fromFileString()` to turn a line back into an object.
Do not read files or handle web requests here, only describe the object.
