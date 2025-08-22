package com.anbui.widget.battery;

import android.os.BatteryManager;
import android.content.Context;
import java.util.Timer;
import java.util.TimerTask;
import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import android.util.Log;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.PowerManager;
import android.content.IntentFilter;
import android.content.Intent;
import android.app.job.JobScheduler;
import android.app.job.JobInfo;
import android.content.ComponentName;

public class CheckBattery {
    
    public static int BatteryPercent = 0;
    public static Timer _timer = new Timer();
    public static TimerTask dem;
    public static GradientDrawable gdd;
    public static int CancelUpdate = 0;
    public static PowerManager pm;
    public static int appWidgetId = 0;
    
    public static String CheckNow(Context _context) {
        BatteryManager bm=(BatteryManager) _context.getSystemService(_context.BATTERY_SERVICE);
        BatteryPercent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return String.valueOf(BatteryPercent).concat("%");
    }
    
    public static void AlwayUpdate(final Context _context, final int _appWidgetId) {
        appWidgetId = _appWidgetId;
        dem = new TimerTask() {
            @Override
            public void run() {
                pm = (PowerManager) _context.getSystemService(Context.POWER_SERVICE);
                if (!pm.isInteractive()) {
                    Log.w("CheckBattery", "No update because the device is not in an interactive state.");
                    return;
                }
                if (_context == null) {
                    CancelUpdate = 0;
                    dem.cancel();
                    Log.w("CheckBattery", "Not updated because context is null.");
                    return;
                }
                if (CancelUpdate > 28) {
                    CancelUpdate = 0;
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(_context);
                    RemoteViews remoteViews = new RemoteViews(_context.getPackageName(), R.layout.main);
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.refresh);
                    remoteViews.setTextViewText(R.id.textview1, "--%");
                    dem.cancel();
                    Log.w("CheckBattery", "Not updated because limit.");
                    return;
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(_context);
                RemoteViews remoteViews = new RemoteViews(_context.getPackageName(), R.layout.main);
                remoteViews.setTextViewText(R.id.textview1, CheckNow(_context));
                if (isCharging(_context)) {
                remoteViews.setInt(R.id.imageview1, "setColorFilter", 0xff00c853);
                } else if (CheckBattery.BatteryPercent < 16) {
                //remoteViews.setInt(R.id.linear1, "setBackgroundColor", 
                //0x80301a00);
                remoteViews.setInt(R.id.imageview1, "setColorFilter", 0xfff44336);
                } else if (CheckBattery.BatteryPercent < 21) {
                //remoteViews.setInt(R.id.linear1, "setBackgroundColor", 
                //0x8033080b);
                remoteViews.setInt(R.id.imageview1, "setColorFilter", 0xffffc107);
                } else {
                //remoteViews.setInt(R.id.linear1, "setBackgroundColor", 
                //0x80000000);
                remoteViews.setInt(R.id.imageview1, "setColorFilter", 0xffffffff);
                }
                if (CheckBattery.BatteryPercent < 10) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_0_bar);
                } else if (CheckBattery.BatteryPercent < 20) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_1_bar);
                } else if (CheckBattery.BatteryPercent < 30) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_2_bar);
                } else if (CheckBattery.BatteryPercent < 50) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_3_bar);
                } else if (CheckBattery.BatteryPercent < 70) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_4_bar);
                } else if (CheckBattery.BatteryPercent < 90) {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_5_bar);
                } else {
                    remoteViews.setImageViewResource(R.id.imageview1, R.drawable.battery_full);
                }
                appWidgetManager.partiallyUpdateAppWidget(_appWidgetId, remoteViews);
                CancelUpdate++;
                Log.i("CheckBattery", "Updated!");
            }
        };
        _timer.scheduleAtFixedRate(dem, (int)(60000), (int)(60000));
    }
    
    public static boolean isCharging(Context _context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = _context.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        Intent intent = new Intent();
        int statusb=intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
        if(status==BatteryManager.BATTERY_STATUS_CHARGING) {
            return true;
        }
        return false;
    }
    
    public static boolean isJobIdRunning(Context context, int JobId) {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE );
        for ( JobInfo jobInfo : jobScheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == JobId ) {
                return true;
            }
        }
        return false;
    }
    
    public static void startjobscheduler(Context context) {
        if (isJobIdRunning(context, 123)) {
            ComponentName componentName = new ComponentName(context, WidgetJob.class);
            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(123, componentName);
            jobInfoBuilder.setPeriodic(1800000);
            //jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobInfoBuilder.setPersisted(true);
            JobInfo jobInfo = jobInfoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            int resultsCode = jobScheduler.schedule(jobInfo);
            if (resultsCode == JobScheduler.RESULT_SUCCESS) {
                Log.i("IOApplication", "Start WidgetJob completed.");
            } else {
                Log.w("IOApplication", "Start WidgetJob failed!");
            }
        } else {
            Log.i("IOApplication", "WidgetJob is running.");
        }
    }
    
    public static void startjobscheduler2(Context context) {
        if (isJobIdRunning(context, 456)) {
            ComponentName componentName = new ComponentName(context, WidgetJob2.class);
            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(456, componentName);
            jobInfoBuilder.setPeriodic(3600000);
            //jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobInfoBuilder.setPersisted(true);
            JobInfo jobInfo = jobInfoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            int resultsCode = jobScheduler.schedule(jobInfo);
            if (resultsCode == JobScheduler.RESULT_SUCCESS) {
                Log.i("IOApplication", "Start WidgetJob2 completed.");
            } else {
                Log.w("IOApplication", "Start WidgetJob2 failed!");
            }
        } else {
            Log.i("IOApplication", "WidgetJob2 is running.");
        }
    }
    
    public static void startjobscheduler3(Context context) {
        if (isJobIdRunning(context, 789)) {
            ComponentName componentName = new ComponentName(context, WidgetJob3.class);
            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(789, componentName);
            jobInfoBuilder.setPeriodic(21600000);
            //jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobInfoBuilder.setPersisted(true);
            JobInfo jobInfo = jobInfoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            int resultsCode = jobScheduler.schedule(jobInfo);
            if (resultsCode == JobScheduler.RESULT_SUCCESS) {
                Log.i("IOApplication", "Start WidgetJob3 completed.");
            } else {
                Log.w("IOApplication", "Start WidgetJob3 failed!");
            }
        } else {
            Log.i("IOApplication", "WidgetJob3 is running.");
        }
    }
    
    public static void startjobscheduler4(Context context) {
        if (isJobIdRunning(context, 101112)) {
            ComponentName componentName = new ComponentName(context, WidgetJob4.class);
            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(101112, componentName);
            jobInfoBuilder.setPeriodic(43200000);
            //jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobInfoBuilder.setPersisted(true);
            JobInfo jobInfo = jobInfoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            int resultsCode = jobScheduler.schedule(jobInfo);
            if (resultsCode == JobScheduler.RESULT_SUCCESS) {
                Log.i("IOApplication", "Start WidgetJob4 completed.");
            } else {
                Log.w("IOApplication", "Start WidgetJob4 failed!");
            }
        } else {
            Log.i("IOApplication", "WidgetJob4 is running.");
        }
    }
}
