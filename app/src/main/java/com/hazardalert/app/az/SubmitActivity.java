package com.hazardalert.app.az;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SubmitActivity extends AppCompatActivity {

    ImageView pickImage;
    String profile_link;
    EditText etRoute;
    Button submitBtn;
    TextView etDepartureDate;
    boolean imagePicked = false;

    String route ;
    Context context;
    FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    public Uri ImageUri;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String name;

    FirebaseFirestore db;

    ProgressDialog pd2;
    final Calendar myCalendar= Calendar.getInstance();


    String lat,lng;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);


        pd2=new ProgressDialog(this);


        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        type = getIntent().getStringExtra("type");

        Log.e("poii", "onCreate: "+ lng);

        db = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(context);
        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        auth = FirebaseAuth.getInstance();

        pickImage = findViewById(R.id.pickImage);


        etRoute = findViewById(R.id.etComments);



        submitBtn = findViewById(R.id.submitBtn);


        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(auth.getCurrentUser().getUid()!=null ){


                    FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                if (imagePicked) {

                                    route = etRoute.getText().toString();

                                    if (!route.isEmpty()){

                                        Toast.makeText(SubmitActivity.this, "Saving list...", Toast.LENGTH_SHORT).show();

                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                addListToFirebase();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(SubmitActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                                    }
                                }else{

                                   Toast.makeText(context, "Please select Image", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"no user login",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    private void choosePic()
    {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data)
    {
        super.onActivityResult(requestcode,resultcode,data);

        if (requestcode==1 && resultcode==RESULT_OK && data!=null && data.getData()!=null)
        {
            ImageUri= data.getData();
            pickImage.setImageURI(ImageUri);
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
                                Toast.makeText(SubmitActivity.this, "Picture uploaded !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(SubmitActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
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


    public String geoHashFromLoc(double lat, double lng){


        Location location = new Location("") ;
        location.setLatitude(lat);
        location.setLongitude(lng);
        GeoHash geoHash = GeoHash.fromLocation(location, 1);//Radius


        return geoHash.toString();



    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private void addListToFirebase(){


        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Saving Date");
        pd.setMessage("Please wait..");

        pd.show();


        //check
        db.collection("hazardsReported").whereEqualTo("reason",route).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int size = queryDocumentSnapshots.getDocuments().size();
                if(size>=1){
                    for(DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){

                        String la = d.get("lat").toString();
                        String ln = d.get("lng").toString();
                        String timeDatabase = d.get("time").toString();
                        double dist = distance(Double.parseDouble(la),Double.parseDouble(ln),Double.parseDouble(lat),Double.parseDouble(lng));

                        long time= System.currentTimeMillis();
                        long timeDB = Long.parseLong(timeDatabase);
                        if(dist>=200){   // 200 km
                            // add new entry

                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseDatabase=FirebaseDatabase.getInstance();



                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());

                            Map<String, Object> stationDoc = new HashMap<>();
                            stationDoc.put("pic", profile_link);
                            stationDoc.put("date", currentDateandTime);
                            stationDoc.put("reason", route);
                            stationDoc.put("type", type);

                            stationDoc.put("timeStamp", System.currentTimeMillis());
                            stationDoc.put("userid", auth.getCurrentUser().getUid());
                            stationDoc.put("lat", lat);
                            stationDoc.put("lng", lng);
                            stationDoc.put("number", "1");
                            stationDoc.put("geoHash", geoHashFromLoc(Double.parseDouble(lat),Double.parseDouble(lng)));

                            stationDoc.put("time",String.valueOf(System.currentTimeMillis()));


                            db.collection("hazardsReported").document().set(stationDoc, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Failed to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();

                                        }
                                    });

                        }else if((time-timeDB)>= Math.abs(86400000) ){ //1 day difference

                            //add new entry

                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseDatabase=FirebaseDatabase.getInstance();



                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());

                            Map<String, Object> stationDoc = new HashMap<>();
                            stationDoc.put("pic", profile_link);
                            stationDoc.put("date", currentDateandTime);
                            stationDoc.put("reason", route);
                            stationDoc.put("type", type);

                            stationDoc.put("timeStamp", System.currentTimeMillis());
                            stationDoc.put("userid", auth.getCurrentUser().getUid());
                            stationDoc.put("lat", lat);
                            stationDoc.put("lng", lng);
                            stationDoc.put("number", "1");
                            stationDoc.put("geoHash", geoHashFromLoc(Double.parseDouble(lat),Double.parseDouble(lng)));

                            stationDoc.put("time",String.valueOf(System.currentTimeMillis()));


                            db.collection("hazardsReported").document().set(stationDoc, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Failed to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();

                                        }
                                    });

                        }else{

                            //ecxxisting

                            int number = Integer.parseInt(d.get("number").toString());

                            Map<String, Object> stationDoc = new HashMap<>();
                            stationDoc.put("number", String.valueOf(number+1));




                            db.collection("hazardsReported").document(d.getId()).set(stationDoc, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd2.dismiss();

                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(SubmitActivity.this, "Failed to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();
                                        }
                                    });
                        }

                    }

                }else{


                    //add new entry no record exist

                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseDatabase=FirebaseDatabase.getInstance();



                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    Map<String, Object> stationDoc = new HashMap<>();
                    stationDoc.put("pic", profile_link);
                    stationDoc.put("date", currentDateandTime);
                    stationDoc.put("reason", route);
                    stationDoc.put("type", type);

                    stationDoc.put("timeStamp", System.currentTimeMillis());
                    stationDoc.put("userid", auth.getCurrentUser().getUid());
                    stationDoc.put("lat", lat);
                    stationDoc.put("lng", lng);
                    stationDoc.put("number", "1");
                    stationDoc.put("geoHash", geoHashFromLoc(Double.parseDouble(lat),Double.parseDouble(lng)));

                    stationDoc.put("time",String.valueOf(System.currentTimeMillis()));


                    db.collection("hazardsReported").document().set(stationDoc, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    pd.dismiss();
                                    Toast.makeText(SubmitActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    pd.dismiss();
                                    Toast.makeText(SubmitActivity.this, "Failed to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                            }
                            )
                    ;
                }
            }
        }
        );

    }

}