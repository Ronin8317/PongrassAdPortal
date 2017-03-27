package adportal.pongrass.com.au.pongrassadportal.data;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class User {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<UserEntry> ITEMS = new ArrayList<UserEntry>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, UserEntry> ITEM_MAP = new HashMap<String, UserEntry>();

    private static final int COUNT = 25;



    private static void addItem(UserEntry item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.toString(), item);

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
    public static class UserEntry {
        public String _Name = "";
        public Map<String, Address> _Addresses = new HashMap<>();
        public String _DisplayName = "";
        public String _ID; // Firebase ID. Created
        public List<UserEntry> _Friends = new ArrayList<>();





        public UserEntry(Bundle bundle) {

            _Name = bundle.getString("name");

        }

        @Override
        public String toString() {
            return _ID;
        }
    }
}
