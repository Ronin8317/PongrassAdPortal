package adportal.pongrass.com.au.pongrassadportal;

/**
 * Created by user on 5/04/2017.
 */

public interface IFirebaseDataReadyCallback<T> {

    public void OnDataReady(T data);

    public void OnError();
}
