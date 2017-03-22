package adportal.pongrass.com.au.pongrassadportal;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to access the SQLite database. Useful for storing informations
 *
 * Created by Ronin on 16/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;


    protected String _DBName;
    protected String _CreateEntryString;

    DatabaseHelper(Context context, String db_name, String createEntryString)
    {
        super(context, db_name, null, DATABASE_VERSION);
        _DBName = db_name;
        _CreateEntryString = createEntryString;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(_CreateEntryString);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String delete_entry = "DROP TABLE IF EXISTS " + _DBName;
        db.execSQL(delete_entry);
        onCreate(db);
    }
}
