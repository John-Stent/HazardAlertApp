package com.hazardalert.app.az;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    EditText etEmail,etPassword;
    Button loginBtn;
    TextView user, officer,change;
    String who = "candidate";
    TextView forgotpassword;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        loginBtn = findViewById(R.id.loginBtn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        forgotpassword = findViewById(R.id.forgotpassword);

        user = findViewById(R.id.user);
        officer = findViewById(R.id.officer);
        who = "candidate";
        change = findViewById(R.id.change);




        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etEmail.getText().toString().isEmpty()){

                    if(isEmailValid(etEmail.getText().toString())){

                        progressDialog.show();

                        FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Password reset email sent",Toast.LENGTH_SHORT).show();
                                        }else{
                                            progressDialog.dismiss();

                                            Toast.makeText(getApplicationContext(),task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();

                    }

                }else{
                        Toast.makeText(getApplicationContext(),"Please Enter Your Email",Toast.LENGTH_SHORT).show();
                }

            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(who.equals("user")){
                    user.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.black_23));
                    user.setTextColor(Color.parseColor("#ffffff"));
                    officer.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
                    officer.setTextColor(Color.parseColor("#000000"));

                }else{

                    user.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.black_23));
                    user.setTextColor(Color.parseColor("#ffffff"));
//                    candidate.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
//                    candidate.setTextColor(Color.parseColor("#000000"));
                    officer.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
                    officer.setTextColor(Color.parseColor("#000000"));

                }
                who = "user";


            }
        });
        officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //candidate.setBackgroundResource(R.drawable.black_23);

                if(who.equals("officer")){

                    user.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
                    user.setTextColor(Color.parseColor("#000000"));

                    officer.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.black_23));
                    officer.setTextColor(Color.parseColor("#ffffff"));

                }else{

                    user.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
                    user.setTextColor(Color.parseColor("#000000"));



                    officer.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.black_23));
                    officer.setTextColor(Color.parseColor("#ffffff"));
//                    employer.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.back_outline));
//                    employer.setTextColor(Color.parseColor("#000000"));

                }
                who = "officer";
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        isReadStoragePermissionGranted();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText().toString().equals("")|| etPassword.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Username and Password can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    loginUser();
                }
            }

        });

    }

    private void loginUser()
    {
        final ProgressDialog progressDialog=ProgressDialog.show(LoginActivity.this, "Please Wait...", "Processing...",true);

        databaseReference=firebaseDatabase.getReference("users");
        firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString().trim(),etPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, ""+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            if(firebaseAuth.getCurrentUser()!=null){
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){

                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, ""+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }else{


                            }


                        }

                        else {
                            progressDialog.dismiss();

                            Toast.makeText(LoginActivity.this, "Logged In Successfully !", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();



                        }
                    }
                });
    }


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
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

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}