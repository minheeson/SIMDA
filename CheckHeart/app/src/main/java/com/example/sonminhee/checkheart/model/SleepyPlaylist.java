package com.example.sonminhee.checkheart.model;

import io.realm.RealmObject;

/**
 * Created by sonminhee on 2017. 11. 20..
 */

public class SleepyPlaylist extends RealmObject {
    public long id = 0;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
