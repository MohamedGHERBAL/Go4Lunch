package com.openclassrooms.go4launch.ui;

import static java.lang.Math.floor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ActivitySettingsBinding;
import com.openclassrooms.go4launch.notification.MyWorker;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    // For Log.d and Log.i
    private static final String TAG = SettingsActivity.class.getSimpleName();

    // For ViewBinding
    private ActivitySettingsBinding settingsBinding;

    // For Toolbar
    private MaterialToolbar toolbar;

    // For notification
    public static final String PREFS= "PREFS";
    SharedPreferences prefs;

    // For Dialog Menu Language
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        prefs = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        settingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = settingsBinding.getRoot();
        setContentView(view);
        this.configureToolbar();

        setAppLocale("fr"); // "fr" Or "en"
        listItems = getResources().getStringArray(R.array.choose_language);
/*
        // For Language
        settingsBinding.button.setOnClickListener(view1 -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
            mBuilder.setTitle(R.string.alert_dialog_menu_language);


            mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {

                settingsBinding.tvResult.setText(listItems[i]);
                dialogInterface.dismiss();
            });


            mBuilder.setNegativeButton("Annulé", (dialogInterface, i) -> {
                // Do Nothin'
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
*/

        // For Notification
        settingsBinding.notificationsSwitch.setChecked(prefs.getBoolean("switch_checked", false));
        settingsBinding.notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            WorkManager mWorkManager = WorkManager.getInstance(this);
            if (isChecked) {

                long delay = calculateDelay(14, 10, 0);

                PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                        1, TimeUnit.DAYS)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

                mWorkManager.enqueue(workRequest);

                prefs.edit().putBoolean("switch_checked", true).apply();
                Toast.makeText(getApplicationContext(), R.string.notifications_on, Toast.LENGTH_LONG).show();

            } else {
                mWorkManager.cancelAllWork();

                prefs.edit().putBoolean("switch_checked", false).apply();
                Toast.makeText(getApplicationContext(), R.string.notifications_off, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setAppLocale(String localeCode) {
        Log.d(TAG, "setAppLocale");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }

    public static long calculateDelay(int hour, int min, int sec) {
        Log.d(TAG, "calculateDelay");

        // Initialize the calendar with today and the preferred time to run the job.
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, hour);
        cal1.set(Calendar.MINUTE, min);
        cal1.set(Calendar.SECOND, sec);

        // Initialize a calendar with now.
        Calendar cal2 = Calendar.getInstance();

        return calculateDelta(cal1,cal2);
    }

    public static long calculateDelta(Calendar calendar1, Calendar calendar2) {
        Log.d(TAG, "calculateDelta");

        long delta = Double.valueOf(floor(calendar1.getTimeInMillis()/1000.0) - floor(calendar2.getTimeInMillis()/1000.0)).longValue();

        return ((delta >= 0) ?
                delta
                : (TimeUnit.DAYS.toSeconds(1)+delta));
    }

    // For Toolbar
    private void configureToolbar() {
        Log.i(TAG, "configureToolbar");

        // Set the toolbar
        this.toolbar = settingsBinding.toolbar.toolbar;
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}