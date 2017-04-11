package adportal.pongrass.com.au.pongrassadportal.data;

import android.os.Bundle;


import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adportal.pongrass.com.au.pongrassadportal.IFirebaseDataReadyCallback;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 *
 */
public class Events {

    public final static String _AllUserPath = "events/users/AllUsers";
    public final static String _AllEvent = "events/AllGroups";

    public final static String EVENT_ID = "event_id"; // unique
    public final static String EVENT_TITLE = "event_title";
    public final static String EVENT_DESCRIPTION = "event_description";

    /**
     * A map of sample (dummy) items, by ID.
     */
    private static Map<String, EventItem> ITEM_MAP;


    static {
        // register the factory

        ITEM_MAP = new HashMap<>();

        for (int i=0;i<20;i++) {
            addItem(createDummyItem(i));
        }

    }



    public static class EventAggregator extends FirebaseDataAggregator
    {

    }

    public static List<EventItem> getDefaultItems()
    {
       List<EventItem> list = new ArrayList<>();

        for (String key : ITEM_MAP.keySet())
        {
            list.add(ITEM_MAP.get(key));
        }

        return list;
    }

    /**
     * An array of sample (dummy) items.
     */
    public static void GetAvailableEvents(IFirebaseDataReadyCallback<List<EventItem>> callback)
    {
        List<EventItem> result = new ArrayList<>();
        // load all the available events based on Firebase ID
        // get the userID
        String UserId = FirebaseData.GetCurrentUserID();
        List<String> SubscribedGroup = FirebaseData.GetCurrentUserGroups();

        // from the user ID, get the current available Events..
        String AllUserPath = _AllUserPath;
        String AllGroupPath = _AllEvent;

        // get the subscribed groups..


        String OwnerPath = "events/users/"+ UserId + "/owner";
        String SubscribedPath =  "events/"+ UserId + "/subscribed";



        // have to chain the result..





    }




    private static final int COUNT = 25;



    private static void addItem(EventItem item) {
        String id = item.getID();
        ITEM_MAP.put(id, item);
    }

    private static EventItem createDummyItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(EVENT_ID, ""+(position+1));
        bundle.putString(EVENT_TITLE, "Event Item " + (position + 1));
        bundle.putString(EVENT_DESCRIPTION, "Dummy event " + (position + 1));

        return new EventItem(bundle);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class EventItem {

        Bundle mBundle;

        protected EventItem(Bundle bundle) {
            mBundle = bundle;
            assert(mBundle != null);

        }

        public String getID()
        {
            return mBundle.getString(EVENT_ID, "");
        }

        public String getDisplayString()
        {
            return mBundle.getString(EVENT_TITLE, "");
        }

        public String getDisplayDetails()
        {
            return mBundle.getString(EVENT_DESCRIPTION, "Description");
        }

        @Override
        public String toString() {
            return mBundle.getString(EVENT_TITLE, "");
        }
    }
}
