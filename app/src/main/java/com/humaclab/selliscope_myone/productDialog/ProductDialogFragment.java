/*
 * Created by Tareq Islam on 3/19/19 4:48 PM
 *
 *  Last modified 3/19/19 4:48 PM
 */

package com.humaclab.selliscope_myone.productDialog;



import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.model.variantProduct.ProductsItem;
import com.humaclab.selliscope_myone.outlet_paging.OutletInjection;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletAdapter;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletSearchViewModel;
import com.humaclab.selliscope_myone.product_paging.ProductInjection;
import com.humaclab.selliscope_myone.product_paging.ui.ProductAdapter;
import com.humaclab.selliscope_myone.product_paging.ui.ProductSearchViewModel;
import com.humaclab.selliscope_myone.utils.Constants;
import com.humaclab.selliscope_myone.utils.DatabaseHandler;
import com.humaclab.selliscope_myone.utils.SessionManager;
import com.humaclab.selliscope_myone.utils.StockResponse;
import com.humaclab.selliscope_myone.utils.ViewDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 19,March,2019.
 */
public class ProductDialogFragment extends DialogFragment implements ProductAdapter.OnItemClickListener {
  public   List<ProductsItem> products = new ArrayList<>();

  EditText mEditText;
    RecyclerView mRecyclerView;
    ProductDialogAdapter mProductDialogAdapter;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;

    private SelliscopeApiEndpointInterface apiService;

    //Bundle constant to save the last searched query
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    //The default query to load
    private static final String DEFAULT_QUERY = "";
    private ProductSearchViewModel mViewModel;
    private ProductAdapter mAdapter;
    private ProgressBar mProgressBar;
    ViewDialog mViewDialog;
    TextView mEmptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getActivity());
        this.sessionManager = new SessionManager(getContext());

        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        products=databaseHandler.getProduct(0,0);

        mViewDialog = new ViewDialog(getActivity());

      //  setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView=inflater.inflate(R.layout.product_dialog_fragment,container);

        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> cancelUpload());
        toolbar.setTitle("My Dialog");*/
        mViewModel = ViewModelProviders.of(this, ProductInjection.provideViewModelFactory(getContext())).get(ProductSearchViewModel.class);

        //emptyView
        mEmptyView= rootView.findViewById(R.id.emptyList);
        mEmptyView.setText(getString(R.string.no_results, "\uD83D\uDE13"));

        //progressBar
        mProgressBar = rootView.findViewById(R.id.ProgressBar);

        //searchView
        mEditText = rootView.findViewById(R.id.searchProduct_EditText);
        //RECYCER
        mRecyclerView= (RecyclerView) rootView.findViewById(R.id.mRecyerID);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));

        /*//ADAPTER
        mProductDialogAdapter=new ProductDialogAdapter(this.getActivity(),products,this);
        mRecyclerView.setAdapter(mProductDialogAdapter);*/

        //for pagination
        initAdapter();

        //Get the query to search
        String query = DEFAULT_QUERY;
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY, DEFAULT_QUERY);
        }

        //Post the query to be searched
        mViewModel.searchProducts(query);

        //Initialize the EditText for Search Actions
        initSearch(query);

        this.getDialog().setTitle("Products");

        rootView.findViewById(R.id.caneclImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


     /*   ( (EditText) rootView.findViewById(R.id.searchProduct_EditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    products = databaseHandler.getSearchedProduct(s.toString());
                     mProductDialogAdapter.updateData(products);
                     mProductDialogAdapter.notifyDataSetChanged();
                } else {
                        products=databaseHandler.getProduct(0,0);
                    mProductDialogAdapter.updateData(products);
                    mProductDialogAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, mViewModel.lastQueryValue());
    }

    /**
     * Initializes the EditText for handling the Search actions
     *
     * @param query The query to be searched for in the Repositories
     */
    private void initSearch(String query) {
        mEditText.setText(query);



        mEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });

        mEditText.setOnKeyListener((view, keyCode, event) -> {
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
        String queryEntered = mEditText.getText().toString().trim();
         if (!TextUtils.isEmpty(queryEntered)) {
            mRecyclerView.scrollToPosition(0);
            //Posts the query to be searched
            mViewModel.searchProducts(queryEntered);
            //Resets the old list
            mAdapter.submitList(null);
        }
    }



    private void initAdapter() {
        mAdapter = new ProductAdapter(getContext(), getActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

        //Subscribing to receive the new PagedList Repos
        mViewModel.getRepos().observe(this, repos -> {
            if (repos != null) {
                Log.d("tareq_test", "initAdapter: Repo List size: " + repos.size());
                showEmptyList(repos.size() == 0);
                mAdapter.submitList(repos);
            }
        });

        //Subscribing to receive the recent Network Errors if any
        mViewModel.getNetworkErrors().observe(this, errorMsg -> {
            Toast.makeText(getContext(), "\uD83D\uDE28 Wooops " + errorMsg, Toast.LENGTH_LONG).show();
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









    @Override
    public void onClick(com.humaclab.selliscope_myone.product_paging.model.ProductsItem productsItem) {
      // Toast.makeText(getActivity().getApplicationContext(), ""+ productsItem.getId(), Toast.LENGTH_SHORT).show();
        OrderActivity orderActivity=(OrderActivity) getActivity();
     //   orderActivity.addProduct(productID.get(sp_product_name.getSelectedItemPosition()), isVariant[0], false, null);
        for ( OrderActivity.SelectedProduct selectedProduct:  orderActivity.mSelectedProducts   ) {
            if(selectedProduct.id.equals(productsItem.id)) {
                Toast.makeText(orderActivity, "Product already selected", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        mViewDialog.showDialog();
        Call<StockResponse> call1 = apiService.getProductStock(productsItem.id);
        call1.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {


                if(response.isSuccessful()) {
                    //for testing as data is not coming
                  //  Toast.makeText(orderActivity, ""+Double.parseDouble(response.body().getStock().getStock().toString()), Toast.LENGTH_SHORT).show();
                    if (Double.parseDouble(response.body().getStock().getStock().toString()) > 0) {


                            orderActivity.addProduct(productsItem, Double.parseDouble(response.body().getStock().getStock().toString()));
                            dismiss();

                    } else {
                        Toast.makeText(orderActivity, "Not enough product in stock", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(orderActivity, "Internal Server Error: "+ response.code(), Toast.LENGTH_SHORT).show();
                }

                mViewDialog.hideDialog();
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {
                Toast.makeText(orderActivity, "Can't establish connection to Server", Toast.LENGTH_SHORT).show();
                mViewDialog.hideDialog();
            }
        });



    }

    /**
     * Shows the Empty view when the list is empty
     *
     * @param show Displays the empty view and hides the list when the boolean is <b>True</b>
     */
    private void showEmptyList(boolean show) {
        if (show) {
          mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

}