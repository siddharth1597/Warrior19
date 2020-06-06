package sidsim.techwarriors.Status_fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sidsim.techwarriors.R;
import sidsim.techwarriors.StatusUpdateDetails;

public class NormalView extends Fragment implements View.OnClickListener {
    Button edit, update;
    Switch available;
    EditText name, address, total_vent, vac_vent, total_bed, vac_bed, phone_no;


    String hospital_name = "", state, city;
    String hospital_address = "", phoneno, latitude, longitude;
    int hospital_availability = 0, count = 0;
    int hospital_totalbeds = 0, hospital_occupiedbeds = 0, hospital_vacantbeds = 0; //beds
    int hospital_total_vent = 0, hospital_occupied_vent = 0, hospital_vacant_vent = 0; //ventilators
    int avl = 0, flg = 0 , varia = 0;
    DatabaseReference databaseReference , db , db2;
    FirebaseAuth auth;
    List<StatusUpdateDetails> hospitalDetails, hospitals, updateDetails;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.normal_view, container, false);

        edit = v.findViewById(R.id.edit);
        update = v.findViewById(R.id.update);
        name = v.findViewById(R.id.hosp_name);
        address = v.findViewById(R.id.address_hosp);
        total_bed = v.findViewById(R.id.total_bed);
        vac_bed = v.findViewById(R.id.vac_bed);
        total_vent = v.findViewById(R.id.total_vent);
        vac_vent = v.findViewById(R.id.vanc_vent);
        available = v.findViewById(R.id.availability);
        phone_no = v.findViewById(R.id.phone_no_hosp);
        dialog = new ProgressDialog(getContext());
        auth = FirebaseAuth.getInstance();
        hospitalDetails = new ArrayList<>();
        hospitals = new ArrayList<>();
        updateDetails = new ArrayList<>();
        databaseReference =FirebaseDatabase.getInstance().getReference("tblhospitals");
        //get the details of hospital

        getData();
        getHospital();
        Toast.makeText(getContext(), String.valueOf(count), Toast.LENGTH_SHORT).show();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                available.setEnabled(true);
                name.setEnabled(true);
                address.setEnabled(true);
                total_bed.setEnabled(true);
                total_vent.setEnabled(true);
                vac_bed.setEnabled(true);
                vac_vent.setEnabled(true);
                phone_no.setEnabled(true);
            }
        });
        update.setOnClickListener(this);

        return v;
    }

    private void getData() {
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot mySnap : dataSnapshot.getChildren()) {
                        NormalView.MyParamas myParamas = new NormalView.MyParamas(mySnap.getKey());
                        new miniOperation().execute(myParamas);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }
    private class miniOperation extends AsyncTask<NormalView.MyParamas,String,String>{
        @Override
        protected String doInBackground(MyParamas... myParamas) {
            if(!isCancelled()) {
                db = databaseReference.child(myParamas[0].key);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                            NormalView.MyParamas myParamas = new NormalView.MyParamas(mySnap.getKey());
                            new majorOperation().execute(myParamas);
                            Log.d("Key1--get", mySnap.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }
    private class majorOperation extends AsyncTask<NormalView.MyParamas,String,String>{
        @Override
        protected String doInBackground(MyParamas... myParamas) {
            if(!isCancelled()) {
                Log.d("ParamKey--get", myParamas[0].key);
                db2 = db.child(myParamas[0].key);
                db2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                            NormalView.MyParamas myParamas = new NormalView.MyParamas(dataSnapshot);
                            Log.d("Key@--get", mySnap.getKey());
                            pivotOperation b = new pivotOperation();
                            b.execute(myParamas);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

    private class pivotOperation  extends AsyncTask<NormalView.MyParamas,String,String>{
        @Override
        protected String doInBackground(MyParamas... myParamas) {
            if(!isCancelled()) {
                int i = 0;
                hospitalDetails.clear();
                for (DataSnapshot mySnap : myParamas[0].dataSnapshot.getChildren()) {
                    StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                    hospitalDetails.add(sd);
                }
                for (i = 0; i < hospitalDetails.size(); i++) {
                    if (hospitalDetails.get(i).getEmail().equals(auth.getCurrentUser().getEmail())) {
                        hospital_availability = hospitalDetails.get(i).getStatus();
                        hospital_totalbeds = hospitalDetails.get(i).getTotalBeds();
                        hospital_address = hospitalDetails.get(i).getLocation_add();
                        hospital_name = hospitalDetails.get(i).getName();
                        phoneno = hospitalDetails.get(i).getPhone();
                        latitude = hospitalDetails.get(i).getLocation_lat();
                        longitude = hospitalDetails.get(i).getLocation_long();
                        hospital_vacantbeds = hospitalDetails.get(i).getVacantBeds();
                        hospital_total_vent = hospitalDetails.get(i).getVentilators();
                        hospital_vacant_vent = hospitalDetails.get(i).getVacantVentilaor();
                        state = hospitalDetails.get(i).getState();
                        city = hospitalDetails.get(i).getCity();
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (hospital_availability == 0)
                    available.setChecked(false);
                else
                    available.setChecked(true);
                name.setText(hospital_name);
                address.setText(hospital_address);
                total_bed.setText(String.valueOf(hospital_totalbeds));
                total_vent.setText(String.valueOf(hospital_total_vent));
                vac_vent.setText(String.valueOf(hospital_vacant_vent));
                vac_bed.setText(String.valueOf(hospital_vacantbeds));
                phone_no.setText(phoneno);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }
    private void setData() {

    }

    private void getHospital() {
        hospitals = new ArrayList<>();
        DatabaseReference db = databaseReference.child("Uttar Pradesh").child("Moradabad");
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
                    Log.d("name" + i, hospitals.get(i).getName());
                    Log.d("Availability" + i, String.valueOf(hospitals.get(i).getStatus()));
                    Log.d("Beds" + i, String.valueOf(hospitals.get(i).getTotalBeds()));
                    Log.d("Address" + i, hospitals.get(i).getLocation_add());
                    Log.d("Name" + i, hospitals.get(i).getName());
                    Log.d("Phone" + i, String.valueOf(hospitals.get(i).getPhone()));
                    Log.d("Vacant Beds" + i, String.valueOf(hospitals.get(i).getVacantBeds()));
                    Log.d("Ventilators" + i, String.valueOf(hospitals.get(i).getVentilators()));
                    Log.d("Vacant Ventilators" + i, String.valueOf(hospitals.get(i).getVacantVentilaor()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        dialog.setMessage("Updating");
        dialog.show();
        boolean avail = available.isChecked();
        if (avail)
            avl = 1;
        hospital_name = name.getText().toString().trim();
        hospital_address = address.getText().toString().trim();
        hospital_totalbeds = Integer.parseInt(total_bed.getText().toString().trim());
        hospital_total_vent = Integer.parseInt(total_vent.getText().toString().trim());
        hospital_vacant_vent = Integer.parseInt(vac_vent.getText().toString().trim());
        hospital_vacantbeds = Integer.parseInt(vac_bed.getText().toString().trim());
        phoneno = phone_no.getText().toString().trim();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot mySnap : dataSnapshot.getChildren()) {


                    NormalView.MyParamas myParamas = new NormalView.MyParamas(mySnap.getKey());
                    new LongOperation().execute(myParamas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private static class MyParamas {
        DataSnapshot dataSnapshot;
        String key;


        public MyParamas(String key) {
            this.key = key;
        }
        public MyParamas(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }
    }


    public void change(){
        Toast.makeText(getContext(), "Data Updated Successful", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        available.setEnabled(false);
        name.setEnabled(false);
        address.setEnabled(false);
        total_bed.setEnabled(false);
        total_vent.setEnabled(false);
        vac_bed.setEnabled(false);
        vac_vent.setEnabled(false);
        phone_no.setEnabled(false);
    }

    private class LongOperation extends  AsyncTask<NormalView.MyParamas,String,String>{

        @Override
        protected String doInBackground(NormalView.MyParamas... myParamas) {
            if(!isCancelled()) {
                db = databaseReference.child(myParamas[0].key);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                            NormalView.MyParamas myParamas = new NormalView.MyParamas(mySnap.getKey());
                            SmallOperation s = new SmallOperation();
                            s.execute(myParamas);
                            // s.cancel(true);
                            Log.d("Key1--", mySnap.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("PostExec Long O","H1");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

    private class SmallOperation extends AsyncTask<NormalView.MyParamas,String,String>{
        @Override
        protected String doInBackground(NormalView.MyParamas... myParamas) {
            if(!isCancelled()) {
                Log.d("ParamKey", myParamas[0].key);
                db2 = db.child(myParamas[0].key);
                db2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                            NormalView.MyParamas myParamas = new NormalView.MyParamas(dataSnapshot);
                            Log.d("Key@--", mySnap.getKey());
                            BigOperation b = new BigOperation();
                            b.execute(myParamas);

                            //  b.cancel(true);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("PostExec Small O","H2");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

    private class BigOperation extends AsyncTask<NormalView.MyParamas,String,String> {
        @Override
        protected String doInBackground(MyParamas... myParamas) {
            if(!isCancelled())
            {
                updateDetails.clear();

            varia = 0;
            for (DataSnapshot mySnap : myParamas[0].dataSnapshot.getChildren()) {
                StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                updateDetails.add(sd);

            }
            Log.d("Updatedetails", updateDetails.get(varia).getEmail());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
            String today_date = df.format(new Date());
            for (varia = 0; varia < updateDetails.size(); varia++) {
                if (updateDetails.get(varia).getEmail().equals(auth.getCurrentUser().getEmail())) {
                    StatusUpdateDetails ad = new StatusUpdateDetails(avl, hospital_totalbeds, hospital_vacantbeds, hospital_total_vent,
                            hospital_vacant_vent, updateDetails.get(varia).getKey(),
                            hospital_name, phoneno, hospital_address, latitude, longitude, auth.getCurrentUser().getEmail(), state, city, today_date);
                    db2.child(String.valueOf(updateDetails.get(varia).getKey())).setValue(ad).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                change();
                                flg = 1;

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Data Saving Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            name.setText("");
                            address.setText("");
                            total_bed.setText("");
                            total_vent.setText("");
                            vac_bed.setText("");
                            vac_vent.setText("");
                            phone_no.setText("");
                        }
                    });

                }
            }
            }



           return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("PostExec Big O","H3");

        }
//Helloooooo
        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

}


