package adportal.pongrass.com.au.pongrassadportal.data;

import android.os.Bundle;

/**
 * Created by user on 29/03/2017.
 */

public interface IFirebaseDataReady {

    public void Success(FirebaseData data);

    public void OnFailure(int ErrorCode, String Message);


}
