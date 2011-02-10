package formaliser.helpers;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.moandjiezana.formaliser.helpers.DateConverter;

public class DateConverterTest {

    private DateConverter converter = new DateConverter();
    
    @Test
    public void can_convert_java_util_Date() throws Exception {
        assertThat(converter.canConvert(Date.class)).isTrue();
    }
    
    @Test
    public void converts_date_and_time() {
        assertThat(converter.convert("19/03/2008 15:32")).isEqualTo(date(2008, Calendar.MARCH, 19, 15, 32));
    }
    
    @Test
    public void converts_java_sql_date() {
        assertThat(converter.canConvert(java.sql.Date.class)).isTrue();
        
        long now = System.currentTimeMillis();
        
        assertThat(converter.convert(new java.sql.Date(now))).isEqualTo(converter.convert(new Date(now)));
    }
    
    @Test
    public void converts_dd_mm_yyyy_date_format() throws Exception {
        assertThat(converter.convert("01/02/2009")).isEqualTo(date(2009, Calendar.FEBRUARY, 1));
    }

    @Test
    public void converts_dd_mm_yyyy_hh_mm_date_format() throws Exception {
        assertThat(converter.convert("01/02/2009 12:31")).isEqualTo(date(2009, Calendar.FEBRUARY, 1, 12, 31));
    }
    
    @Test
    public void converts_invalid_date_to_null() throws Exception {
        assertThat(converter.convert("01/13/2009")).isNull();

        assertThat(converter.convert("03/2009")).isNull();

        assertThat(converter.convert("03/2010 15:31")).isNull();
    }
    
    private Date date(int year, int month, int day) {
        return date(year, month, day, 0, 0);
    }
    
    private Date date(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(year, month, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime();
    }
}
