package com.refect.jarviscoffee;

import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

/**
 * Created by Austin Nelson on 11/17/2015.
 */
public class CoffeeService extends Service {

    // AlarmActivity listens for this broadcast intent, so that other applications
    // can snooze the alarm (after ALARM_ALERT_ACTION and before ALARM_DONE_ACTION).
    public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";
    // AlarmActivity listens for this broadcast intent, so that other applications
    // can dismiss the alarm (after ALARM_ALERT_ACTION and before ALARM_DONE_ACTION).
    public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";
    // A public action send by AlarmService when the alarm has started.
    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";
    // A public action sent by AlarmService when the alarm has stopped for any reason.
    public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_SNOOZE_ACTION);
        filter.addAction(ALARM_DISMISS_ACTION);
        filter.addAction(ALARM_ALERT_ACTION); //further more
        filter.addAction(ALARM_DONE_ACTION); //further more

        registerReceiver(receiver, filter);

        IntentFilter amarinoFilter = new IntentFilter();
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECT);
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
        amarinoFilter.addAction(AmarinoIntent.ACTION_DISCONNECT);
        amarinoFilter.addAction(AmarinoIntent.ACTION_DISCONNECTED);

        registerReceiver(amarinoReceiver, amarinoFilter);

        Toast.makeText(getApplicationContext(), "Starting Coffee Service...", Toast.LENGTH_SHORT).show();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        unregisterReceiver(amarinoReceiver);
        Toast.makeText(getApplicationContext(), "Stopping Coffee Service...", Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver amarinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(AmarinoIntent.ACTION_CONNECT)) {
                Log.d("CoffeeService (onReceive)", "Amarino - Connect");
                //cool story
            } else if(action.equals(AmarinoIntent.ACTION_CONNECTED)) {
                Log.d("CoffeeService (onReceive)", "Amarino - Connected");
                //yey, make coffee
            } else if(action.equals(AmarinoIntent.ACTION_CONNECTION_FAILED)) {
                Log.d("CoffeeService (onReceive)", "Amarino - Connection Failed");
                //try again (limit)
            } else if(action.equals(AmarinoIntent.ACTION_DISCONNECT)) {
                Log.d("CoffeeService (onReceive)", "Amarino - Disconnect");
                //cool story
            } else if(action.equals(AmarinoIntent.ACTION_DISCONNECTED)) {
                Log.d("CoffeeService (onReceive)", "Amarino - Disconnected");
                //cool story
            }
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ALARM_DISMISS_ACTION)){
                Log.d("CoffeeService (onReceive)", "Alarm - Dismiss");
                Toast.makeText(getApplicationContext(), "Dismiss", Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(ALARM_SNOOZE_ACTION)){
                Log.d("CoffeeService (onReceive)", "Alarm - Snooze");
                Toast.makeText(getApplicationContext(), "Snooze", Toast.LENGTH_SHORT).show();
            } else if(action.equals(ALARM_DONE_ACTION)) {
                Log.d("CoffeeService (onReceive)", "Alarm - Done");
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
            } else if(action.equals(ALARM_ALERT_ACTION)) {
                Log.d("CoffeeService (onReceive)", "Alarm - Alert");
                Toast.makeText(getApplicationContext(), "Alert", Toast.LENGTH_SHORT).show();
            }
        }
    };
}