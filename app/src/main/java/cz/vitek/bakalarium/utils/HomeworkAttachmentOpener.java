package cz.vitek.bakalarium.utils;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

import cz.vitek.bakalarium.BuildConfig;
import cz.vitek.bakalarium.R;
import cz.vitek.bakalarium.pojos.Attachment;

public class HomeworkAttachmentOpener {
    private Context context;
    private List<Attachment> attachmentList;
    private Attachment attachment;
    private BroadcastReceiver attachmentDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openAttachment();
        }
    };

    public HomeworkAttachmentOpener(Context context, List<Attachment> attachmentList) {
        this.context = context;
        this.attachmentList = attachmentList;

        if (attachmentList.size() > 1) attachment = attachmentList.get(0);
        else attachment = chooseAttachment();
    }

    public void processAttachment() {
        // if the attachment wasn't downloaded yet, download it
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File attachmentFile = new File(downloadsDir, attachment.getName());
        if (attachmentFile.exists()) openAttachment(attachmentFile);
        else downloadAndOpenAttachment();
    }

    private Attachment chooseAttachment() {
        // TODO: let the user choose which one
        return attachmentList.get(0);
    }

    private void openAttachment(File attachmentFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", attachmentFile), attachment.getType());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.no_app_for_mime), Toast.LENGTH_LONG).show();
        }
    }

    private void openAttachment() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File attachmentFile = new File(downloadsDir, attachment.getName());
        openAttachment(attachmentFile);
    }

    private void downloadAndOpenAttachment() {
        context.registerReceiver(attachmentDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String fileURL = preferences.getString("school_url", "")
                + "login.aspx?hx=" + preferences.getString("token", "")
                + "&pm=priloha"
                + "&fileId=" + attachment.getId();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setTitle(attachment.getName())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getName());

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}
