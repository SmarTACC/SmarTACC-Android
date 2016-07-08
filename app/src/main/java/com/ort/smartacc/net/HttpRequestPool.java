package com.ort.smartacc.net;

import android.os.AsyncTask;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HttpRequestPool {

    Map<String, RequestTask> taskList;

    public HttpRequestPool(){
        taskList = new HashMap<>();
    }

    public void add(String key, RequestTask task){
        taskList.put(key, task);
    }

    public Map<String, HttpResponse> runAndWait(){
        for(Map.Entry<String, RequestTask> taskEntry : taskList.entrySet()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                taskEntry.getValue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                taskEntry.getValue().execute();
            }
        }

        Map<String, HttpResponse> results = new HashMap<>();
        for(Map.Entry<String, RequestTask> taskEntry : taskList.entrySet()){
            try {
                results.put(taskEntry.getKey(), taskEntry.getValue().get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
