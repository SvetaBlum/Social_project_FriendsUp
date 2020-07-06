package com.example.ourappprogect.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourappprogect.R;
import com.example.ourappprogect.adapters.AdapterNotification;
import com.example.ourappprogect.models.ModelNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

   RecyclerView notificationsRv;

   private FirebaseAuth firebaseAuth;
   private ArrayList<ModelNotification> notificationslist;
   private AdapterNotification adapterNotification;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_notifications, container, false);

       notificationsRv= view.findViewById(R.id.notificationRv);

       firebaseAuth = FirebaseAuth.getInstance();
       getAllNotifications();
       return view;
    }

    private void getAllNotifications() {
        notificationslist =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationslist.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelNotification model = ds.getValue(ModelNotification.class);
                    notificationslist.add(model);
                }

                adapterNotification = new AdapterNotification(getActivity(),notificationslist);
                notificationsRv.setAdapter(adapterNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}