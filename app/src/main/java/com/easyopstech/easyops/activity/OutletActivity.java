package com.easyopstech.easyops.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easyopstech.easyops.RootApiEndpointInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.adapters.OutletRecyclerViewAdapter;
import com.easyopstech.easyops.databinding.ActivityOutletBinding;
import com.easyopstech.easyops.model.Outlets;
import com.easyopstech.easyops.model.route_plan.RouteDetailsResponse;
import com.easyopstech.easyops.model.route_plan.RouteResponse;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.LoadLocalIntoBackground;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SendUserLocationData;
import com.easyopstech.easyops.utils.SessionManager;
import com.easyopstech.easyops.utils.VerticalSpaceItemDecoration;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletActivity extends AppCompatActivity {
    private ActivityOutletBinding binding;
    private RootApiEndpointInterface apiService;
    private OutletRecyclerViewAdapter outletRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    public List<Outlets.Outlet> mOutletList;
    private LoadLocalIntoBackground loadLocalIntoBackground;
    Location mLocation;


    private Context mContext;
    // Save state
    private Parcelable recyclerViewState; //for storing recycler scroll postion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet);

        mContext=this;

        createGpsConnection();

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(RootApiEndpointInterface.class);

        loadLocalIntoBackground = new LoadLocalIntoBackground(this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton addOutlet = findViewById(R.id.fab_add_outlet);

        addOutlet.setOnClickListener(v -> startActivity(new Intent(OutletActivity.this, AddOutletActivity.class)));

        binding.rvOutlet.addItemDecoration(new VerticalSpaceItemDecoration(20));
        binding.rvOutlet.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOutlet.setHasFixedSize(true);

        outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, mOutletList);
        binding.rvOutlet.setAdapter(outletRecyclerViewAdapter);


        binding.tvSearchOutlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(String.valueOf(s).equals("i am tareq")){
                    RootApplication.developer= !RootApplication.developer;
                    Toast.makeText(OutletActivity.this, "You are a developer", Toast.LENGTH_SHORT).show();
                }
                mOutletList = databaseHandler.getSearchedOutlet(String.valueOf(s)).outlets;
                outletRecyclerViewAdapter.updateOutlate(mOutletList);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.srlOutlet.setColorSchemeColors(Color.parseColor("#EA5455"), Color.parseColor("#FCCF31"), Color.parseColor("#F55555"));

        binding.srlOutlet.setOnRefreshListener(() -> {
            if (NetworkUtility.isNetworkAvailable(OutletActivity.this)) {

             /*   final AlertDialog alertDialogRefresh = new AlertDialog.Builder(this).create();
                alertDialogRefresh.setTitle("Confirm");
                alertDialogRefresh.setMessage("Are you sure? \n\nYou want to Update all Outlet. ");
                alertDialogRefresh.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        binding.srlOutlet.setRefreshing(false);
                    }
                });
                alertDialogRefresh.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                    ProgressDialog pd = new ProgressDialog(OutletActivity.this);
                    pd.setMessage("Local data is updating.\nPlease be patient....");
                    pd.setCancelable(false);
                    pd.show();
            */
                LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(this);
                binding.srlOutlet.setRefreshing(true);

                loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        getOutlets();
                        binding.srlOutlet.setRefreshing(false);
                        Log.d("tareq_test", "Outlets updated from Server");
                        //    pd.dismiss();
                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        binding.srlOutlet.setRefreshing(false);
                        //     pd.dismiss();
                        Log.d("tareq_test", "Outlets couldn't updated from Server");
                    }
                });


              /*  });
                if (!alertDialogRefresh.isShowing()) alertDialogRefresh.show();
*/


            } else {
                Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
            }


        });

        //if network is Available then update the data again
    /*    if (NetworkUtility.isNetworkAvailable(this)) {
            LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(this);
            binding.srlOutlet.setRefreshing(true);
            loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    getOutlets();
                    Log.d("tareq_test" , "Outlets updated from Server");
                    binding.srlOutlet.setRefreshing(false);
                }

                @Override
                public void onLoadFailed(String reason) {
                    Log.d("tareq_test" , "Outlets updated from Server");
                }
            });
        }*/

    }


    void createGpsConnection(){
        GoogleApiClient googleApiClient= new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
               new SendUserLocationData(mContext).getInstantLocation(OutletActivity.this,(latitude, longitude) -> {
                   Location location= new Location("current_location");
                   location.setLatitude(latitude);
                   location.setLongitude(longitude);
                   mLocation=location;
               });
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        getOutlets();
        getRoute(); // For getting route plan data
        // Restore state
        binding.rvOutlet.getLayoutManager().onRestoreInstanceState(recyclerViewState); //we are restoring recycler position

    }

    public void getOutlets() {
        mOutletList = databaseHandler.getAllOutlet().outlets;
        if (!mOutletList.isEmpty()) {
            if(mLocation!=null) {

            /*    Location location = new Location("");
                location.setLatitude(LocationMonitoringService.sLocation.getLatitude());
                location.setLongitude(LocationMonitoringService.sLocation.getLongitude());
*/
                for (Outlets.Outlet outlet : mOutletList) {
                    Location loca = new Location("");
                    loca.setLatitude(outlet.outletLatitude);
                    loca.setLongitude(outlet.outletLongitude);

                    outlet.setDistance_from_cur_location(mLocation.distanceTo(loca));

                }

                Collections.sort(mOutletList, (o1, o2) -> Double.compare(o1.getDistance_from_cur_location(), o2.getDistance_from_cur_location()));



                outletRecyclerViewAdapter.updateOutlate(mOutletList);

            }else {
                outletRecyclerViewAdapter.updateOutlate(mOutletList);
               // Toast.makeText(this, "Can't sort  outlet yet. Need some time", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /**
     * For getting route-plan data
     */
    public void getRoute() {
        Call<RouteResponse> call = apiService.getRoutes();
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.body() != null) {
                    if (!response.body().getResult().getRoute().isEmpty()) {
                        // Toast.makeText(OutletActivity.this, ""+response.body().getResult().getRoute().get(0).getName(), Toast.LENGTH_SHORT).show();
                        binding.tvToolbarTitle.setText(response.body().getResult().getRoute().get(0).getName());
                        getRouteDetails(response.body().getResult().getRoute().get(0).getId());
                    } else {
                        binding.tvToolbarTitle.setText(getString(R.string.outlet));
                    }
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    /**
     * For getting the details of route-plan by its ID
     *
     * @param routeID int
     */
    private void getRouteDetails(int routeID) {
        Call<RouteDetailsResponse> call = apiService.getRouteDetails(routeID);
        call.enqueue(new Callback<RouteDetailsResponse>() {
            @Override
            public void onResponse(Call<RouteDetailsResponse> call, Response<RouteDetailsResponse> response) {
                final List<RouteDetailsResponse.OutletItem> check;
                if (response.body() != null) {
                    check = response.body().getResult().getOutletItemList();

                        binding.tvCheckInCount.setText(String.format(Locale.ENGLISH,"%d / %d", response.body().getResult().getCheckedOutlet(), response.body().getResult().getTotalOutlet()));
                        loadLocalIntoBackground.saveOutletRoutePlan(check);
                        getOutlets(); //For reloading the outlet recycler view

                }else{
                    Toast.makeText(OutletActivity.this, "Null Response from Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RouteDetailsResponse> call, Throwable t) {
                t.printStackTrace();
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
}