package com.clarifai.android.starter.api.v2.activity;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ProgressBar;

import com.clarifai.android.starter.api.v2.App;
import com.clarifai.android.starter.api.v2.ClarifaiUtil;
import com.clarifai.android.starter.api.v2.DatabaseHandler;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

  @NonNull private List<Concept> concepts = new ArrayList<>();
    File photoFile = null;

    Context context;
  public static final int PICK_IMAGE = 100;
  private static final String TAG ="concepttag" ;
  Snackbar snackbar;


    DatabaseHandler databaseHandler;


  // the list of results that were returned from the API
 // @BindView(R.id.resultsList) RecyclerView resultsList;



  // switches between the text prompting the user to hit the FAB, and the loading spinner
  //@BindView(R.id.switcher) ViewSwitcher switcher;

  // the FAB that the user clicks to select an image
 // @BindView(R.id.fab) View fab;


  @BindView(R.id.upload) View upload;
    @BindView(R.id.progressBar) ProgressBar progressBar;


  //@BindView(R.id.imageView2) View imageView2;
  private int progressbarStatus =0;
  private Handler handler  = new Handler();

    @BindView(R.id.Snap) View snap;

  @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

   // Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.logo2).resize(230,230).centerCrop().into((ImageView) imageView2);
    //Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.snap7).resize(300,300).into((ImageView) snap);
   //   Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.ic_photo_gallery).resize(300,300).into((ImageView) upload);

     // ImageView imageView = (ImageView)findViewById(R.id.imageView);
    //  Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.background2).fit().into(imageView);
      progressBar.setVisibility(View.GONE);
      progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
  }

  @Override protected void onStart() {

    super.onStart();


      //VideoView videoview = (VideoView) findViewById(R.id.videoView2);
     //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bac);
     // videoview.setVideoURI(uri);

      //videoview.start();
   // resultsList.setLayoutManager(new LinearLayoutManager(this));

    //resultsList.setAdapter(adapter);
  }


  static final int REQUEST_IMAGE_CAPTURE = 1;

  @OnClick(R.id.Snap)
  void dispatchTakePictureIntent(View v) {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePictureIntent.putExtra("id", "ACTION_IMAGE_CAPTURE");



      try {
          photoFile = createImageFile();
      } catch (IOException ex) {

      }
      // Continue only if the File was successfully created

          if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
              startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


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


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                data.putExtra("id", "REQUEST_IMAGE_CAPTURE");
                byte[] imageBytes2 = ClarifaiUtil.retrieveSelectedImage(this, data);
                if (imageBytes2 != null) {
                    onImagePicked(imageBytes2);
                }
                Bitmap bitmap = null;
                Cursor cursor = RecognizeConceptsActivity.this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED,
                        null, "date_added DESC");
                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));

                    String photoPath = uri.toString();
                    cursor.close();
                    if (photoPath != null) {
                        bitmap = BitmapFactory.decodeFile(photoPath);
                        saveImageInDB(photoPath);

                        break;
                    }
                }
        }
    }

  private void onImagePicked(@NonNull final byte[] imageBytes) {
    // Now we will upload our image to the Clarifai API
    setBusy(true);

    // Make sure we don't show a list of old concepts while the image is being uploaded
    //adapter.setData(Collections.<Concept>emptyList());

    new  AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
      @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
        // The default Clarifai model that identifies concepts in images
        final ConceptModel foodModel = App.get().clarifaiClient().getDefaultModels().foodModel();

        // Use this model to predict, with the image that the user just selected as the input
        return foodModel.predict()
            .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
            .executeSync();
      }

      @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
        setBusy(false);
        if (!response.isSuccessful()) {
          showErrorSnackbar(R.string.error_while_contacting_api);
          return;
        }
        final List<ClarifaiOutput<Concept>> predictions = response.get();
        Log.d(TAG,String.valueOf(predictions.get(0)));


        if (predictions.isEmpty()) {
          showErrorSnackbar(R.string.no_results_from_api);
          return;
        }

       // adapter.setData(predictions.get(0).data());
        processdata(predictions.get(0).data());
      //  imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
      }

      private  void  showErrorSnackbar(@StringRes int errorString) {

          View parentLayout= findViewById (android.R.id.content);
        Snackbar.make(
                parentLayout ,
            errorString,
            Snackbar.LENGTH_SHORT
        ).show();
      }
    }.execute();
  }

  @Override protected int layoutRes() { return R.layout.activity_recognize; }

  private void setBusy(final boolean busy) {

    // final View Layout= findViewById (android.R.id.content);
    //runOnUiThread(new Runnable() {
    //  @Override public void run() {
    //   switcher.setDisplayedChild(busy ? 1 : 0);
    //fab.setEnabled(!busy);
    // }
    //});
    if(busy==true) {
      progressBar.setVisibility(View.VISIBLE);
    }else{
      progressBar.setVisibility(View.GONE);
    }


    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {
       // ByteArrayOutputStream bytes = new ByteArrayOutputStream();
       //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Uri uri = Uri.parse(path);
        return uri;
    }

    public String getRealPathFromURI(Uri contentURI) {

            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(getApplicationContext(), contentURI, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
    }




    Boolean saveImageInDB(String selectedImage) {


             databaseHandler = new DatabaseHandler(RecognizeConceptsActivity.this);
         databaseHandler.getWritableDatabase();
           //InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
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


    private void processdata(@NonNull List<Concept> concepts){
    this.concepts = concepts;
    String text = concepts.get(0).name();
    Intent intent = new Intent(RecognizeConceptsActivity.this, RecepieActivity.class);
    intent.putExtra("ProductName",text);

        RecognizeConceptsActivity.this.startActivity(intent);

  }
}
