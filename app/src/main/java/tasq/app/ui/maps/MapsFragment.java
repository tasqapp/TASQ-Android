package tasq.app.ui.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private NavController navController;
    private AddEditViewModel model;
    ArrayList<Task> allTasks = new ArrayList<>();
    ArrayList<MarkerOptions> markers = new ArrayList<>();
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    static Location mLastLocation;
    GoogleMap mMap;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 222;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // When you long click on marker info window open google maps app.
        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                String address = marker.getSnippet();
                Uri mapsIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent directionsIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
                startActivity(directionsIntent);
            }
        });

        // Change UI settings.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        googleMap.setPadding(0, 0, 250, 50);

        // Add markers to map.
        for (MarkerOptions opt : markers) {
            googleMap.addMarker(opt);
        }

        // Setup location request for
        locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        if (mLastLocation != null) {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        } else {
            LatLng Bellingham = new LatLng(48.752, 122.478);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Bellingham, 10));
        }

        // Check if we have the proper permissions. If we do try to get current location. If not request them.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback, Looper.myLooper());
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    // Callback for when the location data comes back.
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locations = locationResult.getLocations();
            if (locations.size() > 0) {
                Location location = locations.get(locations.size() - 1);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                mLastLocation = location;
            } else {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
    };

    // Check to see if the permissions we requested were accepted.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission denied.", Toast.LENGTH_LONG).show();
                }

                return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_page);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        navController = Navigation.findNavController(getView());
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            updateMapComponents(item);
        });
    }

    private void updateMapComponents(ArrayList<Task> item) {
        allTasks = item;
        ArrayList<Task> uncompletedTasksWithAddress = new ArrayList<>();

        // Get all tasks that haven't been completed yet.
        for (Task task : allTasks) {
            if (!task.isCompleted() && task.getAddressObj() != null) {
                uncompletedTasksWithAddress.add(task);
            }
        }

        // Make a MarkerOptions for each task.
        for (Task task : uncompletedTasksWithAddress) {
            MarkerOptions opts = new MarkerOptions()
                    .position(task.getLocation())
                    .title(Task.getText(task))
                    .snippet(task.getAddress());
            switch (Task.getColor(task)) {
                case "Red":
                    opts.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED));
                    break;
                case "Green":
                    opts.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN));
                    break;
                case "Blue":
                    opts.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_BLUE));
                    break;
                default:
                    opts.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_ORANGE));
            }

            markers.add(opts);
        }
    }
}