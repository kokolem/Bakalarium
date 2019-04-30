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

import cz.vitek.bakalarium.POJOs.Attachment;
import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.POJOs.HomeworkViewHolder;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.Utils.HomeworkDiffCallback;
import cz.vitek.bakalarium.Utils.MaterialLetterIcon;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkViewHolder> {
    private int type;
    private List<Homework> dataSet;
    private Context context;
    private HomeworkViewModel viewModel;

    public HomeworkAdapter(int type, List<Homework> dataSet, Context context, HomeworkViewModel viewModel) {
        this.type = type;
        this.dataSet = dataSet;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public HomeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View homeworkCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework, parent, false);

        TextView title = homeworkCard.findViewById(R.id.title);
        TextView secondaryTitle = homeworkCard.findViewById(R.id.secondaryTitle);
        TextView supportingText = homeworkCard.findViewById(R.id.supportingText);
        Button statusButton = homeworkCard.findViewById(R.id.buttonChangeStatus);
        Button attachmentButton = homeworkCard.findViewById(R.id.buttonAttachment);
        ImageView icon = homeworkCard.findViewById(R.id.icon);

        return new HomeworkViewHolder(homeworkCard, title, secondaryTitle, supportingText, statusButton, attachmentButton, icon);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkViewHolder holder, final int position) {
        final Homework homework = dataSet.get(position);
        holder.getTitle().setText(homework.getTitle());
        holder.getSupportingText().setText(homework.getDescription());
        holder.getIcon().setImageDrawable(MaterialLetterIcon.build(homework.getSubject()));

        SimpleDateFormat formatter = new SimpleDateFormat("d.M.", Locale.getDefault());
        String assigned = formatter.format(homework.getAssigned());
        String handIn = formatter.format(homework.getHandIn());

        String secondaryTitle;
        if (type == 3)
            secondaryTitle = context.getString(R.string.homework_secondary_title_handed_in, assigned, handIn);
        else
            secondaryTitle = context.getString(R.string.homework_secondary_title_hand_in, assigned, handIn);
        holder.getSecondaryTitle().setText(secondaryTitle);

        Button statusButton = holder.getStatusButton();
        switch (type) {
            case 1:
                statusButton.setText(context.getString(R.string.mark_done));
                break;
            case 2:
                statusButton.setText(context.getString(R.string.mark_todo));
                break;
            case 3:
                statusButton.setVisibility(View.GONE);
                break;
        }

        Button attachmentButton = holder.getAttachmentButton();
        List<Attachment> attachmentList = homework.getAttachmentList();

        if (attachmentList.size() == 0) attachmentButton.setVisibility(View.GONE);
        else if (attachmentList.size() == 1) attachmentButton.setText(context.getString(R.string.open_attachment));
        else attachmentButton.setText(context.getString(R.string.open_attachments));

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.changeDone(homework, type == 1);
            }
        });
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
