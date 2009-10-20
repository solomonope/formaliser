package com.formaliser.helpers;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class DateConverterTest {

    @Test
    public void converts_date_and_time() {
        DateConverter converter = new DateConverter();
        
        Date date = converter.convert("19/03/2008 15:32");
        
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2008, Calendar.MARCH, 19, 15, 32);
        expectedCal = DateUtils.truncate(expectedCal, Calendar.MINUTE);
        
        assertThat(date).isEqualTo(expectedCal.getTime());
    }
}
