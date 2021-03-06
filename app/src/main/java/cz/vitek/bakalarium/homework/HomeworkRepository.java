package cz.vitek.bakalarium.homework;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.interfaces.BakalariAPI;
import cz.vitek.bakalarium.interfaces.HomeworkDao;
import cz.vitek.bakalarium.pojos.Homework;
import cz.vitek.bakalarium.pojos.HomeworkList;
import cz.vitek.bakalarium.utils.TokenGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HomeworkRepository {
    private static final Object LOCK = new Object();
    private static final String TAG = "Bakalarium";
    private static HomeworkRepository instance;
    private static SharedPreferences preferences;
    private final HomeworkDao homeworkDao;
    private final Context context;
    private BakalariAPI bakalariAPI;
    private long lastFetched;

    private HomeworkRepository(Context context) {
        this.context = context.getApplicationContext();
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        // get Dao
        homeworkDao = Database.getInstance(context).homeworkDao();

        // init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(preferences.getString("school_url", ""))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(
                        new Persister(new AnnotationStrategy())))
                .build();
        bakalariAPI = retrofit.create(BakalariAPI.class);
    }

    // this is a singleton
    public static HomeworkRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new HomeworkRepository(context);
            }
        }
        return instance;
    }

    public LiveData<List<Homework>> getToDo() {
        Log.d(TAG, "getToDo: called");
        Log.d(TAG, "getToDo: refreshing? " + (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000));
        // only download new data if the current data is more than five minute old
        if (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000) refresh();
        return homeworkDao.getToDo();
    }

    public LiveData<List<Homework>> getDone() {
        Log.d(TAG, "getDone: called");
        Log.d(TAG, "refreshing? " + (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000));
        if (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000) refresh();
        return homeworkDao.getDone();
    }

    public LiveData<List<Homework>> getArchived() {
        Log.d(TAG, "getArchived: called");
        Log.d(TAG, "refreshing? " + (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000));
        if (Calendar.getInstance().getTimeInMillis() - lastFetched > 60000) refresh();
        return homeworkDao.getArchived();
    }

    public void update(final Homework homework) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                homeworkDao.update(homework);
                return null;
            }
        }.execute();
    }

    public void refresh() {

        // save the time the data was last updated
        lastFetched = Calendar.getInstance().getTimeInMillis();

        // get token and its timestamp from shared prefs
        String token = preferences.getString("token", "");
        long tokenTimestamp = preferences.getLong("token_timestamp", 0);

        // if there is no token or the token is invalid, get a new token
        // once the new token is calculated, fetch the new data
        // if the token is valid, fetch immediately
        if (!DateUtils.isToday(tokenTimestamp) || token.equals("")) {
            TokenGenerator.saveToken(context, new Callable() {
                @Override
                public Object call() {
                    fetch();
                    return null;
                }
            });
        } else fetch();
    }

    private void fetch() {
        // make the request
        Call<HomeworkList> homeworkListCall = bakalariAPI.getHomework(preferences.getString("token", ""));
        homeworkListCall.enqueue(new Callback<HomeworkList>() {
            @Override
            public void onResponse(@NonNull Call<HomeworkList> call, @NonNull final Response<HomeworkList> response) {
                if (response.body() != null) {
                    if (response.body().getList() != null) { // could be null if the server is down or malfunctioning
                        // save the data into the database in background - don't block UI thread
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                for (final Homework homework : response.body().getList()) {

                                    Homework original = homeworkDao.getById(homework.getId());
                                    if (original != null) {
                                        // if this is an existing homework set it's done property to the current value (before the update) and update the rest
                                        homework.setDone(original.getDone());
                                        homeworkDao.update(homework);
                                    } else {
                                        // if this is a new homework, set it's done property to false and save it
                                        homework.setDone(false);
                                        homeworkDao.save(homework);
                                    }

                                }
                                return null;
                            }

                        }.execute();
                    } else {
                        Toast.makeText(context, context.getString(R.string.cant_connect), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeworkList> call, @NonNull Throwable t) {
                // the probable cause is that there is no internet connection - notify user that the shown data may no longer be up to date
                Toast.makeText(context, context.getString(R.string.cant_connect), Toast.LENGTH_LONG).show();
            }
        });
    }
}
