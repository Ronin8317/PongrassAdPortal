package adportal.pongrass.com.au.pongrassadportal.data;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 * Created by user on 6/04/2017.
 */

public class FirebaseDataEvents extends FirebaseData {


    public static final String EVENT_LOCATIONS = "event_details/location"; // can be empty

    public static final String EVENT_TITLE = "event_details/title"; //

    public static final String EVENT_DESCRIPTION = "event_details/description"; // can be empty

    public static final String EVENT_TIME = "event_details/time";
        // may be one of the followig
        // starttime -> timestamp, endtime -> timestamp  for  normal start and stop event
        // recurring ->
        //          -> daily
        //
        //              -> skip days
        //          -> weekly
        //              -> dow : the day of week it applies to
        //          -> monhtly
        //              -> dom : the day of month it applies to
        //           -> yearly
        //              -> doy : the day of year it applies to
        //          -> hourly
        //                -> hours
        //                -> duration


    public static final String EVENT_SUBSCRIBER = "event_details/subscribers";
    public static final String EVENT_OWNER = "event_details/owner"; // can be open, invite only or close
    // open or closed.

    public static final String EVENT_REGISTRATION_STATUS = "event_details/registration_status"; // can be open, invite only or close






    protected String _EventID;

    public static class FirebaseDataEventsFactory implements FirebaseDataFactory
    {

        @Override
        public FirebaseData ReturnClass(String path, String data) {
            return new FirebaseDataEvents(path, data);
        }

        @Override
        public boolean shouldHandle(String path) {
            String Paths[] = path.split("/");
            if (Paths.length == 2)
            {
                if ("events".equals(Paths[0]))
                {
                    return true;
                }
            }
            return false;
        }
    }

    protected FirebaseDataEvents(String path, String dataJSON)
    {
        super(path);
        String Paths[] = path.split("/");

        if (Paths.length == 2)
        {
            _EventID = Paths[1];
        }

        try {
            JSONParser jParser = new JSONParser();
            JSONObject eventJSON = (JSONObject)jParser.parse(dataJSON);
            if (eventJSON != null)
            {
                // the actual data is stored in JSON..
                _ObjectData = eventJSON;
            }
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
            Log.e(this.getClass().getCanonicalName(), pe.getMessage());
        }

    }




}
