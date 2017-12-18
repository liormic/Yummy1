package com.clarifai.android.starter.api.v2.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.clarifai.android.starter.api.v2.App;
import com.clarifai.android.starter.api.v2.ClarifaiUtil;
import com.clarifai.android.starter.api.v2.DatabaseHandler;
import com.clarifai.android.starter.api.v2.ImageCreator;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public final class RecognizeConceptsActivity extends BaseActivity {

    @NonNull
    private List<Concept> concepts = new ArrayList<>();
    File photoFile = null;
    String photoFileabspath;
    RelativeLayout activity_recognize;
    LinearLayout processingLayout;
    Context context;
    public static final int PICK_IMAGE = 100;
    private static final String TAG = "concepttag";
    Snackbar snackbar;
    private String selectedImagePath;
    DatabaseHandler databaseHandler;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    @BindView(R.id.upload)
    View upload;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    RelativeLayout progressBarHolder;
    private byte[] imageBytes2;
    //@BindView(R.id.imageView2) View imageView2;
    private int progressbarStatus = 0;
    private Handler handler = new Handler();
    private Uri photoURI;
    @BindView(R.id.Snap)
    View snap;

    @NonNull
    private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        activity_recognize = (RelativeLayout) findViewById(R.id.activity_recognize);
        progressBarHolder = (RelativeLayout) findViewById(R.id.progressBarHolder);
        processingLayout = (LinearLayout) findViewById(R.id.processing_layout);
    }


    @OnClick(R.id.Snap)
    void dispatchTakePictureIntent(View v) {
        ImageCreator img;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePictureIntent.putExtra("id", "ACTION_IMAGE_CAPTURE");

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                photoFileabspath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.clarifai.android.starter.api.v2.fileprovider",
                        photoFile);

                String suri = photoURI.toString();
                // galleryAddPic();
                //selectedImagePath = getRealPathFromURI(photoURI);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


            }


        }

    }

    @OnClick(R.id.upload)
    void pickImage(View v) {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK).setType("image/*");
        startActivityForResult(pickImageIntent, PICK_IMAGE);
    }

    @OnClick(R.id.Layouthistory)
    void History(View v) {

        Intent intent = new Intent(this, History.class);
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_IMAGE:
                data.putExtra("id", "ACTION_PICK_IMAGE");
                byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data);
                if (imageBytes != null) {
                    onImagePicked(imageBytes);
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                setBusy(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        proceesImage();
                    }
                });

                break;
        }

    }


    // Intent ImageResolverService = new Intent(this, ProcessImageIntentService.class);
    // mageResolverService.putExtra("data", String.valueOf(photoURI));
    //startService(ImageResolverService);


    private void proceesImage() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {

                if (imageBytes2 != null) {
                    onImagePicked(imageBytes2);
                }


            }

            @Override
            protected Void doInBackground(Void... voids) {


                Bundle bundle = new Bundle();

                bundle.putString("data", String.valueOf(photoURI));
                bundle.putCharSequence("id", "REQUEST_IMAGE_CAPTURE");
                final Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtras(bundle);

                imageBytes2 = ClarifaiUtil.retrieveSelectedImage(getApplicationContext(), intent);

                return null;
            }


        }.execute();
    }


    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API


        // Make sure we don't show a list of old concepts while the image is being uploaded
        //adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();


            }

            @Override

            protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel foodModel = App.get().clarifaiClient().getDefaultModels().foodModel();

                // Use this model to predict, with the image that the user just selected as the input
                return foodModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override
            protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                setBusy(false);
                if (!response.isSuccessful()) {
                    showErrorSnackbar(R.string.error_while_contacting_api);
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                Log.d(TAG, String.valueOf(predictions.get(0)));


                if (predictions.isEmpty()) {
                    showErrorSnackbar(R.string.no_results_from_api);
                    return;
                }

                // adapter.setData(predictions.get(0).data());
                processdata(predictions.get(0).data());
                //  imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }

            private void showErrorSnackbar(@StringRes int errorString) {

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(
                        parentLayout,
                        errorString,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        }.execute();
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_recognize;
    }

    private void setBusy(final boolean busy) {

        // final View Layout= findViewById (android.R.id.content);

        //   switcher.setDisplayedChild(busy ? 1 : 0);
        //fab.setEnabled(!busy);
        // }
        //});
        if (busy == true) {
             progressBar.setVisibility(View.VISIBLE);
            //activity_recognize.setVisibility(View.GONE);
            // setContentView(R.layout.processing_layout);
            //     processingLayout.setVisibility(View.VISIBLE);

           // final AnimatedDotsView red = (AnimatedDotsView) findViewById(R.id.adv_2);
            //red.startAnimation();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(2000);

            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);

        } else {
                progressBar.setVisibility(View.GONE);

            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);


        }
    }






    public String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    Boolean saveImageInDB(String selectedImage) {


        databaseHandler = new DatabaseHandler(RecognizeConceptsActivity.this);
        databaseHandler.getWritableDatabase();
        //\\InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
        String date = "date";
        databaseHandler.addUrl(selectedImage);
        databaseHandler.close();
        return true;

    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
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


    private void processdata(@NonNull List<Concept> concepts) {
        this.concepts = concepts;
        String text = concepts.get(0).name();
        Intent intent = new Intent(RecognizeConceptsActivity.this, RecepieActivity.class);
        intent.putExtra("ProductName", text);

        RecognizeConceptsActivity.this.startActivity(intent);

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return path;
    }

        }


