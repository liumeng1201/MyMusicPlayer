package com.lm.musicplayerdemo.test;

import android.app.Application;

public class MyApp extends Application {
	public boolean firstPlay = true;
	public String lastMP3 = null;

	public void setFirstPlay(boolean b) {
		this.firstPlay = b;
	}

	public void setLastMP3(String str) {
		this.lastMP3 = str;
	}
}
