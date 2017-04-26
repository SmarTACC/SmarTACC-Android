package com.ort.smartacc.net;

import android.app.IntentService;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ort.smartacc.R;
import com.ort.smartacc.Util;
import com.ort.smartacc.data.ManagersCollection;
import com.ort.smartacc.data.managers.TableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DbBridgeService extends IntentService {

    public DbBridgeService() {
        super(DbBridgeService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean success = update();

        Intent localIntent =
                new Intent(Util.SERVICE_BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Util.SERVICE_STATUS, success);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private boolean update() {
        ManagersCollection managersCollection = new ManagersCollection();
        ContentResolver contentResolver = getContentResolver();
        ContentProviderClient contentProviderClient = contentResolver.acquireContentProviderClient(getString(R.string.content_authority));

        List<TableUpdaterTask> updaterTasks = new ArrayList<>();

        for(TableManager manager: managersCollection.getTableManagers()) {
            TableUpdaterTask updaterTask = new TableUpdaterTask(manager, contentProviderClient, contentResolver);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                updaterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                updaterTask.execute();
            }
            updaterTasks.add(updaterTask);
        }

        int result = 0;
        int i = 0;
        while(result == TableUpdaterTask.OK && i < updaterTasks.size()) {
            try {
                result = updaterTasks.get(i).get();
                i++;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                result = TableUpdaterTask.WTF_ERROR;
            }
        }

        if (contentProviderClient != null) {
            contentProviderClient.release();
        }

        return result == TableUpdaterTask.OK;
    }

}
