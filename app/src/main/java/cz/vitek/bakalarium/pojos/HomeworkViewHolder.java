package cz.vitek.bakalarium.pojos;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeworkViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView secondaryTitle;
    private TextView supportingText;
    private Button statusButton;
    private ImageView attachment;
    private ImageView icon;

    public HomeworkViewHolder(@NonNull View itemView, TextView title, TextView secondaryTitle, TextView supportingText, Button statusButton, ImageView attachment, ImageView icon) {
        super(itemView);
        this.title = title;
        this.secondaryTitle = secondaryTitle;
        this.supportingText = supportingText;
        this.statusButton = statusButton;
        this.attachment = attachment;
        this.icon = icon;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getSecondaryTitle() {
        return secondaryTitle;
    }

    public void setSecondaryTitle(TextView secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public TextView getSupportingText() {
        return supportingText;
    }

    public void setSupportingText(TextView supportingText) {
        this.supportingText = supportingText;
    }

    public Button getStatusButton() {
        return statusButton;
    }

    public void setStatusButton(Button statusButton) {
        this.statusButton = statusButton;
    }

    public ImageView getAttachment() {
        return attachment;
    }

    public void setAttachment(ImageView attachment) {
        this.attachment = attachment;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }
}
