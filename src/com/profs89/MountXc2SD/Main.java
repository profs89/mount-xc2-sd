package com.profs89.MountXc2SD;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Activity
{

	// /dev/fuse /storage/sdcard0 fuse rw,nosuid,nodev,noexec,relatime,user_id=1023,group_id=1023,default_permissions,allow_other 0 0

	private String APP_TAG = Main.class.getName();
	private static int sBuffLen = 1000;
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
				runCommand();
			}
		});

		mButtonTest.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(testMountPoint()){
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

	private void runCommand(){
		try{
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(process.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /\n");
			out.writeBytes("busybox mount -t vfat -o umask=0000 /dev/block/vold/179:97 /mnt/sdcard\n");
			out.writeBytes("exit\n");
			out.flush();
			process.waitFor();
			mTextView.setText(R.string.text_mounts_y);
			mTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
			mButtonMount.setEnabled(false);
		}
		catch(IOException ex){
			Log.v(APP_TAG, ex.getMessage());
		}
		catch(InterruptedException ex){
			Log.v(APP_TAG, ex.getMessage());
		}

	}


	private boolean testMountPoint(){
		double size = 0;
		try{
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream outStream = new DataOutputStream(process.getOutputStream());

			outStream.writeBytes("df /storage/sdcard0\n");
			InputStream inStream = process.getInputStream();
			byte[] buffer = new byte[sBuffLen];
			int read;
			String result = new String();
			String[] parts;
			while(true){
				read = inStream.read(buffer);
				result += new String(buffer, 0, read);
				if(read<sBuffLen){
					Toast.makeText(this, result, Toast.LENGTH_LONG).show();
					parts = result.split("\\s+");
					Log.v(APP_TAG, result);

					break;
				}
			}

			size = Double.parseDouble(parts[6].replaceAll("[^\\d.]", ""));
			Log.v(APP_TAG, Double.toString(size));

		}
		catch(IOException ex){
			Log.v(APP_TAG, ex.getMessage());
		}

		if (size > 1.5)
		{
			return true;
		}
		return false;

	}

}
