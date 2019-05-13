package cz.vitek.bakalarium.Utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.util.List;

import cz.vitek.bakalarium.POJOs.Attachment;
import cz.vitek.bakalarium.R;

public class HomeworkAttachmentOpener {

    public static void openAttachment(List<Attachment> attachmentList, Context context) {
        Attachment attachment;
        if (attachmentList.size() > 1) attachment = attachmentList.get(0);
        else attachment = chooseAttachment(attachmentList, context);

        downloadAttachment(attachment, context);
    }

    private static Attachment chooseAttachment(List<Attachment> attachmentList, Context context) {
        // TODO: let the user choose which one
        return attachmentList.get(0);
    }

    private static void downloadAttachment(Attachment attachment, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String fileURL = preferences.getString("school_url", "")
                + "login.aspx?hx=" + preferences.getString("token", "")
                + "&pm=priloha"
                + "&fileId=" + attachment.getId();

        Log.d("Download", "downloadAttachment: " + fileURL);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(attachment.getName())
                .setDescription(context.getString(R.string.attachment_download))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getName());

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}
