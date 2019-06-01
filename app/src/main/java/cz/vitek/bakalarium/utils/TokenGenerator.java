package cz.vitek.bakalarium.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.interfaces.BakalariAPI;
import cz.vitek.bakalarium.pojos.TokenData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenGenerator {

    private static final String TAG = "Bakalarium";

    // calculates and saves token and its calculation date into shared prefs
    // and runs callback function once that's done
    public static void saveToken(final Context context, final Callable callback) {

        Log.d(TAG, "saveToken: called");

        // load data from shared prefs
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String url = preferences.getString("school_url", "");
        final String username = preferences.getString("username", "");
        final String password = preferences.getString("password", "");

        final SharedPreferences.Editor editor = preferences.edit();

        // init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BakalariAPI api = retrofit.create(BakalariAPI.class);

        // get salt, id, type
        Call<TokenData> tokenDataCall = api.getTokenData(username);
        tokenDataCall.enqueue(new Callback<TokenData>() {
            @Override
            public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                // calculate token and save it to shared prefs together with it's calculation date
                String token = calculateToken(response.body(), password, username);
                editor.putString("token", token);
                editor.putLong("token_timestamp", new Date().getTime());
                editor.apply();
                // callback
                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TokenData> call, Throwable t) {
                // the probable cause is that there is no internet connection - notify user that the shown data may no longer be up to date
                Toast.makeText(context, context.getString(R.string.cant_connect), Toast.LENGTH_LONG).show();
            }
        });


    }

    // returns the token
    public static String calculateToken(TokenData tokenData, String password, String username) {
        String token = tokenData.getSalt() + tokenData.getID() + tokenData.getType() + password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] passwordDigest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            token = Base64.encodeToString(passwordDigest, Base64.NO_WRAP);
            token = "*login*" + username + "*pwd*" + token + "*sgn*ANDR" + new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            byte[] tokenDigest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            token = Base64.encodeToString(tokenDigest, Base64.NO_WRAP | Base64.URL_SAFE);
            return token;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;

        }
    }

    // returns "Basic ANDR:<token>" in base64
    public static String calculateBasicAuth(TokenData tokenData, String password, String username) {
        String token = calculateToken(tokenData, password, username);
        return "Basic " + Base64.encodeToString(("ANDR:" + token).getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }
}
