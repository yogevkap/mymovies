package my.movies;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

//Download image from rotten tomatoes 

public class DonwloadImageTask extends AsyncTask<String, Integer,Bitmap> {

	private Activity mActivity;
	private ProgressDialog mDialog;
	
	public DonwloadImageTask(Activity activity) {
	
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
