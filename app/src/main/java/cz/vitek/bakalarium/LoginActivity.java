package cz.vitek.bakalarium;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import cz.vitek.bakalarium.Interfaces.BakalariAPI;
import cz.vitek.bakalarium.POJOs.LoginData;
import cz.vitek.bakalarium.POJOs.TokenData;
import cz.vitek.bakalarium.Utils.TokenGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Bakalarium";

    private Button loginButton;
    private Button schoolListButton;
    private ProgressBar progressBar;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputEditText schoolURL;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout schoolURLInputLayout;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting up toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButton = findViewById(R.id.login_button);
        schoolListButton = findViewById(R.id.school_list_button);
        progressBar = findViewById(R.id.progress_bar);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        schoolURL = findViewById(R.id.school_url);
        usernameInputLayout = findViewById(R.id.username_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        schoolURLInputLayout = findViewById(R.id.school_url_input_layout);
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // save user input
                final String passwordText = password.getText().toString();
                final String usernameText = username.getText().toString();
                final String urlText = schoolURL.getText().toString();
                final String urlTextValidated = urlText.endsWith("/") ? urlText : urlText + "/";

                // reset all errors
                usernameInputLayout.setErrorEnabled(false);
                passwordInputLayout.setErrorEnabled(false);
                schoolURLInputLayout.setErrorEnabled(false);

                boolean valid = true;

                // no field can be empty and address must start with http:// or https://
                // but cannot equal to http:// or https:// and cannot start with http://? or https://?
                if (usernameText.equals("")) {
                    usernameInputLayout.setError(getString(R.string.error_blank_field));
                    valid = false;
                }
                if (passwordText.equals("")) {
                    passwordInputLayout.setError(getString(R.string.error_blank_field));
                    valid = false;
                }
                if (!(urlTextValidated.startsWith("http://") || urlTextValidated.startsWith("https://")) || urlTextValidated.equals("http://") || urlTextValidated.equals("https://") || urlTextValidated.startsWith("http://?/") || urlTextValidated.startsWith("https://?/")) {
                    if (urlTextValidated.equals("")) {
                        schoolURLInputLayout.setError(getString(R.string.error_blank_field));
                    } else schoolURLInputLayout.setError(getString(R.string.wrong_adress));
                    valid = false;
                }

                // if above conditions are met
                if (valid){

                    // show progress bar instead of the "Log in" text
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setTextColor(getResources().getColor(android.R.color.transparent));

                    loginButton.setClickable(false);

                    // init retrofit
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(urlTextValidated)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final BakalariAPI api = retrofit.create(BakalariAPI.class);

                    // get salt, id, type
                    Call<TokenData> tokenDataCall = api.getTokenData(usernameText);
                    tokenDataCall.enqueue(new Callback<TokenData>() {
                        @Override
                        public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                            if (response.body() != null) { // the response body is null if  the address is real but not a Bakaláři one
                                if (response.body().getID() != null) { // getID returns null if the username is wrong but the address is a Bakaláři one

                                    // "Basic ANDR:<token>" in base64
                                    String auth = TokenGenerator.calculateBasicAuth(response.body(), passwordText, usernameText);

                                    // Get users real name + verify that the password is right
                                    final Call<LoginData> loginDataCall = api.getLoginData(auth);
                                    loginDataCall.enqueue(new Callback<LoginData>() {
                                        @Override
                                        public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                            if (response.body() != null) { // response body is null if the password isn't right
                                                // save login credentials to shared prefs
                                                SharedPreferences.Editor editor = preferences.edit();
                                                Log.d(TAG, "onResponse: " + urlText);
                                                Log.d(TAG, "onResponse: " + urlTextValidated);
                                                editor.putString("school_url", urlTextValidated);
                                                editor.putString("username", usernameText);
                                                editor.putString("password", passwordText); // security level over 9000 (who cares about ur Bakaláři password srsly)
                                                editor.apply();

                                                // make a toast with the real name obtained from the Bakaláři server
                                                Toast.makeText(LoginActivity.this, getString(R.string.user_logged_in, response.body().getRealName()), Toast.LENGTH_LONG).show();

                                                // login successful - start the main activity, finish this one
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // WRONG PASSWORD
                                                progressBar.setVisibility(View.GONE);
                                                loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                                loginButton.setClickable(true);
                                                passwordInputLayout.setError(getString(R.string.wrong_password));
                                                Log.d(TAG, "onResponse: wrong password");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LoginData> call, Throwable t) {
                                            // INTERNET CONNECTION WAS LOST AFTER FIRST BUT BEFORE THE SECOND REQUEST
                                            // OR IDK SOME WTF ERROR
                                            progressBar.setVisibility(View.GONE);
                                            loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                            loginButton.setClickable(true);
                                            schoolURLInputLayout.setError(getString(R.string.unknown_error_no_internet));
                                            Log.d(TAG, "onFailure: unknown error or no internet connection");
                                        }
                                    });
                                } else {
                                    // WRONG USERNAME
                                    progressBar.setVisibility(View.GONE);
                                    loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                    loginButton.setClickable(true);
                                    usernameInputLayout.setError(getString(R.string.wrong_username));
                                    Log.d(TAG, "onResponse: wrong username");
                                }
                            } else {
                                // REAL ADDRESS BUT NOT A BAKALÁŘI ONE
                                progressBar.setVisibility(View.GONE);
                                loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                loginButton.setClickable(true);
                                schoolURLInputLayout.setError(getString(R.string.wrong_adress));
                                Log.d(TAG, "onFailure: not a Bakaláři address");
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenData> call, Throwable t) {
                            // NOT A REAL ADDRESS OR NO INTERNET CONNECTION
                            progressBar.setVisibility(View.GONE);
                            loginButton.setTextColor(getResources().getColor(android.R.color.white));
                            loginButton.setClickable(true);
                            // TODO: This could also mean that there is no internet connection
                            schoolURLInputLayout.setError(getString(R.string.wrong_address_no_internet));
                            Log.d(TAG, "onFailure: not a real address or no internet connection");
                            Log.d(TAG, "onFailure: " + t.toString());
                        }
                    });
                }
            }
        });
    }

}
