package com.humaclab.selliscope.utility_db.db;

import androidx.room.TypeConverter;

import java.util.Date;

/***
 * Created by mtita on 22,August,2019.
 */
public class RoomDataConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
