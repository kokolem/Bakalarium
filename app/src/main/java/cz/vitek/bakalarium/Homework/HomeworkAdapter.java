package cz.vitek.bakalarium.Homework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.POJOs.HomeworkViewHolder;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.Utils.HomeworkDiffCallback;
import cz.vitek.bakalarium.Utils.MaterialLetterIcon;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkViewHolder> {
    private int type;
    private List<Homework> dataSet;
    private Context context;

    public HomeworkAdapter(int type, @NonNull List<Homework> dataSet, Context context) {
        this.type = type;
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View homeworkCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework, parent, false);
        TextView title = homeworkCard.findViewById(R.id.title);
        TextView secondaryTitle = homeworkCard.findViewById(R.id.secondaryTitle);
        TextView supportingText = homeworkCard.findViewById(R.id.supportingText);
        Button button = homeworkCard.findViewById(R.id.buttonFinished);
        ImageView icon = homeworkCard.findViewById(R.id.icon);
        return new HomeworkViewHolder(homeworkCard, title, secondaryTitle, supportingText, button, icon);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkViewHolder holder, int position) {
        Homework homework = dataSet.get(position);
        holder.getTitle().setText(homework.getTitle());
        holder.getSupportingText().setText(homework.getDescription());
        holder.getIcon().setImageDrawable(MaterialLetterIcon.build(homework.getSubject()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String assigned = formatter.format(homework.getAssigned());
        String handIn = formatter.format(homework.getHandIn());
        holder.getSecondaryTitle().setText(context.getString(R.string.homework_secondary_title, assigned, handIn));

        Button button = holder.getButton();
        if (type == 0) button.setText(context.getString(R.string.mark_todo));
        else if (type == 1) button.setText(context.getString(R.string.mark_done));
        else button.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();

    }

    public void updateList(List<Homework> newDataSet) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new HomeworkDiffCallback(dataSet, newDataSet));
        dataSet = newDataSet;
        diffResult.dispatchUpdatesTo(this);
    }
}
