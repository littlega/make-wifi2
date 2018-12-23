package com.felhr.serialportexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.felhr.serialportexample.utils.WifiApManager;

public class SplashScreen extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
		}
		return true;
	}
    int timeRun;
	WifiApManager wifiApManager;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	     this.setContentView(R.layout.splash_screen);

		//wifiApManager = new WifiApManager(this);
		// force to show the settings page for demonstration purpose of this method
		//wifiApManager.showWritePermissionSettings(true);
		//wifiApManager.setWifiApEnabled(null, true);


		Handler myHandler =new Handler();
		myHandler.postDelayed(new Runnable(){

			@Override
			public  void run() {
				// TODO Auto-generated method stub
				callStartService();
					Intent goSecond1 = new Intent(getApplicationContext(), MainActivity.class);
				   //  goSecond1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(goSecond1, 0);

			}
		},5000);

	}

	public void callStartService(){
		startService(new Intent(this, UsbService.class));
	}
	@Override
	protected void onResume() {
		super.onResume();

		//wifiApManager.showWritePermissionSettings(false);
	}


}
