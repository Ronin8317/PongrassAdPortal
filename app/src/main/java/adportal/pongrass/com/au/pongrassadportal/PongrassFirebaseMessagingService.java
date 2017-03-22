package adportal.pongrass.com.au.pongrassadportal;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by user on 5/01/2017.
 */

public class PongrassFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "FirebaseMessage";

    public PongrassFirebaseMessagingService(){}

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {

            Log.d(TAG, "From: " + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            //Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getTitle(), Toast.LENGTH_LONG);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    ;



}
