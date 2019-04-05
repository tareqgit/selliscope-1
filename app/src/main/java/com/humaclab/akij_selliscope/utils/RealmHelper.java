package com.humaclab.akij_selliscope.utils;

import com.humaclab.akij_selliscope.model.Order.NewOrder;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {
    Realm realm;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //WRITE
/*    public void save(final Spacecraft spacecraft) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Spacecraft s = realm.copyToRealm(spacecraft);

            }
        });
    }*/

    //READ/RETRIEVE

    public List<NewOrder> newOrderList() {
        RealmResults<NewOrder> brandRealmResults = realm.where(NewOrder.class).findAll();
        //RealmResults<NewOrder> brandRealmResults = realm.where(NewOrder.class).equalTo("status",0).findAll();
        return realm.copyFromRealm(brandRealmResults);
    }

    public void deletRealm(int index) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<NewOrder> result = realm.where(NewOrder.class).findAll();
                result.deleteFromRealm(index);

            }
        });

/*        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.


            }
        });*/
    } public void deletAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<NewOrder> result = realm.where(NewOrder.class).findAll();
                result.deleteAllFromRealm();

            }
        });

    }

    public void uPdate(int index) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                NewOrder newOrder = realm.where(NewOrder.class).equalTo("status", 0).findAll().get(index);
                newOrder.setStatus(1);
            }
        });
    }
}
