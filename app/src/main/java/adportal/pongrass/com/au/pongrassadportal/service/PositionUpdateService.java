package adportal.pongrass.com.au.pongrassadportal.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adportal.pongrass.com.au.pongrassadportal.AuthentificationHelper;
import adportal.pongrass.com.au.pongrassadportal.Constants;
import adportal.pongrass.com.au.pongrassadportal.FieldValidation;

import static adportal.pongrass.com.au.pongrassadportal.Constants.*;


/**
 * PositionUpdateService is the remote service responsible for background process, even if the app has closed
 *
 * Created by Ronin on 19/12/2016.
 */



public class PositionUpdateService extends Service {

    protected static boolean _isRunning = false;


    public final static String TAG = "PositionUpdateService";








    private static PositionUpdateService _Singleton = null;

    public static PositionUpdateService getInstance()
    {
        return _Singleton;
    }

    // counter for firebase upload
    private int _lastUploadToFirebase = 0;

    private List<Map<String, String>> mLocationsBuffer = new ArrayList<>();


    protected Messenger mMessengerCommunicator = null;

    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mMessengerCommunicator = new Messenger(service);
            try {
                Log.d(TAG, "Registering Server");
                Message msg = Message.obtain(Message.obtain(null, MSG_REGISTER_SERVER));
                msg.replyTo = mMessenger;
                mMessengerCommunicator.send(msg);
            }
            catch(RemoteException re)
            {
                Log.e(TAG, re.getMessage());
                mMessengerCommunicator = null;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMessengerCommunicator = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Position Update is created");
        FirebaseApp.initializeApp(this);
        _isRunning = true;

        _Singleton = this;


        Log.d(TAG, "Binding to Communication thread");
        Intent intent = new Intent(this, PongrassCommunicationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        RestoreState();
        if (_continue_loop)
        {
            StartLoop();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Position Update is destroyed");
        _isRunning = false;
      // unbind
        unbindService(mConnection);

        SaveState();
    }

    public static boolean isRunning() {
        return _isRunning;
    }

    private Handler mHandler = new Handler();
    // start it..
    public final int POSITION_UPDATE_FREQUENCY = 10000; // 10 minutes

    public boolean _continue_loop = false;
    public boolean _loop_started = false;
    public boolean _should_update_firebase = false;

    public PositionUpdateService() {

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

    public Map<String, String> GetLocationAsMap(Location loc) {
        Map<String, String> result = new HashMap<>();

        if (loc != null) {
            // current time..
            Date now = new Date();
            result.put(Constants.LATITUDE, Double.toString(loc.getLatitude()));
            result.put(Constants.LONGITUDE, Double.toString(loc.getLongitude()));
            result.put(Constants.TIMESTAMP, Long.toString(now.getTime()));
        }
        return result;
    }

   protected Handler mCurrentHandler = null;

    protected void StartLoop() {
        if (_loop_started) return;

        if (!_continue_loop) return;

        Log.d(TAG, "Starting Loop");
        // schedule a handler..
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                if (_continue_loop) {
                    Loop();
                };
            }
        }, GPS_SCAN_PERIOD);


    }

    protected void CancelLoop()
    {
        _continue_loop = false;
    }

    protected void SaveState()
    {
        AuthentificationHelper.SaveString(this, "continue_loop", (_continue_loop ? "1" : "0"));
        AuthentificationHelper.SaveString(this, "update_firebase", (_should_update_firebase ? "1" : "0"));
        Log.d(TAG,"Save State");
    }

    protected void RestoreState()
    {
        _continue_loop = "1".equals(AuthentificationHelper.GetString(this, "continue_loop"));
        _should_update_firebase = "1".equals(AuthentificationHelper.GetString(this, "update_firebase"));
        Log.d(TAG,"Restore State");
    }

    protected void StartPositionUpdate(boolean sendToFirebase)
    {
        _should_update_firebase = sendToFirebase;
        if (_continue_loop) return;

        _continue_loop = true;

        StartLoop();

        SaveState();

    }

    protected void StopPositionUpdate()
    {
        _should_update_firebase = false;
        CancelLoop();
    }

