package com.hazardalert.app.az;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hazardalert.app.az.SendNotificationPack.Token;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword,editTextcPassword;
    public Button UserRegisterBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    LinearLayout login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init Views
        editTextEmail = findViewById(R.id.emailRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        editTextcPassword= findViewById(R.id.confirmPassword);
        UserRegisterBtn= findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        login = findViewById(R.id.login);
        //Firebase authentication
        mAuth = FirebaseAuth.getInstance();


        UserRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check email and password.Then sign in
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));

                registerUser();
            }
        });
        //Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login screen

                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (mAuth.getCurrentUser() != null) {
//
//        }
    }


    private void registerUser() {

        // get text from fields
        final String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextcPassword.getText().toString().trim();
        //check email
        if (email.isEmpty()) {
            editTextEmail.setError("It's empty");
            editTextEmail.requestFocus();
            return;
        }



        //check email. Is it valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Not a valid email");
            editTextEmail.requestFocus();
            return;
        }

        // check password
        if (password.isEmpty()) {
            editTextPassword.setError("Its empty");
            editTextPassword.requestFocus();
            return;
        }

        //Check password lenght
        if (password.length() < 6) {
            editTextPassword.setError("Less length");
            editTextPassword.requestFocus();
            return;
        }
        //match password
        if(!password.equals(cpassword)){
            editTextcPassword.setError("Password Donot Match");
            editTextcPassword.requestFocus();
            return;
        }




        //show loading
        progressBar.setVisibility(View.VISIBLE);

        //create user in database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            //add email in user model class
                            final User user = new User(email);
                            FirebaseUser usernameinfirebase = mAuth.getCurrentUser();

                            //get registered email from database
                            String UserID=usernameinfirebase.getEmail();
                            String resultemail = UserID.replace(".","");

                            //upload user details
                            FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("UserDetails").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        UpdateToken();
                                        SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("email",email);
                                        editor.putString("password",password);
                                        editor.commit();
                                        //move to main screen
                                        Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
        FirebaseDatabase.getInstance().getReference("AdminTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }



}
