package adportal.pongrass.com.au.pongrassadportal;

import android.app.Application;
import android.content.Intent;

import adportal.pongrass.com.au.pongrassadportal.service.PositionUpdateService;

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
