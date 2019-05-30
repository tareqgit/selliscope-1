package com.humaclab.selliscope_myone.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.outlet_paging.OutletInjection;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletAdapter;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletSearchViewModel;
import com.humaclab.selliscope_myone.utils.Constants;
import com.humaclab.selliscope_myone.utils.VerticalSpaceItemDecoration;

public class OutletActivity extends AppCompatActivity {
    private EditText tv_search_outlet;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;

    private SwipeRefreshLayout swipeRefreshLayout;
     //Bundle constant to save the last searched query
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    //The default query to load
    private static final String DEFAULT_QUERY = "";
    private OutletSearchViewModel mViewModel;
    private OutletAdapter mOutletAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet);


        mProgressBar = findViewById(R.id.ProgressBar);
        tv_search_outlet = (EditText) findViewById(R.id.tv_search_outlet);

        mViewModel = ViewModelProviders.of(this, OutletInjection.provideViewModelFactory(this)).get(OutletSearchViewModel.class);

       //turned of swipe refresable as its conflict with search
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_outlet);
        swipeRefreshLayout.setEnabled(false);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.outle));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rv_outlet);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        //Initializing Adapter
        initAdapter();


        //Get the query to search
        String query = DEFAULT_QUERY;
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY, DEFAULT_QUERY);
        }

        //Post the query to be searched
        mViewModel.searchRepo(query);

        //Initialize the EditText for Search Actions
        initSearch(query);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, mViewModel.lastQueryValue());
    }


    private void initAdapter() {
        mOutletAdapter = new OutletAdapter(this, this);
        
        recyclerView.setAdapter(mOutletAdapter);

        //Subscribing to receive the new PagedList Repos
        mViewModel.getRepos().observe(this, repos -> {
            if (repos != null) {
                Log.d("tareq_test", "initAdapter: Repo List size: " + repos.size());
                //showEmptyList(repos.size() == 0);
                mOutletAdapter.submitList(repos);
            }
        });

        //Subscribing to receive the recent Network Errors if any
        mViewModel.getNetworkErrors().observe(this, errorMsg -> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops " + errorMsg, Toast.LENGTH_LONG).show();
        });

        //Subscribing to receive the recent Network State if any
        mViewModel.getNetworkStates().observe(this, network_state -> {
            if(network_state.equals(Constants.NETWORK_STATE.LOADING)){
                mProgressBar.setVisibility(View.VISIBLE);
            }else{
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Initializes the EditText for handling the Search actions
     *
     * @param query The query to be searched for in the Repositories
     */
    private void initSearch(String query) {
     tv_search_outlet.setText(query);



      tv_search_outlet.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });

        tv_search_outlet.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Updates the list with the new data when the User entered the query and hit 'enter'
     * or corresponding action to trigger the Search.
     */
    private void updateRepoListFromInput() {
        String queryEntered = tv_search_outlet.getText().toString().trim();
        if (!TextUtils.isEmpty(queryEntered)) {
            recyclerView.scrollToPosition(0);
            //Posts the query to be searched
            mViewModel.searchRepo(queryEntered);
            //Resets the old list
            mOutletAdapter.submitList(null);
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