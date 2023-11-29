package com.hazardalert.app.az;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hazardalert.app.az.SendNotificationPack.Token;

import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {

    public Uri ImageUri;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    EditText etEmail, etPassword, etName, etProfession, etShort,etlocation;
    Button RegisterBtn;
    TextView pickImage;
    boolean imagePicked = false;
    FirebaseStorage storage;
    private StorageReference storageReference;
    String profile_link = "";
    TextView user, officer, change;
    String who = "user";
    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        isReadStoragePermissionGranted();
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");

        RegisterBtn = findViewById(R.id.RegisterBtn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etlocation = findViewById(R.id.etlocation);

        etName = findViewById(R.id.etName);
        etProfession = findViewById(R.id.etProfession);
        etShort = findViewById(R.id.etShort);
        pickImage = findViewById(R.id.pickImage);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        change = findViewById(R.id.change);

        user = findViewById(R.id.user);
        officer = findViewById(R.id.officer);
        who = "user";

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        etlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Maps3Activity.class));


            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (who.equals("user")) {
                    user.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.black_23));
                    user.setTextColor(Color.parseColor("#ffffff"));
                    officer.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
                    officer.setTextColor(Color.parseColor("#000000"));

                } else {

                    user.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.black_23));
                    user.setTextColor(Color.parseColor("#ffffff"));
//                    candidate.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
//                    candidate.setTextColor(Color.parseColor("#000000"));
                    officer.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
                    officer.setTextColor(Color.parseColor("#000000"));

                }
                who = "user";


            }
        });
        officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //candidate.setBackgroundResource(R.drawable.black_23);

                if (who.equals("officer")) {

                    user.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
                    user.setTextColor(Color.parseColor("#000000"));

                    officer.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.black_23));
                    officer.setTextColor(Color.parseColor("#ffffff"));

                } else {

                    user.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
                    user.setTextColor(Color.parseColor("#000000"));


                    officer.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.black_23));
                    officer.setTextColor(Color.parseColor("#ffffff"));
//                    officer.setBackground(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.back_outline));
//                    officer.setTextColor(Color.parseColor("#000000"));

                }
                who = "officer";
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signin();
            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });


    }


    public void signin() {


        if(etName.getText().toString().isEmpty()) etName.setError("Require!");
        else if(etEmail.getText().toString().isEmpty()) etEmail.setError("Require!");
        else if(etProfession.getText().toString().isEmpty()) etProfession.setError("Require!");
        else if(etShort.getText().toString().isEmpty()) etShort.setError("Require!");
        else if(etPassword.getText().toString().isEmpty()) etPassword.setError("Require!");
        //else if(profile_link.equals("")) Toast.makeText(getApplicationContext(), "Profile photo require!", Toast.LENGTH_LONG).show();
        else if(lat==null){

            Toast.makeText(this, "Please location ", Toast.LENGTH_SHORT).show();
        }
        else {
            registerUser();
        }



//        if (profile_link.equals("") || etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("") || etName.getText().toString().equals("") || etProfession.getText().toString().equals("") || etShort.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), "Please Enter All Details", Toast.LENGTH_LONG).show();
//            return;
//        } else {
//
//        }

    }


    private void registerUser() {
        ProgressDialog progressDialog = ProgressDialog.show(RegistrationActivity.this, "Please Wait...", "Processing...", true);


        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {

                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                } else {
                    addDataToFirebase(progressDialog);

                }
            }
        });


    }

    private void addDataToFirebase(ProgressDialog progressDialog) {
        String userID = FirebaseAuth.getInstance().getUid();
        databaseReference = firebaseDatabase.getReference("users").child(userID);


        databaseReference.child("profileImageUrl").setValue(profile_link);
        databaseReference.child("about").setValue(etShort.getText().toString());
        databaseReference.child("email").setValue(etEmail.getText().toString());
        databaseReference.child("fullName").setValue(etName.getText().toString());
        databaseReference.child("phone").setValue("unknown");
        databaseReference.child("profession").setValue(etProfession.getText().toString());
        databaseReference.child("role").setValue(who);
        databaseReference.child("status").setValue("live");
        databaseReference.child("geoHash").setValue(geoHashFromLoc(Double.parseDouble(lat),Double.parseDouble(lng)));
        databaseReference.child("userId").setValue(userID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                UpdateToken();
                progressDialog.dismiss();

                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();




            }
        });


        Toast.makeText(this, "Successfully Registered !", Toast.LENGTH_SHORT).show();

    }


    private void choosePic() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if (requestcode == 1 && resultcode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();

            // pickImage.setImageURI(ImageUri);

            uploadPic();

        }
    }

    private void uploadPic() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading picture...");
        pd.show();

        final String randomkey = UUID.randomUUID().toString();
        final StorageReference ref = storageReference.child("images/" + randomkey);
        ref.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                pd.dismiss();
                                imagePicked = true;
                                profile_link = downloadUrl.toString();
                                Toast.makeText(RegistrationActivity.this, "Picture uploaded !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred()
                                / snapshot.getTotalByteCount());

                        pd.setMessage("Percentage: " + (int) progressPercent + "%");

                    }
                });
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    }
                    //startLocationUpdates();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    // =========get geohash code============
    public String geoHashFromLoc(double lat, double lng){


        Location location = new Location("") ;
        location.setLatitude(lat);
        location.setLongitude(lng);
        GeoHash geoHash = GeoHash.fromLocation(location, 1);//Radius


        return geoHash.toString();



    }



    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        // String refreshToken= FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    String token = task.getResult();
                    if(firebaseUser!=null){

                        updateToken(token);

                    }

                }
            }
        });
//        Token token= new Token(refreshToken);
//        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
//
    }
    private void updateToken(String refreshToken){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Token token1= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }



}