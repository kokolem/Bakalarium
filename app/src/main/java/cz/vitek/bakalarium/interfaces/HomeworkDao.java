package cz.vitek.bakalarium.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cz.vitek.bakalarium.pojos.Homework;

@Dao
public interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long save(Homework homework);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(Homework homework);

    @Query("SELECT * FROM homework WHERE id = :id")
    Homework getById(String id);

    @Query("SELECT * FROM homework WHERE is_archived ORDER BY assigned DESC")
    LiveData<List<Homework>> getArchived();

    @Query("SELECT * FROM homework WHERE is_done AND NOT is_archived ORDER BY assigned")
    LiveData<List<Homework>> getDone();

    @Query("SELECT * FROM homework WHERE NOT is_done AND NOT is_archived ORDER BY assigned")
    LiveData<List<Homework>> getToDo();
}
