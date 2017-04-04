package adportal.pongrass.com.au.pongrassadportal.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adportal.pongrass.com.au.pongrassadportal.RandomPasswordGenerator;

import static adportal.pongrass.com.au.pongrassadportal.Constants.*;


/**
 * The FirebaseData class is the base class which handles all the saving and retrieval from firebase
 * it will call the service and handle the retrieval and communication to the server
 *
 * Created by Ronin on 29/03/2017.
 */

public class FirebaseData {

    public static class FirebaseID
    {
        // a unique identifier for the firebase path
        protected String _FirebasePath;

        protected FirebaseID _Parent; //  can be root


        protected boolean shouldBePushed; // true if it's a push, false if it's a set

        // need a parent as well

        public String toString()
        {
            return _FirebasePath;
        }

    }



    public FirebaseData(String parentPath)
    {

    }


    protected static String TAG = "FirebaseData";

    protected static ServiceConnection mServiceConnection = null;
    protected static Messenger mCommunicationServiceMessenger = null;
    protected static Messenger mCommunicationClient = new Messenger(new FirebaseIncomingHandler());

    // callback using the arg1..
    protected static Map<String, IFirebaseDataReady> _callback_map = new HashMap<>();
    protected static List<FirebaseDataFactory> _factories = new ArrayList<>();


    protected static boolean ProcessCallback(String id, String result, String path)
    {
        IFirebaseDataReady callback = _callback_map.get(id);
        if (callback != null)
        {
            callback.Success(FirebaseData.ObjectFromPathAndData(path, result));
        }
        return (callback != null);
    }

    public static FirebaseData ObjectFromPathAndData(String path, String data)
    {
        // get the class factory
        FirebaseDataFactory factory = FirebaseData.ReturnFactory(path);
        if (factory != null)
        {
            return factory.ReturnClass(data);
        }

        return null;
    }

    public static FirebaseDataFactory ReturnFactory(String path)
    {
        Iterator<FirebaseDataFactory> it = _factories.iterator();

        while (it.hasNext())
        {
            FirebaseDataFactory factory = it.next();
            if (factory.shouldHandle(path))
            {
                return factory;
            }
        }

        return null;
    }

    public static class FirebaseIncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_EXTRACT_DATA:

                    // extraction of data is successful. The bundle will contain a JSON
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        // the data is a JSONString

                        String JSONData = bundle.getString("data");
                        String JSONClass = bundle.getString("path"); // this actually delegates to what object is returned
                        // get the callback handler
                        ProcessCallback(""+msg.arg1, JSONData, JSONClass);
                    }
                    break;
                case MSG_REQUEST_SERVER_STATUS:
                    if (msg.arg1 == 0)
                    {

                    }
                    else {
                        Log.d(TAG, "Service already started");
                    }
                default:
                    super.handleMessage(msg);
            }
        }

    protected static void InitServiceConnection(final IFirebaseDataReady callback)
    {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                try {
                    mCommunicationServiceMessenger = new Messenger(iBinder);
                    Message msg = Message.obtain(Message.obtain(null, MSG_REGISTER_CLIENT));
                    msg.replyTo = mCommunicationClient;
                    mCommunicationServiceMessenger.send(msg);
                    callback.Success(null);

                }
                catch(RemoteException re)
                {
                    Log.e(TAG, "Init service connection failed");
                    Log.e(TAG, re.getMessage());
                    callback.OnFailure(ERR_NO_SERVER_REGISTERED, "No Server found");

                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                    Log.d(TAG, "Service disconnected");
                    mCommunicationServiceMessenger = null;


            }
        };
    }





    public static void getInstance(final FirebaseID ID, final Context context, final IFirebaseDataReady callback)
    {
        // the callback can be null for a save and forget
        // the bundle is also saved in the temporary cache
        // so if there is no

        // get the binded service
        if (mServiceConnection == null)
        {
            InitServiceConnection(new IFirebaseDataReady() {
                @Override
                public void Success(FirebaseData data) {
                    // bundle should be null
                    getInstance(ID, context, callback);
                }

                @Override
                public void OnFailure(int ErrorCode, String Message) {
                    if (callback != null) {
                        callback.OnFailure(ErrorCode, Message);
                    }
                }
            });

        }
        else {
            Message msg = Message.obtain(null, MSG_EXTRACT_DATA);
            msg.replyTo = mCommunicationClient;
            try {
                String key ="7" + new RandomPasswordGenerator().Digit(8);
                msg.arg1 = Integer.parseInt(key);
                Bundle data = new Bundle();
                data.putString("firebase_path", ID.toString());
                msg.setData(data);
                _callback_map.put(key, callback);
                mCommunicationServiceMessenger.send(msg);
            }
            catch (RemoteException re)
            {
                Log.e(TAG, "Obtaining Firebse Data failed");
                Log.e(TAG, re.getMessage());
                callback.OnFailure(ERR_NO_SERVER_REGISTERED, "No Server found");

            }

        }



        }
    };




}
