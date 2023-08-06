package com.example.cancerware;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;
import java.util.Locale;
import java.util.Arrays;

public class ClinicFragment extends Fragment implements LocationListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private Button button_location;
    private TextView textView_location;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CHECK_SETTINGS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflates the layout for fragment
        View view = inflater.inflate(R.layout.fragment_clinic, container, false);

        textView_location = view.findViewById(R.id.text_location);
        button_location = view.findViewById(R.id.button_location);
        mapView = view.findViewById(R.id.mapView);

        // Runtime permissions
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
            }
        });


        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // The permission is granted, proceed with location-related tasks

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(requireActivity())
                    .checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. Get the current location.
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                                new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            Location location = task.getResult();
                                            // Now you have the user's current location, you can use it to search for nearby hospitals using the Google Places API.
                                            findNearbyHospitals(location);
                                        } else {
                                            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
                    } catch (ApiException exception) {
                        if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            // Location settings are not satisfied. Show the user a dialog to upgrade location settings.
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                        } else {
                            // Handle other exceptions
                            Toast.makeText(requireContext(), "Error getting location settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            // The permission is not granted. Request the permission from the user.
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(requireContext(), "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            textView_location.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void findNearbyHospitals(Location location) {
        String apiKey = getString(R.string.google_maps_key); // Get your API key from resources

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Initialize Places API client
            Places.initialize(requireContext(), apiKey);

            // Create a new Places client instance
            PlacesClient placesClient = Places.createClient(requireContext());

            // Set up the fields to be returned in the Place information
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Use FindCurrentPlaceRequest to fetch nearby places
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

            // Perform the place request
            Context context = requireContext();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);

            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse response = task.getResult();

                        // Process the response and display nearby hospitals on the map
                        List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
                        for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                            Place place = placeLikelihood.getPlace();
                            LatLng latLng = place.getLatLng();
                            String name = place.getName();

                            // Add a marker for each nearby hospital
                            googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(name));
                        }

                        // Move the map camera to the user's location
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f));
                    } else {
                        Toast.makeText(requireContext(), "Error fetching nearby hospitals", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}