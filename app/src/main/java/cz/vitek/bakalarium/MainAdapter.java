package cz.vitek.bakalarium;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cz.vitek.bakalarium.homework.HomeworkFragment;

public class MainAdapter extends FragmentPagerAdapter {
    private static final String TAG = "Bakalarium";
    private final String TYPE = "type";
    private Resources resources;
    private Bundle todoBundle = new Bundle();
    private Bundle doneBundle = new Bundle();
    private Bundle archiveBundle = new Bundle();

    public MainAdapter(FragmentManager fm, Resources resources) {
        super(fm);
        this.resources = resources;
        todoBundle.putInt(TYPE, 1);
        doneBundle.putInt(TYPE, 2);
        archiveBundle.putInt(TYPE, 3);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d(TAG, "getItem: pos 0");
                HomeworkFragment todoFragment = new HomeworkFragment();
                todoFragment.setArguments(todoBundle);
                return todoFragment;
            case 1:
                Log.d(TAG, "getItem: pos 1");
                HomeworkFragment doneFragment = new HomeworkFragment();
                doneFragment.setArguments(doneBundle);
                return doneFragment;
            case 2:
                Log.d(TAG, "getItem: pos 2");
                HomeworkFragment archiveFragment = new HomeworkFragment();
                archiveFragment.setArguments(archiveBundle);
                return archiveFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return resources.getString(R.string.todo);
            case 1:
                return resources.getString(R.string.done);
            case 2:
                return resources.getString(R.string.archive);
        }
        return null;
    }
}
