package com.soumit.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SOUMIT on 11/7/2017.
 */

public class ViewProfile extends AppCompatActivity{

    private static final String TAG = "ViewProfile";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    TextView tv_name, tv_email, tv_address;
    Button btnEditProfile;

    String glideImageUrl;

    ImageView propic;

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_address = (TextView) findViewById(R.id.tv_address);

        propic = (ImageView) findViewById(R.id.profile_picture);

        btnEditProfile = (Button) findViewById(R.id.edit_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        Toast.makeText(ViewProfile.this,
                "Current uid: " + uid, Toast.LENGTH_LONG).show();

                mDatabase.child("users")
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Patient user = dataSnapshot.getValue(Patient.class);

                        // Check for null
                        if (user == null) {
                            Log.e(TAG, "User data is null!");
                            return;
                        }

                        Log.d(TAG, "User data is changed!" +
                                user.name + ", " + user.email + "," + user.age + "," + user.address);

                        tv_name.setText(user.name);
                        tv_email.setText(user.email);
                        tv_address.setText(user.address);

                        glideImageUrl = user.imageUrl;

                        Glide.with(ViewProfile.this)
                                .load(glideImageUrl)
                                .into(propic);

//                        /**
//                         * Loading progressbar before loading image
//                         */
//                        progressBar.setVisibility(View.VISIBLE);
//
//                        Glide.with(ViewProfile.this)
//                                .load(glideImageUrl)
//                                .listener(new RequestListener<String, GlideDrawable>() {
//                                    @Override
//                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                        progressBar.setVisibility(View.GONE);
//                                        return false; // important to return false so the error placeholder can be placed
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(GlideDrawable resource,
//                                                                   String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                        progressBar.setVisibility(View.GONE);
//                                        return false;
//                                    }
//                                })
//                                .into(propic);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read user", error.toException());
                    }
                });


                btnEditProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ViewProfile.this, EditProfileAndUploadImage.class));
                    }
                });



    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//              Patient user = dataSnapshot.getValue(Patient.class);
//
//              tv_name.setText(user.name);
//              tv_email.setText(user.email);
//              tv_address.setText(user.address);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // [START_EXCLUDE]
//                Toast.makeText(ViewProfile.this, "Failed to load post.",
//                        Toast.LENGTH_SHORT).show();
//                // [END_EXCLUDE]
//            }
//        };
//        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(postListener);
//        // [END post_value_event_listener]

    }

}
