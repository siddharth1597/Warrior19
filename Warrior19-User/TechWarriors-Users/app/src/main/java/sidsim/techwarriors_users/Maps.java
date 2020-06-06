package sidsim.techwarriors_users;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Maps extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    double hosp_latitude, hosp_longitude, latitude, longitude, distance; //28.83470802102238,78.78073786385357
    String hospital_name = "";
    String hospital_address = "", hospital_phone = "";
    int hospital_availability = 1;  // 0-red, 1-green
    String location_state, location_city;
    int hospital_totalbeds = 0, hospital_occupiedbeds = 0, hospital_vacantbeds = 0; //beds
    int hospital_total_vent = 0, hospital_occupied_vent = 0, hospital_vacant_vent = 0;
    List<StatusUpdateDetails> hospitals;
    DatabaseReference databaseReference;
    ProgressDialog dialog;
    LocationManager locationManager;
    Button two, five, ten;
    EditText enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals");
        hospitals = new ArrayList<>();
        two = findViewById(R.id.two);
        five = findViewById(R.id.five);
        ten = findViewById(R.id.ten);
        enter = findViewById(R.id.enter);

        if(mMap!=null) {
            mMap.clear();
        }
        DownloadTask d = new DownloadTask();
        d.execute();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap!=null) {
            mMap.clear();
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void getHospital() {

        try {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Fetching all details");
            dialog.setIndeterminate(true);
            dialog.show();

            Log.e("gethospital:", "-" + location_city + "," + location_state + "-");
            DatabaseReference db = databaseReference.child(location_state.trim()).child(location_city.trim());
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hospitals.clear();
                    for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                        StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                        hospitals.add(sd);

                    }
                    Log.d("size", String.valueOf(hospitals.size()));
                    for (int i = 0; i < hospitals.size(); i++) {
                        Log.d("name" + i, String.valueOf(hospitals.get(i).getName()));
                        Log.d("Availability" + i, String.valueOf(hospitals.get(i).getStatus()));
                        Log.d("Beds" + i, String.valueOf(hospitals.get(i).getTotalBeds()));
                        Log.d("Address" + i, String.valueOf(hospitals.get(i).getLocation_add()));
                        Log.d("Phone" + i, String.valueOf(hospitals.get(i).getPhone()));
                        Log.d("Vacant Beds" + i, String.valueOf(hospitals.get(i).getVacantBeds()));
                        Log.d("Ventilators" + i, String.valueOf(hospitals.get(i).getVentilators()));
                        Log.d("Vacant Ventilators" + i, String.valueOf(hospitals.get(i).getVacantVentilaor()));

                        hospital_name = hospitals.get(i).getName();
                        hospital_availability = hospitals.get(i).getStatus();
                        hospital_totalbeds = hospitals.get(i).getTotalBeds();
                        hospital_address = hospitals.get(i).getLocation_add();
                        hospital_phone = String.valueOf(hospitals.get(i).getPhone());
                        hospital_vacantbeds = hospitals.get(i).getVacantBeds();
                        hospital_total_vent = hospitals.get(i).getVentilators();
                        hospital_vacant_vent = hospitals.get(i).getVacantVentilaor();
                        hosp_latitude = Double.parseDouble(hospitals.get(i).getLocation_lat().trim().toString());
                        hosp_longitude = Double.parseDouble(hospitals.get(i).getLocation_long().trim().toString());


                        setMarkers();

                    }
                   // dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setMarkers(){

        try {
            LatLng current = new LatLng(hosp_latitude, hosp_longitude);

            if (hospital_availability != 0) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(current).title(hospital_name)
                        .snippet("Availabile: " + "YES" + "\n" + "Total beds: " + hospital_totalbeds + "\n" +
                                "Vacant beds: " + hospital_vacantbeds + "\n" + "Total ventilators: " + hospital_total_vent +
                                "\n" + "Vacant ventilators: " + hospital_vacant_vent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(current).title(hospital_name)
                        .snippet("Available: " + "NO" + "\n" + "Total beds: " + hospital_totalbeds + "\n" +
                                "Vacant beds: " + hospital_vacantbeds + "\n" + "Total ventilators: " + hospital_total_vent +
                                "\n" + "Vacant ventilators: " + hospital_vacant_vent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marker.showInfoWindow();
            }

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });

            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            dialog.dismiss();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void two_km(View view) {
        enter.setText("");
        filter(2.0);
    }
    public void five_km(View view) {
        enter.setText("");
        filter(5.0);
    }

    public void ten_km(View view) {
        enter.setText("");
        filter(10.0);
    }

    public void enter(View view) {
        double value = Double.parseDouble(enter.getText().toString().trim());
        filter(value);
    }


    public void filter(double value)
    {
        try {
            int flag=0;

            mMap.clear();
            Toast.makeText(this, (int) value+"km radius", Toast.LENGTH_SHORT).show();

            if((int) value==2) {
                two.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
                five.setBackgroundResource(R.drawable.white_rounded_corner);
                ten.setBackgroundResource(R.drawable.white_rounded_corner);
            }
            if((int) value==5) {
                five.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
                ten.setBackgroundResource(R.drawable.white_rounded_corner);
                two.setBackgroundResource(R.drawable.white_rounded_corner);
            }
            if((int) value==10) {
                ten.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
                two.setBackgroundResource(R.drawable.white_rounded_corner);
                five.setBackgroundResource(R.drawable.white_rounded_corner);
            }

            for (int i = 0; i < hospitals.size(); i++) {
                Log.d("name" + i, String.valueOf(hospitals.get(i).getName()));
                Log.d("Availability" + i, String.valueOf(hospitals.get(i).getStatus()));
                hospital_name = hospitals.get(i).getName();
                hospital_availability = hospitals.get(i).getStatus();
                hospital_totalbeds = hospitals.get(i).getTotalBeds();
                hospital_address = hospitals.get(i).getLocation_add();
                hospital_phone = String.valueOf(hospitals.get(i).getPhone());
                hospital_vacantbeds = hospitals.get(i).getVacantBeds();
                hospital_total_vent = hospitals.get(i).getVentilators();
                hospital_vacant_vent = hospitals.get(i).getVacantVentilaor();
                hosp_latitude = Double.parseDouble(hospitals.get(i).getLocation_lat().trim());
                hosp_longitude = Double.parseDouble(hospitals.get(i).getLocation_long().trim());

                Location start = new Location("locationA");
                start.setLatitude(latitude);
                start.setLongitude(longitude);

                Location end = new Location("locationB");
                end.setLatitude(hosp_latitude);
                end.setLongitude(hosp_longitude);


                distance = (start.distanceTo(end)) / 1000;
                Log.e("diatance: ", String.valueOf(distance));
                if(distance<=value) {
                    flag=1;
                    setMarkers();
                }

            }
            if(flag==0)
            {
                mMap.clear();
                Toast.makeText(this, "Not available in the selected range", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    private class DownloadTask extends AsyncTask<InputStream, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           // dialog = new ProgressDialog(getApplicationContext());
           // dialog.setMessage("Fetching all details");
            //dialog.show();
        }

        @Override
        protected String doInBackground(InputStream... inputStreams) {

            getCurrentLocation();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getHospital();
        }
    }



    //GPS location track
    private void getCurrentLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                Maps.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                Maps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = lat;
                longitude = longi;
                Log.e("check: ",String.valueOf(latitude));
                getAddress(lat, longi);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(Maps.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            hospital_address = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            location_state=  obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            location_city =  obj.getLocality();
             Log.e("state: ",location_city+","+location_state);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
