package com.upscrks.iesesecivil.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilesUtils {

    public static boolean isPdfExists(String fileName, Context context){
        String pdfFileName = fileName +".pdf";
        File storageDir = new File(
                context.getFilesDir()
                        + "/IESESESCivil");
        File pdfFile = new File(storageDir, pdfFileName);
        return pdfFile.exists();
    }

    public static String savePdf(String fileName, byte[] pdf, Context context) {

        String savedPdfPath = null;

        // Create the new file in the external storage
        String pdfFileName = fileName +".pdf";
        File storageDir = new File(
                context.getFilesDir()
                        + "/IESESESCivil");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        // Save the new Bitmap
        if (success) {
            File pdfFile = new File(storageDir, pdfFileName);
            savedPdfPath = pdfFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(pdfFile);
                fOut.write(pdf);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedPdfPath;
    }

    public static Uri getFile(String fileName, Context context){
        String pdfFileName = fileName +".pdf";
        File file = new File(
                context.getFilesDir()
                        + "/IESESESCivil/"+pdfFileName);
        return Uri.fromFile(file);
    }
}