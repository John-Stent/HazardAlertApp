package com.hazardalert.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hazardalert.app.az.SendNotificationPack.Data;
import com.hazardalert.app.az.SendNotificationPack.FcmNotificationsSender;
import com.hazardalert.app.az.SendNotificationPack.NotificationSender;

import java.util.HashMap;
import java.util.Map;

public class AdminReportsListActivity extends AppCompatActivity {

    AHBottomNavigation bottomNavigation;
    FirestoreRecyclerAdapter adapter;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String role ="user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //naviga.,

        if(FirebaseAuth.getInstance().getCurrentUser()==null){

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

        }


        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                role = snapshot.child("role").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bnve);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Hazards", R.drawable.hazard, R.color.teal_200);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Users", R.drawable.ic_baseline_perm_identity_24, R.color.teal_200);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Reports", R.drawable.report, R.color.teal_200);

        int qq = getResources().getDimensionPixelSize(R.dimen._9sdp);
        int qqa = getResources().getDimensionPixelSize(R.dimen._10sdp);

        bottomNavigation.setTitleTextSize(qqa,qq);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        progressBar = findViewById(R.id.progressBar);
        bottomNavigation.setVisibility(View.VISIBLE);


        bottomNavigation.setAccentColor(Color.parseColor("#38A1F3"));
        bottomNavigation.setInactiveColor(Color.parseColor("#5F7586"));
        bottomNavigation.enableItemAtPosition(2);
        bottomNavigation.setCurrentItem(2);
        bottomNavigation.setForceTint(true);

        bottomNavigation.setNotificationBackgroundColor(getResources().getColor(R.color.teal_200));
        bottomNavigation.setNotificationBackgroundColorResource(R.color.teal_200);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                switch (position){
                    case 0:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),AdminAllUsersActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AdminReportsListActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        break;
                }

                return false;
            }
        });


        TextView nodate = findViewById(R.id.noData);

        Query query = FirebaseFirestore.getInstance().collection("hazardsReported");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size  =queryDocumentSnapshots.getDocuments().size();
                if(size==0){

                    progressBar.setVisibility(View.GONE);
                    nodate.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.GONE);
                    nodate.setVisibility(View.GONE);
                }
            }
        });


        FirestoreRecyclerOptions<Hazards> options = new FirestoreRecyclerOptions.Builder<Hazards>()
                .setQuery(query, Hazards.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Hazards, TransactionsViewHolder>(options) {
            @Override
            public void onBindViewHolder(TransactionsViewHolder holder, int position, Hazards model) {

                progressBar.setVisibility(View.GONE);
                nodate.setVisibility(View.GONE);

                if(role.equals("user")){
                    holder.llofficer.setVisibility(View.GONE);
                }else{
                    holder.llofficer.setVisibility(View.VISIBLE);

                }


                if(model.getStatus()!=null){

                    holder.reject.setText("Rejected");
                }

                holder.reasonTextview.setText(model.getReason());
                holder.locTextView.setText("Location: "+model.getLat() +","+model.getLat());
                holder.numberTextView.setText("Number: "+model.getNumber());
                holder.timeTextView.setText("Time: "+model.getDate());



                holder.notify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        progressDialog.show();

                        FirebaseDatabase.getInstance().getReference().child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    try {
                                        Thread.sleep(500);

                                        String to = snapshot.child("token").getValue().toString();


                                        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                for (DataSnapshot dataSnapshot1 :snapshot.getChildren()){

                                                    String geeoHash = dataSnapshot1.child("geoHash").getValue().toString();
                                                    if(geeoHash.equals(model.getGeoHash())){

                                                        sendNotifications(to,"Hazard Alert",model.getReason());

                                                        Log.e("Hello", "onDataChange:" +to );



                                                    }
                                                }




                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Notification sent",Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TAG", "onCancelled", databaseError.toException());
                            }
                        });


                    }
                });


                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Map<String, Object> stationDoc = new HashMap<>();
                        stationDoc.put("status", "rejected");


                        FirebaseFirestore.getInstance().collection("hazardsReported").document(getSnapshots().getSnapshot(position).getId()).set(stationDoc, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


                                Toast.makeText(AdminReportsListActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });




            }

            @Override
            public TransactionsViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout called R.layout.message for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_harzards, group, false);

                return new TransactionsViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
    public static class TransactionsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        RelativeLayout rl;
        TextView reasonTextview,numberTextView,locTextView,timeTextView,reject, notify;
        ImageView copy;
        LinearLayout llofficer;

        public TransactionsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            reasonTextview = mView.findViewById(R.id.reasonTextview);
            numberTextView = mView.findViewById(R.id.numberTextView);
            locTextView = mView.findViewById(R.id.locTextView);
            timeTextView = mView.findViewById(R.id.timeTextView);
            reject = mView.findViewById(R.id.reject);
            notify = mView.findViewById(R.id.notify);
            llofficer = mView.findViewById(R.id.llofficer);

        }

    }



    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);

        NotificationSender sender = new NotificationSender(data, usertoken);




        FcmNotificationsSender fcmNotificationsSender  =new FcmNotificationsSender(usertoken,title,message,getApplicationContext(),AdminReportsListActivity.this);


        fcmNotificationsSender.SendNotifications();

    }

}