package android.capsulepharmacy.com.modal;

import android.net.Uri;

import java.io.File;

/**
 * Created by deepak.kumar1 on 18-04-2018.
 */
public class AttachFileModel {
    public File file;
    /**
     * The File name.
     */
    public String fileName;
    /**
     * The File url.
     */
    public String fileUrl;
    /**
     * The Mime type.
     */
    public String mimeType;
    /**
     * The Uri.
     */
    public Uri uri;

    public String docAttachment;

    public boolean isFromServer;


}
