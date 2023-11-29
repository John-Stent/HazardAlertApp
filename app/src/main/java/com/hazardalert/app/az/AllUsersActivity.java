package com.hazardalert.app.az;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView recycleView;

    DatabaseReference databaseReference;

    FirebaseAuth auth;
    CardView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
//        recycleView = findViewById(R.id.RecycleList);
////        back = findViewById(R.id.back);
//
//
//        recycleView.setHasFixedSize(true);
//        recycleView.setLayoutManager(new LinearLayoutManager(this));
//
//
//
//
//
//
//
//        Query conversationQuery = databaseReference.child("Users");
//
//
//        FirebaseRecyclerOptions<Users> options =
//                new FirebaseRecyclerOptions.Builder<Users>()
//                        .setQuery(conversationQuery, Users.class)
//                        .build();
//
//        FirebaseRecyclerAdapter friendsConvAdapter = new FirebaseRecyclerAdapter<Users, Viewholder>(options) {
//            @Override
//            public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.mainitemgrid, parent, false);
//
//                return new Viewholder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(Viewholder viewholder, int position, Users model) {
//
//                viewholder.nameTextview.setText(model.getName());
//                viewholder.emailTextView.setText(model.getEmail());
//
//
//
//                viewholder.nameTextview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//
//
//                    }
//                });
//
//
//
//
//            }
//        };
//
//
//        friendsConvAdapter.startListening();
//        recycleView.setAdapter(friendsConvAdapter);
//        friendsConvAdapter.startListening();
//
//
//

    }

//    static class Viewholder extends RecyclerView.ViewHolder {
//
//        TextView nameTextview, emailTextView;
//
//        CardView card_parent;
//        ImageView imageView;
//
//        public Viewholder(@NonNull View itemView)
//        {
//            super(itemView);
//
//            nameTextview = itemView.findViewById(R.id.nameTextview);
//            emailTextView = itemView.findViewById(R.id.emailTextView);
//            card_parent = itemView.findViewById(R.id.card_parent);
//            imageView = itemView.findViewById(R.id.imageViewId);
//
//
//
//        }
//    }

}