package adportal.pongrass.com.au.pongrassadportal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * PositionUpdateService is the remote service responsible for background process, even if the app has closed
 *
 * Created by Ronin on 19/12/2016.
 */



public class PositionUpdateService extends IntentService {

    protected static boolean _isRunning = false;


    public final static String TAG = "PositionUpdateService";

    public static List<Messenger> mClients = new ArrayList<>();
    public static List<Messenger> mClientsToPing = new ArrayList<>();


    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    // start the position update request ping. Note that if arg1 is 1, then it'll update the Firebase database. otherwise it'll just update the client

    public static final int MSG_START_POSITIONUPDATE = 100;
    // stop the position update request ping
    public static final int MSG_STOP_POSITIONUPDATE = 101;
    // get the current position, without pinging
    public static final int MSG_GET_POSITION_UPDATE = 102;
    // pinging the client about position request.

    public static final int MSG_POSITION_UPDATE_PING = 103;

    private static PositionUpdateService _Singleton = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Position Update is created");
        FirebaseApp.initializeApp(this);
        _isRunning = true;
        //StartLoop();
        _Singleton = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Position Update is destroyed");
        _isRunning = false;
    }

    public static boolean isRunning() {
        return _isRunning;
    }

    private Handler mHandler = new Handler();
    // start it..
    public final int POSITION_UPDATE_FREQUENCY = 10000; // 10 minutes

    public boolean _continue_loop = true;
    public boolean _loop_started = false;
    public boolean _should_update_firebase = false;

    public PositionUpdateService() {
        super("PositionUpdateService");
    }




    protected Location GetCurrentLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public Map<String, Object> GetLocationAsMap(Location loc) {
        Map<String, Object> result = new HashMap<>();

        if (loc != null) {
            result.put(Constants.LATITUDE, Double.toString(loc.getLatitude()));
            result.put(Constants.LONGITUDE, Double.toString(loc.getLongitude()));
            result.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);

        }
        return result;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!_loop_started) {
            StartLoop();
        }
    }

    protected void StartLoop() {
        if (_loop_started) return;

        Log.d(TAG, "Starting Loop");
        // schedule a handler..
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                Loop();
            }
        }, 10000);


    }

    protected void Loop()
    {

        final Context f_context = this;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d(TAG, "Log into Firebase");
            AuthCredential cred;
            if ((cred = AuthentificationHelper.GetSavedCredentialsIfExists(f_context)) != null) {
                // got the credential..
                FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UpdatePosition();
                        }
                    }
                });
            }
        } else {
            UpdatePosition();
        }



    }


    protected void UpdatePosition() {

        Location loc = GetCurrentLocation();
        if (loc != null) {
            // update it..
            if (_should_update_firebase) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    String ref_loc = "locations/" + uid;
                    DatabaseReference myRef = database.getReference(ref_loc);
                    myRef.push().setValue(GetLocationAsMap(loc));
                }
            }
            // get the user id



            // for each of the Messenger that is registered for ping
            for (Messenger msgr : mClientsToPing)
            {
                SendPositionUpdateToClient(msgr, MSG_POSITION_UPDATE_PING);
            }


        }

        _loop_started = false;
        StartLoop();


    }

    private class PongrassServiceBinder extends Binder {

        // the binder is where the service interface is exposed
        public boolean isFirebaseConnect() {
            return (FirebaseAuth.getInstance().getCurrentUser() != null);
        }
    }




    final Messenger mMessenger = new Messenger(new IncomingHandler());


    private final IBinder mBinder = new PongrassServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        //return super.onBind(intent);
        return mMessenger.getBinder();
    }


    void SendPositionUpdateToClient(Messenger client, int code) {
        Message msg = Message.obtain(null, code);
        // create a bundle
        msg.replyTo = mMessenger;

        Location loc = GetCurrentLocation();
        // create a bundle
        Bundle dataBundle = new Bundle();
        dataBundle.putDouble("lat", loc.getLatitude());
        dataBundle.putDouble("lng", loc.getLongitude());
        // time..
        Date now = new Date();
        dataBundle.putLong("time", now.getTime());
        msg.setData(dataBundle);
        try {
            client.send(msg);
        }
        catch (RemoteException re) {
            Log.e(TAG, re.getMessage());
        }

    }




    /**
     * Handler of incoming messages from clients.
     */
    static private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "Message Received");
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    PositionUpdateService.mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    PositionUpdateService.mClients.remove(msg.replyTo);
                    break;
                case MSG_START_POSITIONUPDATE:
                    if (msg.replyTo != null) {
                        PositionUpdateService.mClientsToPing.add(msg.replyTo);

                        _Singleton._should_update_firebase = (msg.arg1 == 1);


                        _Singleton.StartLoop();
                    }
                    else {
                        Log.e(TAG, "Reply to in Start position update is null");
                    }
                    break;
                case MSG_STOP_POSITIONUPDATE:
                    PositionUpdateService.mClientsToPing.remove(msg.replyTo);
                    break;
                case MSG_GET_POSITION_UPDATE:
                    // need to create a bundle..
                    _Singleton.SendPositionUpdateToClient(msg.replyTo, MSG_GET_POSITION_UPDATE);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }


    }

}




