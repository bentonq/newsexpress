package com.bentonq.newsexpress;

public class News {

	public final String title;
	public final String link;
	public final String summary;

	public News(String title, String summary, String link) {
		this.title = title;
		this.link = link;
		this.summary = summary;
	}
}