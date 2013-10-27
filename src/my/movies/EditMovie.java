package my.movies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class EditMovie extends Activity {

	//Attributes
	private MovieHandler db;
	private TextView tvtitle,tvbody, tvdate, tvruntime, tvaudience_score;
	private EditText title,body, edate, edruntime;
	private CheckBox checkbox;
	private RatingBar ratingbar;
	private ImageView onlineImage;
	
	//Download Image Attributes
	private DonwloadImageTaskS downloadTask;
	
	//class attributes
	private int check;
	private int maincheck;
	private int score;
	private int id;
	private int manual;
	private int rottenT;
	private int editFlag = 0;
	private String onlinePicture;
	private String movieUrl;
	private boolean imagedownloaded = false;
	
	//onActivityResult variables
	private static int CAMERA_REQUEST = 1;
	private static int GALLERY_REQUEST = 2;
	
	//Attributes for incase application crash
	private String edtitle, edbody, eddate, edtimerun;
	private boolean moviewatch; 
	private int userrating;
		
	//Buttons
	public void deleteBtn(){
		
		Button delete = (Button)findViewById(R.id.edit_btn_delete);
		delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder delete = new AlertDialog.Builder(EditMovie.this);
				delete.setMessage(getString(R.string.edit_delete_movie));
				delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent delete = new Intent(EditMovie.this, MainActivity.class);
						db.deleteMovie(id);
						
						try {
							File tvfile = new File(Environment.getExternalStorageDirectory() + "/myMoviesImages" + "/" + tvtitle.getText().toString() + ".jpg");
							File file = new File(Environment.getExternalStorageDirectory() + "/myMoviesImages" + "/" + title.getText().toString() + ".jpg");
							
							if(tvfile.exists()) {
								tvfile.delete();
							}
							
							if(file.exists()){
								file.delete();
							}
							
						} catch (Exception e) {
							e.getMessage();
							e.printStackTrace();
						}
						
						startActivity(delete);
					}
				});
				
				delete.setNegativeButton("No", null);
				delete.show();
			}
		});
	}
	public void cancelBtn(){
		
		Button cancel = (Button)findViewById(R.id.edit_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
	}
	public void goBtn(String movieUrl){
		
		final String url = movieUrl;
		Button go = (Button)findViewById(R.id.edit_go_url_btn);
		if(url != null){
			go.setVisibility(View.VISIBLE);
		}
		go.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent internt = new Intent(Intent.ACTION_VIEW);
				internt.setData(Uri.parse(url));
				startActivity(internt);
			}
		});
	}
	public void imageBtn(){
		
		onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
		onlineImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder cam = new AlertDialog.Builder(EditMovie.this);
				cam.setMessage(getString(R.string.edit_camera));
				cam.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(camera, CAMERA_REQUEST);
					}
				} );
				
				cam.setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					
						Intent gallery = new Intent();
						gallery.setType("image/*");
						gallery.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(gallery, "Select Picture With?"), GALLERY_REQUEST);
					}
				});
				
				cam.show();
			}
		});
	}
	
	
	//RatingBar
	public void ratingBar(){
		
		ratingbar = (RatingBar)findViewById(R.id.edit_ratingBar);
		ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				score = (int)rating;				
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_movie);
		
		//Make the keyboard hidden
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//Initialize Buttons
		deleteBtn();
		cancelBtn();
		imageBtn();
		
		//Initialize RatingBar Method
		ratingBar();
		
		//Initialize Handler Attribute
		db = new MovieHandler(this);
		
		//Intent attribute for both edit movie and online information
		Intent edit = getIntent();
		
		//Check if the information are from rotten tomatoes
		rottenT = edit.getIntExtra("OnlineFlag", -1);
		if(rottenT != -1){
			
			final Intent onlineMovie = getIntent();
			
			tvtitle = (TextView)findViewById(R.id.edit_tvx_title);
			tvtitle.setText(onlineMovie.getStringExtra("Title"));	
			
			tvbody = (TextView)findViewById(R.id.edit_tvx_body);
			tvbody.setText(onlineMovie.getStringExtra("Synopsis"));
			
			String tvxdate = onlineMovie.getStringExtra("Date");
			tvdate = (TextView)findViewById(R.id.edit_tvx_date);
			tvdate.setText("Release Date: " + tvxdate);
			
			String tvxruntime = onlineMovie.getStringExtra("RunTime");
			tvruntime = (TextView)findViewById(R.id.edit_tvx_runtime);
			tvruntime.setText("Run Time: "  + tvxruntime + "min");
			
			String tvxAS = Integer.toString(onlineMovie.getIntExtra("AudienceScore", 0));
			if(tvxAS != "0"){
			tvaudience_score = (TextView)findViewById(R.id.edit_tvx_scroe);
			tvaudience_score.setText("Audience Score: " + tvxAS);
			
			//rating colors
			int aScore = onlineMovie.getIntExtra("AudienceScore", 0);
				if(aScore < 25){
					tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Red));
				}
				if(aScore > 25 && aScore < 50){
					tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Orange));
				}
				if(aScore > 50 && aScore < 75){
					tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Yellow));
				}
				if(aScore > 75){
					tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Green));
				}
			}
			
			this.movieUrl = onlineMovie.getStringExtra("Url");
			goBtn(this.movieUrl);
						
			//Download the picture from rotten tomatoes and the name in db
			this.onlinePicture = onlineMovie.getStringExtra("Title") + ".jpg";
			
			downloadTask = new DonwloadImageTaskS(EditMovie.this);
			String[] imageUrl = {onlineMovie.getStringExtra("Picture")}; 
			downloadTask.execute(imageUrl);			
			
			this.onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
			
			checkbox = (CheckBox)findViewById(R.id.edit_checkBox);	
			ratingbar = (RatingBar)findViewById(R.id.edit_ratingBar);
			
			Button add = (Button)findViewById(R.id.edit_btn_add_two);
			add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(imagedownloaded != true){
						Toast.makeText(EditMovie.this, "Picture is still downloading", Toast.LENGTH_LONG).show();
					}else{
					//save picture to SDcard
					onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
					saveImage();
						
					Intent intent = new Intent(EditMovie.this, MainActivity.class);
					
					check = 0;
					maincheck = 0;
					if(checkbox.isChecked() == true){
						 check = 1;
						 maincheck = 1;
						 checkbox.setChecked(true);
					}
					
					//delete all the search pictures
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
					
					//save info to data base
						db.createEntry(score, tvtitle.getText().toString(), tvbody.getText().toString(), onlineMovie.getStringExtra("Date"),
								onlineMovie.getStringExtra("RunTime"), onlineMovie.getIntExtra("AudienceScore", 0), movieUrl, onlinePicture, check, maincheck);
						startActivity(intent);				
					}
				}
			});
			
		}
			
		//Check if the user want to add a new movie or to edit an exist movie.
		id = edit.getIntExtra("ID", -1);
		if(id != -1){
			
			tvtitle = (TextView)findViewById(R.id.edit_tvx_title);
			title = (EditText)findViewById(R.id.edit_edx_title);
			tvtitle.setText(db.getMovie(id).getTitle());
			
			tvbody = (TextView)findViewById(R.id.edit_tvx_body);
			body = (EditText)findViewById(R.id.edit_edx_body);
			tvbody.setText(db.getMovie(id).getBody());
			
			tvdate = (TextView)findViewById(R.id.edit_tvx_date);
			edate = (EditText)findViewById(R.id.edit_edx_datea);
			tvdate.setText("Release Date: " + db.getMovie(id).getReleaseDate());
			
			tvruntime = (TextView)findViewById(R.id.edit_tvx_runtime);
			edruntime = (EditText)findViewById(R.id.edit_edx_runtime);
			tvruntime.setText("Run Time: "  + db.getMovie(id).getRunTime() + "min");
			
			this.movieUrl = db.getMovie(id).getUrl();
			goBtn(this.movieUrl);
			
			//check if the checkbox is mark or not
			checkbox = (CheckBox)findViewById(R.id.edit_checkBox);
			if(db.getMovie(id).getCheckBox() == 1){
				checkbox.setChecked(true);
			}
			
			//check if the user touch and add stars to the RatingBar
			ratingbar = (RatingBar)findViewById(R.id.edit_ratingBar);
			score = db.getMovie(id).getScore();
			if(score != 0){
				ratingbar.setProgress(score);	
			}
			
			String tvxAS = Integer.toString(db.getMovie(id).getRating());
			if(tvxAS != "0"){
			tvaudience_score = (TextView)findViewById(R.id.edit_tvx_scroe);
			tvaudience_score.setText("Audience Score: " + tvxAS);
			}
			
			//tvaudience_score colors
			int aScore = db.getMovie(id).getRating();
			
			if(aScore == -1){
				tvaudience_score.setVisibility(View.GONE);
			}
			
			if(aScore < 25){
				tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Red));
			}
			if(aScore > 25 && aScore < 50){
				tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Orange));
			}
			if(aScore > 50 && aScore < 75){
				tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Yellow));
			}
			if(aScore > 75){
				tvaudience_score.setBackgroundColor(getResources().getColor(R.color.Green));
			}
			
			//bring picture from SDcard
			this.onlinePicture = db.getMovie(id).getPicture();
			onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
			
			String path = Environment.getExternalStorageDirectory().toString() + "/myMoviesImages/" + this.onlinePicture;
			File image = new File(path);
			if(image.exists()){
				Bitmap bitImage = BitmapFactory.decodeFile(image.getAbsolutePath());
				onlineImage.setImageBitmap(bitImage);
			}
			
			Button add = (Button)findViewById(R.id.edit_btn_add_two);
			add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					Intent intent = new Intent(EditMovie.this, MainActivity.class);
					
					saveImage();
					
					//check if the checkbox is mark or not
					if(checkbox.isChecked() == true){
						check = 1;
						maincheck = 1;
						checkbox.setChecked(true);
					}else if(checkbox.isChecked() == false){
						check = 0;
						maincheck = 0;
						checkbox.setChecked(false);
					}
					
					if(title.getVisibility() == View.GONE){
						Movie movie = new Movie(id, score, tvtitle.getText().toString(), tvbody.getText().toString(), db.getMovie(id).getReleaseDate(),
								db.getMovie(id).getRunTime(), db.getMovie(id).getRating() ,movieUrl, onlinePicture, check, maincheck);
						
						db.updateEnrty(movie);
						startActivity(intent);
					}
					
					if(tvtitle.getVisibility() == View.GONE){
						if(title.getText().toString().matches("")){
							title.setText(db.getMovie(id).getTitle());
						}
						
						if(body.getText().toString().matches("")){
							body.setText(db.getMovie(id).getBody());
						}
						
						if(edate.getText().toString().matches("")){
							edate.setText(db.getMovie(id).getReleaseDate());
						}
						
						if(edruntime.getText().toString().matches("")){
							edruntime.setText(db.getMovie(id).getRunTime());
						}
						Movie movie = new Movie(id, score, title.getText().toString(), body.getText().toString(),edate.getText().toString(),
								edruntime.getText().toString(), db.getMovie(id).getRating(), movieUrl, onlinePicture, check, maincheck);
												
						db.updateEnrty(movie);
						startActivity(intent);
					}
					
					
				}
			});
		
		}
		
		//Check if the user manually put new movie information
		Intent intent = getIntent();
		manual = intent.getIntExtra("ManualFlag", -1);
		if(manual != -1){
					
		title = (EditText)findViewById(R.id.edit_edx_title);
		title.setVisibility(View.VISIBLE);
		
		body = (EditText)findViewById(R.id.edit_edx_body);
		body.setVisibility(View.VISIBLE);
		
		edate = (EditText)findViewById(R.id.edit_edx_datea);
		edate.setVisibility(View.VISIBLE);
		
		edruntime = (EditText)findViewById(R.id.edit_edx_runtime);
		edruntime.setVisibility(View.VISIBLE);
		
		checkbox = (CheckBox)findViewById(R.id.edit_checkBox);	
		ratingbar = (RatingBar)findViewById(R.id.edit_ratingBar);
		
		onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
		
		Button add = (Button)findViewById(R.id.edit_btn_add_two);
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(title.getText().toString().trim().length() == 0){
					Toast.makeText(EditMovie.this, "Please add a title", Toast.LENGTH_SHORT).show();
				}else{	
								
				Intent intent = new Intent(EditMovie.this, MainActivity.class);
								
				saveImage();
				
				check = 0;
				maincheck = 0;
				if(checkbox.isChecked() == true){
					 check = 1;
					 maincheck = 1;
					 checkbox.setChecked(true);
				}
			
				db.createEntry(score, title.getText().toString(), body.getText().toString(),null,null,-1, null, onlinePicture,check, maincheck);
				startActivity(intent);
				}
			}
		});
	   }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editmovies, menu);
		
		if(manual != -1 || rottenT != -1 ){
			menu.getItem(2).setVisible(false);
		}
		
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_edit_share:
			
			AlertDialog.Builder smsEmail = new AlertDialog.Builder(EditMovie.this);
			smsEmail.setTitle(getString(R.string.edit_sms_email_btn));
			smsEmail.setPositiveButton("Email", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent email = new Intent(Intent.ACTION_SEND);
					
					if(editFlag == 1){
					email.setType("message/rfc822");
					email.putExtra(email.EXTRA_SUBJECT,title.getText().toString());
					email.putExtra(email.EXTRA_TEXT, body.getText().toString());
					editFlag = 0;
					startActivity(Intent.createChooser(email, "Send email with?"));
					}else{
			
					email.setType("message/rfc822");
					email.putExtra(email.EXTRA_SUBJECT, tvtitle.getText().toString());
					email.putExtra(email.EXTRA_TEXT, tvbody.getText().toString());
					startActivity(Intent.createChooser(email, "Send email with?"));
					}
				}
			});
			
			smsEmail.setNeutralButton("SMS", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent sms = new Intent(Intent.ACTION_VIEW);
					
					if(editFlag == 1){
					sms.setType("vnd.android-dir/mms-sms");
					sms.putExtra("sms_body", body.getText().toString());
					editFlag = 0;
					startActivity(sms);
					}else{
					
					sms.setType("vnd.android-dir/mms-sms");
					sms.putExtra("sms_body", tvbody.getText().toString());
					startActivity(sms);
					}
				}
			});
			
			smsEmail.show();
			
			break;
			
			//Call the camera
		case R.id.action_edit_camara:
			
			AlertDialog.Builder cam = new AlertDialog.Builder(EditMovie.this);
			cam.setTitle(getString(R.string.edit_camera));
			cam.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camera, CAMERA_REQUEST);
				}
			} );
			
			cam.setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
					Intent gallery = new Intent();
					gallery.setType("image/*");
					gallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(gallery, "Select Picture With?"), GALLERY_REQUEST);
				}
			});
			
			cam.show();
			break;
		case R.id.action_edit_edit:
					
			tvtitle.setVisibility(View.GONE);
			tvbody.setVisibility(View.GONE);
			tvdate.setVisibility(View.GONE);
			tvruntime.setVisibility(View.GONE);
					
			title.setVisibility(View.VISIBLE);
			body.setVisibility(View.VISIBLE);
			edate.setVisibility(View.VISIBLE);
			edruntime.setVisibility(View.VISIBLE);
			

			break;
			
			case R.id.help:
			
			AlertDialog.Builder help = new AlertDialog.Builder(EditMovie.this);
			help.setTitle("Edit page help");
			help.setMessage(getString(R.string.edit_actionbar_user_help));
			help.setPositiveButton("OK", null);
			help.show();
			
			break;
			
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	//Get information from camera button
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
			onlineImage.setImageBitmap(photo);	
		}
		
		if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
			
			Uri galleryImages = data.getData();
			Bitmap bitmap;
			try {
				
				bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(galleryImages));
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
				onlineImage = (ImageView)findViewById(R.id.edit_online_movie_picture);
				onlineImage.setImageBitmap(bitmap);
				
				
			} catch (Exception e) {
				e.getMessage();
				e.getStackTrace();
			}
			
			
			
		}
	}
	
	//Save picture to SD 
	public void saveImage(){
		try {
			onlineImage.buildDrawingCache();
		    Bitmap bm = onlineImage.getDrawingCache();
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
			byte[] ba = bao.toByteArray();
			File imagesFolder = new File(Environment.getExternalStorageDirectory() + "/myMoviesImages");
			
			imagesFolder.mkdirs();
			if(this.onlinePicture == null){
				this.onlinePicture = title.getText().toString() + ".jpg";
			}else{
				this.onlinePicture = tvtitle.getText().toString()+".jpg";
			}
			File f = new File(imagesFolder, this.onlinePicture);
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(ba);
			fo.flush();
			fo.close();
		} catch (Exception e) {
			e.getCause();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		
		if(rottenT != -1){
			
			edtitle = tvtitle.getText().toString();
			edbody = tvbody.getText().toString();
			eddate = tvdate.getText().toString();
			edtimerun = tvruntime.getText().toString();
			moviewatch = checkbox.isChecked();
			userrating = (int)ratingbar.getRating();
			
			outState.putString("EDXtitle", edtitle);
			outState.putString("EDXbody", edbody);
			outState.putString("EDdate", eddate);
			outState.putString("EDruntime", edtimerun);
			outState.putBoolean("EDcheck", moviewatch);
			outState.putInt("EDrating", userrating);
			
		}else{
			edtitle = title.getText().toString();
			edbody = body.getText().toString();
			eddate = edate.getText().toString();
			edtimerun = edruntime.getText().toString();
			moviewatch = checkbox.isChecked();
			userrating = (int)ratingbar.getRating();
		
			outState.putString("EDXtitle", edtitle);
			outState.putString("EDXbody", edbody);
			outState.putString("EDdate", eddate);
			outState.putString("EDruntime", edtimerun);
			outState.putBoolean("EDcheck", moviewatch);
			outState.putInt("EDrating", userrating);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		edtitle = savedInstanceState.getString("EDXtitle");
		edbody = savedInstanceState.getString("EDXbody");
		eddate = savedInstanceState.getString("EDdate");
		edtimerun = savedInstanceState.getString("EDruntime");
		moviewatch = savedInstanceState.getBoolean("EDcheck");
		userrating = savedInstanceState.getInt("EDrating");
		
		if(rottenT != -1){
			tvtitle.setText(edtitle);
			tvbody.setText(edbody);
			tvdate.setText(eddate);
			tvruntime.setText(edtimerun);
			checkbox.setChecked(moviewatch);
			ratingbar.setRating(userrating);
		}else{	
			title.setText(edtitle);
			body.setText(edbody);
			edate.setText(eddate);
			edruntime.setText(edtimerun);
			checkbox.setChecked(moviewatch);
			ratingbar.setRating(userrating);
		}
	}
	
	 class DonwloadImageTaskS extends AsyncTask<String, Integer,Bitmap> {

		private Activity mActivity;
		private ProgressDialog mDialog;
		
		public DonwloadImageTaskS(Activity activity) {
		
			this.mActivity = activity;
			mDialog = new ProgressDialog(mActivity);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ImageView image = (ImageView) mActivity.findViewById(R.id.edit_online_movie_picture);
			image.setImageBitmap(null);
			
			//Reset the progress bar
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setCancelable(true);
			mDialog.setTitle("Loading...");
			mDialog.setProgress(0);
			mDialog.show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mDialog.show();
			mDialog.setProgress(progress[0]);
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap image = downloadImage(urls[0]);
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if(result != null){
							
				ImageView image = (ImageView)mActivity.findViewById(R.id.edit_online_movie_picture);
				image.setImageBitmap(result);
				imagedownloaded = true;
			}
			mDialog.dismiss();	
		}
		
		private Bitmap downloadImage(String urlString){
			
			URL url;
			try{
				url = new URL(urlString);
				HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
				InputStream is = httpcon.getInputStream();
				int fileLength = httpcon.getContentLength();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead, totalByteRead = 0;
				byte[] data = new byte[2048];
				mDialog.setMax(fileLength);
				
				// Read the image bytes in chunks of 2048 bytes
				while((nRead = is.read(data, 0, data.length)) != -1){
					buffer.write(data, 0, nRead);
					totalByteRead += nRead;
					publishProgress(totalByteRead);
				}
				buffer.flush();
				byte[] image = buffer.toByteArray();
				Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
				
				return bitmap;
				
			}catch(Exception e){
				e.getMessage();
				e.getStackTrace();
			}
			
			return null;
		}
		
	}
	 
}
