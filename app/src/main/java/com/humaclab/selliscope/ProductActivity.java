package com.humaclab.selliscope;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.Utils.DatabaseHandler;
import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.adapters.ProductRecyclerViewAdapter;
import com.humaclab.selliscope.model.BrandResponse;
import com.humaclab.selliscope.model.CategoryResponse;
import com.humaclab.selliscope.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private RecyclerView rv_product;
    private SwipeRefreshLayout srl_product;
    private Spinner sp_category, sp_brand;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private Paint paint = new Paint();

    private List<ProductResponse.ProductResult> productResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.products));
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);
        sp_category = (Spinner) findViewById(R.id.sp_category);
        sp_brand = (Spinner) findViewById(R.id.sp_brand);

        rv_product = (RecyclerView) findViewById(R.id.rv_product);
        rv_product.setLayoutManager(new LinearLayoutManager(this));
        srl_product = (SwipeRefreshLayout) findViewById(R.id.srl_product);
        srl_product.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(ProductActivity.this)) {
                    getCategory();
                    getBrand();
                    getProducts();
                } else {
                    getFromLocal();
                    Toast.makeText(ProductActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            getCategory();
            getBrand();
            getProducts();
        } else {
            getFromLocal();
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }

        //Populating spinner
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_brand.getSelectedItemPosition()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(sp_category.getSelectedItemPosition()), brandID.get(position))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Populating spinner

        //init swipe
//        initSwipe();
    }

    private void getProducts() {
        SessionManager sessionManager = new SessionManager(ProductActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_product.isRefreshing())
                            srl_product.setRefreshing(false);

                        List<ProductResponse.ProductResult> products = response.body().result.productResults;
                        productResult = products;
                        //for removing previous data
                        databaseHandler.removeProductCategoryBrand();
                        for (ProductResponse.ProductResult result : products) {
                            databaseHandler.addProduct(result.id, result.name, result.price, result.img, result.brand.id, result.brand.name, result.category.id, result.category.name);
                        }
                        rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(ProductActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getCategory() {
        SessionManager sessionManager = new SessionManager(ProductActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_product.isRefreshing())
                            srl_product.setRefreshing(false);

                        List<CategoryResponse.CategoryResult> categories = response.body().result.categoryResults;
                        categoryName.clear();
                        categoryName.add("Select Category");
                        categoryID.clear();
                        categoryID.add(0);
                        for (CategoryResponse.CategoryResult result : categories) {
                            categoryName.add(result.name);
                            categoryID.add(result.id);
                            databaseHandler.addCategory(result.id, result.name);
                        }
                        sp_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(ProductActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getBrand() {
        SessionManager sessionManager = new SessionManager(ProductActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<BrandResponse> call = apiService.getBrands();
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_product.isRefreshing())
                            srl_product.setRefreshing(false);

                        List<BrandResponse.BrandResult> brands = response.body().result.brandResults;
                        brandName.clear();
                        brandName.add("Select Brand");
                        brandID.clear();
                        brandID.add(0);
                        for (BrandResponse.BrandResult result : brands) {
                            brandName.add(result.name);
                            brandID.add(result.id);
                            databaseHandler.addBrand(result.id, result.name);
                        }
                        sp_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(ProductActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getFromLocal() {
        if (srl_product.isRefreshing())
            srl_product.setRefreshing(false);
        List<ProductResponse.ProductResult> products = databaseHandler.getProduct(0, 0);
        rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));

        //For spinner
        List<ProductResponse.Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (ProductResponse.Category result : categories) {
            categoryName.add(result.name);
            categoryID.add(result.id);
        }
        sp_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_category.setSelection(0);

        List<ProductResponse.Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (ProductResponse.Brand result : brands) {
            brandName.add(result.name);
            brandID.add(result.id);
        }
        sp_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
        sp_brand.setSelection(0);
        //For spinner
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showProductPromotionAndPitch(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        paint.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        /*icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear_black_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);*/
                    } else {
                        paint.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        /*icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_black_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);*/
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv_product);
    }

    private void showProductPromotionAndPitch(int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.product_promotion_dialog);
        dialog.setTitle("Product Promotion");
        TextView tv_product_description = (TextView) dialog.findViewById(R.id.tv_product_promotion);
        TextView tv_product_usp = (TextView) dialog.findViewById(R.id.tv_product_usp);
        TextView tv_product_tips = (TextView) dialog.findViewById(R.id.tv_product_tips);
        try {
            tv_product_description.setText(productResult.get(position).promotion.discount);
        } catch (Exception e) {
            e.printStackTrace();
            tv_product_description.setText("This product has no Promotion offer yet.");
        }
        try {
            tv_product_usp.setText(productResult.get(position).pitch.usp);
        } catch (Exception e) {
            e.printStackTrace();
            tv_product_usp.setText("This product has no USP yet.");
        }
        try {
            tv_product_tips.setText(productResult.get(position).pitch.tips);
        } catch (Exception e) {
            e.printStackTrace();
            tv_product_tips.setText("This product has no Tips yet.");
        }
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
