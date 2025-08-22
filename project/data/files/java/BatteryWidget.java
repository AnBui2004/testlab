package com.anbui.widget.battery;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.util.Log;
import android.os.Bundle;
import android.content.ComponentName;

public class BatteryWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each widget that belongs to this
        // provider.
        for (int i=0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            CheckBattery.CancelUpdate = 0;
            Log.i("BatteryWidget", "Updated!");
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, BatteryWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            //PendingIntent pendingIntent = PendingIntent.getActivity(
                ///* context = */ context,
                ///* requestCode = */ 0,
                ///* intent = */ intent,
                ///* flags = */ PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            //);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); //You need to specify a proper flag for the intent. Or else the intent will become deleted.

            // Get the layout for the widget and attach an onClick listener to
            // the button.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
            views.setTextViewText(R.id.textview1, CheckBattery.CheckNow(context));
            views.setOnClickPendingIntent(R.id.linear1, pendingIntent);
            
            if (CheckBattery.isCharging(context)) {
                views.setInt(R.id.imageview1, "setColorFilter", 0xff00c853);
            } else if (CheckBattery.BatteryPercent < 16) {
                //views.setInt(R.id.linear1, "setBackgroundColor", 
                //0x80301a00);
                views.setInt(R.id.imageview1, "setColorFilter", 0xfff44336);
            } else if (CheckBattery.BatteryPercent < 21) {
                //remoteViews.setInt(R.id.linear1, "setBackgroundColor", 
                //0x8033080b);
                views.setInt(R.id.imageview1, "setColorFilter", 0xffffc107);
            } else {
                //views.setInt(R.id.linear1, "setBackgroundColor", 
                //0x80000000);
                views.setInt(R.id.imageview1, "setColorFilter", 0xffffffff);
             }
            
            if (CheckBattery.BatteryPercent < 10) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_0_bar);
            } else if (CheckBattery.BatteryPercent < 20) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_1_bar);
            } else if (CheckBattery.BatteryPercent < 30) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_2_bar);
            } else if (CheckBattery.BatteryPercent < 50) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_3_bar);
            } else if (CheckBattery.BatteryPercent < 70) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_4_bar);
            } else if (CheckBattery.BatteryPercent < 90) {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_5_bar);
            } else {
                views.setImageViewResource(R.id.imageview1, R.drawable.battery_full);
            }

            // Tell the AppWidgetManager to perform an update on the current app
            // widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
            CheckBattery.AlwayUpdate(context, appWidgetId);
            CheckBattery.startjobscheduler(context);
            CheckBattery.startjobscheduler2(context);
            CheckBattery.startjobscheduler3(context);
            CheckBattery.startjobscheduler4(context);
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), BatteryWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
            Log.i("BatteryWidgetonReceive", "Updated!");
        } else {
            Log.w("BatteryWidgetonReceive", "Null!");
        }
    }
}
