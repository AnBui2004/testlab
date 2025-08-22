package com.anbui.widget.battery;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Build;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.app.PendingIntent;
import android.util.Log;
import android.appwidget.AppWidgetManager;

public class WidgetJob4 extends JobService {
    
    private Context context;
    private boolean isJobCancelled = false;
    private Intent intent;
    
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            if (!CheckBattery.isJobIdRunning(this, 789)) {
                Intent intent = new Intent(this, BatteryWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, CheckBattery.appWidgetId);
                sendBroadcast(intent);
                Log.i("WidgetJob4", "Start WidgetJob completed.");
            } else {
                Log.i("WidgetJob4", "WidgetJob is running.");
            }
        } catch (Exception _e) {
            Log.e("WidgetJob4", "Failed!");
            Log.e("WidgetJob4", _e.toString());
        }
        return true;
    }
    
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
