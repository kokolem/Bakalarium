package cz.vitek.bakalarium;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import cz.vitek.bakalarium.Utils.TokenGenerator;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String TAG = "Bakalarium";

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        FragmentPagerAdapter adapter = new MainAdapter(getSupportFragmentManager(), getResources());
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        String url = preferences.getString("school_url", "");
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");
        String token = preferences.getString("token", "");
        Long tokenTimestamp = preferences.getLong("token_timestamp", 0);

        Log.d(TAG, "onCreate");
        Log.d(TAG, "url: " + url);
        Log.d(TAG, "username: " + username);
        Log.d(TAG, "password: " + password);
        Log.d(TAG, "token: " + token);
        Log.d(TAG, "token timestamp: " + tokenTimestamp);

        // if the user is logged out, make him log in
        if (username.equals("") || password.equals("") || url.equals("")) {
            Log.d(TAG, "user isn't logged in");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // tabs configuration
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        // setting up toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
