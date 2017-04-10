package adportal.pongrass.com.au.pongrassadportal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ronin on 19/12/2016.
 * Last updated by Ronin on 23/3/2017
 * Contains the string constant.
 */

public class Constants {

    public static final String BROADCAST_ACTION = "adportal.pongrass.com.au.pongrassadport.BROADCAST";
    public static final String DATA_STATUS = "adportal.pongrass.com.au.pongrassadport.STATUS";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIMESTAMP = "timestamp";
    public static final String POSITIONS = "positions";

    public static final int POSITION_UPDATE_FREQUENCY = 300000;

    public static final int GPS_SCAN_PERIOD = 30000; // every 30 seconds..
    public static final int FIREBASE_UPLOAD_PERIOD = 60000; // every minutes.


    public static final String DB_LOCATION = "location";

    //-----------------------------------------------------------------------------------------------------------------------------------------
    //
    //      Message codes for communication between client and server
    //
    //

    // sent from the client.
    // client -> communication service
    // ack is the response + 1,

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_REGISTER_CLIENT_ACK = 2;

    public static final int MSG_UNREGISTER_CLIENT = 3;
    public static final int MSG_UNREGSTER_CLIENT_ACK = 4;

    // sent from the server
    // server -> communication service
    public static final int MSG_REGISTER_SERVER = 5;
    public static final int MSG_REGISTER_SERVER_ACK = 6;

    public static final int MSG_UNREGISTER_SERVER = 7;
    public static final int MSG_UNREGISTER_SERVER_ACK = 8;

    public static final int MSG_REQUEST_SERVER_STATUS = 9;
    public static final int MSG_REUQEST_SERVER_STATUS_ACK = 10;



    // start the position update request ping. Note that if arg1 is 1, then it'll update the Firebase database. otherwise it'll just update the client
    // sent from the client to the server
    // client -> communication service -> server
    public static final int MSG_START_POSITIONUPDATE = 101;
    public static final int MSG_START_POSITIONUPDATE_ACK = 102;

    // stop the position update request ping
    public static final int MSG_STOP_POSITIONUPDATE = 103;
    public static final int MSG_STOP_POSITIONUPDATE_ACK = 104;


    // get the current position, from client to server
    // client <-> communication service <-> server
    public static final int MSG_GET_POSITION_UPDATE = 105;
    public static final int MSG_GET_POSITION_UPDATE_ACK = 106;

    public static final int MSG_REQUEST_POSITION_UPDATE_STATUS = 107;
    public static final int MSG_REQUEST_POSITION_UPDATE_STATUS_ACK = 108;

    // add the user that is allowed to access the location
    // note that the permisson is time contrained as well


    public static final int MSG_CREATE_USER = 200;
    public static final int MSG_CREATE_USER_ACK = 201;

    public static final int MSG_EDIT_USER = 202;
    public static final int MSG_EDIT_USER_ACK = 203;

    public static final int MSG_DELETE_USER = 204;
    public static final int MSG_DELETE_USER_ACK = 205;


    public static final int MSG_ALLOW_USER_TO_ACCESS_LOCATION = 206;
    public static final int MSG_ALLOW_USER_TO_ACCESS_LOCATION_ACK = 207;

    public static final int MSG_DISALLOW_USER_TO_ACCESS_LOCATION = 208;
    public static final int MSG_DISALLOW_USER_TO_ACCESS_LOCATION_ACK = 208;

    public static final int MSG_GET_CURRENT_USER = 1000;
    public static final int MSG_GET_CURRENT_USER_ACK = 1001;

    public static final int MSG_UPDATE_CURRENT_USER = 1002;
    public static final int MSG_UPDATE_CURRENT_USER_ACK = 1003;

    // the event flag will be added
    // events
    //    |
    //    ------- Unique Events ID
    //                  |
    //                  ------ access : <public or private>. A public event is open to all to register, a private event can only be registered by the owner
    //                  |
    //                  ------ owner : <unique id> The unique ID of the owner who created the event
    //                  |

    //                  |
    //                  ------- details
    //                            |
    //                             -------- fixed_locations : link to a fixed location
    //                            |
    //                             --------- locations : link to a mobile location/person.
    //                            |
    //                             ------ time_id
    //                                     |
    //                                      ------ start : <startimg time>
    //                                     |
    //                                      ------- stop : <stopping time>



    public static final int MSG_CREATE_EVENT = 2000;
    public static final int MSG_CREATE_EVENT_ACK = 2001;


    public static final int MSG_SUBSCRIBE_TO_EVENT = 2002;
    public static final int MSG_SUBSCRIBE_TO_EVENT_ACK = 2003;

    public static final int MSG_UNSUBCRIBE_FROM_EVENT = 2004;
    public static final int MSG_UNSUBCRIBE_FROM_EVENT_ACK = 2005;

    public static final int MSG_EDIT_EVENT          = 2006;
    public static final int MSG_EDIT_EVENT_ACK      = 2007;

    public static final int MSG_CANCEL_EVENT        = 2008;
    public static final int MSG_CANCEL_EVENT_ACK    = 2009;

    public static final int MSG_SEARCH_EVENTS = 3000; //  no such thing



    public static final int MSG_EXTRACT_DATA        = 1000000;
    public static final int MSG_EXTRACT_DATA_ACK    = 1000001;
    public static final int MSG_SAVE_DATA       = 2000000;
    public static final int MSG_SAVE_DATA_ACK   = 2000001;









    // error
    public static final int ERR_NO_SERVER_REGISTERED = -100;
    public static final int ERR_NO_CLIENT_REGISTERED = -99;

    public static final int ERR_DATA_SAVING_FAILED = -101;
    public static final int ERR_DATA_EXTRACTION_FAILED = -102;

    public static final int ERR_FIREBASE_CANNOT_CONNECT = -103;
    public static final int ERR_FIREBASE_NO_USER = -104;
    public static final int ERR_FIREBASE_CANNOT_LOGIN = -105;

    protected static Map<Integer, String> mCodeMap = new HashMap<>();

    // a map from Integer to String
    static {
        mCodeMap.put(MSG_REGISTER_CLIENT, "Register Client");
       mCodeMap.put(MSG_REGISTER_CLIENT_ACK, "Register Client Ack");

        mCodeMap.put(MSG_UNREGISTER_CLIENT, "Unregister Client");
        mCodeMap.put(MSG_UNREGSTER_CLIENT_ACK, "Unregister Client Ack");





    }


    public static String CodeToDescription(int Code)
    {
        String res =  mCodeMap.get(Code);
        if (res == null) {

            res = "Message Code (" + Code + ")";
        }
        return res;
    }





}
