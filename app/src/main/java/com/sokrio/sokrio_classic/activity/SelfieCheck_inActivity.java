package com.sokrio.sokrio_classic.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sokrio.sokrio_classic.LocationMonitoringService;
import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.SelliscopeApiEndpointInterface;
import com.sokrio.sokrio_classic.SelliscopeApplication;
import com.sokrio.sokrio_classic.databinding.ActivitySelfieCheckInBinding;
import com.sokrio.sokrio_classic.model.Outlets;
import com.sokrio.sokrio_classic.model.UserLocation;
import com.sokrio.sokrio_classic.performance.daily_activities.model.OutletWithCheckInTime;
import com.sokrio.sokrio_classic.service.NotificationHandler;
import com.sokrio.sokrio_classic.utility_db.db.UtilityDatabase;
import com.sokrio.sokrio_classic.utility_db.model.RegularPerformanceEntity;
import com.sokrio.sokrio_classic.utils.BatteryUtils;
import com.sokrio.sokrio_classic.utils.Constants;
import com.sokrio.sokrio_classic.utils.CurrentTimeUtilityClass;
import com.sokrio.sokrio_classic.utils.GetAddressFromLatLang;
import com.sokrio.sokrio_classic.utils.LoadingDialog;
import com.sokrio.sokrio_classic.utils.NetworkUtility;
import com.sokrio.sokrio_classic.utils.SessionManager;
import com.mti.pushdown_ext_onclick_single.SingleClick;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class SelfieCheck_inActivity extends AppCompatActivity {

    ActivitySelfieCheckInBinding mActivitySelfieCheckInBinding;
    private File output = null;
    public static final String AUTHORITY = "com.sokrio.sokrio_classic.fileprovider";
    private final int CAMERA_REQUEST = 3214;
    public String selfieImage;
    UtilityDatabase mUtilityDatabase;
    private SelliscopeApiEndpointInterface apiService;
    public int outletId;
    public Outlets.Outlet outlet;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_selfie_check_in);
        mActivitySelfieCheckInBinding = DataBindingUtil.setContentView(this, R.layout.activity_selfie_check_in);
        mContext=this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Selfie Check-in");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        outletId = getIntent().getIntExtra("outletId", -1);
        outlet= (Outlets.Outlet) getIntent().getSerializableExtra("outlet");


        Log.d("tareq_test", "SelfieCheck_inActivity #67: onCreate:  " + outletId);


        SessionManager sessionManager = new SessionManager(this);

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);


        mUtilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(this);

        mActivitySelfieCheckInBinding.imageViewSelfie.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    output = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.d("tareq_test", "InspectionActivity #87: onCreate:  " + ex.getMessage());
                }
                // Continue only if the File was successfully created
                if (output != null) {

                    Uri photoURI = FileProvider.getUriForFile(SelfieCheck_inActivity.this, AUTHORITY, output);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        });

       SingleClick.get(mActivitySelfieCheckInBinding.tvSend).setOnSingleClickListener(v -> {
           v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
           if(selfieImage==null){
               Toast.makeText(mContext, "Please add a Selfie", Toast.LENGTH_SHORT).show();
               return;
           }
            if (NetworkUtility.isNetworkAvailable(getApplicationContext())) { //if Internet is available

                LoadingDialog loadingDialog= new LoadingDialog(this);
                loadingDialog.showDialog(); //show loading

                if (LocationMonitoringService.sLocation != null) {
                    List<UserLocation.Visit> visitList = new ArrayList<UserLocation.Visit>() ;


                    //ready payload
                    visitList.add(  new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(),LocationMonitoringService.sLocation.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(mContext,LocationMonitoringService.sLocation.getLatitude(),LocationMonitoringService.sLocation.getLongitude()),CurrentTimeUtilityClass.getCurrentTimeStamp(),outletId,selfieImage, BatteryUtils.getBatteryLevelPercentage(mContext),mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));

                    Log.d("tareq_test", "SelfieCheck_inActivity #132: onCreate:  "+ new Gson().toJson(new UserLocation(visitList)));

                    apiService.sendUserLocation(new UserLocation(visitList)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            loadingDialog.hideDialog();


                            scheduleNotification();


                            if(response.isSuccessful()){

                                Toast.makeText(mContext, "Selfie Check-in Successful", Toast.LENGTH_SHORT).show();
                                //region Update checkedin for Activities
                                updateRegularPerformance(SelfieCheck_inActivity.this.mContext, SelfieCheck_inActivity.this.outlet);


                               // startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                finish();

                            }else if(response.code()>=500 && response.code()<=599){

                                //region Update checked-in for Activities
                                updateRegularPerformance(SelfieCheck_inActivity.this.mContext, SelfieCheck_inActivity.this.outlet);


                                Executors.newSingleThreadExecutor().execute(()->{
                                    mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, BatteryUtils.getBatteryLevelPercentage(mContext) ,mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                                    runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                                  //  startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                    finish();

                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {



                          scheduleNotification();


                            //region Update checked-in for Activities
                            updateRegularPerformance(SelfieCheck_inActivity.this.mContext, SelfieCheck_inActivity.this.outlet);


                            Executors.newSingleThreadExecutor().execute(()->{
                                mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, BatteryUtils.getBatteryLevelPercentage(mContext),mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                                runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                             //   startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                finish();

                            });
                        }
                    });
                }else
                    Toast.makeText(SelfieCheck_inActivity.this, "Please wait a while and try again after some minutes", Toast.LENGTH_SHORT).show();

            } else {
                //region Update checkedin for Activities
                updateRegularPerformance(SelfieCheck_inActivity.this.mContext, SelfieCheck_inActivity.this.outlet);

                scheduleNotification();

                Executors.newSingleThreadExecutor().execute(() -> {
                    if (LocationMonitoringService.sLocation != null) {
                        mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, BatteryUtils.getBatteryLevelPercentage(mContext), mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                        runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                       // startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                        finish();
                    } else
                        runOnUiThread(()->  Toast.makeText(SelfieCheck_inActivity.this, "Please wait a while and try again after some minutes", Toast.LENGTH_SHORT).show());
                });
            }
        });
    }

    private void scheduleNotification() {

        //for checking last check-In time for first-check-in-selfie-check-in feature
        new SessionManager(SelfieCheck_inActivity.this.mContext).updateLastCheckInDate(CurrentTimeUtilityClass.getCurrentTimeStampDate());
        new SessionManager(SelfieCheck_inActivity.this.mContext).updateLastSelfieCheckInTime(CurrentTimeUtilityClass.getCurrentTimeStamp2());
        new SessionManager(SelfieCheck_inActivity.this.mContext).updateLastCheckInTime(CurrentTimeUtilityClass.getCurrentTimeStamp2());




        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).cancel( 2); //we had used tag mti and for selfie reminder id has been used 2
        Objects.requireNonNull(notificationManager).cancelAll();


        //add a notification scheduler for next checkIn request
        NotificationHandler.cancelReminder(SelfieCheck_inActivity.this.mContext, "check_in_reminder");
        //2 hr schedule /// 30 sec
        NotificationHandler.scheduleReminder(SelfieCheck_inActivity.this.mContext, Constants.SCHEDULER_INTERVAL_BIR*60*1000, new Data.Builder()
                .putString(Constants.EXTRA_TITLE, "Warning!")
                .putString(Constants.EXTRA_TEXT, "You hadn't given any Selfie for last 2 hours, please make sure next check-in is Selfie check-in")
                .putInt(Constants.EXTRA_ID, 2)//for selfie reminder id has been used 2
                .build(), "check_in_reminder" );
    }

    private void updateRegularPerformance(Context context, Outlets.Outlet outlet) {

        UtilityDatabase utilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(context);
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = formatDate.format(d);
        SimpleDateFormat formathour = new SimpleDateFormat("HH-mm", Locale.ENGLISH);
        String hour = formathour.format(d);
        outlet.setSelfie(true); //as this is selfie check_in
        OutletWithCheckInTime outletWithCheckInTime = new OutletWithCheckInTime(outlet, hour);

        new Thread(() -> {
            List<RegularPerformanceEntity> regularPerformanceEntities = utilityDatabase.returnUtilityDao().getRegularPerformance(date);
            if (regularPerformanceEntities.size() == 0) {
                utilityDatabase.returnUtilityDao().insertRegularPerformance(new RegularPerformanceEntity.Builder().withDate(date).withDistance(0).withOutlets_checked_in(new Gson().toJson(outletWithCheckInTime) + "~;~").build());
            } else {

                String outlets = regularPerformanceEntities.get(0).outlets_checked_in + new Gson().toJson(outletWithCheckInTime) + "~;~";


                utilityDatabase.returnUtilityDao().updateRegularPerformanceOutlets(outlets, date);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);
            final Uri imageUri = outputUri;
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap photo = BitmapFactory.decodeStream(imageStream);
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photo = rotateImageIfRequired(getApplicationContext(),photo, imageUri );
                }
                int factor = 10;
                while (photo.getWidth() >= 1200) {
                    photo = Bitmap.createScaledBitmap(photo, (Math.round(photo.getWidth() * 0.1f * factor)), (Math.round(photo.getHeight() * 0.1f * factor)), false);
                    factor--;
                }
*/
              photo= handleSamplingAndRotationBitmap(getApplicationContext(), outputUri);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                selfieImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);


                Glide.with(mActivitySelfieCheckInBinding.imageViewStore)
                        .load(photo)
                        // .transform(new CircleCrop())
                        .into(mActivitySelfieCheckInBinding.imageViewStore);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    String currentPhotoPath;
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
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            img = rotateImageIfRequired(context, img, selectedImage);
        }
        return img;
    }


    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
