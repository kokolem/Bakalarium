package cz.vitek.bakalarium.homework;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import cz.vitek.bakalarium.interfaces.HomeworkDao;
import cz.vitek.bakalarium.pojos.Homework;

@androidx.room.Database(entities = {Homework.class}, version = 1)
public abstract class Database extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static Database instance;

    // this is a singleton
    public static Database getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "database").build();
            }
        }
        return instance;
    }

    public abstract HomeworkDao homeworkDao();
}
