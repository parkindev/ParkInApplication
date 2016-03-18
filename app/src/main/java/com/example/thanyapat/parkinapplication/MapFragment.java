package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MapFragment extends StatedFragment {

    private static MapView mapView;
    private static GoogleMap map;

    private static LatLng userPosition;
    protected static HashMap<String, ParkingArea> hashMarker = new HashMap<>();

    private static View rootView;
    private static LocationListener listener;
    protected static SeekBarManager seekbarmanager;
    private Marker searchMarker;
    private Marker selectedMarker;

    private FloatingActionButton searchFab;
    private FloatingActionButton locationFab;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static boolean isLocated = false;
    protected static DatabaseManager dbManager;
    protected SlidePanel slidePanel;

    private static final String INITIAL_LOCATION = "initial-location";


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(LatLng latLng) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(INITIAL_LOCATION, latLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("MapFragment", "onCreate");
        MainActivity.navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            dbManager = new DatabaseManager(this);
            dbManager.queryAll("ParkArea");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Log.w("MapFragment", "onCreateView");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (isAdded()) {
                    Log.w("MapFragment", "passing userPosition to MainActivity");
                    ((MainActivity) getActivity()).setUserPosition(userPosition);
                }
                if (!isLocated) {
                    Log.w("MapFragment", "move camera to userLocation");
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                    isLocated = true;
                }
            }
        };
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        initComponents();
        return rootView;
    }

    public void initComponents() {
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("CLICK", ("Click on map"));
                slidePanel.hide();
            }
        });
        seekbarmanager = new SeekBarManager(this, rootView);
        slidePanel = (SlidePanel) rootView.findViewById(R.id.slide_panel);
        slidePanel.initAnim(this);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("CLICK", ("Click on map"));
                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker
                            , hashMarker.get(selectedMarker.getId()).getPrice() != null ? "" + ApplicationUtils.durationToPrice(hashMarker.get(selectedMarker.getId()), seekbarmanager.getValue()) : "")));
                    selectedMarker = null;
                }
                slidePanel.hide();
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(searchMarker)) { // search marker must do nothing when clicked
                    return true;
                }
                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker
                            , hashMarker.get(selectedMarker.getId()).getPrice() != null ? "" + ApplicationUtils.durationToPrice(hashMarker.get(selectedMarker.getId()), seekbarmanager.getValue()) : "")));
                }
                selectedMarker = marker;
                selectedMarker.showInfoWindow();
                selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker_clicked
                        , hashMarker.get(selectedMarker.getId()).getPrice() != null ? "" + ApplicationUtils.durationToPrice(hashMarker.get(selectedMarker.getId()), seekbarmanager.getValue()) : "")));
                Log.e("marker press", hashMarker.get(marker.getId()).getName() + " is pressed");
                slidePanel.show(hashMarker.get(marker.getId()));
                return true;

            }
        });
        searchFab = (FloatingActionButton) rootView.findViewById(R.id.search_fab);
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
        locationFab = (FloatingActionButton) rootView.findViewById(R.id.location_fab);
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userPosition != null)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
            }
        });

    }

    public void goToPlace(Place place) {
        if (searchMarker != null) searchMarker.remove();
        searchMarker = map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));

        Log.w("goToPlace", "Go to " + place.getName());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15), 1000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.w("goToPlace", "Finish Animating");
            }

            @Override
            public void onCancel() {
                Log.w("goToPlace", "Cancel Animating");
            }
        });
        searchMarker.showInfoWindow();
    }

    public LocationListener getLocationListener() {
        if (listener != null) return listener;
        else return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w("MapFragment", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.w("MapFragment", "onResume");
        if (this.getArguments().getParcelable(INITIAL_LOCATION) != null) {
            try {
                LatLng latLng = getArguments().getParcelable(INITIAL_LOCATION);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                isLocated = true;
                slidePanel.show(getParkingAreaByLatLng(latLng));
                getArguments().clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    private ParkingArea getParkingAreaByLatLng(LatLng latlng) {
        Iterator it = hashMarker.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LatLng tem = new LatLng(((ParkingArea) pair.getValue()).getLat(), ((ParkingArea) pair.getValue()).getLong());
            if (tem.equals(latlng)) {
                return (ParkingArea) pair.getValue();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        Log.e("MapFragment", "No area match " + latlng.toString());
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.w("MapFragment", "onPause");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("MapFragment", "onDestroy");
        mapView.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w("MapFragment", "onStop");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    protected void markAll() {
        for (ParkingArea object : ((MainActivity) getActivity()).getAreaList()) {
            Marker temp = map.addMarker(new MarkerOptions()
                    .position(new LatLng(object.getLat(), object.getLong()))
                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker
                            , object.getPrice() != null ? "" + ApplicationUtils.durationToPrice(object, seekbarmanager.getValue()) : ""))));
            hashMarker.put(temp.getId(), object);
        }
        Log.w("MapFragment", "Finish Adding Markers");
    }

    protected Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.DKGRAY);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(this.getContext(), 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(bm);
        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(this.getContext(), 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) + ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text.equals("") ? "" : text + "฿", xPos, yPos, paint);
        return bm;
    }

    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);

    }

    protected void onFirstTimeLaunched() {
        isLocated = false;
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString("text", tvSample.getText().toString());
        Log.w("MapFragment", "Saving State");
        if (map != null && seekbarmanager != null) {
            outState.putParcelable("camera", map.getCameraPosition());
            outState.putInt("seekBarValue", seekbarmanager.getValue());
        }
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString("text"));
        Log.w("MapFragment", "Loading State");
        if (map != null && seekbarmanager != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition((CameraPosition) savedInstanceState.getParcelable("camera")));
            seekbarmanager.updateSeekBar(savedInstanceState.getInt("seekBarValue"));
        }
        markAll();
    }

    public void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this.getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e("AutoComplete", message);
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == MainActivity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this.getContext(), data);
                Log.w("AutoComplete", "Place Selected: " + place.getName());

                goToPlace(place);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this.getContext(), data);
                Log.e("AutoComplete", "Error: Status = " + status.toString());
            } else if (resultCode == MainActivity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

}
