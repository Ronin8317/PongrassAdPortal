package adportal.pongrass.com.au.pongrassadportal.data;

import android.os.Bundle;

import java.util.List;

/**
 * Created by user on 29/03/2017.
 */

public interface IFirebaseDataReady {

    public void Success(FirebaseData data);

    public void Success(List<FirebaseData> data);

    public void OnFailure(int ErrorCode, String Message);


}
