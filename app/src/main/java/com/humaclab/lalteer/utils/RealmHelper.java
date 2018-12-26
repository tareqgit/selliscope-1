package com.humaclab.lalteer.utils;

import com.humaclab.lalteer.model.Dashboard.Access;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmHelper {
    Realm realm;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public int accessList(String name) {
        /*// Build the query looking at all users:
        RealmQuery<User> query = realm.where(User.class);

        // Add query conditions:
        query.equalTo("name", "John");
        query.or().equalTo("name", "Peter");

        // Execute the query:
        RealmResults<User> result1 = query.findAll();

        // Or alternatively do the same all at once (the "Fluent interface"):
        RealmResults<User> result2 = realm.where(User.class)
                .equalTo("name", "John")
                .or()
                .equalTo("name", "Peter")
                .findAll();*/

        RealmQuery<Access> accessRealmQuery = realm.where(Access.class);
        accessRealmQuery.equalTo("name", name);

        Access accessRealmResults = accessRealmQuery.findFirst();
        if (accessRealmResults != null) {
            return accessRealmResults.getAccess();
        }
        return 100;
    }
}
