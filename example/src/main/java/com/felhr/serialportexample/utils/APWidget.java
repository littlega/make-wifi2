package com.felhr.serialportexample.utils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class APWidget extends AppWidgetProvider {

    public static String WIFIAP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private static final String STATE = "state";
    private boolean state;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Implement this method
       // lg("on recev");

        WifiApManager apMan = new WifiApManager(context);
        state = apMan.isWifiApEnabled();
     //   lg("state: " + state);

        if (WIFIAP_STATE_CHANGED.equals(intent.getAction())) {
      //      lg("ap changed");
        }

        if (intent.getBooleanExtra(STATE, false)) {
            state = !state;
            apMan.setWifiApEnabled(null, state);
           // lg("Widget button click");
        }

        if (intent.getBooleanExtra(STATE, false) || (WIFIAP_STATE_CHANGED.equals(intent.getAction()) && !intent.getBooleanExtra(STATE, false))) {
            AppWidgetManager awm = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, this.getClass());
            int[] ids = awm.getAppWidgetIds(cn);
           // onUpdate(context, awm, ids);
        }

        super.onReceive(context, intent);
    }


}
