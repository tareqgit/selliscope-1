package com.humaclab.lalteer.activity;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.DueRecyclerViewAdapter;
import com.humaclab.lalteer.model.Payment;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DueActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private RecyclerView rv_due;
    private SwipeRefreshLayout srl_due;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Dues");
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);

        rv_due = (RecyclerView) findViewById(R.id.rv_due);
        rv_due.setLayoutManager(new LinearLayoutManager(this));
        srl_due = (SwipeRefreshLayout) findViewById(R.id.srl_due);
        srl_due.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(DueActivity.this)) {
                    loadDues();
                } else {
                    Toast.makeText(DueActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadDues();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDues() {
        SessionManager sessionManager = new SessionManager(DueActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        apiService.getPayment().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<Payment>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<Payment> response) {
                        if (response.code() == 200) {
                            try {
                                if (srl_due.isRefreshing())
                                    srl_due.setRefreshing(false);

                                System.out.println("Response " + new Gson().toJson(response.body()));
                                List<Payment.OrderList> orders = null;
                                if (response.body() != null) {
                                    orders = response.body().result.orderList;
                                }

                                rv_due.setAdapter(new DueRecyclerViewAdapter(getApplication(), orders));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (response.code() == 401) {
                            Toast.makeText(DueActivity.this,
                                    "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DueActivity.this,
                                    "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable!=null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
        }
    }
}
