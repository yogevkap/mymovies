package my.movies;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends ListActivity  {

	//Attributes
	private ArrayList<Movie> movieList;
	private MovieAdapter movieAdpater;
	private ListView listView;
	private MovieHandler db;
	private Cursor cursor;
	private SearchView mSearchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.main_menu);
		
		//Make the keyboard hidden
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
				
		//Initialize Attributes
		movieList = new ArrayList<Movie>();
		
		//Initialize Handler Attribute
		db = new MovieHandler(this);
		cursor = db.getAllMovies();
		refreahList(cursor);
			
		movieAdpater = new MovieAdapter(this, android.R.layout.simple_list_item_1, movieList);
		
		//Initialize ListView
		listView = getListView();
		listView.setAdapter(movieAdpater);
		
		if(movieList.size() == 0){
			
			AlertDialog.Builder emptylist = new AlertDialog.Builder(MainActivity.this);
			emptylist.setTitle(getString(R.string.emptylist_title));
			emptylist.setMessage(getString(R.string.emptylist_body));
			emptylist.setPositiveButton("OK", null);
			emptylist.show();
		}
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(MainActivity.this, EditMovie.class);
				intent.putExtra("ID", movieAdpater.getItem(position).getId());
				intent.putExtra("LISTID", movieList.get(position).getId());
				startActivity(intent);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				final int pos = position;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Choose an action:");
				builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						Intent intent = new Intent(MainActivity.this, EditMovie.class);
						intent.putExtra("ID", movieAdpater.getItem(pos).getId());
						startActivity(intent);	
					}
				});
				
				builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						db.deleteMovie(movieAdpater.getItem(pos).getId());
						Intent intent = new Intent(MainActivity.this, MainActivity.class);
						startActivity(intent);
					}
				});
				
				builder.show();
				return false;
			}
		});
		
			
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == event.KEYCODE_BACK){
			
			Toast.makeText(this, "Have a great day :)", Toast.LENGTH_SHORT).show();
			Intent exit = new Intent(Intent.ACTION_MAIN);
			exit.addCategory(Intent.CATEGORY_HOME);
			exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(exit);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void refreahList(Cursor cursor){
		
		while(cursor.moveToNext()){
			
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
			int maincheckbox = cursor.getInt(10);
			Movie movie = new Movie(ide ,score, title, body, rdate,runtime, audience_score, url, onlinepicture, checkbox, maincheckbox);
			movieList.add(movie);
		}
	}
	
	public void deleteAllList(Cursor cursor){
		
		while(cursor.moveToNext()){
			
			int id = cursor.getInt(0);
			db.deleteMovie(id);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainactivity_settings, menu);
		
		mSearchView = (SearchView) menu.findItem(R.id.main_actionbar_search).getActionView();
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
            	MainActivity.this.movieAdpater.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("onQueryTextSubmit----------");
                return true;
            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_delete_all:
			
			AlertDialog.Builder deleteAll = new AlertDialog.Builder(MainActivity.this);
			deleteAll.setMessage(getString(R.string.edit_delete_all_movie));
			deleteAll.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					cursor = db.getAllMovies();
					deleteAllList(cursor);
					Intent intent = new Intent(MainActivity.this, MainActivity.class);
					
					try {
						File imgFolder = new File(Environment.getExternalStorageDirectory() + "/myMoviesImages");
						if(imgFolder.exists()) {
							File[] files = imgFolder.listFiles();
							if(files == null) {
								imgFolder.delete();
							} else {
								for (int i = 0; i < files.length; i++) {
									files[i].delete();
								}
								imgFolder.delete();
							}
						}
					} catch (Exception e) {
						e.getMessage();
						e.printStackTrace();
					}
					
					
					
					startActivity(intent);
				}
			});
			
			deleteAll.setNegativeButton("No", null);
			deleteAll.show();
			
			break;

		case R.id.action_exit:
			
			Toast.makeText(this, "Have a great day :)", Toast.LENGTH_SHORT).show();
			Intent exit = new Intent(Intent.ACTION_MAIN);
			exit.addCategory(Intent.CATEGORY_HOME);
			exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(exit);
			
			break;
			
		case R.id.settings_help:
			
			AlertDialog.Builder helps = new AlertDialog.Builder(MainActivity.this);
			helps.setTitle("Main page help");
			helps.setMessage(getString(R.string.actionbar_user_help));
			helps.setPositiveButton("OK", null);
			helps.show();
			break;
			
		//ActionBar
		case R.id.plus:	
			
			AlertDialog.Builder offlineOnline = new AlertDialog.Builder(MainActivity.this);
			offlineOnline.setTitle(getString(R.string.main_add_online_offline));
			offlineOnline.setPositiveButton("Online", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent online = new Intent(MainActivity.this, MovieOnLine.class);
					startActivity(online);
				}
			});
			
			offlineOnline.setNeutralButton("Manual", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent manual = new Intent(MainActivity.this, EditMovie.class);
					manual.putExtra("ManualFlag", 1);
					startActivity(manual);
				}
			});
			
			offlineOnline.show();
			
		
			break;
		
		case R.id.help:
			
			AlertDialog.Builder help = new AlertDialog.Builder(MainActivity.this);
			help.setTitle("Main page help");
			help.setMessage(getString(R.string.actionbar_user_help));
			help.setPositiveButton("OK", null);
			help.show();
			
			break;
			
		default:
			break;
		}
		
	
		
		return super.onOptionsItemSelected(item);
	}

}
