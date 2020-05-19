package com.humaclab.lalteer.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.AdvancePaymentAdapter;
import com.humaclab.lalteer.databinding.ActivityOutletDetailsBinding;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsItem;
import com.humaclab.lalteer.model.advance_payment.AdvancedPaymentResponse;
import com.humaclab.lalteer.model.outlets.Outlets;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingDueResponse;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentBody;
import com.humaclab.lalteer.model.target.OutletTarget;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentLedger;
import com.humaclab.lalteer.room_lalteer.LalteerRoomDb;
import com.humaclab.lalteer.service.OutstandingPaymentWorker;
import com.humaclab.lalteer.utils.Constants;
import com.humaclab.lalteer.utils.CurrentTimeUtilityClass;
import com.humaclab.lalteer.utils.MyDialog;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.humaclab.lalteer.activity.InspectionActivity.decodeBase64;

public class OutletDetailsActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    private SelliscopeApiEndpointInterface apiService;
    protected ActivityOutletDetailsBinding binding;
    private Outlets.Outlet outlet;
    private List<AdvancePaymentsItem> mAdvancePaymentResponseList = new ArrayList<>();
    AdvancePaymentAdapter mAdvancePaymentAdapter;
    DialogFragment mDialogFragment;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private Context mContext;

    private int butClickedPos = 0;
    private int butClickedPos_spinner = 0;

    String currentPhotoPath;
    public static final String AUTHORITY = "com.humaclab.lalteer.fileprovider";
    private File output=null;
    private String outstandingPaymentImg;
    private LalteerRoomDb mLalteerRoomDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet_details);
        mContext=this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.Dealer_Information));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLalteerRoomDb= (LalteerRoomDb) LalteerRoomDb.getInstance(mContext);
        SessionManager sessionManager = new SessionManager(OutletDetailsActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);


        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        Log.d("tareq_test", "ou" + new Gson().toJson(outlet));
        binding.setOutletDetailsVar(outlet); //setting binding variable

        if (Float.parseFloat(outlet.outletDue.replace(",", "")) < 0) {
            binding.tvDueAmount.setVisibility(View.GONE);
        }

        Glide.with(getApplicationContext())
                .load(Constants.BASE_URL.substring(0, Constants.BASE_URL.length() - 4) + outlet.outletImgUrl)
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_map)
                .centerCrop()
                .transform(new CircleCrop())
                .into(binding.ivAddOutletImage);

        binding.btnEditOutlet.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditOutletActivity.class);
            intent.putExtra("outletDetails", outlet);
            startActivity(intent);
        });
        binding.btnCallCard.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CallCardActivity.class);
            intent.putExtra("outletDetails", outlet);
            startActivity(intent);
        });


        binding.recyclerPayments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        mAdvancePaymentAdapter = new AdvancePaymentAdapter(this, mAdvancePaymentResponseList);

        binding.recyclerPayments.setAdapter(mAdvancePaymentAdapter);


        loadAdvanceMoney();
        loadTargetOutlet();
        loadOutstandingDue();

        binding.imageButton.setOnClickListener(v->{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    output = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.d("tareq_test", "InspectionActivity #87: onCreate:  "+ ex.getMessage());
                }
                // Continue only if the File was successfully created
                if (output != null) {

                    Uri photoURI = FileProvider.getUriForFile(OutletDetailsActivity.this, AUTHORITY,                            output);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        });

        /**
         * open date picker for setting date
         */
        binding.textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((TextView) binding.textViewDate).setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        binding.spPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {

                    case 0: {
                        binding.etBranchName.setVisibility(View.GONE);
                        binding.spinnerBankName.setVisibility(View.GONE);
                        binding.etChequeNo.setVisibility(View.GONE);
                        binding.etDepositTo.setVisibility(View.VISIBLE);
                        binding.textViewDate.setVisibility(View.GONE);
                        binding.etDepositSlip.setVisibility(View.VISIBLE);
                        binding.imageButton.setVisibility(View.VISIBLE);

                        break;
                    }
                    case 1: {
                        binding.etBranchName.setVisibility(View.VISIBLE);
                        binding.spinnerBankName.setVisibility(View.VISIBLE);
                        binding.etChequeNo.setVisibility(View.VISIBLE);
                        binding.etDepositTo.setVisibility(View.VISIBLE);
                        binding.etDepositSlip.setVisibility(View.VISIBLE);
                        binding.textViewDate.setVisibility(View.VISIBLE);
                        binding.imageButton.setVisibility(View.VISIBLE);

                        butClickedPos_spinner = binding.spPaymentType.getSelectedItemPosition();

                        Log.d("tareq_test", "id " + butClickedPos);
                        break;
                    }
                    case 2: {
                        binding.etBranchName.setVisibility(View.VISIBLE);
                        binding.spinnerBankName.setVisibility(View.VISIBLE);
                        binding.etChequeNo.setVisibility(View.GONE);
                        binding.etDepositTo.setVisibility(View.VISIBLE);
                        binding.etDepositSlip.setVisibility(View.VISIBLE);
                        binding.textViewDate.setVisibility(View.VISIBLE);
                        binding.imageButton.setVisibility(View.VISIBLE);

                        butClickedPos_spinner = binding.spPaymentType.getSelectedItemPosition();
                        Log.d("tareq_test", "id " + butClickedPos);
                        break;
                    }
                    default: {
                        Toast.makeText(mContext, "default", Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        mDialogFragment = MyDialog.newInstance(outlet.outletId, apiService);
        ((MyDialog) mDialogFragment).setOnDismissListener(this::loadAdvanceMoney);

        PushDownAnim.setPushDownAnimTo(binding.addAdvanceMoney).setOnSingleClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
           /*AlertDialog.Builder  builder=new AlertDialog.Builder(OutletDetailsActivity.this);
           View layout=getLayoutInflater().inflate(R.layout.cus_dialog,null);
           builder.setView(layout);
           AlertDialog dialog = builder.create();
           dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
           dialog.show();*/
            loadDialog();


        });

        PushDownAnim.setPushDownAnimTo(binding.addOutstandingPayment).setOnSingleClickListener(v->{
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration

            double amount =Double.parseDouble( binding.etPayment.getEditText()==null?"0":binding.etPayment.getEditText().getText().toString());
            if(amount<=Double.parseDouble(binding.textViewDueInput.getText().toString())) {
                postOutstandingPayment();
            }else{
                binding.etPayment.setError("Payment can't exit due amount");
            }


        });


        Executors.newSingleThreadExecutor().execute(()->{
            OutstandingPaymentLedger outstandingPaymentLedger= mLalteerRoomDb.returnOutstandingPaymentLadger().getOutstandingPaymentLedger(outlet.outletId);
            if(outstandingPaymentLedger!=null){
                binding.textViewDueInput.setText(String.valueOf(outstandingPaymentLedger.getDue()));
                binding.textViewPaidInp.setText(String.valueOf(outstandingPaymentLedger.getPaid()));


            }
        });


    }

    private void postOutstandingPayment() {

        SessionManager sessionManager = new SessionManager(mContext);

        Objects.requireNonNull(binding.etPayment.getEditText());
        String payment = binding.etPayment.getEditText().getText().toString();


        if(!payment.isEmpty()
                &&
                Double.parseDouble(payment)>0 ){
            OutstandingPaymentBody outstandingPaymentBody = new OutstandingPaymentBody(Double.parseDouble(payment), CurrentTimeUtilityClass.getCurrentTimeStamp());


            outstandingPaymentBody.setType(binding.spPaymentType.getSelectedItemPosition()+1);

            if (payment.equals("")) {
                View view = binding.etPayment;
                binding.etPayment.setError(mContext.getString(R.string.error_field_required));
                view.requestFocus();

                return;
            } else if (Double.parseDouble(payment) <=0 ) {
                View view = binding.etPayment;
                binding.etPayment.setError("Payment Value must be Greater than 0");
                view.requestFocus();

                return;

            } else if (Double.parseDouble(payment) > Double.parseDouble(binding.textViewDueInput.getText().toString())) {
                View view = binding.etPayment;
                binding.etPayment.setError("Payment Value Exceed");
                view.requestFocus();

                return;
            } else {
                outstandingPaymentBody.setAmount( Double.parseDouble(binding.etPayment.getEditText().getText().toString()));

            }

            outstandingPaymentBody.setBank_name( outstandingPaymentBody.getType() != 1 ? binding.spinnerBankName.getSelectedItem().toString() + " (" + binding.etBranchName.getText().toString() + ")" : "");

            if (outstandingPaymentBody.getType() == 2) { //for cheque
                if (binding.etChequeNo.getText().toString().equals("")) {
                    View view =binding.etChequeNo;
                    binding.etChequeNo.setError(mContext.getString(R.string.error_field_required));
                    view.requestFocus();

                    return;
                }


                if (binding.textViewDate.getText().toString().equals("")) {
                    View view = binding.textViewDate;
                    binding.textViewDate.setError(mContext.getString(R.string.error_field_required));
                    view.requestFocus();

                    return;

                }

                if (binding.spinnerBankName.getSelectedItemPosition() == 0) {
                    View view = binding.spinnerBankName;
                    TextView errorText = (TextView) binding.spinnerBankName.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Bank name must be selected");//changes the selected item text to this

                    view.requestFocus();

                    return;

                }


            }


            outstandingPaymentBody.setCheque_no(binding.etChequeNo.getText().toString());
            outstandingPaymentBody.setCheque_date( outstandingPaymentBody.getType() != 1 ? binding.textViewDate.getText().toString() : "");
            outstandingPaymentBody.setDeposit_to(binding.etDepositTo.getText().toString());
            outstandingPaymentBody.setDeposit_from(sessionManager.getUserEmail());


            if (outstandingPaymentBody.getType() == 3) {
                if (binding.etDepositSlip.getText().toString().equals("")) {
                    View view = binding.etDepositSlip;
                    binding.etDepositSlip.setError(mContext.getString(R.string.error_field_required));
                    view.requestFocus();

                    return;

                }

                if (binding.spinnerBankName.getSelectedItemPosition() == 0) {
                    View view =binding.spinnerBankName;
                    TextView errorText = (TextView) binding.spinnerBankName.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Bank name must be selected");//changes the selected item text to this

                    view.requestFocus();

                    return;

                }

            }

            outstandingPaymentBody.setDepositedSlipNumber(binding.etDepositSlip.getText().toString());
            outstandingPaymentBody.setImg(outstandingPaymentImg);


        /*   if(NetworkUtility.isNetworkAvailable(mContext)) {*/
               Log.d("tareq_test", "OutletDetailsActivity #168: postOutstandingPayment:  outletId: "+ outlet.outletId);
              // UploadOutstandingPayment(outstandingPaymentBody, outlet.outletId, mCompositeDisposable);

        /*       Data.Builder data= new Data.Builder();
               data.putInt("outletId", outlet.outletId);
               data.putString("img", outstandingPaymentImg);
               data.putString("paymentBody", new Gson().toJson(outstandingPaymentBody));*/

               OneTimeWorkRequest oneTimeWorkRequest= new OneTimeWorkRequest.Builder(OutstandingPaymentWorker.class)
                       .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                       .addTag("sync outstanding Payment")
                    //   .setInputData(data.build())
                       .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                       .build();

               WorkManager.getInstance(mContext)
                       .beginUniqueWork("sync outstanding Payment", ExistingWorkPolicy.KEEP, oneTimeWorkRequest )
                       .enqueue();


            Toast.makeText(mContext, "Outstanding payment success", Toast.LENGTH_SHORT).show();
            binding.etPayment.getEditText().setText("");


            Executors.newSingleThreadExecutor().execute(()->{
              LalteerRoomDb lalteerRoomDb= (LalteerRoomDb) LalteerRoomDb.getInstance(mContext);
              outstandingPaymentBody.setOutlet_id(outlet.outletId);
              lalteerRoomDb.returnOutstandingPaymentDao().insertOutstandingPayment(outstandingPaymentBody);
            });

              /*
                   WorkManager.getInstance(mContext).getWorkInfoById(oneTimeWorkRequest.getId()).addListener(() -> {
                       Log.d("tareq_test", "OutletDetailsActivity #198: postOutstandingPayment:  Done");
                       runOnUiThread(this::loadOutstandingDue);

                   }, Executors.newSingleThreadExecutor());


            */
                   startActivity(new Intent(OutletDetailsActivity.this, OutletActivity.class ));
                   finish();

                  
         




         /*  }else{
               Toast.makeText(mContext, "No Internet", Toast.LENGTH_SHORT).show();
           }*/
        }else{
            binding.etPayment.setError("can't be empty or 0");
        }
    }



    private void loadDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.alert_present, R.anim.alert_dismiss);
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        mDialogFragment.show(ft, "dialog");

    }

    private void loadTargetOutlet() {
        apiService.getOutletTarget(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<OutletTarget>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OutletTarget> response) {
                        if (response.code() == 200) {
                            binding.progressBar.setVisibility(View.GONE);
                            Log.d("tareq_test", "outlet" + new Gson().toJson(response.body()));

                            String sales_types = response.body().getResult().getSalesTypes();
                            Double total = Double.valueOf(response.body().getResult().getSalesTarget().replace(",", ""));
                            Double achieved = Double.valueOf(response.body().getResult().getAchieved().replace(",", ""));
                            Double remaining = total - achieved;
                            int completePersentage = (int) ((achieved * 100) / total);

                            binding.tvTargetLabel.setText(response.body().getResult().getTargetType());
                            binding.tvTargetAchieved.setText(response.body().getResult().getAchieved() + " " + sales_types);
                            binding.tvTargetTotal.setText(response.body().getResult().getSalesTarget() + " " + sales_types);
                            binding.tvVisited.setText(response.body().getResult().getVisited().toString());
                            binding.tvTargetRemaining.setText(remaining.toString() + " " + sales_types);
                            binding.circleProgressView.setTextEnabled(false);
                            binding.circleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
                            binding.circleProgressView.setStartAngle(10);
                            binding.circleProgressView.setProgressWithAnimation(completePersentage, 2000);


                        } else if (response.code() == 401) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(OutletDetailsActivity.this,
                                    "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(OutletDetailsActivity.this,
                                    "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });

    }


    void loadAdvanceMoney() {
        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.getAdvancePayments(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AdvancedPaymentResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<AdvancedPaymentResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Log.d("tareq_test", "" + new Gson().toJson(response.body().getAdvancePayments()));
                            assert response.body() != null;
                            mAdvancePaymentResponseList.clear();
                            mAdvancePaymentResponseList.addAll(response.body().getAdvancePayments());
                            mAdvancePaymentAdapter.notifyDataSetChanged();

                            binding.textInputLayouTotalAmount.getEditText().setText(String.valueOf(response.body().getTotalPaid()));

                        } else if (response.code() == 401) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(OutletDetailsActivity.this,
                                    "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OutletDetailsActivity.this, "Internal server error.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                        if(NetworkUtility.isNetworkAvailable(mContext))
                        Toast.makeText(OutletDetailsActivity.this,
                                "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    void loadOutstandingDue() {
        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.getOutstandingDues(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<OutstandingDueResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OutstandingDueResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Log.d("tareq_test", "" + new Gson().toJson(response.body()));
                            if (response.body() != null) {


                                binding.textViewDueInput.setText(String.valueOf(response.body().getDue()));
                                binding.textViewPaidInp.setText(String.valueOf(response.body().getPaid()));




                                Executors.newSingleThreadExecutor().execute(()->{
                                    if(mLalteerRoomDb.returnOutstandingPaymentLadger().getOutstandingPaymentLedger(outlet.outletId)==null)
                                    mLalteerRoomDb.returnOutstandingPaymentLadger().insertOutstandingPaymentLedger(new OutstandingPaymentLedger(outlet.outletId, response.body().getPaid(), response.body().getDue()));
                                    else
                                        mLalteerRoomDb.returnOutstandingPaymentLadger().updateOutstandingPaymentLedger(new OutstandingPaymentLedger(outlet.outletId, response.body().getPaid(), response.body().getDue()));
                                });


                            }
                        } else if (response.code() == 401) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(OutletDetailsActivity.this,
                                    "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OutletDetailsActivity.this, "Internal server error.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                        if(NetworkUtility.isNetworkAvailable(mContext))
                        Toast.makeText(OutletDetailsActivity.this,
                                "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST  && resultCode == Activity.RESULT_OK){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);

            final Uri imageUri = outputUri;
            final InputStream imageStream;

            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap photo = BitmapFactory.decodeStream(imageStream);

                int factor = 10;
                while (photo.getWidth() >= 1200) {
                    photo = Bitmap.createScaledBitmap(photo, (Math.round(photo.getWidth() * 0.1f * factor)), (Math.round(photo.getHeight() * 0.1f * factor)), false);
                    factor--;
                }


                Log.d("tareq_test", "InspectionActivity #205: onActivityResult:  " + photo.getHeight() + " , " + photo.getWidth());
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outstandingPaymentImg = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);


                Glide.with(mContext)
                        .load(decodeBase64(outstandingPaymentImg))
                        // .transform(new CircleCrop())
                        .into(binding.imageButton);


               /* SharedPreferences sharedPreferencesLanguage = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
                editor.putString("img", outstandingPaymentImg);
                editor.apply();*/


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
    }


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
}
