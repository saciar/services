package crm.services.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;



public class DateConverter  {
    
    /**
     * Convierte un string en fecha
     * 
     * @param value
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date convertStringToDate(String value, String format) throws ParseException {
        /*if (StringUtils.isBlank(value) || StringUtils.isBlank(format)) {
            return null;
        }*/
    	if (value == null || format == null){
    		return null;
    	}

        DateFormat convertDate = new SimpleDateFormat(format);
        Date date = convertDate.parse(value);
                
        return date;
    }

    /**
     * Convierte una fecha en string
     * 
     * @param date
     * @param format
     * @param locale
     * @param timeZone
     * @return
     * @throws InvalidCallException
     */
    public static String convertDateToString(Date date, String format, Locale locale, TimeZone timeZone) {
        if (date == null)
            return null;
        
        DateFormat df;
        
        if (format != null) {
            df = new SimpleDateFormat(format);
        }
        else if (locale != null) {
            df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        }
        else {
            return null;
        }

        if (timeZone != null) {
            df.setCalendar(new GregorianCalendar(timeZone));
        }

        //if (log.isTraceEnabled()){
        //  log.trace("Formato: " + format + " - Resultado: " + df.format(date));
        //}
        
        return df.format(date);
    }
    
    public static String convertDateToString(Date date, String format) {
        return convertDateToString(date, format, null, null);
    }
    
    public static String convertDateToString(Date date, Locale locale) {
        return convertDateToString(date, null, locale, null);
    }
    
    public static String convertDateToString(Date date, Locale locale, TimeZone timeZone) {
        return convertDateToString(date, null, locale, timeZone);
    }

}
