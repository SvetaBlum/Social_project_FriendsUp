package com.example.ourappprogect.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ourappprogect.AddPostActivity;
import com.example.ourappprogect.MainActivity;
import com.example.ourappprogect.R;
import com.example.ourappprogect.SettingsActivity;
import com.example.ourappprogect.adapters.AdapterPosts;
import com.example.ourappprogect.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;

    /////////////////////////
    RecyclerView recyclerView;
    List <ModelPost> postList;
    AdapterPosts adapterPosts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        /////////////
        recyclerView = view.findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //// show newest post
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);
        /// init post list
        postList = new ArrayList<>();
        loadPost();
        return view;
    }
    /////////
    private void loadPost() {
        // path of all post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){

                  ModelPost modelpost = ds.getValue(ModelPost.class);
                  postList.add(modelpost);

                    // adapter
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    /// set adapter recyclerview
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ///in case of error
                Toast.makeText(getActivity(), "" +error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private  void searchPosts(final String searchQuery){
        //// path of all post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())||
                            modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }

                    postList.add(modelPost);

                    // adapter
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    /// set adapter recyclerview
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ///in case of error
                Toast.makeText(getActivity(), "" +error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void CheckUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

            // p_profileTV.setText(user.getEmail());
        }else {

            startActivity(new Intent(getActivity() , MainActivity.class));
            getActivity().finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //show menu option in fragment
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //inflate menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        ////////////
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //////search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /// called when user press search btn
                if (!TextUtils.isEmpty(query)){

                    searchPosts(query);
                }else {

                    loadPost();
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /////////////// called as and when user press any letter

                if (!TextUtils.isEmpty(newText)){

                    searchPosts(newText);
                }else {

                    loadPost();
                }

                return false;
            }
        });




        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id== R.id.action_logout){

            firebaseAuth.signOut();
            CheckUserStatus();
        }
        else if (id== R.id.action_add_post){
               startActivity(new Intent(getActivity(), AddPostActivity.class));
        }
        else  if(id==R.id.action_settings){
            startActivity(new Intent(getActivity() , SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}