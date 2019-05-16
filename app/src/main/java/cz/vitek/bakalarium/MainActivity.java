package cz.vitek.bakalarium;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;


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
        boolean askedForPermissions = preferences.getBoolean("permissions_asked", false);

        // if the user is logged out, make him log in
        if (username.equals("") || password.equals("") || url.equals("")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // if permissions need to be granted on runtime bcs of the system version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // and the app didn't already ask for permissions
            if (!askedForPermissions) {
                // create a dialog explaining why the permissions are needed
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.additional_permissions))
                        .setMessage(getString(R.string.additional_permissions_explenation))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // request permissions
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                // save the fact that the app asked for permissions into shared prefs
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("permissions_asked", true);
                editor.apply();
            }
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
