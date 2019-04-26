package cz.vitek.bakalarium.Homework;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import cz.vitek.bakalarium.Utils.TokenGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import cz.vitek.bakalarium.Interfaces.BakalariAPI;
import cz.vitek.bakalarium.Interfaces.HomeworkDao;
import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.POJOs.HomeworkList;
import cz.vitek.bakalarium.R;

public class HomeworkRepository {
    private final HomeworkDao homeworkDao;
    private final Context context;
    private BakalariAPI bakalariAPI;
    private long lastFetched;

    private static final Object LOCK = new Object();
    private static HomeworkRepository instance;

    private static final String TAG = "Bakalarium";

    // this is a singleton
    public static HomeworkRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new HomeworkRepository(context);
            }
        }
        return instance;
    }

    private HomeworkRepository(Context context) {
        this.context = context.getApplicationContext();

        // get Dao
        homeworkDao = Database.getInstance(context).homeworkDao();

        // init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zsbila.bakalari.cz")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(
                        new Persister(new AnnotationStrategy())))
                .build();
        bakalariAPI = retrofit.create(BakalariAPI.class);
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

    public void refresh() {

        // save the time the data was last updated
        lastFetched = Calendar.getInstance().getTimeInMillis();

        // get token and its timestamp from shared prefs
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String token = preferences.getString("token","");
        long tokenTimestamp = preferences.getLong("token_timestamp",0);

        // if there is no token or the token is invalid, get a new token
        // once the new token is calculated, fetch the new data
        // if the token is valid, fetch immediately
        if (!DateUtils.isToday(tokenTimestamp) || token.equals("")) {
            TokenGenerator.saveToken(context, new Callable() {
                @Override
                public Object call(){
                    fetch();
                    return null;
                }
            });
        } else fetch();
    }

    private void fetch(){
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        // make the request
        Call<HomeworkList> homeworkListCall = bakalariAPI.getHomework(preferences.getString("token",""));
        homeworkListCall.enqueue(new Callback<HomeworkList>() {
            @Override
            public void onResponse(@NonNull Call<HomeworkList> call, @NonNull final Response<HomeworkList> response) {
                if (response.body().getList() != null) { // could be null if the server is down or if the token is invalid

                    // save the data to the database in background - do not block UI thread
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            for (final Homework homework : response.body().getList()) {

                                // if there is a new homework, save it - if there is an existing homework, update it (its status could be changed)
                                long exists = homeworkDao.save(homework);
                                if (exists == -1) homeworkDao.update(homework);
                                Log.d(TAG, "doInBackground: saved, id: " + exists);
                            }
                            return null;
                        }

                    }.execute();
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
