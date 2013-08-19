package com.bentonq.newsexpress;

import android.graphics.Bitmap;

public class News {

	public final String title;
	public final String link;
	public final String summary;
	public final Bitmap image;

	public News(String title, String summary, Bitmap image, String link) {
		this.title = title;
		this.link = link;
		this.summary = summary;
		this.image = image;
	}
}