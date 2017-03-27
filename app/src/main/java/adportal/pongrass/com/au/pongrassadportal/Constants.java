package adportal.pongrass.com.au.pongrassadportal;

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
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;

    // sent from the server
    // server -> communication service
    public static final int MSG_REGISTER_SERVER = 3;
    public static final int MSG_UNREGISTER_SERVER = 4;

    public static final int MSG_REQUEST_SERVER_STATUS = 5;



    // start the position update request ping. Note that if arg1 is 1, then it'll update the Firebase database. otherwise it'll just update the client
    // sent from the client to the server
    // client -> communication service -> server
    public static final int MSG_START_POSITIONUPDATE = 100;
    // stop the position update request ping
    public static final int MSG_STOP_POSITIONUPDATE = 101;



    // get the current position, from client to server
    // client <-> communication service <-> server
    public static final int MSG_GET_POSITION_UPDATE = 102;
    public static final int MSG_GET_POSITION_UPDATE_RESPONSE = 103;

    // add the user that is allowed to access the location
    // note that the permisson is time contrained as well


    public static final int MSG_CREATE_USER = 200;
    public static final int MSG_EDIT_USER = 201;
    public static final int MSG_DELETE_USER = 202;

    public static final int MSG_ALLOW_USER_TO_ACCESS_LOCATION = 204;
    public static final int MSG_DISALLOW_USER_TO_ACCESS_LOCATION = 205;

    // the event flag will be added
    // events
    //    |
    //    ------- Unique Event ID
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



    public static final int MSG_CREATE_EVENT = 300;
    public static final int MSG_ADD_USERS_TO_EVENT = 301;
    public static final int MSG_REMOVE_USERS_FROM_EVENT = 302;
    public static final int MSG_CHANGE_EVENT = 303;
    public static final int MSG_CANCEL_EVENT = 304;

    public static final int MSG_SEARCH_EVENTS = 400;

    public static final int MSG_LIST_USERS = 500;
    public static final int MSG_LINK_USERS = 501; // phone or email. Note that the link can be a lot
    public static final int MSG_UNLINK_USERS = 502;









    // error
    public static final int ERR_NO_SERVER_REGISTERED = -100;






}
