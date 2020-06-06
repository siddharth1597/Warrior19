package sidsim.techwarriors_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Normal_View extends AppCompatActivity {
    EditText etTotal, etVacant, etVentilator,etVacantV,name,address, phone ;
    Switch available;
    String hospital_name = "";
    String hospital_address = "", hospital_phone = "";
    int hospital_availability = 1, key;  // 0-red, 1-green
    String location_state, location_city;
    int hospital_totalbeds = 0,  hospital_vacantbeds = 0; //beds
    int hospital_total_vent = 0, hospital_vacant_vent = 0;
    DatabaseReference databaseReference;
    List<StatusUpdateDetails> hospitals;
    ProgressDialog dialog;
    TextView aval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal__view);

        Intent in = getIntent();
        location_state = in.getStringExtra("state");
        location_city = in.getStringExtra("city");
        key = Integer.valueOf(in.getStringExtra("key"));

        setIds();

        Log.e("gethospital:", "-" + location_city + "," + location_state + "-");
        DatabaseReference db = databaseReference.child(location_state.trim()).child(location_city.trim());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog = new ProgressDialog(Normal_View.this);
                dialog.setMessage("Fetching all details");

                dialog.show();

                new LongOperation().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class LongOperation extends AsyncTask<DataSnapshot, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(DataSnapshot... dataSnapshot) {
            try {

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
                    Log.d("Vacant Ventilators" + i, String.valueOf(hospitals.get(i).getVacantVentilaor()));
                    if(hospitals.get(i).getKey() == key) {
                        hospital_name = hospitals.get(i).getName();
                        hospital_availability = hospitals.get(i).getStatus();
                        hospital_totalbeds = hospitals.get(i).getTotalBeds();
                        hospital_address = hospitals.get(i).getLocation_add();
                        hospital_phone = hospitals.get(i).getPhone();
                        hospital_vacantbeds = hospitals.get(i).getVacantBeds();
                        hospital_total_vent = hospitals.get(i).getVentilators();
                        hospital_vacant_vent = hospitals.get(i).getVacantVentilaor();
                    }


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

                Log.e("POST: ", String.valueOf(hospital_name));

                if (hospital_availability == 0) {
                    available.setChecked(false);
                    aval.setText("Not Available");
                }
                else {
                    available.setChecked(true);
                    aval.setText("Available");
                }
                name.setText(hospital_name);
                address.setText(hospital_address);
                etTotal.setText(String.valueOf(hospital_totalbeds));
                etVentilator.setText(String.valueOf(hospital_total_vent));
                etVacantV.setText(String.valueOf(hospital_vacant_vent));
                etVacant.setText(String.valueOf(hospital_vacantbeds));
                phone.setText(hospital_phone);

                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setIds() {
        available = findViewById(R.id.availability);
        name = findViewById(R.id.hosp_name);
        etVacant = findViewById(R.id.vac_bed);
        etTotal = findViewById(R.id.total_bed);
        etVacantV = findViewById(R.id.vac_vent);
        etVentilator = findViewById(R.id.total_vent);
        phone = findViewById(R.id.phone_no_hosp);
        address = findViewById(R.id.address_hosp);
        aval = findViewById(R.id.avail);
        hospitals = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals");
    }
}
