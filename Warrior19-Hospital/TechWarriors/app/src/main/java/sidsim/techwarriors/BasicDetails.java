package sidsim.techwarriors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BasicDetails extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    EditText Name, phone_no, locEdit;
    Button current_loc, next_button;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference, databaseRegisterReference;
    String name;
    String phone;
    String location_address, location_state, location_city, type = "", nrmlkey;
    String latitude;
    String longitude;
    LocationManager locationManager;
    List<RegistrationDetails> registrationDetails, registrationDetailsList;
    List<StatusUpdateDetails> hospitalDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_details);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals");

        getData();
        setIds();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        FirebaseApp.initializeApp(this);

    }

    private void setIds() {
        Name = findViewById(R.id.name);
        phone_no = findViewById(R.id.phone);
        next_button = findViewById(R.id.next);
        current_loc = findViewById(R.id.current_loc);
        locEdit = findViewById(R.id.cur_location);
        dialog = new ProgressDialog(this);

        databaseRegisterReference = FirebaseDatabase.getInstance().getReference("tblregister");
        registrationDetails = new ArrayList<>();
        registrationDetailsList = new ArrayList<>();
        databaseRegisterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registrationDetails.clear();
                int i = 0;
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    RegistrationDetails rd = mySnap.getValue(RegistrationDetails.class);
                    registrationDetails.add(rd);
                    if (registrationDetails.get(i).getEmail().equals(auth.getCurrentUser().getEmail())) {
                        if (registrationDetails.get(i).getName().length() != 0) {
                            Name.setText(registrationDetails.get(i).getName());
                            nrmlkey = registrationDetails.get(i).getKey();
                            Log.d("key", nrmlkey);
                            Name.setEnabled(false);
                        } else {
                            nrmlkey = registrationDetails.get(i).getKey();
                            Log.d("key", nrmlkey);
                            type = "gmail";
                        }

                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void next(View view) {
        name = Name.getText().toString().trim();
        phone = phone_no.getText().toString().trim();
        if (type.equals("gmail")) {
            databaseRegisterReference.child(nrmlkey).child("name").setValue(name);

        }
        if (name.length() != 0) {
            if (phone.length() == 10) {
                if (location_address.length() != 0) {
                    // move to next page
                    Intent in = new Intent(BasicDetails.this, Status_Update.class);
                    in.putExtra("state", location_state);
                    in.putExtra("city", location_city);
                    in.putExtra("name", name);
                    in.putExtra("phone", phone);
                    in.putExtra("address", location_address);
                    in.putExtra("lat", String.valueOf(latitude));
                    in.putExtra("long", String.valueOf(longitude));
                    startActivity(in);
                } else
                    Toast.makeText(this, "Add your location first", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Phone No. should be equal to 10 digits", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Fill all entries", Toast.LENGTH_SHORT).show();
    }

    //GPS location track
    public void getCurrentLocation(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                BasicDetails.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                BasicDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.e("check: ", latitude);
                getAddress(lat, longi);
                locEdit.setText(location_address);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(BasicDetails.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            location_address = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            location_state = obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            location_city = obj.getLocality();
            // Toast.makeText(this, location_address+"\n"+location_city+"\n"+location_state, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        hospitalDetails = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalDetails.clear();
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    Log.d("Key--1",String.valueOf(mySnap.getKey()));
                    DatabaseReference db = databaseReference.child(mySnap.getKey());
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                                Log.d("Child--",mySnap.getKey());
                                db.child(mySnap.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int i = 0;
                                        hospitalDetails.clear();
                                        for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                                            Log.d("Avail--",mySnap.getKey());
                                            StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                                            hospitalDetails.add(sd);
                                            Log.d("Email2---",hospitalDetails.get(i).getEmail().trim());
                                            if (hospitalDetails.get(i).getEmail().trim().equals(auth.getCurrentUser().getEmail())){
                                             startActivity(new Intent(getApplicationContext(),Dashboard.class));
                                            }
                                            i++;

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
