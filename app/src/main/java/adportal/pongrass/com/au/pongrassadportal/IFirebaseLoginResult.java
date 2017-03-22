package adportal.pongrass.com.au.pongrassadportal;

import com.google.firebase.auth.FirebaseUser;

/**
 *
 * Callback for Firebase logn result using the AuthentificationHelper
 * Created by Ronin on 14/03/2017.
 *
 * @see AuthentificationHelper
 */



public interface IFirebaseLoginResult {

    /**
     *
     * @param user is a valid reference to the user if successful, null if not successful
     */
    public void LoginResult(FirebaseUser user);

}
