package com.openclassrooms.go4launch.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ActivitySettingsBinding;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    // For Log.d or Log.i
    private static final String TAG = MainActivity.class.getSimpleName();

    // For ViewBinding
    private ActivitySettingsBinding settingsBinding;

    // For Dialog Menu Language
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        settingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = settingsBinding.getRoot();
        setContentView(view);

        //setAppLocale("fr"); // Or "en"

        listItems = getResources().getStringArray(R.array.choose_language);

        settingsBinding.button.setOnClickListener(view1 -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
            mBuilder.setTitle(R.string.alert_dialog_menu_language);

            mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
                settingsBinding.tvResult.setText(listItems[i]);
                dialogInterface.dismiss();
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
    }

    private void setAppLocale(String localeCode) {
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
}