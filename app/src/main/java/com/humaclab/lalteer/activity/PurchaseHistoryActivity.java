package com.humaclab.lalteer.activity;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.PurchaseHistoryRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityPurchaseHistoryBinding;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.purchase_history.PurchaseHistoryItem;
import com.humaclab.lalteer.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseHistoryActivity extends AppCompatActivity {

    enum STATUS {ACCEPTED, PENDING, REJECT, CANCELLED}

    ;

    private ActivityPurchaseHistoryBinding binding;
    private Outlets.Outlet outlet;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PurchaseHistoryRecyclerAdapter mPurchaseHistoryRecyclerAdapter;
    private List<PurchaseHistoryItem> mPurchaseHistoryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_history);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.Purchase_History) + outlet.outletName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        binding.rlPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));


        mPurchaseHistoryRecyclerAdapter = new PurchaseHistoryRecyclerAdapter(PurchaseHistoryActivity.this, mPurchaseHistoryItems, outlet);
        binding.rlPurchaseHistory.setAdapter(mPurchaseHistoryRecyclerAdapter);

        binding.srlPurchaseHistory.setOnRefreshListener(() -> getPurchaseHistory(STATUS.PENDING));
        binding.srlPurchaseHistory.setRefreshing(true);

        binding.btnPayment.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseHistoryActivity.this, PaymentActivity.class);
            intent.putExtra("outletID", outlet.outletId);
            startActivity(intent);
        });

        binding.btnOrder.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseHistoryActivity.this, OrderActivity.class);
            intent.putExtra("outletName", outlet.outletName);
            intent.putExtra("outletID", String.valueOf(outlet.outletId));
            intent.putExtra("outletType", outlet.outletType);
            startActivity(intent);
        });
        getPurchaseHistory(STATUS.PENDING);
    }

    private void getPurchaseHistory(STATUS status) {

        switch (status) {
            case ACCEPTED:
                apiService.getPurchaseHistoryAccepted(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Response<PurchaseHistoryResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Response<PurchaseHistoryResponse> response) {
                                binding.tvTotalPaid.setText(response.body() != null ? response.body().getResult().getTotalPaid() : null);
                                binding.tvTotalDue.setText(response.body() != null ? response.body().getResult().getTotalDue() : null);
                                binding.srlPurchaseHistory.setRefreshing(false);
                                mPurchaseHistoryItems = response.body() != null ? response.body().getResult().getPurchaseHistory() : null;
                                mPurchaseHistoryRecyclerAdapter.updateData(mPurchaseHistoryItems);
                                Log.d("tareq_test", "Res " + response.code() + "  " + new Gson().toJson(response.body() != null ? response.body().getResult() : null));
                                if(response.body().getResult().getPurchaseHistory().size()==0)
                                    Toast.makeText(PurchaseHistoryActivity.this, "No Accepted Order found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable t) {
                                binding.srlPurchaseHistory.setRefreshing(false);
                                t.printStackTrace();
                                Log.e("tareq_test", "Error: " + t.getMessage());
                            }
                        });
                break;
            case PENDING:
                apiService.getPurchaseHistoryPending(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Response<PurchaseHistoryResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Response<PurchaseHistoryResponse> response) {
                                binding.tvTotalPaid.setText(response.body() != null ? response.body().getResult().getTotalPaid() : null);
                                binding.tvTotalDue.setText(response.body() != null ? response.body().getResult().getTotalDue() : null);
                                binding.srlPurchaseHistory.setRefreshing(false);
                                mPurchaseHistoryItems = response.body() != null ? response.body().getResult().getPurchaseHistory() : null;
                                mPurchaseHistoryRecyclerAdapter.updateData(mPurchaseHistoryItems);
                                Log.d("tareq_test", "Res " + response.code() + "  " + new Gson().toJson(response.body() != null ? response.body().getResult() : null));
                                if(response.body().getResult().getPurchaseHistory().size()==0)
                                    Toast.makeText(PurchaseHistoryActivity.this, "No Pending Order found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable t) {
                                binding.srlPurchaseHistory.setRefreshing(false);
                                t.printStackTrace();
                                Log.e("tareq_test", "Error: " + t.getMessage());
                            }
                        });
                break;
            case REJECT:
                apiService.getPurchaseHistoryRejected(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Response<PurchaseHistoryResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Response<PurchaseHistoryResponse> response) {
                                binding.tvTotalPaid.setText(response.body() != null ? response.body().getResult().getTotalPaid() : null);
                                binding.tvTotalDue.setText(response.body() != null ? response.body().getResult().getTotalDue() : null);
                                binding.srlPurchaseHistory.setRefreshing(false);
                                mPurchaseHistoryItems = response.body() != null ? response.body().getResult().getPurchaseHistory() : null;
                                mPurchaseHistoryRecyclerAdapter.updateData(mPurchaseHistoryItems);
                                Log.d("tareq_test", "Res " + response.code() + "  " + new Gson().toJson(response.body() != null ? response.body().getResult() : null));
                                if(response.body().getResult().getPurchaseHistory().size()==0)
                                    Toast.makeText(PurchaseHistoryActivity.this, "No Rejected Order found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable t) {
                                binding.srlPurchaseHistory.setRefreshing(false);
                                t.printStackTrace();
                                Log.e("tareq_test", "Error: " + t.getMessage());
                            }
                        });
                break;
            case CANCELLED:
                apiService.getPurchaseHistoryCancelled(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Response<PurchaseHistoryResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Response<PurchaseHistoryResponse> response) {
                                binding.tvTotalPaid.setText(response.body() != null ? response.body().getResult().getTotalPaid() : null);
                                binding.tvTotalDue.setText(response.body() != null ? response.body().getResult().getTotalDue() : null);
                                binding.srlPurchaseHistory.setRefreshing(false);
                                mPurchaseHistoryItems = response.body() != null ? response.body().getResult().getPurchaseHistory() : null;
                                mPurchaseHistoryRecyclerAdapter.updateData(mPurchaseHistoryItems);
                                Log.d("tareq_test", "Res " + response.code() + "  " + new Gson().toJson(response.body() != null ? response.body().getResult() : null));
                                if(response.body().getResult().getPurchaseHistory().size()==0)
                                    Toast.makeText(PurchaseHistoryActivity.this, "No cancelled Order found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable t) {
                                binding.srlPurchaseHistory.setRefreshing(false);
                                t.printStackTrace();
                                Log.e("tareq_test", "Error: " + t.getMessage());
                            }
                        });
                break;

        }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.purchase_status_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.accepted:
                getPurchaseHistory(STATUS.ACCEPTED);
                break;
            case R.id.pending:
                getPurchaseHistory(STATUS.PENDING);
                break;
            case R.id.rejected:
                getPurchaseHistory(STATUS.REJECT);
                break;
            case R.id.cancelled:
                getPurchaseHistory(STATUS.CANCELLED);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
