package com.ort.smartacc.net;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.ort.smartacc.data.ManagersCollection;
import com.ort.smartacc.data.managers.TableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver contentResolver;
    ManagersCollection managersCollection;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        managersCollection = new ManagersCollection();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
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

        if(result == TableUpdaterTask.DB_ERROR) {
            syncResult.databaseError = true;
        }
    }
}
