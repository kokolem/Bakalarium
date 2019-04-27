package cz.vitek.bakalarium.Homework;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.Utils.MaterialLetterIcon;

public class HomeworkFragment extends Fragment {
    private static final String TAG = "Bakalarium";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init
        final int type = getArguments().getInt("type");
        View fragmentView = inflater.inflate(R.layout.homework_tab, container, false);

        Log.d(TAG, "onCreateView: type " + type);

        // get and init view model
        final HomeworkViewModel viewModel = ViewModelProviders.of(this).get(HomeworkViewModel.class);
        viewModel.init(type);

        // setup recyclerView and its adapter
        final RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // if this is the first time the user runs this app, viewModel.getHomework().getValue() will return null as the database is empty
        // if so, construct the adapter with an empty list as the data set
        // view model will supply the data as soon as it's fetched from the server via notifying the observer
        List<Homework> homework = viewModel.getHomework().getValue() == null ? new ArrayList<Homework>() : viewModel.getHomework().getValue();
        final HomeworkAdapter adapter = new HomeworkAdapter(type, homework, getContext());
        recyclerView.setAdapter(adapter);

        // when the data is changed, update the adapters dataset
        viewModel.getHomework().observe(this, new Observer<List<Homework>>() {
            @Override
            public void onChanged(List<Homework> homework) {
                adapter.updateList(homework);
            }
        });

        return fragmentView;
    }
}

