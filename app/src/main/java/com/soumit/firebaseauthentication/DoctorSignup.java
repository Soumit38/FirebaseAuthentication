package com.soumit.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.soumit.firebaseauthentication.R.id.address;
import static com.soumit.firebaseauthentication.R.id.age;

/**
 * Created by SOUMIT on 10/20/2017.
 */

public class DoctorSignup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = DoctorSignup.class.getSimpleName();
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    ImageView proPic;
    private String userId;

    private Spinner spinner;
    private static final String[]paths = {"Doctor", "Patient"};

    //Patient DB info
    private EditText inputNameDb, inputEmailDb, inputAddressDb, inputQualificationsDb;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_signup);

        auth = FirebaseAuth.getInstance();

//        //validation
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//        if(currentUser != null)
//            userId = auth.getCurrentUser().getUid();

        //Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DoctorSignup.this,
                android.R.layout.simple_spinner_dropdown_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        proPic = (ImageView) findViewById(R.id.pro_pic);

        //Patient DB info
        inputNameDb = (EditText) findViewById(R.id.name);
        inputEmailDb = (EditText) findViewById(R.id.email);
        inputQualificationsDb = (EditText) findViewById(R.id.qualifications);
        inputAddressDb = (EditText) findViewById(address);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("doctors");


        /*Loading profile picture*/
/*

        Glide.with(DoctorSignup.this)
                .load("")
                .override(100,100)
                .into(proPic);

*/

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DoctorSignup.this, ResetPasswordActivity.class));
//            }
//        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                final String name = inputNameDb.getText().toString().trim();
                String emailDb = inputEmailDb.getText().toString().trim();
                final String qualifications = inputQualificationsDb.getText().toString().trim();
                final String address = inputAddressDb.getText().toString().trim();
                final String verificationCode = "1234";

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

//                // Check for already existed userId
//                if (TextUtils.isEmpty(userId)) {
//                    createUser(name, email, address, qualifications, verificationCode);
//                } else {
//                    updateUser(name, email, address, qualifications, verificationCode);
//                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(DoctorSignup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(DoctorSignup.this, "createUserWithEmail:onComplete: " +
                                //  task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(DoctorSignup.this, auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if(!task.isSuccessful()){
                                    Toast.makeText(DoctorSignup.this, "Authentication failed." +
                                            task.getException(), Toast.LENGTH_SHORT).show();
                                }else {
                                    createUser(name, email, address, qualifications, verificationCode);
                                    startActivity(new Intent(DoctorSignup.this, MainActivity.class));
                                    finish();
                                }

                            }
                        });
            }
        });

    }

    //Patient DB info

    private void createUser(String name, String email, String address,
                            String qualifications, String verificationCode) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
//        if (TextUtils.isEmpty(userId)) {
////            userId = mFirebaseDatabase.push().getKey();
//            userId = auth.getCurrentUser().getUid();
//        }

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null)
            userId = auth.getCurrentUser().getUid();



        Doctor user = new Doctor(name, email, address, qualifications, verificationCode);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Doctor user = dataSnapshot.getValue(Doctor.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email +
                         "," + user.address + ", " + user.qualifications + ", " + user.verificationCode);


                // clear edit text
                inputEmail.setText("");
                inputNameDb.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, String email, String address,
                            String qualifications, String verificationCode) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userId).child("email").setValue(email);

        if(!TextUtils.isEmpty(address))
            mFirebaseDatabase.child(userId).child("address").setValue(address);

        if(!TextUtils.isEmpty(qualifications)) {
            mFirebaseDatabase.child(userId).child("qualifications").setValue(qualifications);
        }
    }



//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = auth.getCurrentUser();
//        String userKey = currentUser.getUid();
//        Log.d("User Key : ", userKey);
//    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position){

            case 0:

//                finish();
                break;
            case 1:
                startActivity(new Intent(DoctorSignup.this, SignupActivity.class));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}