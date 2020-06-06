package sidsim.techwarriors_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class List_Hospitals extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    RecyclerView recycle;
    RecyclerAdapter_hospitals adap;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    ProgressDialog dialog;
    LocationManager locationManager;
    String location_state, location_city;
    List<StatusUpdateDetails> hospitals;
    ArrayList<StatusUpdateDetails> searchResults;

    String[] hospital_name = new String[1000];
    double[] hospital_lat = new double[1000];
    double[] hospital_long = new double[1000];
    String[] hospital_city = new String[1000];
    String[] hospital_state = new String[1000];
    int[] hospital_key = new int[1000];
    int[] hospital_vacantbeds = new int[1000];
    int[] hospital_vacant_vent = new int[1000];
    double latitude, longitude, distance;
    Button two, five, ten;
    EditText enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list__hospitals);
        two = findViewById(R.id.two);
        five = findViewById(R.id.five);
        ten = findViewById(R.id.ten);
        enter = findViewById(R.id.enter);

        getCurrentLocation();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals");
        hospitals = new ArrayList<>();
        searchResults = new ArrayList<>();
        dialog = new ProgressDialog(List_Hospitals.this);

        recycle = findViewById(R.id.recycleview);
        recycle.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(layoutManager);
        


        Log.e("gethospital:", "-" + location_city + "," + location_state + "-");
        DatabaseReference db = databaseReference.child(location_state.trim()).child(location_city.trim());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.setMessage("Fetching all details");

                dialog.show();

                new LongOperation().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void two_km(View view) {
        enter.setText("");
        searchResults.clear();
        filter(2.0);
    }
    public void five_km(View view) {
        enter.setText("");
        searchResults.clear();
        filter(5.0);
    }

    public void ten_km(View view) {
        enter.setText("");
        searchResults.clear();
        filter(10.0);
    }

    public void enter(View view) {
        double value = Double.parseDouble(enter.getText().toString().trim());
        searchResults.clear();
        filter(value);
    }

    public void filter(double value)
    {
        try {
            dialog.setMessage("Fetching all details");

            dialog.show();

            int flag=0;

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
                Log.d("Beds" + i, String.valueOf(hospitals.get(i).getTotalBeds()));
                Log.d("Address" + i, String.valueOf(hospitals.get(i).getLocation_add()));
                Log.d("Phone" + i, String.valueOf(hospitals.get(i).getPhone()));
                Log.d("Vacant Beds" + i, String.valueOf(hospitals.get(i).getVacantBeds()));
                Log.d("Ventilators" + i, String.valueOf(hospitals.get(i).getVentilators()));

                hospital_name[i] = hospitals.get(i).getName();
                hospital_vacantbeds[i] = hospitals.get(i).getVacantBeds();
                hospital_vacant_vent[i] = hospitals.get(i).getVentilators();
                hospital_city[i] = hospitals.get(i).getCity();
                hospital_state[i] = hospitals.get(i).getState();
                hospital_key[i] = hospitals.get(i).getKey();
                hospital_lat[i] = Double.parseDouble(hospitals.get(i).getLocation_lat().trim());
                hospital_long[i] = Double.parseDouble(hospitals.get(i).getLocation_long().trim());

                Log.d("name[0]: ", String.valueOf(hospital_name[0]));

                Location start = new Location("locationA");
                start.setLatitude(latitude);
                start.setLongitude(longitude);

                Location end = new Location("locationB");
                end.setLatitude(hospital_lat[i]);
                end.setLongitude(hospital_long[i]);


                distance = (start.distanceTo(end)) / 1000;
                Log.e("diatance: ", String.valueOf(distance));
                if(distance<=value) {
                    flag=1;
                    setList(i);
                }

            }
            if(flag==0)
            {
                Toast.makeText(this, "Not available in the selected range", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    private void setList(int i) {
        try {

            Log.e("SetList: ", String.valueOf(hospital_name[i]));
            if (hospital_name[i]!=null ) {
                StatusUpdateDetails sr1 = new StatusUpdateDetails();
                sr1.setName(hospital_name[i]);
                sr1.setVacantBeds(hospital_vacantbeds[i]);
                sr1.setVacantVentilaor(hospital_vacant_vent[i]);
                sr1.setCity(hospital_city[i]);
                sr1.setState(hospital_state[i]);
                sr1.setKey(hospital_key[i]);

                searchResults.add(sr1);
            }
            adap = new RecyclerAdapter_hospitals(List_Hospitals.this, searchResults);

            recycle.setAdapter(adap);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class LongOperation extends AsyncTask<DataSnapshot, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
                protected String doInBackground(DataSnapshot... dataSnapshot) {
                    try {

                                hospitals.clear();
                                for (DataSnapshot mySnap : dataSnapshot[0].getChildren()) {
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

                                    hospital_name[i] = hospitals.get(i).getName();
                                    hospital_vacantbeds[i] = hospitals.get(i).getVacantBeds();
                                    hospital_vacant_vent[i] = hospitals.get(i).getVentilators();
                                    hospital_city[i] = hospitals.get(i).getCity();
                                    hospital_state[i] = hospitals.get(i).getState();
                                    hospital_key[i] = hospitals.get(i).getKey();

                                    Log.d("name[0]: ", String.valueOf(hospital_name[0]));

                                }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {
                        int i = 0;
                        Log.e("POST: ", String.valueOf(hospital_name[0]));
                        searchResults.clear();
                        while (hospital_name[i]!=null ) {
                            StatusUpdateDetails sr1 = new StatusUpdateDetails();
                            sr1.setName(hospital_name[i]);
                            sr1.setVacantBeds(hospital_vacantbeds[i]);
                            sr1.setVacantVentilaor(hospital_vacant_vent[i]);
                            sr1.setCity(hospital_city[i]);
                            sr1.setState(hospital_state[i]);
                            sr1.setKey(hospital_key[i]);
                              /*  if (type[i].equals("0")) {
                                    sr1.setImages(R.drawable.auto_icon);
                                } else if (type[i].equals("1")) {
                                    sr1.setImages(R.drawable.erik_rides);
                                }*/
                            searchResults.add(sr1);
                            Log.e("Address" + i, String.valueOf(searchResults.get(i).getName()));
                            i++;
                        }
                        adap = new RecyclerAdapter_hospitals(List_Hospitals.this, searchResults);

                        recycle.setAdapter(adap);
                        dialog.dismiss();
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
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
                List_Hospitals.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                List_Hospitals.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        Geocoder geocoder = new Geocoder(List_Hospitals.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
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
