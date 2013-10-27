package my.movies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TestAdapter extends ArrayAdapter<Movie> {

	private Activity mActivity;
	private DownloadImageTaskOnline downloadImageTaskOnline;
	private String movieNames;
	private ArrayList<Movie> movieList;
	
	public TestAdapter(MovieOnLine movieOnLine, int simpleListItem1, ArrayList<Movie> movieList) {
		super(movieOnLine, simpleListItem1, movieList);
		
		this.mActivity = movieOnLine;
		this.movieList = movieList;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
				
		View row = convertView;
		ImageViewHolder viewHolder = null;
		Movie movie = movieList.get(position);

		if (row == null) {
			LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = layout.inflate(R.layout.search_movie_row, parent, false);	
						
			viewHolder = new ImageViewHolder();
			viewHolder.title = (TextView)row.findViewById(R.id.search_movieonline_tvx_title);
			viewHolder.releasedate = (TextView)row.findViewById(R.id.search_movieonline_tvx_releasedate);
			viewHolder.runtime = (TextView)row.findViewById(R.id.search_movieonline_tvx_runtime);
			viewHolder.rating = (TextView)row.findViewById(R.id.search_movieonline_tvx_rating);
			viewHolder.imageOnline = (ImageView)row.findViewById(R.id.search_movieonline_imageview);
	
			row.setTag(viewHolder);		
		
		}else{
			viewHolder = (ImageViewHolder) row.getTag();
		}
		
		
		
		viewHolder.title.setText(movie.getTitle());
		this.movieNames = movie.getTitle();
		
		viewHolder.releasedate.setText("Release Date: " + movie.getReleaseDate());
		viewHolder.runtime.setText("Run Time: "  + movie.getRunTime() + "min");
		
		String number = Integer.toString(movie.getRating());
		viewHolder.rating.setText("Audience Score: " + number);
		
		int aScore = movie.getRating();
		
		if(aScore < 25){
			viewHolder.rating.setBackgroundColor(mActivity.getResources().getColor(R.color.Red));
		}
		if(aScore > 25 && aScore < 50){
			viewHolder.rating.setBackgroundColor(mActivity.getResources().getColor(R.color.Orange));
		}
		if(aScore > 50 && aScore < 75){
			viewHolder.rating.setBackgroundColor(mActivity.getResources().getColor(R.color.Yellow));
		}
		if(aScore > 75){
			viewHolder.rating.setBackgroundColor(mActivity.getResources().getColor(R.color.Green));
		}
				
		
		
			//check if the image is exists, if not download it.
//			viewHolder.imageOnline.setVisibility(View.GONE);
			viewHolder.imageOnline.setImageResource(R.drawable.movie);
			String path = Environment.getExternalStorageDirectory().toString() + "/mySearchImages/" + movieNames + ".jpg";
			File oimage = new File(path);
			if(oimage.exists()){
				Bitmap bitImage = BitmapFactory.decodeFile(oimage.getAbsolutePath());
				if(viewHolder.imageOnline != null){
//					viewHolder.imageOnline.setVisibility(View.VISIBLE);
					viewHolder.imageOnline.setImageBitmap(bitImage);
				}else{
					viewHolder.imageOnline.setImageResource(R.drawable.movie);
				}
				
			}else{
				downloadImageTaskOnline = new DownloadImageTaskOnline(this.movieNames);
				String[] imageUrl = {movie.getPicture()}; 
				downloadImageTaskOnline.execute(imageUrl);
			}
		return row;
	
}
	
	static class ImageViewHolder {
		ImageView imageOnline = null;
		String onlinePicture;
		TextView title, releasedate, runtime, rating;
		Movie movie;
		int position;
	}
		
	class DownloadImageTaskOnline extends AsyncTask<String, Void, Bitmap> {

		private String movieNames;
		
		public DownloadImageTaskOnline(String movieNames) {
			this.movieNames = movieNames;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {

			Bitmap image = downloadImage(urls[0]);
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			if (isCancelled()) {
				result = null;
			}

			if(result != null){
				saveImage(result, movieNames);	
				}	
			}

	private Bitmap downloadImage(String urlString) {

		HttpURLConnection httpcon = null;
		InputStream is = null;
		ByteArrayOutputStream buffer = null;
		Bitmap bitmap = null;

		URL url;
		try {
			url = new URL(urlString);
			httpcon = (HttpURLConnection) url.openConnection();
			is = httpcon.getInputStream();
			buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[2048];

			// Read the image bytes in chunks of 2048 bytes
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] image = buffer.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, buffer);

		} catch (Exception e) {
			e.getMessage();
			e.getStackTrace();
		}

		return bitmap;

		}
	}
	
	public void saveImage(Bitmap result, String movieNames){
		try {
					
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			result.compress(Bitmap.CompressFormat.JPEG, 100, bao);
			byte[] ba = bao.toByteArray();
			File imagesFolder = new File(Environment.getExternalStorageDirectory() + "/mySearchImages");

			imagesFolder.mkdirs();
			movieNames = movieNames + ".jpg";
			
			File f = new File(imagesFolder, movieNames);
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(ba);
			fo.flush();
			fo.close();
		} catch (Exception e) {
			e.getCause();
		}
		notifyDataSetChanged();
	}
}




	
