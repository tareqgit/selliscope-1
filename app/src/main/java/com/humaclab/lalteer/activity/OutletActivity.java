package com.humaclab.lalteer.activity;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.OutletRecyclerViewAdapter;
import com.humaclab.lalteer.databinding.ActivityOutletBinding;
import com.humaclab.lalteer.model.outlets.Outlets;
import com.humaclab.lalteer.model.route_plan.RouteDetailsResponse;
import com.humaclab.lalteer.model.route_plan.RouteResponse;
import com.humaclab.lalteer.model.checked_in_dealer.CheckedInDealerResponse;
import com.humaclab.lalteer.performance.claim.ClaimActivity;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;
import com.humaclab.lalteer.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class OutletActivity extends AppCompatActivity {
    private ActivityOutletBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private OutletRecyclerViewAdapter outletRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    List<Outlets.Outlet> outlets = new ArrayList<>();
    private LoadLocalIntoBackground loadLocalIntoBackground;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MutableLiveData<Integer> sTotalCheckIn = new MutableLiveData<>();

    // Save state
    private Parcelable recyclerViewState; //for storing recycler scroll position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        loadLocalIntoBackground = new LoadLocalIntoBackground(this, mCompositeDisposable);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton addOutlet = findViewById(R.id.fab_add_outlet);

        addOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OutletActivity.this, AddOutletActivity.class));
            }
        });

        //  new AddOutletActivity().setOnOutletCreateListener(this);

        getRoute(); // For getting route plan data

        binding.rvOutlet.addItemDecoration(new VerticalSpaceItemDecoration(20));
        binding.rvOutlet.setLayoutManager(new LinearLayoutManager(this));

        //   outlets = new ArrayList<>();
        outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outlets);
        binding.rvOutlet.setAdapter(outletRecyclerViewAdapter);

       /* if (!NetworkUtility.isNetworkAvailable(this)) {
            Toast.makeText(this, "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
        }*/

        binding.tvSearchOutlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                outlets.clear();
                outlets = databaseHandler.getSearchedOutlet(String.valueOf(s)).outlets;
                outletRecyclerViewAdapter.updateOutlets(outlets);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.srlOutlet.setOnRefreshListener(() -> {
            if (!NetworkUtility.isNetworkAvailable(OutletActivity.this)) {
                Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
                binding.srlOutlet.setRefreshing(false);
            }

            loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    Toast.makeText(OutletActivity.this, "Data load complete", Toast.LENGTH_SHORT).show();
                    getOutlets();
                }

                @Override
                public void onLoadFailed(String reason) {
                    Toast.makeText(OutletActivity.this, "Data Load failed: " + reason, Toast.LENGTH_SHORT).show();
                }
            });

        });


        binding.tvCheckInCount.setOnClickListener(v -> startActivity(new Intent(OutletActivity.this, ClaimActivity.class)));


          getOutlets();


    }

    private void loadOutlets() {
        if (NetworkUtility.isNetworkAvailable(OutletActivity.this)) {
            loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    Toast.makeText(OutletActivity.this, "Data load complete", Toast.LENGTH_SHORT).show();
                    getOutlets();
                }

                @Override
                public void onLoadFailed(String reason) {
                    Toast.makeText(OutletActivity.this, "Data Load failed: " + reason, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getOutlets();
            Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTotalCheckIn() {
        apiService.getCheckedInDealers().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Response<CheckedInDealerResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onSuccess(Response<CheckedInDealerResponse> checkedInDealerResponseResponse) {

                List<Integer> outletIDLists = null;
                if (checkedInDealerResponseResponse.body() != null) {
                    outletIDLists = new ArrayList<>(checkedInDealerResponseResponse.body().getDealerIds());
                    sTotalCheckIn.setValue(outletIDLists.size());

                }


            }

            @Override
            public void onError(Throwable e) {
                Log.e("tareq_test", "OutletActivity #194: onError:  " + e);
            }
        });
    }


    public void getOutlets() {
        outlets.clear();
        outlets = databaseHandler.getAllOutlet().outlets;
        if (!outlets.isEmpty()) {
            if (binding.srlOutlet.isRefreshing())
                binding.srlOutlet.setRefreshing(false);

            outletRecyclerViewAdapter.updateOutlets(outlets);

            getTotalCheckIn();

            sTotalCheckIn.observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer no) {
                    binding.tvCheckInCount.setText(String.format(Locale.ENGLISH, "%d ", no));

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "You don't have any Dealer in your list.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * For getting route-plan data
     */
    public void getRoute() {
        apiService.getRoutes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<RouteResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);

                    }

                    @Override
                    public void onSuccess(Response<RouteResponse> response) {
                        if (response.body() != null) {
                            binding.tvToolbarTitle.setText(response.body().getResult().getRoute().size() != 0 ? response.body().getResult().getRoute().get(0).getName() : "Dealers");

                            getRouteDetails(response.body().getResult().getRoute().size() != 0 ? response.body().getResult().getRoute().get(0).getId() : 0);

                        } else {
                            binding.tvToolbarTitle.setText("Outlet");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("tareq_test", "OutletActivity #209: onError:  " + e.getMessage());
                    }
                });

    }

    /**
     * For getting the details of route-plan by its ID
     *
     * @param routeID int
     */
    private void getRouteDetails(int routeID) {
        apiService.getRouteDetails(routeID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<RouteDetailsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<RouteDetailsResponse> response) {
                        final List<RouteDetailsResponse.OutletItem> check = response.body() != null ? response.body().getResult().getOutletItemList() : null;
                        if (response.isSuccessful()) {

                            binding.tvCheckInCount.setText(String.format(Locale.ENGLISH, "%d / %d", response.body() != null ? response.body().getResult().getCheckedOutlet() : 0, response.body() != null ? response.body().getResult().getTotalOutlet() : 0));
                            loadLocalIntoBackground.saveOutletRoutePlan(check);
                            getOutlets(); //For reloading the outlet recycler view
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("tareq_test", "OutletActivity #237: onError:  " + e.getMessage());
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();

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

