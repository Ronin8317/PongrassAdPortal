package adportal.pongrass.com.au.pongrassadportal.data;

/**
 * Created by user on 30/03/2017.
 */

public interface FirebaseDataFactory {

    // the class factory creates the class
    public FirebaseData ReturnClass(String path, String data);

    public boolean shouldHandle(String path);
}
