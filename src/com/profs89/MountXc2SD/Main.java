package com.profs89.MountXc2SD;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity
{

	// /dev/fuse /storage/sdcard0 fuse rw,nosuid,nodev,noexec,relatime,user_id=1023,group_id=1023,default_permissions,allow_other 0 0

	private String APP_TAG = Main.class.getName();
	private Button mButtonMount;
	private Button mButtonTest;
	private TextView mTextView;

	/**
	 * Called when the activity is first created.
	 */
	//df /storage/sdcard0
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initControls();
	}


	private void initControls(){
		mButtonMount = (Button) findViewById(R.id.button_mount);
		mButtonTest = (Button) findViewById(R.id.button_test);
		mTextView = (TextView) findViewById(R.id.text_message);
		mButtonMount.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MountService.swapMemory(getBaseContext());
				if (MountService.testMountPoint(getBaseContext()))
				{
					mTextView.setText(R.string.text_mounts_y);
					mTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
					mButtonMount.setEnabled(false);
				}
			}
		});

		mButtonTest.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (MountService.testMountPoint(getBaseContext()))
				{
					mTextView.setText(R.string.text_mounts_y);
					mTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
					mButtonMount.setEnabled(false);
				}
				else{
					mTextView.setText(R.string.text_mounts_n);
					mTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					mButtonMount.setEnabled(true);
				}
			}
		});
	}

}
