package com.cherish.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormattingUtil {


    public static LocalDateTime stringDateFormatToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss +SSSS");
        return LocalDateTime.parse(date, formatter);
    }

    public static String localDateTimeToString(LocalDateTime expiredTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss +SSSS").format(expiredTime);
    }

}
