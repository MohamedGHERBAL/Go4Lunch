package com.openclassrooms.go4launch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ActivityLoginBinding;
import com.openclassrooms.go4launch.databinding.ActivityMainBinding;
import com.openclassrooms.go4launch.manager.UserManager;
import com.openclassrooms.go4launch.services.UserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 30/12/2021
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // For Datas
    private static final int RC_SIGN_IN = 123;
    public static final int RC_GOOGLE_SIGN_IN = 4567;
    public static final int RC_EMAIL_SIGN_IN = 5678;

    // For Auth
    private UserManager userManager = UserManager.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private LoginManager mLoginManager;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthCredential mAuthCredential;

    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init(); // Login with FirebaseUI

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        initAlt(); // Login with Firebase Auth Credential
    }

    private void init() {
        Log.d(TAG, "init");

        launchSignInActivity();
    }

    private void initAlt() {
        Log.d(TAG, "initAlt");

        configureGoogleSignIn();
        configureFacebookSignIn();
        configureTwitterSignIn();
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (userManager.getCurrentUser() != null) {
            moveToMainActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Launch FirebaseUI for Authentification
    private void launchSignInActivity() {
        Log.i(TAG, "launchSignInActivity");

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                //new AuthUI.IdpConfig.EmailBuilder().build(), // Use for Auth with email address
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_go4lunch)
                        .build(),
                RC_SIGN_IN);
    }

    private void configureGoogleSignIn() {
        Log.d(TAG, "configureGoogleSignIn");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }

    private void configureFacebookSignIn() {
        Log.i(TAG, "configureFacebookSignIn");

        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.logOut();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "configureFacebookSignIn: onSuccess");

                firebaseAuthWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "configureFacebookSignIn: onCancel");

                showSnackBar(getString(R.string.error_authentication_canceled));
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "configureFacebookSignIn: onError", error);

                // Should check connection status to send a "no connection" message to user if not available
                showSnackBar(getString(R.string.error_unknown_error));
            }
        });
    }

    private void configureTwitterSignIn() {
        Log.d(TAG, "configureTwitterSignIn");


    }

    private void firebaseAuthWithCredential(AuthCredential credential) {
        Log.d(TAG, "firebaseAuthWithCredential");

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "firebaseAuthWithCredential: success.");

                        mCurrentUser = task.getResult().getUser();
                        if (mCurrentUser != null) {
                            if (mAuthCredential != null) {
                                mCurrentUser.linkWithCredential(mAuthCredential);
                            }
                            createUserInFirestore();
                            moveToMainActivity();
                        }
                    } else {
                        Log.i(TAG, "firebaseAuthWithCredential: failure.", task.getException());

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            FirebaseAuthUserCollisionException e = (FirebaseAuthUserCollisionException) task.getException();
                            if (e.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") && e.getUpdatedCredential() != null) {
                                makeAlertDialogExistingSignIn(e.getEmail(), e.getUpdatedCredential());
                            } else {
                                showSnackBar(getString(R.string.error_unknown_error));
                            }
                        } else {
                            showSnackBar(getString(R.string.error_unknown_error));
                        }
                    }
                });
    }

    private void makeAlertDialogExistingSignIn(String email, AuthCredential credential) {
        Log.d(TAG, "makeAlertDialogExistingSignIn");

        String providerName = "";
        switch (credential.getProvider()) {
            case TwitterAuthProvider.PROVIDER_ID:
                providerName = "Twitter";
                break;
            case FacebookAuthProvider.PROVIDER_ID:
                providerName = "Facebook";
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.email_already_used)
                .setMessage(email + getString(R.string.email_already_used_message, providerName))
                .setPositiveButton(R.string.popup_message_choice_yes, (dialog, which) -> {
                    mAuthCredential = credential;
                    signInWithGoogle();
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .create()
                .show();
    }

    private void setupListeners() {
        // Login|Profile Button

        binding.loginFacebookLoginButton.setOnClickListener(view ->
                mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"))
        );

        binding.loginGoogleLoginButton.setOnClickListener(view ->
                signInWithGoogle()
        );

        binding.loginTwitterLoginButton.setOnClickListener(view ->
            mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"))
        );
    }

    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle");

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    // Move to MainActivity if User already login
    private void moveToMainActivity() {
        Log.d(TAG, "moveToMainActivity");

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void createUserInFirestore() {
        FirebaseUser user = userManager.getCurrentUser();

        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(!doc.exists()){
                    String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
                    String name = getCurrentUser().getDisplayName();
                    String uid = getCurrentUser().getUid();
                    String email = getCurrentUser().getEmail();
                    String restName = "";
                    String restId = "";
                    ArrayList<String> likedRestaurant = new ArrayList<>();

                    UserHelper.createUser(uid, name, email, urlPicture, restName, restId, likedRestaurant);
                }
            }
        });

    }

    @Nullable
    protected static FirebaseUser getCurrentUser() {
        Log.d(TAG, "getCurrentUser");

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        this.handleResponseAfterSignIn(requestCode, resultCode, data);
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        handleGoogleSignInRequest(requestCode, data);
    }

    private void handleGoogleSignInRequest(int requestCode, @Nullable Intent data) {
        Log.d(TAG, "handleGoogleSignInRequest");

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithCredential(credential);
            } catch (ApiException e) {
                Log.i(TAG, "onActivityResult: Google sign in failed.", e);
            }
        }
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                //userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(binding.loginMainLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
