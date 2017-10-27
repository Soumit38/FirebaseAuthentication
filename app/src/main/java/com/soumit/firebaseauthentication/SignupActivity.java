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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.id.input;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by SOUMIT on 10/14/2017.
 */

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText inputEmail, inputPassword, inputConfPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    ImageView proPic;
    private String userId;

    private Spinner spinner;
    private static final String[]paths = {"Patient", "Doctor"};

    //Patient DB info
    private EditText inputNameDb, inputEmailDb, inputAgeDb, inputAddressDb;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

//        //extra validation
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//        if(currentUser != null)
//            userId = auth.getCurrentUser().getUid();
        //---------------------------------------------

        //Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_spinner_dropdown_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfPassword = (EditText) findViewById(R.id.confpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        proPic = (ImageView) findViewById(R.id.pro_pic);

        //Patient DB info
        inputNameDb = (EditText) findViewById(R.id.name);
        inputEmailDb = (EditText) findViewById(R.id.email);
        inputAgeDb = (EditText) findViewById(R.id.age);
        inputAddressDb = (EditText) findViewById(R.id.address);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        /*Loading profile picture*/
/*

        Glide.with(SignupActivity.this)
                .load("")
                .override(100,100)
                .into(proPic);

*/

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
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
                String confpass = inputConfPassword.getText().toString().trim();

                final String name = inputNameDb.getText().toString().trim();
                String emailDb = inputEmailDb.getText().toString().trim();
                final String age = inputAgeDb.getText().toString().trim();
                final String address = inputAddressDb.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isValidEmailAddress(email)){
                    Toast.makeText(getApplicationContext(), "Email address not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(confpass) || !password.equals(confpass) ){
                    Toast.makeText(getApplicationContext(), "Confirm password! ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }



                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete: " +
                                //  task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                //Toast.makeText(SignupActivity.this, auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);

                                if(!task.isSuccessful()){
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(SignupActivity.this,
                                                "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else {
                                        Toast.makeText(SignupActivity.this, "Authentication failed." +
                                                task.getException(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }else {

//                                    createUser(name, email, age, address);

                                    /*Email verification*/
                                    sendVerificationEmail(name, email, age, address);
                                    progressBar.setVisibility(View.GONE);

//                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }

                            }
                        });


//                // Check for already existed userId
//                if (TextUtils.isEmpty(userId)) {
//                    createUser(name, email, age, address);
//                } else {
//                    updateUser(name, email, age, address);
//                }

            }
        });

    }


    /* Checking if email address is valid or not */

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /* Email verification method */

    private void sendVerificationEmail(final String name, final String email, final String age, final String address)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent

                            createUser(name, email, age, address);

                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    //



    //Patient DB info

    private void createUser(String name, String email, String age, String address) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
//            userId = mFirebaseDatabase.push().getKey();

//            userId = auth.getCurrentUser().getUid();
                    //extra validation
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null)
            userId = auth.getCurrentUser().getUid();
        }

        Patient user = new Patient(name, email, age, address);

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
                Patient user = dataSnapshot.getValue(Patient.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email + "," + user.age + "," + user.address);


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

    private void updateUser(String name, String email, String age, String address) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userId).child("email").setValue(email);

        if(!TextUtils.isEmpty(age))
            mFirebaseDatabase.child(userId).child("age").setValue(age);

        if(!TextUtils.isEmpty(address))
            mFirebaseDatabase.child(userId).child("address").setValue(address);
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

                break;
            case 1:
                startActivity(new Intent(SignupActivity.this, DoctorSignup.class));
//                finish();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}









