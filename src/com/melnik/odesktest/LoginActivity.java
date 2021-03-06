package com.melnik.odesktest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.melnik.odesktest.menu.MySlidingMenu;

public class LoginActivity extends SherlockFragmentActivity {

	private Button backButton;
	private SlidingMenu menu;
	
	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		
		setContentView(R.layout.activity_login);
		
		backButton = (Button) findViewById(R.id.login_header);
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		menu = MySlidingMenu.getMenu(this, getString(R.string.menu_login));
		
		showActionBar();
	}
	
	private void showActionBar() {
		//getSupportActionBar().setCustomView(R.layout.profile_action_bar);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflator.inflate(R.layout.profile_action_bar, null);
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.setDisplayShowHomeEnabled (false);
	    actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setCustomView(v);
	    
	    ImageButton menuButton = (ImageButton) v.findViewById(R.id.menuButton);
	    menuButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				menu.toggle();
			}
		});
	}
}
