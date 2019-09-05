package com.humaclab.lalteer.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.TargetRecyclerViewAdapter;
import com.humaclab.lalteer.model.TargetItem;
import com.humaclab.lalteer.model.Targets;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;
import com.humaclab.lalteer.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miaki on 2/27/17.
 */

public class WeeklyTargetFragment extends Fragment {
    private static final String KEY_POSITION = "position";
    SelliscopeApiEndpointInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private TargetRecyclerViewAdapter targetRecyclerViewAdapter;
    private List<TargetItem> targetItems;

    public static WeeklyTargetFragment newInstance(int position) {
        WeeklyTargetFragment frag = new WeeklyTargetFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        return (frag);
    }

//    static String getTitle(Context ctxt, int position) {
//        return(String.format(ctxt.getString(R.string.hint), position + 1));
//    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View weeklyTargetView = inflater.inflate(R.layout.target_layout, container, false);
        //recyclerView = (RecyclerView) weeklyTargetView.findViewById(R.id.rv_target);
        swipeRefreshLayout = (SwipeRefreshLayout) weeklyTargetView.findViewById(R.id.srl_target);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(getActivity()))
                    getTargets();
                else
                    Toast.makeText(getActivity(), "Connect to Wifi or Mobile Data",
                            Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        if (NetworkUtility.isNetworkAvailable(getActivity()))
            getTargets();
        else
            Toast.makeText(getActivity(), "Connect to Wifi or Mobile Data",
                    Toast.LENGTH_SHORT).show();
        return weeklyTargetView;
    }

    void getTargets() {
        SessionManager sessionManager = new SessionManager(getContext());
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
       apiService.getTargets().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
               .subscribe(new SingleObserver<Response<ResponseBody>>() {
                   @Override
                   public void onSubscribe(Disposable d) {

                   }

                   @Override
                   public void onSuccess(Response<ResponseBody> response) {
                       Gson gson = new Gson();
                       if (response.code() == 200) {
                           try {
                               Targets.Successful getTargetListSuccessful = gson.fromJson(response.body().string()
                                       , Targets.Successful.class);
                               if (swipeRefreshLayout.isRefreshing())
                                   swipeRefreshLayout.setRefreshing(false);
                               targetRecyclerViewAdapter = new TargetRecyclerViewAdapter(getContext(),
                                       getTargetListSuccessful.targetResult.weeklyTarget);

                               recyclerView.setAdapter(targetRecyclerViewAdapter);

                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (response.code() == 401) {
                           Toast.makeText(getContext(),
                                   "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                       } else {
                           Toast.makeText(getContext(),
                                   "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                       }
                   }

                   @Override
                   public void onError(Throwable e) {
                       Log.d("Response", e.toString());
                   }
               });


    }
}
