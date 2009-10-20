package com.formaliser.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    public boolean canConvert(Class<?> classToConvert) {
        return Date.class.equals(classToConvert);
    }

    public Date convert(String value) {
        try {
            DateFormat dateFormat;
            if (value.contains(":")) {
                dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK);
            } else {
                dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
            }
            dateFormat.setLenient(false);
            return dateFormat.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public String convert(Date value) {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(value);
    }

}
