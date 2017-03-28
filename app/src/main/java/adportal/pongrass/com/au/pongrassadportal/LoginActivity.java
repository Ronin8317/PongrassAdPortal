package adportal.pongrass.com.au.pongrassadportal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static adportal.pongrass.com.au.pongrassadportal.Constants.*;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_CODE_ASK_MULTIPLE = 1;



    static final int RC_SIGN_IN = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;
    private AccountOrdersTask mOrderTask = null;

    // UI references.
    private EditText mNewEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mLayoutView;
    private TextView mJSONResultView;
    protected FirebaseAuth mAuth;
    private Button mEmailSignInButton;
    private Button mGoogleSignInButton;
    private Button mSignOutButton;
    private Button mStartPositionUpdate;
    private Button mStopPositionUpdate;
    protected GoogleApiClient mGoogleApiClient;
    protected LoginButton mFacebookLoginButton;
    protected CallbackManager mFacebookCallbackManager;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private final static String TAG = "TAG";

    private PositionUpdateReceiver mReceiver;
    private PositionUpdateListener mListener;

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the login

        // needed for facebook
       //FacebookSdk.sdkInitialize(getApplicationContext());

        // does not setup content view..

        // Start the Service if it is not running..

        Intent updateIntent = new Intent(getBaseContext(), PositionUpdateService.class);
        startService(updateIntent);


        setContentView(R.layout.activity_login);
        setupFacebookLogin();
        setupGoogleLogin();
        setupEmailSignin();
        mSignOutButton = (Button)findViewById(R.id.logout_button);
        mSignOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // sign out..
                Signout();
            }
        });

        mStartPositionUpdate = (Button)findViewById(R.id.start_position_update);
        mStartPositionUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StartPositionUpdate();
            }
        });

        mStopPositionUpdate = (Button)findViewById(R.id.stop_position_update);
        mStopPositionUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StopPositionUpdate();
            }
        });

        PrepareLoginScreen();



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Logging in");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if (askForPermissions()) {


            AuthCredential cred;
            if ((cred = AuthentificationHelper.GetSavedCredentialsIfExists(this)) != null) {
                // got the credential..
                FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            StartPositionUpdateService();
                        }
                    }
                });
                // don't call the rest of the code.
                return;
            }

        }
    }

    private void setupFacebookLogin()
    {
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mFacebookLoginButton = (LoginButton) findViewById(R.id.facebook_signin_button);
        mFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        Profile current_profile = Profile.getCurrentProfile();
        if (current_profile != null)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.facebook_signin_ok) + " " + current_profile.getName(),
                    Toast.LENGTH_SHORT).show();
        }

        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // get the access token
                AccessToken token = loginResult.getAccessToken();
                if (token != null) {
                    handleFacebookAccessToken(token);
                }

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), getString(R.string.facebook_signin_cancelled),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_facebook_signin) + " " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupGoogleLogin()
    {
        mGoogleSignInButton = (Button) findViewById(R.id.google_signin_button);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.gso_oauth_clientid))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    private void setupEmailSignin()
    {
        mNewEmailView = (EditText) findViewById(R.id.email);
        //  populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private boolean askForPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(mNewEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,CAMERA, WRITE_EXTERNAL_STORAGE }, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private void Signout()
    {
        // sign out of Firebase
        FirebaseAuth.getInstance().signOut();
       PrepareLoginScreen();
    }




    private void GoogleSignIn() {
        ShowWaiting();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        ShowWaiting();
        final String FEmail = acct.getEmail();

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {



                            AuthentificationHelper.LinkWithCredentials(credential, LoginActivity.this, FEmail, new IFirebaseLoginResult() {
                                @Override
                                public void LoginResult(FirebaseUser user) {
                                    StopWaiting();
                                    if (user != null) {
                                        StartPositionUpdateService();
                                    }
                                }
                            });

                        }
                        else {
                            StopWaiting();
                            Log.i(TAG, task.toString());
                        }
                        // ...
                    }
                });
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            String AuthCode = acct.getServerAuthCode();



            firebaseAuthWithGoogle(acct);


        }
    }
    // [END handleSignInResult]

    private void attemptRegister()
    {
        mNewEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mNewEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mNewEmailView.setError(getString(R.string.error_field_required));
            focusView = mNewEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mNewEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mNewEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //
            //showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "User " + email + " Login ok",
                                Toast.LENGTH_SHORT).show();

                        StartPositionUpdateService();
                    }
                }
            });

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mOrderTask != null) {
            return;
        }

        // Reset errors.
        mNewEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mNewEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mNewEmailView.setError(getString(R.string.error_field_required));
            focusView = mNewEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mNewEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mNewEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //
            //showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "User " + email + " Created",
                                Toast.LENGTH_SHORT).show();

                        StartPositionUpdateService();
                    }
                }
            });
            /**
            try{
                URL url = new URL("http://10.200.200.166:8090/Adbooking/adbooking/JSONService");
                JSONObject jo = new JSONObject();
                jo.put("id", "1");
                jo.put("method","info");

                mOrderTask = new AccountOrdersTask(url, jo);
                mOrderTask.execute(email);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
             **/
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }
    **/

    /**
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }
     **/

    /**
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    **/

    /**

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mNewEmailView.setAdapter(adapter);
    }
     **/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;

    }



    public class AccountOrdersTaskTest extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute()
        {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... accountid) {

            // create a fake JSONArray
            JSONArray ja_result = new JSONArray();





            return true;
        }

        protected void onPostExecute(final Boolean success) {

        }
    }

    public class AccountOrdersTask extends AsyncTask<String, Integer, Boolean>
    {

        protected URL _JSONURL = null;
        protected JSONObject _jsonParam = null;
        protected String _jsonResult = "";

        public AccountOrdersTask(URL pongrassJSON, JSONObject param)
        {
            _JSONURL = pongrassJSON;
            _jsonParam = param;
        }

        @Override
        protected void onPreExecute()
        {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... accountid) {

            URL url = null;
            try {

                HttpURLConnection conn = (HttpURLConnection)_JSONURL.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");

                conn.setDoOutput(true);
                String jsonParam = _jsonParam.toString();

                OutputStream c_out = conn.getOutputStream();
                c_out.write(jsonParam.getBytes("UTF-8"));
                c_out.flush();

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                char[] arr = new char[8 * 1024];
                StringBuffer result = new StringBuffer();

                int numCharsRead;
                while ((numCharsRead = in.read(arr, 0, arr.length)) != -1) {
                    result.append(arr, 0, numCharsRead);
                }
                in.close();
                conn.disconnect();
                _jsonResult = result.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

           showProgress(false);
            setContentView(R.layout.layout);

            mJSONResultView = (TextView)findViewById(R.id.jsonView);
            if (mJSONResultView != null)
            {
                mJSONResultView.setText(_jsonResult);
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /**
    public classified UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }**/

    private Messenger mMessengerService = null;
    private Messenger mMessangerClient = new Messenger(new IncomingHandler());

    private boolean mConnectionBinded = false;

    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mMessengerService = new Messenger(service);
            try {
                // request server status..

                Message msg = Message.obtain(Message.obtain(null, MSG_REGISTER_CLIENT));
                msg.replyTo = mMessangerClient;
                mMessengerService.send(msg);

                msg = Message.obtain(null, MSG_REQUEST_SERVER_STATUS);
                msg.replyTo = mMessangerClient;
                mMessengerService.send(msg);
            }
            catch(RemoteException re)
            {
                Log.e(TAG, re.getMessage());
                mMessengerService = null;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mMessengerService = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(AuthentificationHelper.GetAuthorizationHandler());
        // start the service, but does not bind..

        Intent updateIntent = new Intent(this, PongrassCommunicationService.class);
        bindService(updateIntent, mConnection, Context.BIND_AUTO_CREATE);
        mConnectionBinded = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent updateIntent = new Intent(this, PongrassCommunicationService.class);
        bindService(updateIntent, mConnection, Context.BIND_AUTO_CREATE);
        mConnectionBinded = false;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        // unregister the service
        try {
            if (mMessengerService != null) {
                Message msg = Message.obtain(Message.obtain(null, MSG_UNREGISTER_CLIENT));
                msg.replyTo = mMessangerClient;
                mMessengerService.send(msg);
                unbindService(mConnection);
                mConnectionBinded = false;
            }
        }
        catch (RemoteException re)
        {
            Log.e(TAG, re.getMessage());
        }

        //StartAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {

            // unbind if onStop is not called for some reason
            if (mConnectionBinded ) {
                Message msg = Message.obtain(Message.obtain(null, MSG_UNREGISTER_CLIENT));
                msg.replyTo = mMessangerClient;
                mMessengerService.send(msg);

                mConnectionBinded = false;
                unbindService(mConnection);
            };
        }
        catch (RemoteException re)
        {
            Log.e(TAG, re.getMessage());
        }



    }


    private void handleFacebookAccessToken(final AccessToken token) {


        ShowWaiting();

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());




        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            StopWaiting();
                        }
                        else {
                            // register the token with firebase
                            // get the email address



                            mAuth.signOut();

                            GraphRequest request = GraphRequest.newMeRequest(
                                    token,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {

                                            StopWaiting();

                                            try {
                                                String FEmail = object.getString("email");
                                                AuthentificationHelper.LinkWithCredentials(credential, LoginActivity.this.getApplicationContext(), FEmail, new IFirebaseLoginResult() {
                                                    @Override
                                                    public void LoginResult(FirebaseUser user) {
                                                        if (user != null)
                                                        {
                                                            StartPositionUpdateService();
                                                        }
                                                    }
                                                });




                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email");
                            request.setParameters(parameters);
                            request.executeAsync();





                        }

                        // ...
                    }
                });
    }



    protected class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_GET_POSITION_UPDATE:
                    // get the bundle
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        double lat = bundle.getDouble("lat");
                        double lng = bundle.getDouble("lng");
                        String text_to_display = String.format("Location : Lat %.4f, Lng %.4f", lat, lng);
                        Toast.makeText(LoginActivity.this, text_to_display, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_REQUEST_SERVER_STATUS:
                    if (msg.arg1 == 0)
                    {
                        Log.d(TAG, "No service found. Starting server");
                        Intent serverIntent = new Intent(LoginActivity.this, PositionUpdateService.class);
                        startService(serverIntent);
                    }
                    else {
                        Log.d(TAG, "Service already started");
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    // this is a timer..
    private void StartPositionUpdateService() {
        PrepareLogoutScreen();

        // launch the navigation channel.
        Intent navIntent = new Intent(this, NavigationActivity.class);
        startActivity(navIntent);

    }


    private void StartPositionUpdate() {
        // send procast
        Intent intent = new Intent("adportal.pongrass.com.au.pongrassadportal.POSITIONUPDATE");
        intent.putExtra("action", getString(R.string.pongrass_service_start));

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Message msg = Message.obtain(null, MSG_START_POSITIONUPDATE, 1, 0);
        msg.replyTo = mMessangerClient;
        try {
            if (mMessengerService != null) {
                mMessengerService.send(msg);
            }
            else {
                Log.d(TAG, "Messenger service not initialized");
            }
        }
        catch (RemoteException re) {
            Log.e(TAG, re.getMessage());
        }
    }

    private void StopPositionUpdate() {
        // send procast

        Message msg = Message.obtain(null, MSG_STOP_POSITIONUPDATE, 1, 0);
        msg.replyTo = mMessangerClient;
        try {
            if (mMessengerService != null) {
                mMessengerService.send(msg);
            }
            else {

                Log.d(TAG, "Messenger service not initialized");

            }
        }
        catch (RemoteException re) {
            Log.e(TAG, re.getMessage());
        }
    }

    public static int FREQUENCY = 30000;

    /**
    protected void StartAlarm()
    {

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), PositionUpdateReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, 0);

        am.set(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis() + 10000, pending);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), FREQUENCY, pending);

    }

    protected void CancelAlarm()
    {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), PositionUpdateReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pending != null)
        {
            pending.cancel();
        }
    }

     **/


    private void StartTakingPhotos()
    {
        Intent intent = new Intent(this, GetPictureActivity.class);
        startActivity(intent);

    }

    private void OpenOrders()
    {
        Intent intent = new Intent(this, OrdersScrollActivity.class);

        startActivity(intent);
    }

    private void ShowWaiting()
    {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        };
    }

    private void StopWaiting()
    {
        mProgressDialog.hide();
    }


    private void PrepareLoginScreen()
    {
        mFacebookLoginButton.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.VISIBLE);
        mNewEmailView.setVisibility(View.VISIBLE);
        mPasswordView.setVisibility(View.VISIBLE);
        mGoogleSignInButton.setVisibility(View.VISIBLE);
        mFacebookLoginButton.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.GONE);
        mStartPositionUpdate.setVisibility(View.GONE);
        mStopPositionUpdate.setVisibility(View.GONE);


    }

    private void PrepareLogoutScreen()
    {
        mFacebookLoginButton.setVisibility(View.GONE);
        mEmailSignInButton.setVisibility(View.GONE);
        mNewEmailView.setVisibility(View.GONE);
        mGoogleSignInButton.setVisibility(View.GONE);
        mPasswordView.setVisibility(View.GONE);
        mFacebookLoginButton.setVisibility(View.GONE);
        mSignOutButton.setVisibility(View.VISIBLE);
        mStartPositionUpdate.setVisibility(View.VISIBLE);
        mStopPositionUpdate.setVisibility(View.VISIBLE);

    }


}

