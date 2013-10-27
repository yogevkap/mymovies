package my.movies;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class MovieOnLine extends ListActivity {
	
	//Attributes
	private ListView listView;
	private ProgressBar progressBar;
	private ArrayList<Movie> movieList;
	private TestAdapter testAdapter;
	private String encodeQ;
	
	//Rotten Tomatoes API key
	private static final String API_KEY = "axvpf5ukpngzfc4yj2sheu7e";
	
	//Numbers of movies each page get from Rotten Tomatoes
	private static final int MOVIE_PAGE_LIMIT = 10;
	
	//cancel button
	public void cancelBtn(){
		
		Button cancel = (Button)findViewById(R.id.online_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				finish();
			}
		});
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.movie_online);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		cancelBtn();
		handleIntent(getIntent());
		isOnline();
		
		movieList = new ArrayList<Movie>();
		listView = getListView();
				
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(MovieOnLine.this, EditMovie.class);
				intent.putExtra("OnlineFlag", 1);
				intent.putExtra("Title", movieList.get(position).getTitle());
				intent.putExtra("Synopsis", movieList.get(position).getBody());
				intent.putExtra("Url", movieList.get(position).getUrl());
				intent.putExtra("Picture", movieList.get(position).getPicture());
				intent.putExtra("Date", movieList.get(position).getReleaseDate());
				intent.putExtra("RunTime", movieList.get(position).getRunTime());
				intent.putExtra("AudienceScore", movieList.get(position).getRating());
				startActivity(intent);
				
			}
		});
	}
		
	//Belong to ActionBar
	@Override
	protected void onNewIntent(Intent intent) {
	    handleIntent(intent);
	    super.onNewIntent(intent);
	}
	//Belong to ActionBar
	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	MovieOnLine.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      try {
				encodeQ = URLEncoder.encode(query, "utf-8");  
		} catch (Exception e) {
			e.getMessage();
			e.getStackTrace();
		}
	      
	      new RequestTask().execute("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=" 
					+ API_KEY + "&q=" + encodeQ + "&page_limit=" + MOVIE_PAGE_LIMIT);
	    }	
	}
	
	private void refreshMovieList(ArrayList<Movie> movieList){		
		testAdapter = new TestAdapter(this, android.R.layout.simple_list_item_1, movieList);
		listView.setAdapter(testAdapter);				
		testAdapter.notifyDataSetChanged();
	}
	
	public class RequestTask extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(movieList.isEmpty() == false){
				movieList.clear();
			}
			
			progressBar = (ProgressBar)findViewById(R.id.online_progressBar);
			progressBar.setVisibility(View.FOCUS_UP);
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			HttpURLConnection httpcon = null;
			InputStream input_stream = null;
			InputStreamReader input_stream_reader = null;
			BufferedReader buffer = null;
			
			StringBuilder response = new StringBuilder();
			
			try {
				//make HTTP request
				URL url = new URL(params[0]);
				httpcon = (HttpURLConnection)url.openConnection();
				if(httpcon.getResponseCode() != HttpURLConnection.HTTP_OK){
					return null;
				}
				
				//Request successful 
				input_stream = httpcon.getInputStream();
				input_stream_reader = new InputStreamReader(input_stream);
				buffer = new BufferedReader(input_stream_reader);
				
				String line;
				while((line = buffer.readLine()) != null){
					response.append(line);
				}
					
			} catch (Exception e) {
				e.getMessage();
				e.printStackTrace();
				//Close connection
			}finally{
				if(buffer != null){
					try {
						input_stream_reader.close();
						input_stream.close();
						buffer.close();
					} catch (IOException e) {
						e.getMessage();
						e.printStackTrace();
					}finally{
						if(httpcon != null){
							httpcon.disconnect();
						}
					}
					
				}
			}
			return response.toString();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			progressBar = (ProgressBar)findViewById(R.id.online_progressBar);
			progressBar.setVisibility(View.GONE);
			
			if(result != null){
				try {
					
					 // convert the String response to a JSON object
					JSONObject json = new JSONObject(result);
					 
					// fetch the array of movies in the response
					JSONArray movies = json.getJSONArray("movies");
					
					for(int i = 0; i<movies.length();i++){
						
						//Movie id from rotten tomatoes
						JSONObject movie = movies.getJSONObject(i);
								
						//Create object with movie information from rotten tomatoes (any information i want).
						Movie mov = new Movie(movie.getString("title"), movie.getString("synopsis"), movie.getJSONObject("links").getString("alternate"),
							movie.getJSONObject("posters").getString("detailed"),movie.getJSONObject("ratings").getInt("audience_score"),
							movie.getJSONObject("release_dates").optString("theater"),movie.getString("runtime"),null,i);
						
						movieList.add(mov);	
			
					}
					refreshMovieList(movieList);
					
					
				} catch (JSONException e) {
					e.getMessage();
					e.getStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.movie_search_online, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem menuItem = menu.findItem(R.id.online_actionbar_search);
		SearchView searchView = (SearchView) menuItem.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.requestFocus();
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case R.id.help:
			
			AlertDialog.Builder help = new AlertDialog.Builder(MovieOnLine.this);
			help.setTitle("Movie Search Online page help");
			help.setMessage(getString(R.string.onlinesearch_actionbar_user_help));
			help.setPositiveButton("OK", null);
			help.show();
			
			break;
			
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "press", Toast.LENGTH_SHORT).show();
		
		try {
			File imgFolder = new File(Environment.getExternalStorageDirectory() + "/mySearchImages");
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
		
		super.onBackPressed();
	}
	
	//Check if there is Internet connection or not
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
	    return false;
	}

}