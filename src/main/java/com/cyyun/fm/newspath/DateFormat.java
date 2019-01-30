package com.cyyun.fm.newspath;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFormat
{

    private static final String m1 = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";
    private static final String m2 = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}";
    private static final String m3 = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}";
    private static final String m4 = "\\d{4}-\\d{1,2}-\\d{1,2}";
    private static final String m5 = "\\d{2}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";
    private static final String m6 = "\\d{2}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}";
    private static final String m7 = "\\d{2}-\\d{1,2}-\\d{1,2} \\d{1,2}";
    private static final String m8 = "\\d{2}-\\d{1,2}-\\d{1,2}";
    private static final String m9 = "\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";
    private static final String m10 = "\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}";
    private static final String m11 = "\\d{1,2}-\\d{1,2} \\d{1,2}";
    private static final String m12 = "\\d{1,2}-\\d{1,2}";
    public static final String postTimeRex = "(\\d{2,4}[-,年,/,\\.])?\\d{1,2}[-,月,/,\\.]\\d{1,2}日?( {0,4}?\\d{1,2}[:,时]\\d{1,2}分?(:\\d{2})?)?";

    public static Timestamp parse(String date)
    {
        date = formatSeparate(date);
        String[] datePattern = matchPattern(date);
        if (datePattern[1] != null)
            date = datePattern[1];
        if (datePattern[0] == null)
            return null;

        java.sql.Timestamp result = null;
        SimpleDateFormat dateFromat = new SimpleDateFormat(datePattern[0]);
        java.util.Date d = null;
        try
        {
            d = dateFromat.parse(date);
        }
        catch (ParseException e)
        {
            //e.printStackTrace();
        }
        if (d != null)
        {
            result = new java.sql.Timestamp(d.getTime());
        }

        return result;
    }

    private static String[] matchPattern(String date)
    {
        String datePattern = null;
        String fixDate = null;//补全year
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (date.matches(m1))
            datePattern = "yyyy-MM-dd HH:mm:ss";
        else if (date.matches(m2))
            datePattern = "yyyy-MM-dd HH:mm";
        else if (date.matches(m3))
            datePattern = "yyyy-MM-dd HH";
        else if (date.matches(m4))
            datePattern = "yyyy-MM-dd";
        if (date.matches(m5))
            datePattern = "yy-MM-dd HH:mm:ss";
        else if (date.matches(m6))
            datePattern = "yy-MM-dd HH:mm";
        else if (date.matches(m7))
            datePattern = "yy-MM-dd HH";
        else if (date.matches(m8))
            datePattern = "yy-MM-dd";
        if (date.matches(m9))
        {
            fixDate = year + "-" + date;
            datePattern = "yyyy-MM-dd HH:mm:ss";
        }
        else if (date.matches(m10))
        {
            fixDate = year + "-" + date;
            datePattern = "yyyy-MM-dd HH:mm";
        }
        else if (date.matches(m11))
        {
            fixDate = year + "-" + date;
            datePattern = "yyyy-MM-dd HH";
        }
        else if (date.matches(m12))
        {
            fixDate = year + "-" + date;
            datePattern = "yyyy-MM-dd";
        }
        String[] result =
        {
            "", ""
        };
        result[0] = datePattern;
        result[1] = fixDate;
        return result;
    }

    private static String formatSeparate(String date)
    {
        date = date.replaceAll("年|月|/|\\.", "-");
        date = date.replaceAll("日 {0,4}", " ");
        date = date.replaceAll("分$", "");
        date = date.replaceAll("时$", "");
        date = date.replaceAll("时|分", ":");
        return date.replace("秒", "").trim();
    }
}
