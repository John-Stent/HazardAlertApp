package com.hazardalert.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    CardView btnFlood,btnFire,btnTornado,btnEarth;
    AHBottomNavigation bottomNavigation;
    String role="user";
    TextView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnFlood =  findViewById(R.id.btnFlood);
        btnFire =  findViewById(R.id.btnFire);
        btnTornado =  findViewById(R.id.btnTornado);
        btnEarth =  findViewById(R.id.btnEarth);
        logout =  findViewById(R.id.logout);




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
        bottomNavigation.enableItemAtPosition(0);
        bottomNavigation.setCurrentItem(0);
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



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();

            }
        });




        if(FirebaseAuth.getInstance().getCurrentUser()==null){

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists()){
                    role = snapshot.child("role").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(role.equals("user")){
            btnFlood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(),Maps2Activity.class);
                    intent.putExtra("type","Flood");
                    startActivity(intent);


                }
            });
            btnFire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(),Maps2Activity.class);
                    intent.putExtra("type","Fire");
                    startActivity(intent);


                }
            });
            btnTornado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(),Maps2Activity.class);
                    intent.putExtra("type","Tornado");
                    startActivity(intent);


                }
            });
            btnEarth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(),Maps2Activity.class);
                    intent.putExtra("type","Earthquake");
                    startActivity(intent);


                }
            });

        }






    }
}