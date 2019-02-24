package com.humaclab.akij_selliscope.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.model.Order.Order;
import com.humaclab.akij_selliscope.utils.SendUserLocationData;
import com.humaclab.akij_selliscope.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLineB extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    private final int CAMERA_REQUEST1 = 3215;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private String str_take_image_outlet, str_take_image_memo;
    private String outletName, outletID, outletType;
    private ImageView iv_take_image_outlet, iv_take_image_memo,detail_image;
    private TextView tv_quantity, text_take_image_outlet, text_take_image_memo;
    private EditText et_stock;
    private AlertDialog builder;
    private Button btn_submit;
    private ProgressDialog pd;
    private SendUserLocationData sendUserLocationData;
    private Double lat = 0.0, lon = 0.0;
    String millisInString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_b);

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");
        outletType = getIntent().getStringExtra("outletType");


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        millisInString = dateFormat.format(new Date());


        //iv_take_image_outlet = findViewById(R.id.iv_take_image_outlet);
        //iv_take_image_memo = findViewById(R.id.iv_take_image_memo);


        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(outletName + "-" + getResources().getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pd = new ProgressDialog(this);


        //For getting location
        sendUserLocationData = new SendUserLocationData(this);
        sendUserLocationData.getInstantLocation(this, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                lat = latitude;
                lon = longitude;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //assert photo != null;
            //photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //str_take_image_outlet = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            //iv_take_image_outlet.setImageBitmap(photo);

            Image image = ImagePicker.getFirstImageOrNull(data);
            Bitmap bmp;
            ByteArrayOutputStream bos;
            try {
                bmp = BitmapFactory.decodeFile(image.getPath());
                iv_take_image_outlet.setImageBitmap(bmp);
                bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                str_take_image_outlet = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUEST1 && resultCode == Activity.RESULT_OK) {
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //assert photo != null;
            //photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //str_take_image_memo = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            //iv_take_image_memo.setImageBitmap(photo);

            Image image = ImagePicker.getFirstImageOrNull(data);
            Bitmap bmp;
            ByteArrayOutputStream bos;
            try {
                bmp = BitmapFactory.decodeFile(image.getPath());
                iv_take_image_memo.setImageBitmap(bmp);
                bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                str_take_image_memo = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }



    public void updateDialog( int image, final String slab) {
        builder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.popup_order, null);
        builder.setView(dialogView);


        iv_take_image_outlet = dialogView.findViewById(R.id.iv_take_image_outlet);
        et_stock = dialogView.findViewById(R.id.et_stock);
        detail_image = dialogView.findViewById(R.id.detail_image);
        detail_image.setImageResource(image);
        iv_take_image_memo = dialogView.findViewById(R.id.iv_take_image_memo);
        text_take_image_memo = dialogView.findViewById(R.id.text_take_image_memo);
        text_take_image_outlet = dialogView.findViewById(R.id.text_take_image_outlet);
        btn_submit = dialogView.findViewById(R.id.btn_submit_inspection);
        tv_quantity = dialogView.findViewById(R.id.tv_quantity);
        tv_quantity.setText(slab);

        iv_take_image_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);

                ImagePicker.cameraOnly().start(ActivityLineB.this,CAMERA_REQUEST);
/*                        .returnMode(ReturnMode.CAMERA_ONLY) // set whether pick and / or camera action should return immediate result or not.
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Folder") // folder selection title
                        .toolbarImageTitle("Tap to select") // image selection title
                        .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                        .single() // single mode
//                .multi() // multi mode (default mode)
//                        .limit(10) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        //.imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .origin(images) // original selected images, used in multi mode
//                .exclude(images) // exclude anything that in image.getPath()
//                .excludeFiles(files) // same as exclude but using ArrayList<File>
//                .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
//                .enableLog(false) // disabling log
//                .imageLoader(new GrayscaleImageLoder()) // custom image loader, must be serializeable

                        .start(CAMERA_REQUEST); // start image picker activity with request code*/

            }
        });

        iv_take_image_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
               // startActivityForResult(cameraIntent, CAMERA_REQUEST1);

                ImagePicker.cameraOnly().start(ActivityLineB.this,CAMERA_REQUEST1);


            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_stock.getText().toString().isEmpty()) {
                if (!(str_take_image_outlet == null)) {

                    if (!(str_take_image_memo == null)) {
                        pd.setMessage("Submitting your Order......");
                        pd.setCancelable(false);
                        pd.show();
                        Order order = new Order();
                        Order.NewOrder newOrder = new Order.NewOrder();


                        newOrder.setComment("");
                        newOrder.setDiscount(0.0);
                        newOrder.setLatitude(lat.toString());
                        newOrder.setLongitude(lon.toString());
                        newOrder.setOutletId(Integer.parseInt(outletID));
                        newOrder.setLine("B");
                        newOrder.setSlab(slab);
                        newOrder.setOutlet_img(str_take_image_outlet);
                        newOrder.setMemo_img(str_take_image_memo);
                        newOrder.setOrder_date(millisInString);
                        newOrder.setStock(et_stock.getText().toString());
                        order.newOrder = newOrder;
                        Log.d("test", "" + new Gson().toJson(newOrder));
                        Call<Order.OrderResponse> orderResponseCall = apiService.order(order);
                        orderResponseCall.enqueue(new Callback<Order.OrderResponse>() {
                            @Override
                            public void onResponse(Call<Order.OrderResponse> call, Response<Order.OrderResponse> response) {
                                pd.dismiss();
                                response.code();
                                if (response.code() == 201) {
                                    builder.dismiss();
                                    finish();
                                }
                                else {
                                    Toast.makeText(ActivityLineB.this, ""+response.code()+" Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Order.OrderResponse> call, Throwable t) {

                            }
                        });
                    } else {
                        text_take_image_memo.setError("Capture Memo Image");
                    }
                } else {
                    text_take_image_outlet.setError("Capture Outlet Image");
                }
                } else {
                    et_stock.setError("Must Be Set");
                }
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public void b_ll_slab_one(View view) {
        updateDialog(R.drawable.petslab1promo,"slab-1");

    }

    public void b_ll_slab_two(View view) {
        updateDialog(R.drawable.petslab2promo,"slab-2");
    }

    public void b_ll_slab_three(View view) {
        updateDialog(R.drawable.petslab3promo,"slab-3");
    }

    public void b_ll_slab_four(View view) {
        updateDialog(R.drawable.petslab4promo,"slab-4");
    }
}
