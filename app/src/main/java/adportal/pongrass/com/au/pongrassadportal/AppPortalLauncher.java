package adportal.pongrass.com.au.pongrassadportal;

import android.app.Application;
import android.content.Intent;

/**
 * Created by user on 16/03/2017.
 */

public class AppPortalLauncher extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent serviceIntent = new Intent(getApplicationContext(), PositionUpdateService.class);
        startService(serviceIntent);
    }
}
