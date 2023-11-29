package com.hazardalert.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminAllUsersActivity extends AppCompatActivity {


    AHBottomNavigation bottomNavigation;
    FirebaseRecyclerAdapter adapter;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_users);




        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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

        bottomNavigation.setVisibility(View.VISIBLE);


        bottomNavigation.setAccentColor(Color.parseColor("#38A1F3"));
        bottomNavigation.setInactiveColor(Color.parseColor("#5F7586"));
        bottomNavigation.enableItemAtPosition(1);
        bottomNavigation.setCurrentItem(1);
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

                return true;
            }
        });

        //========================

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");



        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(databaseReference, Users.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Users, Viewholder>(options) {
            @NonNull
            @Override
            public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

                // attach layout to RecyclerView
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);

                return new Viewholder(view);
            }

            @Override
            protected void onBindViewHolder(Viewholder holder, int position, Users model) {
                //progress_bar.setVisibility(View.GONE);

                holder.nameTextview.setText("Name: "+model.getFullName());
                holder.emailTextView.setText("Email: "+model.getEmail());





            }
        };


        adapter.startListening();
        // add adapter to recyclerview

        recyclerView.setAdapter(adapter);



    }


    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView nameTextview, emailTextView;
        CardView card_parent;
        ImageView imageView;

        public Viewholder(@NonNull View itemView)
        {
            super(itemView);

            nameTextview = itemView.findViewById(R.id.nameTextview);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            card_parent = itemView.findViewById(R.id.card_parent);
            imageView = itemView.findViewById(R.id.imageViewId);



        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}