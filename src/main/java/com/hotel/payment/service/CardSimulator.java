package com.hotel.payment.service;

import java.time.YearMonth;
import java.time.DateTimeException;
import java.util.Map;

/** Only published demonstration numbers are accepted. Nothing contacts a card processor. */
public final class CardSimulator {
    private static final Map<String,String> CARDS=Map.of("4242424242424242","Visa","5555555555554444","Mastercard","4000000000000002","Visa");
    public record Result(String type, String last4) {}
    public Result validate(String holder, String number, String expiry, String cvv, boolean charge) {
        if (holder == null || holder.trim().length()<2 || holder.length()>100) throw new IllegalArgumentException("Enter a cardholder name (2–100 characters).");
        String clean=number == null ? "" : number.replace(" ","").replace("-","");
        if (!CARDS.containsKey(clean)) throw new IllegalArgumentException("Use a displayed test card number. Real cards are not accepted.");
        if (expiry == null || !expiry.matches("[0-9]{2}/[0-9]{2}"))
            throw new IllegalArgumentException("Enter the expiry as MM/YY, for example 12/30.");
        try {
            YearMonth month = YearMonth.of(2000 + Integer.parseInt(expiry.substring(3)), Integer.parseInt(expiry.substring(0, 2)));
            if (month.isBefore(YearMonth.now())) throw new IllegalArgumentException("Enter a current or future expiry month.");
        } catch (DateTimeException e) { throw new IllegalArgumentException("Enter a valid expiry month (01–12) and year as MM/YY."); }
        if (cvv == null || !cvv.matches("[0-9]{3}")) throw new IllegalArgumentException("Enter a three-digit test CVV.");
        if (charge && clean.equals("4000000000000002")) throw new IllegalArgumentException("Simulation: card declined. Try the successful test card; nothing was recorded or charged.");
        return new Result(CARDS.get(clean),clean.substring(12));
    }
}
