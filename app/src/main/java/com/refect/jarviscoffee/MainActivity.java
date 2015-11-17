package com.refect.jarviscoffee;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ADDRESS = "00:06:66:45:0E:97";
    private int retry = 5;

    private ImageButton btnToggleCoffee;
    private Switch switchStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggleCoffee = (ImageButton) findViewById(R.id.btn_toggle_coffee);
        switchStartService = (Switch) findViewById(R.id.switch_start_service);

        IntentFilter amarinoFilter = new IntentFilter();
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECT);
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
        amarinoFilter.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
        amarinoFilter.addAction(AmarinoIntent.ACTION_DISCONNECT);
        amarinoFilter.addAction(AmarinoIntent.ACTION_DISCONNECTED);
        amarinoFilter.addAction(AmarinoIntent.ACTION_RECEIVED);

        registerReceiver(amarinoReceiver, amarinoFilter);

        //Connect to amarino
        Amarino.connect(getApplicationContext(), DEVICE_ADDRESS);

        if(isMyServiceRunning(CoffeeService.class)) {
            switchStartService.setChecked(true);
        } else {
            switchStartService.setChecked(false);
        }

        switchStartService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    stopService(new Intent(getBaseContext(), CoffeeService.class));
                } else {
                    startService(new Intent(getBaseContext(), CoffeeService.class));
                }
            }
        });

        btnToggleCoffee.setColorFilter(new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP));
        btnToggleCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnToggleCoffee.isSelected()) {
                    //turn coffee maker off
                    btnToggleCoffee.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
                    Amarino.sendDataToArduino(getApplicationContext(), DEVICE_ADDRESS, 'c', 0);
                   // btnToggleCoffee.setText("Start Coffee Service");
                } else {
                    //turn coffee maker on
                    btnToggleCoffee.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP));
                    Amarino.sendDataToArduino(getApplicationContext(), DEVICE_ADDRESS, 'c', 1);
                   // btnToggleCoffee.setText("Stop Coffee Service");
                }

                btnToggleCoffee.setSelected(!btnToggleCoffee.isSelected());
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private final BroadcastReceiver amarinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(AmarinoIntent.ACTION_CONNECT)) {
                Log.d("MainActivity (onReceive)", "Amarino - Connect");
                //cool story
            } else if(action.equals(AmarinoIntent.ACTION_CONNECTED)) {
                Log.d("MainActivity (onReceive)", "Amarino - Connected");
                retry = 5;
                //ask which state the coffee maker is in
                Amarino.sendDataToArduino(getApplicationContext(), DEVICE_ADDRESS, 's', 1);
            } else if(action.equals(AmarinoIntent.ACTION_CONNECTION_FAILED)) {
                Log.d("MainActivity (onReceive)", "Amarino - Connection Failed");
                //try again (limit)
                if(retry >= 0) {
                    Toast.makeText(getApplicationContext(), "Connection Failed - Retrying: " + retry--, Toast.LENGTH_SHORT).show();
                    Amarino.connect(getApplicationContext(), DEVICE_ADDRESS);
                } else {
                    retry = 5;
                    Toast.makeText(getApplicationContext(), "Could not connect to device", Toast.LENGTH_SHORT).show();
                    btnToggleCoffee.getDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                }
            } else if(action.equals(AmarinoIntent.ACTION_DISCONNECT)) {
                Log.d("MainActivity (onReceive)", "Amarino - Disconnect");
                //cool story
            } else if(action.equals(AmarinoIntent.ACTION_DISCONNECTED)) {
                Log.d("MainActivity (onReceive)", "Amarino - Disconnected");
                btnToggleCoffee.getDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                //cool story
            } else if(action.equals(AmarinoIntent.ACTION_RECEIVED)) {
                int data = intent.getIntExtra(AmarinoIntent.EXTRA_DATA, -1);
                Log.d("MainActivity (onReceive)", "Data Received: " + data);
                if(data != -1) {
                    if(data == 0) {
                        //off
                        btnToggleCoffee.setSelected(false);
                        btnToggleCoffee.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
                    } else {
                        //on
                        btnToggleCoffee.setSelected(true);
                        btnToggleCoffee.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP));
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
