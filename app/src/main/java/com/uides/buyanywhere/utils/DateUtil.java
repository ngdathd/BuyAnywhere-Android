package com.uides.buyanywhere.utils;

import java.util.Date;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class DateUtil {
    public static final String[] results = new String[]{"just now", " min ago", " hour ago", " day ago", " month ago", " year ago"};
    public static final int[] units = new int[]{1000, 60, 60, 24, 30, 12};

    public static String getDateDiff(Date start, Date end) {
        long startInMilliSecond = start.getTime();
        long endInMilliSecond = end.getTime();
        if (startInMilliSecond > endInMilliSecond) {
            return "";
        }

        return getResult(0, endInMilliSecond - startInMilliSecond);
    }

    public static String getDateDiffNow(Date start) {
        return getDateDiff(start, new Date());
    }

    private static String getResult(int index, long value) {
        long newValue = value / units[index];
        int lastIndex = units.length - 1;
        if(index >= lastIndex) {
           return newValue + results[lastIndex];
        }
        if(newValue >= units[index + 1]) {
            return getResult(index + 1, newValue);
        } else {
            if(index == 0) {
                return results[index];
            } else {
                return newValue + results[index];
            }
        }
    }
}