    protected void Loop()
    {

        final Context f_context = this;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            LogOntoFirebase(true);
            /**
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
                        else {
                            Log.d(TAG, "Cannot sign on with the saved credentials");
                        }
                        _loop_started = false;
                        StartLoop();
                    }
                });
            }
            else {
                Log.d(TAG, "Cannot log into firebase");
            }
             **/
        } else {
            UpdatePosition();
            _loop_started = false;
            StartLoop();
        }



    }


    protected void LogOntoFirebase(final boolean startLoop)
    {

        final Context f_context = this;
        AuthCredential cred;
        if ((cred = AuthentificationHelper.GetSavedCredentialsIfExists(f_context)) != null) {
            // got the credential..
            FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Log onto Firebase successfully");
                        if (startLoop)
                        {
                            _loop_started = false;
                            StartLoop();
                        }
                    }
                    else {
                        Log.d(TAG, "Cannot sign on with the saved credentials");
                    }
                }
            });
        }
        else {
            Log.d(TAG, "Cannot log into firebase");
        }

    }


    protected void UpdatePosition() {

        Location loc = GetCurrentLocation();
        if (loc != null) {
            // update it..
            mLocationsBuffer.add(GetLocationAsMap(loc));
            // save it to the preference


            if (_should_update_firebase && (_lastUploadToFirebase >= FIREBASE_UPLOAD_PERIOD)){
                Log.i(TAG, "Updating Firebase");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    String ref_loc = "locations/" + uid;
                    // add the year, month, day
                    // note that the time is assumed to be correct (will map to database time later
                    Calendar now = GregorianCalendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    String month = now.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    int day_of_month = now.get(Calendar.DAY_OF_MONTH);
                    ref_loc += "/" + year + "/" + month + "/" + day_of_month;

                    DatabaseReference myRef = database.getReference(ref_loc);
                    // create a hash map of location and
                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);
                    tmpMap.put(Constants.POSITIONS, mLocationsBuffer);
                    myRef.push().setValue(tmpMap);
                    mLocationsBuffer.clear();
                    _lastUploadToFirebase = 0;
                }
                else {
                    Log.d(TAG, "There is no current user for Firebase");
                    // try to log on
                    LogOntoFirebase(false);
                }

            }
            else {
                _lastUploadToFirebase += GPS_SCAN_PERIOD;
                // add the location to the array.
                if (_should_update_firebase) {
                    Log.i(TAG, "Pooling updates before updating firebase");
                }
                else {
                    Log.d(TAG, "Updating Firebase is turned off");
                }


            }
            // get the user id
        }





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


        return mMessenger.getBinder();
    }




    void SendPositionUpdateToClient(Messenger client, int code, int id) throws RemoteException{
        Message msg = Message.obtain(null, code);
        // create a bundle
        msg.replyTo = mMessenger;
        msg.arg1 = id;

        Location loc = GetCurrentLocation();
        // create a bundle
        Bundle dataBundle = new Bundle();
        dataBundle.putDouble("lat", loc.getLatitude());
        dataBundle.putDouble("lng", loc.getLongitude());
        // time..
        Date now = new Date();
        dataBundle.putLong("time", now.getTime());
        msg.setData(dataBundle);

            client.send(msg);




    }

    // create a broadcast receiver rather than using the bind/unbine








    /**
     * Handler of incoming messages from clients.
     */
    static private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Message Received " + CodeToDescription(msg.what));
            PositionUpdateService serviceMain = PositionUpdateService.getInstance();
            if (serviceMain == null)
            {
                super.handleMessage(msg);
                return;
            }

            try {
                Message msg_reply;
                switch (msg.what) {

                    case MSG_START_POSITIONUPDATE:

                        serviceMain.StartPositionUpdate(msg.arg2 == 1);
                        // send back the acknowledgement
                        msg_reply = Message.obtain(null, MSG_START_POSITIONUPDATE_ACK, msg.arg1, 0);
                        msg.replyTo.send(msg_reply);
                        break;
                    case MSG_STOP_POSITIONUPDATE:

                        serviceMain.StopPositionUpdate();
                        msg_reply = Message.obtain(null, MSG_STOP_POSITIONUPDATE_ACK, msg.arg1, 0);
                        msg.replyTo.send(msg_reply);
                        break;
                    case MSG_GET_POSITION_UPDATE:
                        serviceMain.SendPositionUpdateToClient(msg.replyTo, MSG_GET_POSITION_UPDATE_ACK, msg.arg1);
                        break;
                    case MSG_CREATE_USER:

                        // get the bundle with the intent
                        Bundle bundle = msg.getData();
                        Map<String, String> res = FieldValidation.validateUserInfo(bundle);
                        if (res.isEmpty() == false)
                        {

                        }
                        else {
                            // create user..
                            //serviceMain.CreateUser(bundle);
                        }

                    default:
                        super.handleMessage(msg);
                }
            }
            catch (RemoteException re)
            {
                re.printStackTrace();
                Log.e(TAG, re.getMessage());

            }
        }


    }

}




