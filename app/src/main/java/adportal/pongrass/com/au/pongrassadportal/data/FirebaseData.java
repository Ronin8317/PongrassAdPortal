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


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

        public FirebaseID(String Path)
        {
            _FirebasePath = Path;
        }


        protected boolean shouldBePushed; // true if it's a push, false if it's a set

        // need a parent as well

        public String toString()
        {
            return _FirebasePath;
        }

    }



    // special string..

    protected final static String CurrentUserPath = "CurrentUser";

    protected JSONObject _ObjectData = null;
    protected JSONArray _ArrayData = null;

    protected FirebaseID _ID = null;


    protected static String TAG = "FirebaseData";

    protected static ServiceConnection mServiceConnection = null;
    protected static Messenger mCommunicationServiceMessenger = null;
    protected static Messenger mCommunicationClient = new Messenger(new FirebaseIncomingHandler());

    // callback using the arg1..
    protected static Map<String, IFirebaseDataReady> _callback_map = new HashMap<>();
    protected static List<FirebaseDataFactory> _factories = new ArrayList<>();

    protected static String mCurrentUserID = null;
    protected static List<String> mCurrentUserGroups = new ArrayList<>();

    public static void RegisterFactory(FirebaseDataFactory factory)
    {
        _factories.add(factory);
    }




    protected static boolean ProcessCallback(String id, String path, String result)
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
            return factory.ReturnClass(path, data);
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

    protected FirebaseData(String Path)
    {
        // should not get here
        _ID = new FirebaseID(Path);

    }

    public String getDataString(String FieldName)
    {
        // split it up into
        String Paths[] = FieldName.split("/");
        // note that it must be an object..
        JSONObject currentObject = _ObjectData;

        for (int i=0;i<Paths.length-1 && currentObject != null;i++)
        {
            currentObject = (JSONObject) currentObject.get(Paths[i]);
        }
        if (currentObject != null)
        {
            return (String) currentObject.get(Paths[Paths.length-1]);
        }
        return null;
    }

    public void setDataString(String FieldName, String Value)
    {
        // split it up into
        String Paths[] = FieldName.split("/");
        // note that it must be an object..
        JSONObject currentObject = _ObjectData;
        JSONObject prevObject = _ObjectData;
        for (int i=0;i<Paths.length-1 && prevObject != null;i++)
        {
            currentObject = (JSONObject) prevObject.get(Paths[i]);
            if (currentObject == null)
            {
                currentObject = new JSONObject();
                prevObject.put(Paths[i], currentObject);
            }
            prevObject = currentObject;
        }
        if (prevObject != null)
        {
            prevObject.put(Paths[Paths.length-1], Value);
        }

    }


    public List<String> getDataStringArray(String FieldName)
    {
        // split it up into
        String Paths[] = FieldName.split("/");
        // note that it must be an object..
        JSONObject currentObject = _ObjectData;

        for (int i=0;i<Paths.length-1 && currentObject != null;i++)
        {
            currentObject = (JSONObject) currentObject.get(Paths[i]);
        }
        if (currentObject != null)
        {
             JSONArray ja = (JSONArray) currentObject.get(Paths[Paths.length-1]);
            // convert the JSONArray to a list
            return JSONHelper.ConvertJSONArrayToList(ja);
        }
        return null;

    }

    public void setDataStringArray(String FieldName, List<String> values)
    {

        String Paths[] = FieldName.split("/");
        // note that it must be an object..
        JSONObject currentObject = _ObjectData;

        for (int i=0;i<Paths.length-1 && currentObject != null;i++)
        {
            currentObject = (JSONObject) currentObject.get(Paths[i]);
        }
        if (currentObject != null)
        {
            JSONArray ja = JSONHelper.ConvertListToJSONArray(values);
            currentObject.put(Paths[Paths.length-1], ja);
        }
    }

    public void addToDataStringArray(String FieldName, String value)
    {
        List<String> array = getDataStringArray(FieldName);
        if (array.contains(value) == false)
        {
            array.add(value);
            setDataStringArray(FieldName, array);
        }
    };

    public Map<String, String> getDataStringMap(String FieldName)
    {
        JSONObject jo = (JSONObject)_ObjectData.get(FieldName);
        if (jo == null) return null;

        return JSONHelper.ConvertObjectToStringMap(jo);
    }

    public void setDataStringMap(String FieldName, Map<String, String> map)
    {
        JSONObject jo = JSONHelper.ConvertStringMapToObject(map);
        _ObjectData.put(FieldName, jo);
    }


    public static void storeData(final FirebaseID ID,  final String data_string, final Context context, final IFirebaseDataReady callback)
    {
        if (mServiceConnection == null)
        {
            InitServiceConnection(new IFirebaseDataReady() {
                @Override
                public void Success(FirebaseData data) {
                    storeData(ID, data_string, context, callback);
                }

                @Override
                public void Success(List<FirebaseData> datalist) {
                    storeData(ID, data_string, context, callback);
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
            Message msg = Message.obtain(null, MSG_SAVE_DATA);
            msg.replyTo = mCommunicationClient;
            try {
                String key ="7" + new RandomPasswordGenerator().Digit(8);
                msg.arg1 = Integer.parseInt(key);
                Bundle data = new Bundle();
                data.putString("firebase_path", ID.toString());
                data.putString("firebase_data", data_string);
                msg.setData(data);
                _callback_map.put(key, callback);
                mCommunicationServiceMessenger.send(msg);
            }
            catch (RemoteException re)
            {
                Log.e(TAG, "Saving Firebse Data failed");
                Log.e(TAG, re.getMessage());
                callback.OnFailure(ERR_NO_SERVER_REGISTERED, "No Server found");

            }



        }

    }


    public static void extractData(final FirebaseID ID, final Context context, final IFirebaseDataReady callback) {

        // the callback can be null for a save and forget
        // the bundle is also saved in the temporary cache
        // so if there is no

        // get the binded service
        if (mServiceConnection == null)
        {
            InitServiceConnection(new IFirebaseDataReady() {
                @Override
                public void Success(FirebaseData data) {
                    extractData(ID, context, callback);
                }

                @Override
                public void Success(List<FirebaseData> datalist) {
                    extractData(ID, context, callback);
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






    public static class FirebaseIncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_EXTRACT_DATA_ACK:

                    // extraction of data is successful. The bundle will contain a JSON
                    // register the callback

                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        // the data is a JSONString

                        String JSONData = bundle.getString("firebase_data");
                        String JSONPath = bundle.getString("firebase_path"); // this actually delegates to what object is returned
                        // get the callback handler
                        ProcessCallback("" + msg.arg1, JSONPath, JSONData);
                    }
                    break;


                case MSG_REQUEST_SERVER_STATUS:
                    if (msg.arg1 == 0) {

                    } else {
                        Log.d(TAG, "Service already started");
                    }
                default:
                    super.handleMessage(msg);
            }
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


                    msg = Message.obtain(Message.obtain(null, MSG_GET_CURRENT_USER));
                    msg.replyTo = mCommunicationClient;
                    String key ="7" + new RandomPasswordGenerator().Digit(8);
                    msg.arg1 = Integer.parseInt(key);

                    _callback_map.put(key, new HandleCurrentUserCallback());


                    // register the callback..

                    mCommunicationServiceMessenger.send(msg);


                    // register the callback..
                    callback.Success((FirebaseData)null);



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




   public static class HandleCurrentUserCallback implements IFirebaseDataReady
   {

       @Override
       public void Success(FirebaseData data) {

       }

       @Override
       public void Success(List<FirebaseData> data) {

       }

       @Override
       public void OnFailure(int ErrorCode, String Message) {

       }
   }


    public static String GetCurrentUserID()
    {
        return mCurrentUserID;
    }

    public static List<String> GetCurrentUserGroups()
    {
        return mCurrentUserGroups;
    }


}
