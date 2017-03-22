package adportal.pongrass.com.au.pongrassadportal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 19/12/2016.
 */

public class PositionUpdateListener implements LocationListener {

    protected Location mLoc = null;




    public PositionUpdateListener(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
    }

    public String GetLocationAsString()
    {
        String res = "";
        try {
            if (mLoc != null) {
                // don't do it as string, do it as a map..
                JSONObject jo = new JSONObject();
                jo.put(Constants.LATITUDE, Double.toString(mLoc.getLatitude()));
                jo.put(Constants.LONGITUDE, Double.toString(mLoc.getLongitude()));
                res = jo.toString();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    public Map<String, Object> GetLocationAsMap()
    {
        Map<String, Object> result = new HashMap<String, Object>();

        if (mLoc != null)
        {
            result.put(Constants.LATITUDE, Double.toString(mLoc.getLatitude()));
            result.put(Constants.LONGITUDE, Double.toString(mLoc.getLongitude()));
            result.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);

        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLoc = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
