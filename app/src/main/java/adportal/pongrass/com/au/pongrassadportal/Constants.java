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

    public static final String DB_LOCATION = "location";

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

    // error
    public static final int ERR_NO_SERVER_REGISTERED = -100;


}
