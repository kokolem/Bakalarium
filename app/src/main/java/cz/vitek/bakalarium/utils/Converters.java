package cz.vitek.bakalarium.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cz.vitek.bakalarium.pojos.Attachment;

public class Converters {

    @TypeConverter
    public static Date timestampToDate(Long date) {
        return date == null ? null : new Date(date);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String attachmentsToString(List<Attachment> attachments) {
        if (attachments == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Attachment>>() {
        }.getType();
        return gson.toJson(attachments, type);
    }

    @TypeConverter
    public static List<Attachment> stringToAttachments(String attachments) {
        if (attachments == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Attachment>>() {
        }.getType();
        return gson.fromJson(attachments, type);
    }

}
