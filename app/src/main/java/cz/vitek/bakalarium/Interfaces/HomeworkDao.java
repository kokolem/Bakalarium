package cz.vitek.bakalarium.Interfaces;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import cz.vitek.bakalarium.POJOs.Homework;

@Dao
public interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long save(Homework homework);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(Homework homework);

    @Query("SELECT * FROM homework WHERE is_archived ORDER BY assigned")
    LiveData<List<Homework>> getArchived();

    @Query("SELECT * FROM homework WHERE is_done AND NOT is_archived ORDER BY assigned")
    LiveData<List<Homework>> getDone();

    @Query("SELECT * FROM homework WHERE NOT is_done AND NOT is_archived ORDER BY assigned")
    LiveData<List<Homework>> getToDo();
}
