package my.movies;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

public class Movie {

	//Attributes
	private int id;
	private int score;
	private String title;
	private String body;
	private String url;
	private String onlinepicture;
	private String releasedate;
	private String runtime;
	private Bitmap bitmap;
	private int rating;
	private int checkbox;
	private int maincheckbox;
	private int position;
	
	//CTOR
	public Movie(int id, int score, String title, String body, String rdate, String runtime, int audience_score, String url, String onlinepicture, int checkbox, int maincheckbox){
		setId(id);
		setScore(score);
		setTitle(title);
		setBody(body);
		setReleaseDate(rdate);
		setRunTime(runtime);
		setRating(audience_score);
		setUrl(url);
		setPicture(onlinepicture);
		setCheckBox(checkbox);
		setMainCheckBox(maincheckbox);
	}
	
	public Movie(int score, String title, String body, String url, String onlinepicture, int checkbox, int maincheckbox){
		setScore(score);
		setTitle(title);
		setBody(body);
		setUrl(url);
		setPicture(onlinepicture);
		setCheckBox(checkbox);
		setMainCheckBox(maincheckbox);
	}
	
	public Movie(String title, String body,String url, String onlinepicture ,int rating, String releasedate, String run, Bitmap bitmap, int position){
		setTitle(title);
		setBody(body);
		setUrl(url);
		setPicture(onlinepicture);
		setReleaseDate(releasedate);
		setRating(rating);
		setRunTime(run);
		setBitmap(bitmap);
		setPosition(position);
	}	
		

	//Getters and Setters
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getScore(){
		return this.score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getBody(){
		return this.body;
	}
	
	public void setBody(String body){
		this.body = body;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getPicture(){
		return this.onlinepicture;
	}
	
	public void setPicture(String picture){
		this.onlinepicture = picture;
	}
		
	public String getReleaseDate(){
		return this.releasedate;
	}
	
	public void setReleaseDate(String release){
		this.releasedate = release;
	}
	
	public String getRunTime(){
		return this.runtime;
	}
	
	public void setRunTime(String run){
		this.runtime = run;
	}
	
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	public void setBitmap(Bitmap bit){
		this.bitmap = bit;
	}
	
	public int getRating(){
		return this.rating;
	}
	
	public void setRating(int rate){
		this.rating = rate;
	}
	
	
	public int getCheckBox(){
		return this.checkbox;
	}
	
	public void setCheckBox(int checkbox){
		this.checkbox = checkbox;
	}
	
	public int getMainCheckBox(){
		return this.maincheckbox; 
	}
	
	public void setMainCheckBox(int maincheckbox){
		this.maincheckbox = maincheckbox;
	}
	
	public String toString(){
		return this.title;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
}
