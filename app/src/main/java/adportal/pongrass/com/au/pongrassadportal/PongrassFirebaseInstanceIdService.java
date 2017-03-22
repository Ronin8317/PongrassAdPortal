package adportal.pongrass.com.au.pongrassadportal;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by user on 5/01/2017.
 */

public class PongrassFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public final static String TAG = "PongrassFirebase";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}
