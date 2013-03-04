package org.app.musicplayer;

import android.os.Bundle;

public class AboutActivity extends SettingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTopTitle(getResources().getString(R.string.about_title));
	}

}
