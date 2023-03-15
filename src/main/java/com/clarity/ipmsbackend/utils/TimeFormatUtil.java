package com.clarity.ipmsbackend.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author: clarity
 * @date: 2023年03月05日 11:36
 */
public class TimeFormatUtil {

    /**
     * 将 Date 类的时间转换成格式为 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String dateFormatting(Date date) {
        String dateFormattingTime = "";
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            // Date转换为LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            // 重点 方式三：自定义的格式。如：ofPattern("yyyy-MM-dd hh:mm:ss")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateFormattingTime = formatter.format(localDateTime);
        }
        return dateFormattingTime;
    }

    /**
     * 将 Date 类的时间转换成格式为 yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        String dateFormattingTime = "";
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            // Date转换为LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            // 重点 方式三：自定义的格式。如：ofPattern("yyyy-MM-dd hh:mm:ss")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            dateFormattingTime = formatter.format(localDateTime);
        }
        return dateFormattingTime;
    }
}
