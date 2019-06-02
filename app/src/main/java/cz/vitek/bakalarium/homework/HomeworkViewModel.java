package cz.vitek.bakalarium.homework;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cz.vitek.bakalarium.pojos.Homework;

public class HomeworkViewModel extends AndroidViewModel {
    private static final String TAG = "Bakalarium";
    private final int TYPE_TODO = 1;
    private final int TYPE_DONE = 2;
    private final int TYPE_ARCHIVE = 3;
    private HomeworkRepository homeworkRepository = HomeworkRepository.getInstance(getApplication());
    private int type;

    public HomeworkViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "HomeworkViewModel: created");
    }

    public void init(int type) {

        // type doesn't change
        if (this.type != 0) {
            return;
        }
        this.type = type;
    }

    public LiveData<List<Homework>> getHomework() {
        LiveData<List<Homework>> homework = null;
        switch (type) {
            case TYPE_TODO:
                homework = homeworkRepository.getToDo();
                break;
            case TYPE_DONE:
                homework = homeworkRepository.getDone();
                break;
            case TYPE_ARCHIVE:
                homework = homeworkRepository.getArchived();
        }
        return homework;
    }

    public void changeDone(Homework homework, boolean done) {
        homework.setDone(done);
        homeworkRepository.update(homework);
    }
}

