package com.profs89.MountXc2SD;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by profs89 on 9/14/14.
 */
public class MountService extends Service
{
	private static int sBuffLen = 1000;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		showToastMessage(getBaseContext(), getBaseContext().getString(R.string.app_name) + ": Service is started");
		if (!testMountPoint(this))
		{
			swapMemory(this);
		}
		else
		{
			this.stopSelf();
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		showToastMessage(getBaseContext(), getBaseContext().getString(R.string.app_name) + ": Service is stopped");
	}


	public static boolean testMountPoint(Context context)
	{
		double size = 0;
		String result = "";
		try
		{
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream outStream = new DataOutputStream(process.getOutputStream());

			outStream.writeBytes("df /storage/sdcard0\n");
			InputStream inStream = process.getInputStream();
			byte[] buffer = new byte[sBuffLen];
			int read;
			String[] parts;
			while (true)
			{
				read = inStream.read(buffer);
				if (read != -1)
				{
					result += new String(buffer, 0, read);
					if (read < sBuffLen)
					{
						parts = result.split("\\s+");
						Log.v(MountService.class.getName(), result);
						size = Double.parseDouble(parts[6].replaceAll("[^\\d.]", ""));
						Log.v(MountService.class.getName(), Double.toString(size));
						break;
					}
				}
				else
				{
					showToastMessage(context, context.getString(R.string.app_name) + ": You don't have superuser rights");
					showNotification(context, context.getString(R.string.app_name), "You don't have superuser rights");
					break;
				}
			}

		}
		catch (IOException ex)
		{
			Log.v(MountService.class.getName(), ex.getMessage());
		}

		if (size > 1.5)
		{
			showToastMessage(context, context.getString(R.string.app_name) + ": Memory is swapped");
			if (!result.equals(""))
			{
				showToastMessage(context, context.getString(R.string.app_name) + ": " + result);
			}
			context.stopService(new Intent(context, MountService.class));
			return true;
		}
		return false;

	}


	public static void swapMemory(Context context)
	{
		try
		{
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(process.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /\n");
			out.writeBytes("busybox mkdir -p /data/internal_sd\n");
			out.writeBytes("busybox mount -o bind /mnt/sdcard /data/internal_sd\n");
			out.writeBytes("busybox mount -o bind /data/internal_sd /mnt/extSdCard\n");
			out.writeBytes("busybox mount -t vfat -o umask=0000 /dev/block/vold/179:97 /mnt/sdcard\n");
			out.writeBytes("exit\n");
			out.flush();
			process.waitFor();
			if (testMountPoint(context))
			{
				showNotification(context, context.getString(R.string.app_name), "External / internal memories are now swapped");
			}
		}
		catch (IOException ex)
		{
			Log.v(MountService.class.getName(), ex.getMessage());
		}
		catch (InterruptedException ex)
		{
			Log.v(MountService.class.getName(), ex.getMessage());
		}

	}


	public static void showNotification(Context context, String title, String message)
	{
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification.Builder(context)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(R.drawable.hdpi_icon)
				.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
		notificationManager.notify(10, notification);
	}


	public static void showToastMessage(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
