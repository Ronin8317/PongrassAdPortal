package adportal.pongrass.com.au.pongrassadportal;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import static adportal.pongrass.com.au.pongrassadportal.Constants.*;

/**
 * Commincation service between the background service and the activity
 * This allows the binding interface to be used to communicate between server and client without the main
 * service being closed down during unbinding
 *
 *
 *
 * Created by Ronin on 24/03/2017.
 */

public class PongrassCommunicationService extends IntentService {

    private final static String TAG = "PongrassCommunication";

    public static List<Messenger> mClients = new ArrayList<>();
    public static Messenger mServer = null;

    // the messenger
    public static Messenger mCommunicator = new Messenger(new IncomingHandler());


    public PongrassCommunicationService()
    {
        super("PongrassCommunicationService");
    }

    // handle the binding

     /**
     * Handler of incoming messages from clients.
     */
    static private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Message Received");
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    Log.i(TAG, "Position Client Registered");
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    Log.i(TAG, "Position Client Unregistered");
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_REGISTER_SERVER:
                    Log.i(TAG, "Position Server Registered");
                    mServer = msg.replyTo;
                    break;
                case MSG_UNREGISTER_SERVER:
                    Log.i(TAG, "Position Server Unregistered");
                    mServer = null;
                    break;
                case MSG_START_POSITIONUPDATE:
                    Log.i(TAG, "Position Update Request Updated");
                    try {
                        if (mServer != null) {

                            Log.i(TAG, "Sending message to server");
                            Message msg_to_server = Message.obtain(null, MSG_START_POSITIONUPDATE, msg.arg1, -1);
                            mServer.send(msg_to_server);
                        } else {
                            Log.i(TAG, "No server registered");
                            Message msg_to_client = Message.obtain(null, ERR_NO_SERVER_REGISTERED);
                            msg.replyTo.send(msg_to_client);

                        }
                    }catch(RemoteException re)
                    {
                        Log.e(TAG, "Start Position Update Failed");
                        Log.e(TAG, re.getMessage());
                    }

                    break;
                case MSG_STOP_POSITIONUPDATE:
                    Log.i(TAG, "Position Update Request stopped");
                    try {
                        if (mServer != null) {

                            Log.i(TAG, "Sending message to server");
                            Message msg_to_server = Message.obtain(null, MSG_STOP_POSITIONUPDATE);
                            msg_to_server.replyTo = mCommunicator;
                            mServer.send(msg_to_server);
                        } else {
                            Log.i(TAG, "No server registered");
                            Message msg_to_client = Message.obtain(null, ERR_NO_SERVER_REGISTERED);
                            msg_to_client.replyTo = mCommunicator;
                            msg.replyTo.send(msg_to_client);

                        }
                    }catch(RemoteException re)
                    {
                        Log.e(TAG, "Stop Position Update Failed");
                        Log.e(TAG, re.getMessage());
                    }
                    break;
                case MSG_GET_POSITION_UPDATE:
                    // it's from the client to the server
                    Log.i(TAG, "Single Position Update Request");
                    try {
                        if (mServer != null)
                        {
                            Log.i(TAG, "Sending message to server");
                            Message msg_to_server = Message.obtain(null, MSG_GET_POSITION_UPDATE);
                            msg_to_server.replyTo = mCommunicator;
                            mServer.send(msg_to_server);

                        }
                        else {
                            Log.i(TAG, "No server registered");
                            Message msg_to_client = Message.obtain(null, ERR_NO_SERVER_REGISTERED);
                            msg.replyTo.send(msg_to_client);

                        }

                    }
                    catch (RemoteException re)
                    {
                        Log.e(TAG, "Get Position Update Failed");
                        Log.e(TAG, re.getMessage());
                    }

                    break;
                case MSG_GET_POSITION_UPDATE_RESPONSE:
                    // it's from the server to the client
                    Log.i(TAG, "Single Position Update Response");
                    try {
                        // send each client a copy..
                        for (Messenger client : mClients)
                        {
                            Message msg_to_client = Message.obtain(null, MSG_GET_POSITION_UPDATE_RESPONSE);
                            msg_to_client.setData(msg.getData());
                            msg_to_client.replyTo = mCommunicator;
                            client.send(msg_to_client);

                        }

                    }
                    catch (RemoteException re)
                    {
                        Log.e(TAG, "Get Position Update Response Failed");
                        Log.e(TAG, re.getMessage());
                    }

                case MSG_REQUEST_SERVER_STATUS:
                    Log.d(TAG, "Server status request");
                    try {
                        Message msg_to_client = Message.obtain(null, MSG_REQUEST_SERVER_STATUS);
                        msg_to_client.arg1 = (mServer == null) ? 0 : 1;
                        msg.replyTo.send(msg_to_client);
                    }
                    catch (RemoteException re)
                    {
                        Log.e(TAG, "Server status request Failed");
                        Log.e(TAG, re.getMessage());
                    }

                default:
                    super.handleMessage(msg);
            }
        }


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCommunicator.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Communication thread has started");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
