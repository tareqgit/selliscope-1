/*
 * Created by Tareq Islam on 9/30/19 4:01 PM
 *
 *  Last modified 9/30/19 4:01 PM
 */

package com.humaclab.lalteer.performance.claim.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ReasonItem;
import com.humaclab.lalteer.performance.claim.repository.ClaimRepository;

import java.util.List;

/***
 * Created by mtita on 30,September,2019.
 */
public class ClaimViewModel extends AndroidViewModel {

    private LiveData<List<ReasonItem>> mClaimResponseLiveData;
    private ClaimRepository mClaimRepository;

    public ClaimViewModel(@NonNull Application application) {
        super(application);
        mClaimRepository= new ClaimRepository( application);
        mClaimResponseLiveData= mClaimRepository.getClaimResponseLiveData();
    }


    public LiveData<List<ReasonItem>> getClaimResponseLiveData() {
        return mClaimResponseLiveData;
    }

    public void sendClaim(Claim claim){
        mClaimRepository.postClaim(claim);
    }

}
