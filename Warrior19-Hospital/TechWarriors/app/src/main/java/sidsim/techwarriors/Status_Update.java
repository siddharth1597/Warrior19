package sidsim.techwarriors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Status_Update extends AppCompatActivity {
    String city, state, name, phone, lat, lang, address;
    Switch switchAvail;
    EditText etTotal, etVacant, etVentilator,etVacantV;
    Button btsave;
    ImageView img1, img2, img3;
    DatabaseReference databaseReference,databaseReference2;
    ProgressDialog dialog;
    int total = 0, vacant = 0, venti = 0, id = 1000, vacantVentilator = 0 , j=0,id_forward = 1000;
    int count = 0,flag = 0;
    List<StatusUpdateDetails> hospitalDetails,hospitals;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status__update);
        city = getIntent().getExtras().getString("city");
        state = getIntent().getExtras().getString("state");
        name = getIntent().getExtras().getString("name");
        phone = getIntent().getExtras().getString("phone");
        lat = getIntent().getExtras().getString("lat");
        lang = getIntent().getExtras().getString("long");
        address = getIntent().getExtras().getString("address");
        Toast.makeText(this, city + state + String.valueOf(id), Toast.LENGTH_SHORT).show();

        setIds();

        switchAvail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etTotal.setEnabled(true);
                    etVacant.setEnabled(true);
                    etVentilator.setEnabled(true);
                    etVacantV.setEnabled(true);
                } else {
                    etTotal.setText("0");
                    etVacantV.setText("0");
                    etVacant.setText("0");
                    etVentilator.setText("0");
                    etTotal.setEnabled(false);
                    etVacant.setEnabled(false);
                    etVentilator.setEnabled(false);
                    etVacantV.setEnabled(false);
                }
            }
        });
    }

    private void setIds() {
        switchAvail = findViewById(R.id.availability);
        etTotal = findViewById(R.id.total_beds);
        etVacant = findViewById(R.id.vacant_beds);
        etVentilator = findViewById(R.id.ventilator);
        etVacantV = findViewById(R.id.vacant_ventilator);
        img1 = findViewById(R.id.ic_beds);
        img2 = findViewById(R.id.icon_ventialtors);
        img3 = findViewById(R.id.icon_vacant_beds);
        img3 = findViewById(R.id.icon_vacant_ventilator);
        btsave = findViewById(R.id.save);
        databaseReference = FirebaseDatabase.getInstance().getReference("tblhospitals").child(state).child(city);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("tblhospitals");
        dialog = new ProgressDialog(this);
        hospitalDetails = new ArrayList<>();
        hospitals = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
    }

    public void save(View view) {
        if (switchAvail.isChecked()) {
            if (!(etTotal.length() == 0 || etVacant.length() == 0 || etVentilator.length() == 0)) {
                if (Integer.parseInt(etVacant.getText().toString()) <= Integer.parseInt(etTotal.getText().toString()))
                        if(Integer.parseInt(etVacantV.getText().toString()) <= Integer.parseInt(etVentilator.getText().toString()))
                            dbreg(1);
                        else{
                            Toast.makeText(this, "Vacant Ventilators should be less than Total Ventilators", Toast.LENGTH_SHORT).show();
                            etVacantV.setText("");
                            etVacantV.requestFocus();
                        }
                else {
                    Toast.makeText(this, "Vacant should be less than Total beds", Toast.LENGTH_SHORT).show();
                    etVacant.setText("");
                    etVacant.requestFocus();
                }
            } else {
                Toast.makeText(this, "Need to fill the entries", Toast.LENGTH_SHORT).show();
                etTotal.setText("");
                etVacant.setText("");
                etVentilator.setText("");
            }
        } else
            dbreg(0);
    }

    private void dbreg(int i) {
        if (i == 1) {
            total = Integer.parseInt(etTotal.getText().toString());
            vacant = Integer.parseInt(etVacant.getText().toString());
            venti = Integer.parseInt(etVentilator.getText().toString());
            vacantVentilator = Integer.parseInt(etVacantV.getText().toString());
        }
        existence(i);
    }

    private void existence(int j) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalDetails.clear();
                int i = 0;
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    StatusUpdateDetails rd = mySnap.getValue(StatusUpdateDetails.class);
                    hospitalDetails.add(rd);
                    if ((hospitalDetails.get(i).getEmail().equals(auth.getCurrentUser().getEmail()))) {
                        flag = 1;
                        break;
                    }
                    i++;
                }
                if(flag == 0){
                    dialog.setMessage("Saving");
                    dialog.show();
                    count = (int)dataSnapshot.getChildrenCount();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
                    String today_date = df.format(new Date());
                    Log.e("TAG","date - "+today_date);
                    StatusUpdateDetails ad = new StatusUpdateDetails(j, total, vacant, venti, vacantVentilator,id+count ,name, phone, address, lat, lang,auth.getCurrentUser().getEmail(),state,city,today_date);
                    databaseReference.child(String.valueOf(id + count)).setValue(ad).
                            addOnCompleteListener(Status_Update.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Status_Update.this, "Data Saved Successful", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Intent in = new Intent(getApplicationContext(), Dashboard.class);
                                        startActivity(in);
                                    }
                                }
                            })
                            .addOnFailureListener(Status_Update.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Status_Update.this, "Data Saving Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                }
                else
                    Toast.makeText(Status_Update.this, "Already Exists", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
 public void newCode(){
          /*   hospitals = new ArrayList<>();
                  DatabaseReference db = databaseReference2.child(state).child(city);
                  db.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          hospitals.clear();
                          for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                              StatusUpdateDetails sd = mySnap.getValue(StatusUpdateDetails.class);
                              hospitals.add(sd);

                          }
                          Log.d("size", String.valueOf(hospitals.size()));
                          for (int m = 0; m < hospitals.size(); m++)
                              Log.d("HID", String.valueOf(hospitals.get(m).getKey()));
                          id_forward = hospitals.get(hospitals.size() - 1).getKey();
                          Log.e("Hospital_ID", String.valueOf(id_forward));

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
                  Log.e("Return_ID", String.valueOf(id_forward));*/
 }
    }
