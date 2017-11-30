package com.example.sonminhee.checkheart.model;

import io.realm.Realm;

/**
 * Created by sonminhee on 2017. 11. 20..
 */

public class RealmHelper {
    Realm mRealm;

    public RealmHelper(Realm realm) {
        this.mRealm = realm;
    }

    public void insertSleepyPlaylist(long id) {
        mRealm.beginTransaction();
        SleepyPlaylist sleepyPlaylist = mRealm.createObject(SleepyPlaylist.class);
        sleepyPlaylist.setId(id);
        mRealm.commitTransaction();
    }

    public void insertExcitedPlaylist(long id) {
        mRealm.beginTransaction();
        ExcitedPlaylist excitedPlaylist = mRealm.createObject(ExcitedPlaylist.class);
        excitedPlaylist.setId(id);
        mRealm.commitTransaction();
    }


}
