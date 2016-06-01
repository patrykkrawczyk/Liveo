package com.patrykkrawczyk.liveo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.patrykkrawczyk.liveo.managers.NotificationManager;
import com.patrykkrawczyk.liveo.managers.accelerometer.AccelerometerManager;
import com.patrykkrawczyk.liveo.managers.data.DataBroadcaster;
import com.patrykkrawczyk.liveo.managers.location.MyLocationManager;
import com.patrykkrawczyk.liveo.managers.sap.SapBroadcaster;
import com.patrykkrawczyk.liveo.managers.sap.SapManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Patryk Krawczyk on 15.05.2016.
 */
public class MonitorService extends Service {

    private EventBus eventBus;
    private LocalBinder binder = new LocalBinder();

    private AccelerometerManager accelerometerManager;
    private MyLocationManager locationManager;
    private NotificationManager notificationManager;
    private DataBroadcaster dataBroadcaster;
    private SapBroadcaster sapBroadcaster;
    private SapManager sapManager;


    @Override
    public void onCreate() {
        super.onCreate();

        eventBus             = EventBus.getDefault();
        accelerometerManager = new AccelerometerManager(this);
        locationManager      = new MyLocationManager(this);
        notificationManager  = new NotificationManager(this);
        dataBroadcaster      = DataBroadcaster.getDefault(this);
        sapManager           = new SapManager(this);
        //sapBroadcaster       = SapBroadcaster.getDefault(this);

        //sapManager.connect();
        //dataBroadcaster.run();

        Log.d(getString(R.string.APP_TAG), "STARTTTT");
    }

    public void sendData(String data) {
        sapManager.send(data);
    }

    @Override
    public void onDestroy() {
        accelerometerManager.setEnabled(false);
        dataBroadcaster.kill();
        sapBroadcaster.kill();
        sapManager.disconnect();
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class LocalBinder extends Binder {
        public MonitorService getService() {
            return MonitorService.this;
        }
    }
}
