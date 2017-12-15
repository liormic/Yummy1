package com.clarifai.android.starter.api.v2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.clarifai.android.starter.api.v2.activity.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lior on 12/10/2017.
 */

public class ImageCreator   extends BaseActivity {


    File file;
    Bitmap bitmap;

    public ImageCreator(File file, Bitmap bitmap) {

        this.file = file;
        this.bitmap = bitmap;

    }

    public Bitmap createBitmapFromPath(String photoFileabspath) {
        Bitmap bitmap = BitmapFactory.decodeFile(photoFileabspath);
        return bitmap;
    }

    String mCurrentPhotoPath;

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return path;
    }


    @Override
    protected int layoutRes() {
        return 0;
    }


        public boolean saveImageInDB(String selectedImage, Context context) {


        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.getWritableDatabase();
        //\\InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
        String date = "date";
        databaseHandler.addUrl(selectedImage);
        databaseHandler.close();
        return true;

    }


    public Bitmap createBitmapFromContentResolver(Uri photoUri,Context context) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        ContentResolver cr = context.getContentResolver();
        InputStream input = null;
        InputStream input1 = null;
        try {
            input = cr.openInputStream(photoUri);
            BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Bitmap takenImage = null;
        try {
            input1 = cr.openInputStream(photoUri);
            takenImage = BitmapFactory.decodeStream(input1);

            if (input1 != null) {
                input1.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return takenImage;
    }

}
