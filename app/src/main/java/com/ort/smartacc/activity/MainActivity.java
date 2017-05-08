package com.ort.smartacc.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.common.view.SlidingTabLayout;
import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.Util;
import com.ort.smartacc.adapter.CustomPagerAdapter;
import com.ort.smartacc.fragment.SearchFragment;
import com.ort.smartacc.fragment.SearchResultsFragment;
import com.ort.smartacc.net.DbBridgeService;

import java.util.ArrayList;

import im.delight.apprater.AppRater;

public class MainActivity extends AppCompatActivity
        implements SearchFragment.OnSearchInteractionCallback {

    private static final String SERVICE_PREF = "service_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        //Log.e("Main", preferences.getBoolean(SERVICE_PREF, false)+"");
        if(!preferences.getBoolean(SERVICE_PREF, false)) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    new ServiceReadyReceiver(),
                    new IntentFilter(Util.SERVICE_BROADCAST_ACTION));
            startService(new Intent(this, DbBridgeService.class));
        } else {
            onReady();
        }
    }

    private void onReady() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SERVICE_PREF, true);
        editor.apply();

        prepareSync();
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (viewPager != null) {
            viewPager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager()));
        }

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab);
        if (slidingTabLayout != null) {
            slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
            slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                slidingTabLayout.setScrollBarSize(100);
            }
            slidingTabLayout.setViewPager(viewPager);
        }

        //Crear una instancia de AppRater para decidir si se muestra o no el mensaje de Rating del App
        AppRater appRater = new AppRater(this);
        appRater.setDaysBeforePrompt(3);
        appRater.setLaunchesBeforePrompt(7);
        appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
        appRater.show();
    }
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rate:
                // User chose the "Rate" item, direct user to rating page:
                OpenRatingPage();
                return true;

            case R.id.action_credits:
                // User chose the "Credits" action, direct user to credits activity
                Intent intent = new Intent(this, CreditsActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void showResultsFragment(ArrayList<SearchResult> results) {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        Bundle args = new Bundle();
        args.putParcelableArrayList(SearchResultsFragment.ARG_RESULTS, results);
        intent.putExtra(SearchResultsActivity.ARG_EXTRA_BUNDLE, args);
        startActivity(intent);
    }

    public void OpenRatingPage(){
        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));
        }
    }

    private void prepareSync() {
        String authority = getString(R.string.content_authority);
        String userName = getString(R.string.default_user);
        String accountType = getString(R.string.account_type);

        Account newAccount = new Account(userName, accountType);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if(accountManager.addAccountExplicitly(newAccount, null, null)) {
            ContentResolver.setIsSyncable(newAccount, authority, 1);
            ContentResolver.setSyncAutomatically(newAccount, authority, true);

            ContentResolver.addPeriodicSync(
                    newAccount,
                    authority,
                    Bundle.EMPTY,
                    60*60*24);
        }
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ServiceReadyReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ServiceReadyReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getBooleanExtra(Util.SERVICE_STATUS, false)) {
                    onReady();
                } else {
                    MainActivity.this.setContentView(R.layout.screen_error);
                    TextView txtDescripcion = (TextView) findViewById(R.id.txt_error_descripcion);
                    if (txtDescripcion != null) {
                        txtDescripcion.setText(getString(R.string.error_load));
                    }
                }
            } catch(IllegalStateException ise){
                ise.printStackTrace();
            }
        }
    }
}
