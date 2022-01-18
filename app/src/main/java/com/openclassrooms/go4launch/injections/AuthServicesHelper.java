package com.openclassrooms.go4launch.injections;

import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.openclassrooms.go4launch.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 31/12/2021
 */
public class AuthServicesHelper implements AuthServices {

    @Override
    public Intent getAuthSignWithGoogle() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

                return (AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_logo_auth)
                .build());
    }
}
