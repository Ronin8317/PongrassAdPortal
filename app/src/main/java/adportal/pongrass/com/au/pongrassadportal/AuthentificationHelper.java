package adportal.pongrass.com.au.pongrassadportal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

/**
 * This is the class that handles storing the login credentials so that you don't have to log in every time the application starts up
 * The login/password is stored in a SharedPreferences storage area
 *
 *
 *
 * Created by user on 14/03/2017.
 */

public class AuthentificationHelper implements FirebaseAuth.AuthStateListener{

    public final static String SAVED_CREDENTIALS = "PONGRASS_CREDENTIALS";
    public final static String SAVED_EMAILS = "saved_emails";
    public final static String SAVED_PASSWORD = "saved_password";

    private final static String TAG = "AuthenificationHelper";

    protected static AuthentificationHelper _singleton = new AuthentificationHelper();

    protected static SecureRandom _RandomGenerator = null;

    public static AuthCredential GetSavedCredentialsIfExists(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String saved_email = pref.getString(SAVED_EMAILS, "");
        String saved_password = pref.getString(SAVED_PASSWORD, "");

        if ((saved_email.isEmpty() == false) &&
                (saved_password.isEmpty() == false)) {
            return EmailAuthProvider.getCredential(saved_email, saved_password);
        }
        return null;

    }

    public static void SaveCredentials(Context context, String saved_email, String saved_password)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SAVED_EMAILS, saved_email);
        editor.putString(SAVED_PASSWORD, saved_password);
        editor.commit();


        /**
        // assert getting it is the same
        pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String stored_email = pref.getString(SAVED_EMAILS, "");
        String stored_password = pref.getString(SAVED_PASSWORD, "");
        assert(stored_email.equals(saved_email));
        assert(stored_password.equals(saved_password));
         **/

    }

    public static void SaveString(Context context, String key, String value)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String GetString(Context context, String key)
    {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return pref.getString(key, "");

    }


    // this is to change passwords..
    public static void SavePasswordForAccount(final AuthCredential credential, final Context context,
                                              final String Femail, final String Password, final IFirebaseLoginResult callback)
    {
        // get the current user..
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
        {
            String email = currentUser.getEmail();
           // RandomPasswordGenerator rnd = new RandomPasswordGenerator();
            //final String password = rnd.DefaultPassword();
            currentUser.updatePassword(Password).addOnCompleteListener(new OnCompleteListener<Void>(){

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Log.i(TAG, "Update of Password successful. Password is " + Password);
                        SaveCredentials(context, Femail, Password);
                        callback.LoginResult(FirebaseAuth.getInstance().getCurrentUser());
                    }
                    else {
                        Log.e(TAG, "Cannot set password");
                        callback.LoginResult(null);
                    }
                }
            });
        }


    }

    public static void LinkWithCredentials(final AuthCredential credential, final Context context, final String email_to_link, final IFirebaseLoginResult callback)
    {
        // get the email..
        // generate a random password
        if (_RandomGenerator == null) {
            _RandomGenerator = new SecureRandom();
        };

        final String random_password = new RandomPasswordGenerator().DefaultPassword();

        // create one..
        AuthCredential email_credential = EmailAuthProvider.getCredential(email_to_link, random_password);

       FirebaseAuth mAuth = FirebaseAuth.getInstance();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.linkWithCredential(email_credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                SaveCredentials(context, email_to_link, random_password);
                                callback.LoginResult(task.getResult().getUser());
                            }
                            else {
                                callback.LoginResult(null);
                            }
                        }
                    });




    };






    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }


    public static FirebaseAuth.AuthStateListener GetAuthorizationHandler()
    {
        return _singleton;
    }



}
