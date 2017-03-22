package adportal.pongrass.com.au.pongrassadportal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 19/12/2016.
 */

public class PositionUpdateReceiver extends BroadcastReceiver {





    public PositionUpdateReceiver()
    {
    }

    protected Location GetCurrentLocation(Context context)
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
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
                return null;
            }
            return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    public Map<String, Object> GetLocationAsMap(Location loc)
    {
        Map<String, Object> result = new HashMap<String, Object>();

        if (loc != null)
        {
            result.put(Constants.LATITUDE, Double.toString(loc.getLatitude()));
            result.put(Constants.LONGITUDE, Double.toString(loc.getLongitude()));
            result.put(Constants.TIMESTAMP, ServerValue.TIMESTAMP);

        }
        return result;
    }

    public String GetLocationAsString(Location mLoc)
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


    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Received Alarm", Toast.LENGTH_SHORT).show();

        // log onto Firebase if not already logged on..
        /**
        final Context f_context = context;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            AuthCredential cred;
            if ((cred = AuthentificationHelper.GetSavedCredentialsIfExists(context)) != null) {
                // got the credential..
                FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UpdatePosition(f_context);
                        }
                    }
                });
            }
        }
        else {
            UpdatePosition(f_context);
        }
         **/

        //Intent updateIntent = new Intent(context, PositionUpdateService.class);
        //updateIntent.putExtra(Intent.EXTRA_TEXT, "abc");
        //context.startService(updateIntent);
    }

    public void UpdatePosition(Context context)
    {
        Location loc = GetCurrentLocation(context);
        if (loc != null)
        {
            String txt = GetLocationAsString(loc);
            Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String ref_loc = "locations/" + uid;
            DatabaseReference myRef = database.getReference(ref_loc);
            // get the user id

            myRef.push().setValue(GetLocationAsMap(loc));

        }

    }
}
