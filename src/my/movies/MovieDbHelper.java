package my.movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

	public MovieDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieDbConstants.DATABASE_TABLE_NAME + " (" +
				MovieDbConstants.KEYROW_ID + " INTEGER PRIMARY KEY, " +
				MovieDbConstants.KEY_SCORE + " INTEGER, " +
				MovieDbConstants.KEY_TITLE + " TEXT, " +
				MovieDbConstants.KEY_BODY + " TEXT, " +
				MovieDbConstants.KEY_RELEASE_DATE + " TEXT, " +
				MovieDbConstants.KEY_RUN_TIME + " TEXT, " +
				MovieDbConstants.KEY_AUDIENCE_SCORE + " TEXT, " +
				MovieDbConstants.KEY_URL + " TEXT, " +
				MovieDbConstants.KEY_ONLINE_PICTURE + " TEXT, " +
				MovieDbConstants.KEY_CHECK_BOX + " INTEGER, " +
				MovieDbConstants.KEY_MAIN_CHACK_BOX + " TEXT);";
			
		try{
			db.execSQL(CREATE_MOVIE_TABLE);
		}catch(Exception e){
			e.getMessage();
			e.fillInStackTrace();
		}
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP IF TABLE EXISTS" + MovieDbConstants.DATABASE_TABLE_NAME);
		onCreate(db);

	}

}
