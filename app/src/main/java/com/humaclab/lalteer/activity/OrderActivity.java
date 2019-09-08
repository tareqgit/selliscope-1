package com.humaclab.lalteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.adapters.OrderProductRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityOrderBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;

import com.humaclab.lalteer.model.variant_product.Brand;
import com.humaclab.lalteer.model.variant_product.Category;
import com.humaclab.lalteer.model.variant_product.ProductsItem;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class OrderActivity extends AppCompatActivity implements OrderProductRecyclerAdapter.OnSelectProductListener {

    private ActivityOrderBinding binding;
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private String outletName, outletID, outletType;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();

    public static List<SelectedProductHelper> selectedProductList = new ArrayList<>();

    public CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    // Save state
    private Parcelable recyclerViewState; //for storing recycler scroll postion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        context = OrderActivity.this;
        binding.srlProduct.setColorSchemeColors(Color.parseColor("#EA5455"), Color.parseColor("#FCCF31"), Color.parseColor("#F55555"));

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");
        outletType = getIntent().getStringExtra("outletType");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(outletName + "-" + getResources().getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(OrderActivity.this);

        binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 2));


        //  getProducts();

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()), s.toString())
                            .subscribeOn(Schedulers.io())
                            .flatMapIterable(new Function<List<ProductsItem>, List<ProductsItem>>() {

                                @Override
                                public List<ProductsItem> apply(List<ProductsItem> productsItems) throws Exception {
                                    return productsItems;
                                }
                            })
                            .filter(new Predicate<ProductsItem>() {
                                @Override
                                public boolean test(ProductsItem productsItem) throws Exception {
                                    return productsItem.getStatus().equals("1");
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<ProductsItem>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    mCompositeDisposable.add(d);
                                }

                                @Override
                                public void onSuccess(List<ProductsItem> productsItems) {
                                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItems, selectedProductList, OrderActivity.this));

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                } else {
                    databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()))
                            .subscribeOn(Schedulers.io())
                            .flatMapIterable(new Function<List<ProductsItem>, List<ProductsItem>>() {

                                @Override
                                public List<ProductsItem> apply(List<ProductsItem> productsItems) throws Exception {
                                    return productsItems;
                                }
                            })
                            .filter(new Predicate<ProductsItem>() {
                                @Override
                                public boolean test(ProductsItem productsItem) throws Exception {
                                    return productsItem.getStatus().equals("1");
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<ProductsItem>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    mCompositeDisposable.add(d);
                                }

                                @Override
                                public void onSuccess(List<ProductsItem> productsItems) {
                                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItems, selectedProductList, OrderActivity.this));

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(OrderActivity.this, mCompositeDisposable);


        binding.srlProduct.setOnRefreshListener(() -> {
            //if network is Available then update the data again
            if (NetworkUtility.isNetworkAvailable(OrderActivity.this)) {

                binding.progressBar.setVisibility(View.VISIBLE);
                loadLocalIntoBackground.loadCategory(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        loadLocalIntoBackground.loadBrand(new LoadLocalIntoBackground.LoadCompleteListener() {
                            @Override
                            public void onLoadComplete() {

                                getCategory();
                                getBrand();
                                getProducts();
                                binding.progressBar.setVisibility(View.GONE);
                                Log.d("tareq_test", "Loading data complete");
                                binding.srlProduct.setRefreshing(false);
                            }

                            @Override
                            public void onLoadFailed(String reason) {
                                Log.e("tareq_test", "Loading data  brand failed " + reason);
                                Toast.makeText(context, "Loading Interrupted \n" +
                                        "Please Pull Down to Reload", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                                binding.srlProduct.setRefreshing(false);
                            }
                        });

                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        Log.e("tareq_test", "Loading data category failed " + reason);
                        Toast.makeText(context, "Loading Interrupted \n" +
                                "Please Pull Down to Reload", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.srlProduct.setRefreshing(false);
                    }
                });


            }

        });

        //if network is Available then update the data again
        if (NetworkUtility.isNetworkAvailable(OrderActivity.this)) {

            binding.progressBar.setVisibility(View.VISIBLE);
            loadLocalIntoBackground.loadCategory(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    loadLocalIntoBackground.loadBrand(new LoadLocalIntoBackground.LoadCompleteListener() {
                        @Override
                        public void onLoadComplete() {

                            getCategory();
                            getBrand();
                            getProducts();
                            binding.progressBar.setVisibility(View.GONE);
                            Log.d("tareq_test", "Loading data complete");
                        }

                        @Override
                        public void onLoadFailed(String reason) {
                            Log.e("tareq_test", "Loading data  brand failed " + reason);
                            Toast.makeText(context, "Loading Interrupted \n" +
                                    "Please Pull Down to Reload", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });

                }

                @Override
                public void onLoadFailed(String reason) {
                    Log.e("tareq_test", "Loading data category failed " + reason);
                    Toast.makeText(context, "Loading Interrupted \n" +
                            "Please Pull Down to Reload", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            });


        } else {
            getCategory();
            getBrand();
            getProducts();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrderProductRecyclerAdapter != null) {
            mOrderProductRecyclerAdapter.notifyDataSetChanged(); //we are Notify to update recycler view cz if we delete any Item from cart should be live in this activity also

            // Restore state
            binding.rvProduct.getLayoutManager().onRestoreInstanceState(recyclerViewState); //we are restoring recycler position
        }
        updateTotal_Discount_Grnd(); //need to update snackbar on resume
    }

    private void getCategory() {
        List<Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add(getString(R.string.select_category));
        categoryID.add(0);
        for (Category result : categories) {
            categoryName.add(result.getName());
            categoryID.add(Integer.valueOf(result.getId()));
        }
        binding.spProductCategory.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, categoryName));
        binding.spProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    //binding.srlProduct.setRefreshing(false);
                    databaseHandler.getProduct(categoryID.get(position), brandID.get(binding.spProductBrand.getSelectedItemPosition()))
                            .subscribeOn(Schedulers.io())
                            .flatMapIterable(new Function<List<ProductsItem>, List<ProductsItem>>() {

                                @Override
                                public List<ProductsItem> apply(List<ProductsItem> productsItems) throws Exception {
                                    return productsItems;
                                }
                            })
                            .filter(new Predicate<ProductsItem>() {
                                @Override
                                public boolean test(ProductsItem productsItem) throws Exception {
                                    return productsItem.getStatus().equals("1");
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<ProductsItem>>() {
                                           @Override
                                           public void onSubscribe(Disposable d) {
                                               mCompositeDisposable.add(d);
                                           }

                                           @Override
                                           public void onSuccess(List<ProductsItem> productsItems) {

                                               binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItems, selectedProductList, OrderActivity.this));

                                           }

                                           @Override
                                           public void onError(Throwable e) {

                                           }
                                       }

                            );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<Brand> getBrandsAsOutletType() {
        List<Brand> brands = new ArrayList<>();
        List<Brand> brandsFromDb = databaseHandler.getBrand();
        if (outletType.equalsIgnoreCase("ccsl-pesticide")) {
            for (Brand brand : brandsFromDb) {
                if (brand.getName().equalsIgnoreCase("pes"))
                    brands.add(brand);
            }
        } else if (outletType.equalsIgnoreCase("rice-nssl")) {
            for (Brand brand : brandsFromDb) {
                if (brand.getName().equalsIgnoreCase("cer-r"))
                    brands.add(brand);
            }
        } else if (outletType.equalsIgnoreCase("ltsl-veg")) {
            for (Brand brand : brandsFromDb) {
                if (!brand.getName().equalsIgnoreCase("pes") && !brand.getName().equalsIgnoreCase("cer-r")) {
                    brands.add(brand);
                }

            }
        } else {
            brands.addAll(brandsFromDb);
        }

        return brands;
    }

    private void getBrand() {

        brandName.clear();
        brandName.add(getString(R.string.select_brand));
        brandID.add(0);
        for (Brand result : getBrandsAsOutletType()) {
            brandName.add(result.getName());
            brandID.add(Integer.valueOf(result.getId()));
        }
        binding.spProductBrand.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, brandName));
        binding.spProductBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    // binding.srlProduct.setRefreshing(false);
                    databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(position))
                            .subscribeOn(Schedulers.io())
                            .flatMapIterable(new Function<List<ProductsItem>, List<ProductsItem>>() {

                                @Override
                                public List<ProductsItem> apply(List<ProductsItem> productsItems) throws Exception {
                                    return productsItems;
                                }
                            })
                            .filter(new Predicate<ProductsItem>() {
                                @Override
                                public boolean test(ProductsItem productsItem) throws Exception {
                                    return productsItem.getStatus().equals("1");
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<ProductsItem>>() {
                                           @Override
                                           public void onSubscribe(Disposable d) {
                                               mCompositeDisposable.add(d);
                                           }

                                           @Override
                                           public void onSuccess(List<ProductsItem> productsItems) {
                                               binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItems, selectedProductList, OrderActivity.this));

                                           }

                                           @Override
                                           public void onError(Throwable e) {

                                           }
                                       }

                                  );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (brandName.size() > 1) {
            binding.spProductBrand.setSelection(1);
        }
    }

    OrderProductRecyclerAdapter mOrderProductRecyclerAdapter;

    private void getProducts() {

        // binding.srlProduct.setRefreshing(false);


        databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()))
                .subscribeOn(Schedulers.io())
                .flatMapIterable(new Function<List<ProductsItem>, List<ProductsItem>>() {

                    @Override
                    public List<ProductsItem> apply(List<ProductsItem> productsItems) throws Exception {
                        return productsItems;
                    }
                })
                .filter(new Predicate<ProductsItem>() {
                    @Override
                    public boolean test(ProductsItem productsItem) throws Exception {
                        return productsItem.getStatus().equals("1");
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ProductsItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<ProductsItem> productsItems) {
                        mOrderProductRecyclerAdapter = new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItems, selectedProductList, OrderActivity.this);

                        binding.rvProduct.setAdapter(mOrderProductRecyclerAdapter);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                } );


        //if this activity called from product activity
        updateTotal_Discount_Grnd();
    }

    void updateTotal_Discount_Grnd() {

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            //totalDiscount += Double.valueOf(selectedProductHelper.getTotalPrice());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        //binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));

    }

    @Override
    public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
        for (Iterator<SelectedProductHelper> iterator = selectedProductList.iterator(); iterator.hasNext(); ) {
            SelectedProductHelper selected = iterator.next();
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductID().equalsIgnoreCase(selectedProduct.getProductID())) {
                Log.d("tareq_test", "product matched" + selected.getProductName());
                iterator.remove();
            }
        }

        /*for (SelectedProductHelper selected : selectedProductList) {
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductID().equalsIgnoreCase(selectedProduct.getProductID())) {
                Log.d("tareq_test", "product matched" + selected.getProductName());

                selectedProductList.remove(selected);
            }
        }*/
     /*   if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
            selectedProductList.add(selectedProductList.indexOf(selectedProduct), selectedProduct);
        } else*/

        selectedProductList.add(selectedProduct);
        Log.d("tareq_test", "product added in selected product list :" + selectedProductList.size());

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            //totalDiscount += Double.valueOf(selectedProductHelper.getTotalPrice());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        //binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));

        //   getProducts();//for refreshing
    }

    @Override
    public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {
        if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
        }

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;

        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());

        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 2));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        //clear selected Item list
        selectedProductList.clear();
        super.onDestroy();

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear selected Item list
        selectedProductList.clear();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_cart:
                if (!selectedProductList.isEmpty()) {
                    Intent intent = new Intent(OrderActivity.this, ActivityCart.class);
                    intent.putExtra("outletID", outletID);
                    intent.putExtra("outletName", outletName);
                    intent.putExtra("products", (Serializable) selectedProductList);


                    recyclerViewState = binding.rvProduct.getLayoutManager().onSaveInstanceState(); //before moving to another activity store recycler state

                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No product selected yet.\nPlease select some product first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
