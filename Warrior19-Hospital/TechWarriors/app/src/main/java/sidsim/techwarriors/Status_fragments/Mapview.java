package sidsim.techwarriors.Status_fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sidsim.techwarriors.R;
import sidsim.techwarriors.StatusUpdateDetails;

public class Mapview extends Fragment implements LocationListener, OnMapReadyCallback{

    private GoogleMap mMap;
    double latitude , longitude;
    String hospital_name="";
    String hospital_address="";
    int hospital_availability =0;
    int hospital_totalbeds =0,  hospital_vacantbeds=0; //beds
    int hospital_total_vent =0,  hospital_vacant_vent=0; //ventilators
    SupportMapFragment mapFragment;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    List<StatusUpdateDetails> hospitalDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapview, container, false);

        //get the data from firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals");
        hospitalDetails = new ArrayList<>();
        getData();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    private void set() {

        // Add a marker in location and move the camera
        LatLng current = new LatLng(latitude, longitude);

        if(hospital_availability!=0) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(current).title(hospital_name)
                    .snippet("Availabile: "+"YES" + "\n" + "Total beds: "+ hospital_totalbeds+"\n"+
                            "Vacant beds: "+ hospital_vacantbeds + "\n"+"Total ventilators: "+hospital_total_vent+
                            "\n"+ "Vacant ventilators: "+hospital_vacant_vent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            marker.showInfoWindow();
        }
        else {
            Marker marker = mMap.addMarker(new MarkerOptions().position(current).title(hospital_name)
                    .snippet("Available: "+"NO" + "\n" + "Total beds: "+ hospital_totalbeds+"\n"+
                            "Vacant beds: "+ hospital_vacantbeds + "\n"+"Total ventilators: "+hospital_total_vent+
                            "\n"+ "Vacant ventilators: "+hospital_vacant_vent).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker.showInfoWindow();
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.getMaxZoomLevel();

    }

    private void getData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalDetails.clear();
                int i = 0;
                for(DataSnapshot mySnap : dataSnapshot.getChildren()){
                    //Log.d("Key",String.valueOf(mySnap.getKey()));
                    DatabaseReference db = databaseReference.child(mySnap.getKey());
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot mySnap:dataSnapshot.getChildren()){
                                //Log.d("Child",mySnap.getKey());
                                db.child(mySnap.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int i =0;
                                        hospitalDetails.clear();
                                        for(DataSnapshot mySnap:dataSnapshot.getChildren()){

                                            StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                                            hospitalDetails.add(sd);

                                            if(String.valueOf(hospitalDetails.get(i).getEmail()).equals(auth.getCurrentUser().getEmail())){
                                                hospital_availability = hospitalDetails.get(i).getStatus();
                                                hospital_totalbeds = hospitalDetails.get(i).getTotalBeds();
                                                hospital_address = hospitalDetails.get(i).getLocation_add();
                                                latitude = Double.parseDouble(hospitalDetails.get(i).getLocation_lat());
                                                longitude = Double.parseDouble(hospitalDetails.get(i).getLocation_long());
                                                hospital_name = hospitalDetails.get(i).getName();
                                                hospital_vacantbeds = hospitalDetails.get(i).getVacantBeds();
                                                hospital_total_vent = hospitalDetails.get(i).getVentilators();
                                                hospital_vacant_vent = hospitalDetails.get(i).getVacantVentilaor();
                                                break;
                                            }
                                            i++;

                                        }
                                        set();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
