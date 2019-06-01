package cz.vitek.bakalarium.Homework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.vitek.bakalarium.POJOs.Homework;
import cz.vitek.bakalarium.POJOs.HomeworkViewHolder;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.Utils.HomeworkAttachmentOpener;
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
        ImageView attachment = homeworkCard.findViewById(R.id.attachment);
        ImageView icon = homeworkCard.findViewById(R.id.icon);

        return new HomeworkViewHolder(homeworkCard, title, secondaryTitle, supportingText, statusButton, attachment, icon);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkViewHolder holder, final int position) {
        final Homework homework = dataSet.get(position);
        holder.getTitle().setText(homework.getTitle());
        holder.getSupportingText().setText(homework.getDescription());
        holder.getIcon().setImageDrawable(MaterialLetterIcon.build(homework.getSubject()));

        // e.g. format to 24.3.
        SimpleDateFormat formatter = new SimpleDateFormat("d.M.", Locale.getDefault());
        String assigned = formatter.format(homework.getAssigned());
        String handIn = formatter.format(homework.getHandIn());

        String secondaryTitle;
        if (type == 3)
            // the homework was handed in if the homework is in archive
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
                // homework in archive have no button
                statusButton.setVisibility(View.GONE);
                break;
        }

        // show attachment icon if the homework has attachment
        ImageView attachment = holder.getAttachment();
        if (homework.getAttachmentList().size() > 0) attachment.setVisibility(View.VISIBLE);
        else attachment.setVisibility(View.GONE);

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the app has the permission to download / open the attachment(s)
                boolean hasPermissions = false;

                // the permission is granted at install time on older versions of android
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // only needs to check WRITE permission because read and write are in the same permission group
                    hasPermissions = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                } else hasPermissions = true;

                if (hasPermissions) {
                    // open / download the attachment
                    HomeworkAttachmentOpener.openAttachment(homework.getAttachmentList(), context);
                } else {
                    // check if user clicked "never ask again" in the system permission dialog
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // explain that in order to download / open the attachment user must allow the permission in the settings
                        new MaterialAlertDialogBuilder(context)
                                .setTitle(context.getString(R.string.homework_attachments))
                                .setMessage(context.getString(R.string.homework_attachments_permission_naa))
                                .setPositiveButton(context.getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // open settings
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                        intent.setData(uri);
                                        ActivityCompat.startActivityForResult((Activity) context, intent, 0, null);
                                        Toast.makeText(context, context.getString(R.string.settings_permissions), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.cancel), null)
                                .show();
                    } else {
                        // explain why the permission is needed
                        new MaterialAlertDialogBuilder(context)
                                .setTitle(context.getString(R.string.homework_attachments))
                                .setMessage(context.getString(R.string.homework_attachments_permissions))
                                .setPositiveButton(context.getString(R.string.grant_it), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // ask for WRITE_EXTERNAL_STORAGE permission (READ will also be granted too because of the permission groups)
                                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.cancel), null)
                                .show();
                    }
                }
            }
        });

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
