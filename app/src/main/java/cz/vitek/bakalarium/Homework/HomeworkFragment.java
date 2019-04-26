package cz.vitek.bakalarium.Homework;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.Utils.MaterialLetterIcon;

public class HomeworkFragment extends Fragment {
    private TextView textView;
    private StringBuilder text;

    private final int TYPE_TODO = 1;
    private final int TYPE_DONE = 2;
    private final int TYPE_ARCHIVE = 3;

    private static final String TAG = "Bakalarium";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final int type = getArguments().getInt("type");

        Log.d(TAG, "onCreateView: type " + type);

        // init
        View fragmentView = inflater.inflate(R.layout.homework_tab, container, false);
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        ImageView icon = fragmentView.findViewById(R.id.icon);
        textView = fragmentView.findViewById(R.id.text);

        // get and init view model
        HomeworkViewModel viewModel = ViewModelProviders.of(this).get(HomeworkViewModel.class);
        viewModel.init(type);

        // TODO: make an adapter

        // make the text view scrollable
        textView.setMovementMethod(new ScrollingMovementMethod());

        // when the data is changed, display it
        viewModel.getHomework().observe(this, new Observer<List<Homework>>() {
            @Override
            public void onChanged(List<Homework> homework) {
                Log.d(TAG, "onChanged: changed type " + type);
                if (homework != null) {
                    text = new StringBuilder();
                    for (Homework element : homework) {
                        text.append(element.toString());
                    }
                    textView.setText(text.toString());
                }
            }
        });

        // set up the icon on the top
        switch (type) {
            case TYPE_TODO:
                icon.setImageDrawable(MaterialLetterIcon.build("ZÉ"));
                break;
            case TYPE_DONE:
                icon.setImageDrawable(MaterialLetterIcon.build("HÉ"));
                break;
            case TYPE_ARCHIVE:
                icon.setImageDrawable(MaterialLetterIcon.build("AV"));
                break;
        }

        return fragmentView;
    }
}

