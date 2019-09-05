package com.humaclab.lalteer.activity;

import android.app.ProgressDialog;
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
import com.humaclab.lalteer.adapters.SellsReturnRecyclerAdapter;
import com.humaclab.lalteer.model.DeliveryResponse;
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

public class SalesReturnActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srl_sells_return;
    private RecyclerView rv_return_list;
    private int outletID;
    private ProgressDialog pd;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Sells Return List");
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(SalesReturnActivity.this);
        pd = new ProgressDialog(this);

        outletID = getIntent().getIntExtra("outletID", 0);

        rv_return_list = (RecyclerView) findViewById(R.id.rv_return_list);
        rv_return_list.setLayoutManager(new LinearLayoutManager(this));

        srl_sells_return = (SwipeRefreshLayout) findViewById(R.id.srl_sells_return);
        srl_sells_return.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnActivity.this)) {
                    loadReturns();
                } else {
                    Toast.makeText(SalesReturnActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadReturns();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReturns() {
        pd.setMessage("Loading sales returns.....");
        pd.show();

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
         apiService.getSalesReturn(outletID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new SingleObserver<Response<DeliveryResponse>>() {
                     @Override
                     public void onSubscribe(Disposable d) {
                         if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
                     }

                     @Override
                     public void onSuccess(Response<DeliveryResponse> response) {
                         pd.dismiss();
                         if (response.code() == 200) {
                             try {
                                 if (srl_sells_return.isRefreshing())
                                     srl_sells_return.setRefreshing(false);

                                 System.out.println("Return Response " + new Gson().toJson(response.body()));
                                 List<DeliveryResponse.DeliveryList> delivers = response.body().result.deliveryList;
                                 rv_return_list.setAdapter(new SellsReturnRecyclerAdapter(getApplication(), delivers));
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         } else if (response.code() == 401) {
                             Toast.makeText(SalesReturnActivity.this,
                                     "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(SalesReturnActivity.this,
                                     "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onError(Throwable e) {
                         pd.dismiss();
                     }
                 });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
    }
}
