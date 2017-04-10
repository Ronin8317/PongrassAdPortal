package adportal.pongrass.com.au.pongrassadportal.data;

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



    static {
        // register the factory


    }



    public static class EventAggregator extends FirebaseDataAggregator
    {

    }

    public static List<EventItem> getDefaultItems()
    {
        List<EventItem> res = new ArrayList<>();
        return res;
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

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, EventItem> ITEM_MAP = new HashMap<String, EventItem>();

    private static final int COUNT = 25;



    private static void addItem(EventItem item) {
        ITEM_MAP.put(item.id, item);
    }

    private static EventItem createDummyItem(int position) {
        return new EventItem(String.valueOf(position), "Item " + position, makeDetails(position));
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
        public final String id;
        public final String content;
        public final String details;

        public EventItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
