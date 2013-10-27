package my.movies;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

//Adapter for main menu list
public class MovieAdapter extends ArrayAdapter<Movie> {

	private Context mContext;
	private String onlinePicture;
	
	public MovieAdapter(Context context, int textViewResourceId,List<Movie> objects) {
		super(context, textViewResourceId, objects);
		
		this.mContext = context;
	}

	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
//		SearchMovieRow row = null;
//		View rowView = convertView;
		
		ImageViewHolder viewHolder;
		Movie movie = getItem(position);
		
		if(view == null){
			
			LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.main_adapter_row, null);
			
			viewHolder = new ImageViewHolder();
			
			viewHolder.title = (TextView)view.findViewById(R.id.movie_adapter_row_tvx_moviename);
			viewHolder.releasedate = (TextView)view.findViewById(R.id.movie_adapter_row_tvx_releasedate);
			viewHolder.runtime = (TextView)view.findViewById(R.id.movie_adapter_row_tvx_runtime);
			viewHolder.rating = (TextView)view.findViewById(R.id.movie_adapter_row_tvx__rating);
			viewHolder.imageOnline = (ImageView)view.findViewById(R.id.movie_adapter_imageview);
			viewHolder.checkbox = (CheckBox)view.findViewById(R.id.checkBox1);
			viewHolder.checkbox.setClickable(false);
			
			view.setTag(viewHolder);	
			
		}else{
			
			viewHolder = (ImageViewHolder) view.getTag();
		}
			
		viewHolder.title.setText(movie.getTitle());
		viewHolder.releasedate.setText("Release Date: " + movie.getReleaseDate());
		viewHolder.runtime.setText("Run Time: "  + movie.getRunTime() + "min");
		
		String number = Integer.toString(movie.getRating());
		viewHolder.rating.setText("Audience Score: " + number);	
		
		if(movie.getRating() == -1){
			viewHolder.rating.setVisibility(View.GONE);
		}
		
		//rating colors
		int aScore = movie.getRating();
		
		if(aScore < 25){
			viewHolder.rating.setBackgroundColor(mContext.getResources().getColor(R.color.Red));
		}
		if(aScore > 25 && aScore < 50){
			viewHolder.rating.setBackgroundColor(mContext.getResources().getColor(R.color.Orange));
		}
		if(aScore > 50 && aScore < 75){
			viewHolder.rating.setBackgroundColor(mContext.getResources().getColor(R.color.Yellow));
		}
		if(aScore > 75){
			viewHolder.rating.setBackgroundColor(mContext.getResources().getColor(R.color.Green));
		}
		
		
		//check if the user check the checkbox in the editmovie or not
		if(movie.getMainCheckBox() == 1){
			viewHolder.checkbox.setChecked(true);
		}else{
			viewHolder.checkbox.setChecked(false);
		}
		
		//bring picture from SDcard
		this.onlinePicture = movie.getPicture();
		
		String path = Environment.getExternalStorageDirectory().toString() + "/myMoviesImages/" + this.onlinePicture;
		File image = new File(path);
		if(image.exists()){
			Bitmap bitImage = BitmapFactory.decodeFile(image.getAbsolutePath());
			viewHolder.imageOnline.setImageBitmap(bitImage);
		}
		
		
		
		return view;
	}
	
	static class ImageViewHolder {
		ImageView imageOnline;
		TextView title, releasedate, runtime, rating;
		CheckBox checkbox;
	}
	

	
}
