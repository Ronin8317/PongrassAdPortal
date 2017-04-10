package adportal.pongrass.com.au.pongrassadportal.data;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

/**
 * Created by user on 5/04/2017.
 */

public class FirebaseDataCurrentUser extends FirebaseData {

    public final static String USER_NAME = "user_details/name";
    public final static String TRUSTED_NICKNAME = "user_details/trusted_nickname"; // one and only
    public final static String ANON_NICKNAME = "user_details/anon_nicknames"; // anonymous nicks, can be more than one
    public final static String SUBSCRIBED_GROUPS = "user_details/subscribed_groups";
    public final static String ACTIVE_SCHEDULE = "user_details/active/schedule";
    public final static String PAST_SCHEDULE = "user_details/past/schedule";
    public final static String FRIENDS = "user_details/friends";
    // user locations
    public final static String USER_LOCATIONS = "user_details/locations"; // user owned locations, including home

    public final static String USER_EVENTS = "user_details/events"; // user owned events
    public final static String USER_FAVORITE_LOCATIONS = "user_details/favorite/locations";
    public final static String USER_FAVORITE_EVENTS = "user_details/favorite/events";
    public final static String USER_FAVORITE_FRIENDS = "user_details/favorite/friends";




    public static class CurrentUserFactory implements FirebaseDataFactory
    {

        @Override
        public FirebaseData ReturnClass(String path, String data) {


            return new FirebaseDataCurrentUser(path, data);
        }

        @Override
        public boolean shouldHandle(String path) {

            if (path == null) return false;
                // split it
            String Paths[] = path.split("/");
            if (Paths.length != 2) return false;
            if ("users".equals(Paths[0]))
            {
                return true;
            }
            return false;
        }
    }

    static {
        // register the user
        FirebaseData.RegisterFactory(new CurrentUserFactory());
    }

    protected String _ID = "";

    public FirebaseDataCurrentUser(String Path, String dataJSON)
    {
        // Path should be 'users/$FirebaseID'
        super(Path);

        String Paths[] = Path.split("/");

        _ID = Paths[1];


        try {
            JSONParser jParser = new JSONParser();
            JSONObject currentUserJSON = (JSONObject)jParser.parse(dataJSON);
            if (currentUserJSON != null)
            {
                // the actual data is stored in JSON..
                _ObjectData = currentUserJSON;
            }
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
            Log.e(this.getClass().getCanonicalName(), pe.getMessage());
        }

    }



    // the user
    // the structure is this
    // users/$FirebaseID$/user_details

    public String getFirebaseID()
    {
        return _ID;
    }

    //





    // friends, trusted.



}
