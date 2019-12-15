package com.humaclab.selliscope.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.SalesReturnHistoryRecyclerAdapter;
import com.humaclab.selliscope.model.sales_return.SalesReturnHistory;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReturnHistoryActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srl_sells_return_history;
    private RecyclerView rv_return_list_history;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_history);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Sells Return History List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sessionManager = new SessionManager(SalesReturnHistoryActivity.this);
        pd = new ProgressDialog(this);


        rv_return_list_history = (RecyclerView) findViewById(R.id.rv_return_list_history);
        rv_return_list_history.setLayoutManager(new LinearLayoutManager(this));


        srl_sells_return_history = (SwipeRefreshLayout) findViewById(R.id.srl_sells_return_history);
        srl_sells_return_history.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnHistoryActivity.this)) {
                    loadSalesReturnHistory();

                } else {
                    Toast.makeText(SalesReturnHistoryActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();

                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadSalesReturnHistory();

        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();

        }

    }



    private void loadSalesReturnHistory(){

        pd.setMessage("Loading History");
        pd.show();


        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(SelliscopeApiEndpointInterface.class);
        Call<SalesReturnHistory> salesReturnHistoryCall = apiService.getSalesReturnHistory();
        salesReturnHistoryCall.enqueue(new Callback<SalesReturnHistory>() {
            @Override
            public void onResponse(Call<SalesReturnHistory> call, Response<SalesReturnHistory> response) {
                pd.dismiss();
                if(response.code()== 200){
                    List<SalesReturnHistory.Result> resultList = response.body().results;
                    rv_return_list_history.setAdapter(new SalesReturnHistoryRecyclerAdapter(getApplication(),resultList));

                }
                else {
                    Toast.makeText(SalesReturnHistoryActivity.this,
                            response.code()+" Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<SalesReturnHistory> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
            }
        });



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
