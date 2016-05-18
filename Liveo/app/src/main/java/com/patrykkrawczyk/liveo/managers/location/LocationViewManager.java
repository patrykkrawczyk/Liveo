package com.patrykkrawczyk.liveo.managers.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.patrykkrawczyk.liveo.R;
import com.patrykkrawczyk.liveo.activities.HubActivity;
import com.patrykkrawczyk.liveo.managers.StateManager;

import butterknife.OnClick;

/**
 * Created by Patryk Krawczyk on 07.05.2016.
 */
public class LocationViewManager implements OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnScrollListener {

    private MapboxMap mapboxMap;
    private SupportMapFragment mapFragment;
    private boolean enabled = false;
    private boolean locked = true;
    private Icon markerIcon;
    private MarkerOptions currentMarker = new MarkerOptions();

    public LocationViewManager(FragmentActivity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enabled = true;
        }

        if (enabled) {
            MapboxMapOptions options = new MapboxMapOptions();
            options.accessToken(activity.getString(R.string.LIVEO_MAPBOX_TOKEN));
            options.styleUrl(activity.getString(R.string.LIVEO_MAPBOX_STYLE));
            options.attributionEnabled(false);
            options.rotateGesturesEnabled(false);
            options.compassEnabled(false);
            options.logoEnabled(false);
            mapFragment = SupportMapFragment.newInstance(options);

            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.mapContainer, mapFragment, "com.mapbox.map");
            transaction.commit();

            IconFactory iconFactory = IconFactory.getInstance(activity);
            Drawable iconDrawable = ContextCompat.getDrawable(activity, R.drawable.map_marker_circle);
            markerIcon = iconFactory.fromDrawable(iconDrawable);

            mapFragment.getMapAsync(this);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }


    public void centerView() {
        locked = true;

        if (currentMarker != null && enabled) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(currentMarker.getPosition())
                    .zoom(17)
                    .tilt(45)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position));
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMyLocationEnabled(false);
    }

    public void animateCamera(Location location) {
        if (locked && location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mapboxMap.removeMarker(currentMarker.getMarker());
            currentMarker = new MarkerOptions().icon(markerIcon).position(latLng);
            mapboxMap.addMarker(currentMarker);

            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17)
                    .tilt(45)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position));
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) { locked = false; }

    @Override
    public void onScroll() { locked = false; }
}
