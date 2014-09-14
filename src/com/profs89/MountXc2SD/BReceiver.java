package com.profs89.MountXc2SD;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by profs89 on 9/14/14.
 */
public class BReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		context.startService(new Intent(context, MountService.class));
	}
}
