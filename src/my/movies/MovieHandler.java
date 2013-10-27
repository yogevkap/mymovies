package my.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MovieHandler {

	public MovieDbHelper dbhelper;
	
	//CTOR
	public MovieHandler(Context context){
		dbhelper = new MovieDbHelper(context, MovieDbConstants.DATABASE_NAME, null, MovieDbConstants.DATABASE_VERSION);
	}
	
	//Methods
	
	public Cursor getAllMovies(){
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query(MovieDbConstants.DATABASE_TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public Movie getMovie(int id){
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query(MovieDbConstants.DATABASE_TABLE_NAME, null, MovieDbConstants.KEYROW_ID + "=?", new String[]{ String.valueOf(id) }, null, null, null);
		
		Movie movie = null;
		
		if(cursor.moveToNext()){
			int ide = cursor.getInt(0);
			int score = cursor.getInt(1);
			String title = cursor.getString(2);
			String body = cursor.getString(3);
			String rdate = cursor.getString(4);
			String runtime = cursor.getString(5);
			int audience_score = cursor.getInt(6);
			String url = cursor.getString(7);
			String onlinepicture = cursor.getString(8);
			int checkbox = cursor.getInt(9);
			int maincheck = cursor.getInt(10);
			movie = new Movie(ide ,score, title, body, rdate, runtime, audience_score, url, onlinepicture, checkbox, maincheck);
		}
		return movie;

	}
	
	public void createEntry(float score, String title, String body, String rdate, String runtime, int audience_score , String url, String onlinepicture, int checkbox, int maincheckbox){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(MovieDbConstants.KEY_SCORE, score);
		cv.put(MovieDbConstants.KEY_TITLE, title);
		cv.put(MovieDbConstants.KEY_BODY, body);
		cv.put(MovieDbConstants.KEY_RELEASE_DATE, rdate);
		cv.put(MovieDbConstants.KEY_RUN_TIME, runtime);
		cv.put(MovieDbConstants.KEY_AUDIENCE_SCORE, audience_score);
		cv.put(MovieDbConstants.KEY_URL, url);
		cv.put(MovieDbConstants.KEY_ONLINE_PICTURE, onlinepicture);
		cv.put(MovieDbConstants.KEY_CHECK_BOX, checkbox);
		cv.put(MovieDbConstants.KEY_MAIN_CHACK_BOX, maincheckbox);
		db.insertOrThrow(MovieDbConstants.DATABASE_TABLE_NAME, null, cv);
		db.close();
	}
	
	public void updateEnrty(Movie movie){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(MovieDbConstants.KEY_SCORE, movie.getScore());
		cv.put(MovieDbConstants.KEY_TITLE, movie.getTitle());
		cv.put(MovieDbConstants.KEY_BODY, movie.getBody());
		cv.put(MovieDbConstants.KEY_RELEASE_DATE, movie.getReleaseDate());
		cv.put(MovieDbConstants.KEY_RUN_TIME, movie.getRunTime());
		cv.put(MovieDbConstants.KEY_AUDIENCE_SCORE, movie.getRating());
		cv.put(MovieDbConstants.KEY_URL, movie.getUrl());
		cv.put(MovieDbConstants.KEY_ONLINE_PICTURE, movie.getPicture());
		cv.put(MovieDbConstants.KEY_CHECK_BOX, movie.getCheckBox());
		cv.put(MovieDbConstants.KEY_MAIN_CHACK_BOX, movie.getMainCheckBox());
		db.update(MovieDbConstants.DATABASE_TABLE_NAME, cv, MovieDbConstants.KEYROW_ID + "=?", new String[]{ String.valueOf(movie.getId()) });
		db.close();
	}
	
	public void deleteMovie(Movie movie){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.delete(MovieDbConstants.DATABASE_TABLE_NAME, MovieDbConstants.KEYROW_ID + "=?", new String[]{ String.valueOf(movie.getId())});
		db.close();
	}
	
	public void deleteMovie(int id){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		db.delete(MovieDbConstants.DATABASE_TABLE_NAME, MovieDbConstants.KEYROW_ID + "=?", new String[]{ String.valueOf(id) });
		db.close();
	}
}
