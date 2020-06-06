package sidsim.techwarriors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText etPass, etEmail;
    ImageView imgEmail, imgPass;
    Button btnLogin, btnSignUp;
    TextView tvForget;
    FirebaseAuth auth;
    ProgressDialog dialog;
    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions gso;
    DatabaseReference databaseReference, databaseReference2;
    FirebaseUser user;
    int flag = 0;
    int flg=0;
    String email = "";
    List<RegistrationDetails> registrationDetails;
    List<StatusUpdateDetails> hospitalDetails;
    TextView hidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            permission();
        }
        setIds();
        if (user != null) {
          startActivity(new Intent(this,BasicDetails.class));
        }
        FirebaseApp.initializeApp(this);
    }

    private void setIds() {
        hidden = findViewById(R.id.hidden);
        etEmail = findViewById(R.id.username_input);
        etPass = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        imgEmail = findViewById(R.id.username_icon);
        imgPass = findViewById(R.id.icon);
        tvForget = findViewById(R.id.tvforget);
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        user = auth.getCurrentUser();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        registrationDetails = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("tblregister");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("tblhospitals");
        hospitalDetails = new ArrayList<>();

    }

    private void permission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "LOCATION PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage("Do you want to exit this application??");
        builder.setCancelable(false);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void forget(View view) {
        Toast.makeText(this, "Will be available soon", Toast.LENGTH_SHORT).show();
    }

    public void login(View view) {
        if (!(etEmail.length() == 0 || etPass.length() == 0)) {
            if (!(etPass.length() < 6))
                authenticate();
            else
                Toast.makeText(this, "Password Length more than 6", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Fields can't be left empty", Toast.LENGTH_SHORT).show();
    }

    private void authenticate() {
        email = etEmail.getText().toString();
        dialog.setMessage("Logging In");
        dialog.show();
        auth.signInWithEmailAndPassword(email.trim(), etPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   intent();
                    Toast.makeText(Login.this, "Successfully Login!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    etEmail.setText("");
                    etPass.setText("");
                    finish();
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Fail to Login " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                etEmail.setText("");
                etPass.setText("");
            }
        });
    }

    public void signup(View view) {
        startActivity(new Intent(this, Registration.class));
    }

    @Override
    public void onClick(View v) {
        Log.d("Foolish", "Hi");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 5678);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5678) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthWithG(accountTask);
        }

    }

    private void firebaseAuthWithG(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount acc = accountTask.getResult(ApiException.class);
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(acc);

        } catch (Exception e) {
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if (account == null)
            Toast.makeText(this, "No acc exists", Toast.LENGTH_SHORT).show();
        else {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                email = user.getEmail();
                                dbreg(email);
                                intent();
                            } else {
                                Log.w("TAG", "signInWithCredential:failure ", task.getException());
                                Toast.makeText(Login.this, "Authentication failed!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void intent() {
        getData();
        int flg = Integer.valueOf(hidden.getText().toString());
        Toast.makeText(this, "intent: "+flg, Toast.LENGTH_SHORT).show();
        if(flg ==1) {
            hidden.setText("0");
            startActivity(new Intent(this, Dashboard.class));
        }
        else {
            hidden.setText("0");
            startActivity(new Intent(this, BasicDetails.class));
        }
        finish();

    }

    private void dbreg(String email) {
        dialog.setMessage("Login");

        dialog.show();
        String key = databaseReference.push().getKey();
        RegistrationDetails ad = new RegistrationDetails("", email, "", key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registrationDetails.clear();
                int i = 0;
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    RegistrationDetails rd = mySnap.getValue(RegistrationDetails.class);
                    registrationDetails.add(rd);
                    if ((registrationDetails.get(i).getEmail().equals(email))) {
                        flag = 1;
                        break;
                    }
                    i++;
                }
                if (flag == 0) {
                    databaseReference.child(key).setValue(ad).
                            addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Data Saved Successful", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    }
                                }
                            })
                            .addOnFailureListener(Login.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Data Saving Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getData() {

        hospitalDetails = new ArrayList<>();
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalDetails.clear();
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    Toast.makeText(Login.this, String.valueOf(dataSnapshot.getChildrenCount()), Toast.LENGTH_SHORT).show();
                    Log.d("Key--1",String.valueOf(mySnap.getKey()));
                    DatabaseReference db = databaseReference2.child(mySnap.getKey());
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
                                            Log.d("Email---",email.trim());
                                            Log.d("Email2---",hospitalDetails.get(i).getEmail().trim());
                                            if (hospitalDetails.get(i).getEmail().trim().equals(email.trim())) {
                                                custom(1);
                                                break;
                                            }
                                            i++;

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                if(flg==1)
                                {
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    if(flg==1)
                    {
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void custom(int i) {
        flg = i;
    }
}
