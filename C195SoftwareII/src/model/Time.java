/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Class for various time-related methods, namely conversions from default
 * time zone to UTC or EST.
 * @author tim83
 */
public class Time {
    /**
     * Gives a timestamp of now(), translated to UTC timezone
     * @return now() in UTC
     */
    public static Timestamp utcNow(){
        //Create Timestamp format for now()
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        //Convert to LocalDateTime
        LocalDateTime localNow = ts.toLocalDateTime();
        //Convert to ZonedDateTime with system default timezone
        ZonedDateTime zdtNow = localNow.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        //Convert to UTC
        ZonedDateTime zdtUTCNow = zdtNow.withZoneSameInstant(ZoneId.of("UTC"));
        //Convert back to TimeStamp format for SQL
        Timestamp utcTS = Timestamp.valueOf(zdtUTCNow.toLocalDateTime());
        
        return utcTS;
    }
    
    /**
     * Converts input Timestamp from UTC timezone to same time in systemDefault timezone
     * @param ts Timestamp to convert to local time
     * @return Timestamp in local time, same instant as the input UTC timestamp
     */
    public static Timestamp utcToZone(Timestamp ts){
        //Convert input Timestamp to LocalDateTime
        LocalDateTime tsUTC = ts.toLocalDateTime();
        //Convert to ZonedDateTime with UTC timezone
        ZonedDateTime zdtOut = tsUTC.atZone(ZoneId.of("UTC"));
        //Convert to system default timezone
        ZonedDateTime zdtLocal = zdtOut.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        //Convert back to Timestamp format for SQL
        Timestamp tsLocal = Timestamp.valueOf(zdtLocal.toLocalDateTime());
        
        return tsLocal;
    }
    
    /**
     * Manually creates a timestamp from individual year, month, day, hour, and minute parts.
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @return Timestamp of input parts, converted to UTC for database storage
     */
    public static Timestamp assembleStampUTC(int year, int month, int day, int hour, int min){
        Timestamp ts;
        //Create timestamp with individual parts
        ts = new Timestamp(year, month, day, hour, min, 0, 0);
        //Convert to local date time
        LocalDateTime ldt = ts.toLocalDateTime();
        //Convert to zoned date time with system default timezone
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        //Convert to UTC
        zdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        //Convert back to Timestamp
        ts = Timestamp.valueOf(zdt.toLocalDateTime());
        
        return ts;
        
    }
    
    /**
     * Calculates hour difference between systemDefault timezone and EST.
     * Determines difference even if timezones are in different days.
     * @return integer of number of hours difference (positive number if local time is before EST, negative number
     * if local time is after EST.  Example, MST to EST is +2
     */
    public static int timezoneDifferenceToEST(){
        //Create now() of EST timezone
        ZonedDateTime est = ZonedDateTime.now();
        est = est.withZoneSameInstant(ZoneId.of("America/New_York"));
        //Create now() of local timezone
        ZonedDateTime local = ZonedDateTime.now();
        local = local.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        
        //Get just the hour of each timezone
        int estHour = est.getHour();
        int localHour = local.getHour();
        
        
        //If the two timezones have different days, adjust accordingly
        if(est.getDayOfMonth() != local.getDayOfMonth()){
            if(est.toLocalDateTime().isAfter(local.toLocalDateTime())){
                localHour = localHour - 24;
                
            } else if(est.toLocalDateTime().isBefore(local.toLocalDateTime())){
                estHour = estHour + 24;
            }
        }
        
        //return the difference of EST to localtime (i.e. MST to EST is +2)
        return estHour - localHour;
        
    }
    
    /**
     * Generates a new Timestamp that is 7 days out from the Timestamp input.
     * Utilized in setting up the weekly view on calendar table (finding if appointments are between
     * 'now' and seven days from now).
     * @param ts Timestamp to find 7 days out from
     * @return 
     */
    public static Timestamp sevenDays(Timestamp ts){
        ZonedDateTime zdt = ts.toInstant().atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        Timestamp sevenOut = Timestamp.from(zdt.plus(7, ChronoUnit.DAYS).toInstant());
        
        return sevenOut;
    }
}
