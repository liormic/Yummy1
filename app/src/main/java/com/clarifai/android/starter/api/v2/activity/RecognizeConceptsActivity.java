package com.clarifai.android.starter.api.v2.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.clarifai.android.starter.api.v2.App;
import com.clarifai.android.starter.api.v2.ClarifaiUtil;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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


  public static final int PICK_IMAGE = 100;
  private static final String TAG ="concepttag" ;
  Snackbar snackbar;



  // the list of results that were returned from the API
 // @BindView(R.id.resultsList) RecyclerView resultsList;



  // switches between the text prompting the user to hit the FAB, and the loading spinner
  //@BindView(R.id.switcher) ViewSwitcher switcher;

  // the FAB that the user clicks to select an image
 // @BindView(R.id.fab) View fab;


   @BindView(R.id.Upload) View upload;


  //@BindView(R.id.imageView2) View imageView2;

    @BindView(R.id.Snap) View snap;

  @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);




   // Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.logo2).resize(230,230).centerCrop().into((ImageView) imageView2);
    Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.ic_photo_camera).resize(230,230).centerCrop().into((ImageView) snap);
      Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.ic_photo_gallery).resize(230,230).into((ImageView) upload);

     // ImageView imageView = (ImageView)findViewById(R.id.imageView);
    //  Picasso.with(RecognizeConceptsActivity.this).load(R.drawable.background2).fit().into(imageView);
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
  void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    takePictureIntent.putExtra("id", "ACTION_IMAGE_CAPTURE");
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


    }
  }







  @OnClick(R.id.Upload)
  void pickImage() {
    Intent pickImageIntent = new Intent(Intent.ACTION_PICK).setType("image/*");

    startActivityForResult(pickImageIntent, PICK_IMAGE);


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
        break;
    }
  }

  private void onImagePicked(@NonNull final byte[] imageBytes) {
    // Now we will upload our image to the Clarifai API
    setBusy(true);

    // Make sure we don't show a list of old concepts while the image is being uploaded
    //adapter.setData(Collections.<Concept>emptyList());

    new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
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

      private void showErrorSnackbar(@StringRes int errorString) {

          View parentLayout= findViewById (android.R.id.content);
        Snackbar.make(
                parentLayout ,
            errorString,
            Snackbar.LENGTH_INDEFINITE
        ).show();
      }
    }.execute();
  }


  @Override protected int layoutRes() { return R.layout.activity_recognize; }

  private void setBusy(final boolean busy) {

      final View Layout= findViewById (android.R.id.content);
    runOnUiThread(new Runnable() {
      @Override public void run() {
     //   switcher.setDisplayedChild(busy ? 1 : 0);
          snackbar.make(
                  Layout ,
                  "Baking the image...",
                  snackbar.LENGTH_LONG
          ).show();
        //fab.setEnabled(!busy);
      }
    });
  }


  @Override
  protected void onPause() {
    super.onPause();

  }

  private void processdata(@NonNull List<Concept> concepts){
    this.concepts = concepts;
    String text = concepts.get(0).name();
    Intent intent = new Intent(getApplicationContext(), RecepieActivity.class);
    intent.putExtra("ProductName",text);
    snackbar.dismiss();
    getApplicationContext().startActivity(intent);



  }



}
