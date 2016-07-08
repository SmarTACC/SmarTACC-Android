package com.ort.smartacc.net;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.ort.smartacc.db.SQLiteHelper;
import com.ort.smartacc.Util;
import com.ort.smartacc.db.TableManager;

public class DbBridgeService extends IntentService {

    public DbBridgeService() {
        super(DbBridgeService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean success = true;

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Util.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();

        helper.update(db);

        for (TableManager MANAGER : SQLiteHelper.TABLE_MANAGERS) {
            if (db.query(MANAGER.getName(), null, null, null, null, null, null).getCount() == 0) {
                success = false;
            }
        }

        Intent localIntent =
                new Intent(Util.SERVICE_BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Util.SERVICE_STATUS, success);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

}
