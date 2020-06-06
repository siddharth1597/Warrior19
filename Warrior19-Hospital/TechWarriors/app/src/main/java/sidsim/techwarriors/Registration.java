package sidsim.techwarriors;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    EditText etEmail,etPass,etRetype;
    AutoCompleteTextView etName;
    ImageView imgName,imgEmail , imgPass;
    Button btregister;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference;
    String[] hospitals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        setIds();
        hospitals = getResources().getStringArray(R.array.hospitals);
        setHospitalName();
    }

   private void setIds() {
        etEmail = findViewById(R.id.email_register);
        etName = findViewById(R.id.reg_name);
        etPass = findViewById(R.id.reg_pass);
        etRetype = findViewById(R.id.reg_retype_pass);
        imgName = findViewById(R.id.reg_name_icon);
        imgPass = findViewById(R.id.reg_pass_icon);
        imgEmail = findViewById(R.id.reg_email_icon);
        btregister = findViewById(R.id.reg_btn);
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblregister");
    }
    private void setHospitalName() {
        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,hospitals);
        etName.setAdapter(hospitalAdapter);
    }

    public void register(View view) {
        if(!(etName.length()==0 || etEmail.length()==0 || etPass.length()==0)){
            if(!(etPass.length() < 6)){
                if(etPass.getText().toString().equals(etRetype.getText().toString()))
                    authenticate();
                else{
                    Toast.makeText(this, "Password Reentered should be same", Toast.LENGTH_SHORT).show();
                    etPass.setText("");
                    etRetype.setText("");
                }
            }
            else
                Toast.makeText(this, "Password Length more than 6", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Fields can't be left empty", Toast.LENGTH_SHORT).show();
    }

    private void authenticate() {
        dialog.setMessage("Registering User");
        dialog.show();
        auth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            dbreg();
                            Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            etEmail.setText("");
                            etPass.setText("");
                            etName.setText("");
                            etRetype.setText("");
                        }

                    }

                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration.this, "Registration Failed!! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                etEmail.setText("");
                etPass.setText("");
                etName.setText("");
                etRetype.setText("");
            }
        });
    }

    private void dbreg() {
        String key = databaseReference.push().getKey();
        RegistrationDetails ad = new RegistrationDetails( etName.getText().toString(), etEmail.getText().toString(), etPass.getText().toString(),key);
        databaseReference.child(key).setValue(ad).
                addOnCompleteListener(Registration.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Data Saved Successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            etEmail.setText("");
                            etPass.setText("");
                            etName.setText("");
                            etRetype.setText("");
                        }
                    }
                })
                .addOnFailureListener(Registration.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registration.this, "Data Saving Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                        etName.setText("");
                        etPass.setText("");
                        etRetype.setText("");
                        dialog.dismiss();
                    }
                });

    }
}
