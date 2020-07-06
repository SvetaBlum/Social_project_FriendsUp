package com.example.ourappprogect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourappprogect.MainActivity;
import com.example.ourappprogect.R;
import com.example.ourappprogect.SettingsActivity;
import com.example.ourappprogect.adapters.AdapterChatlist;
import com.example.ourappprogect.adapters.AdapterUsers;
import com.example.ourappprogect.models.ModelChat;
import com.example.ourappprogect.models.ModelChatlist;
import com.example.ourappprogect.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.recyclerView);
        chatlistList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              userList  = new ArrayList<>();
              reference = FirebaseDatabase.getInstance().getReference("Users");
              reference.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      userList.clear();
                      for(DataSnapshot ds: snapshot.getChildren()){
                          ModelUser user = ds.getValue(ModelUser.class);
                          for (ModelChatlist chatlist: chatlistList){
                              if(user.getUid() != null && user.getUid().equals(chatlist.getId())){
                                  userList.add(user);
                                  break;
                              }
                          }
                          //adapter
                          adapterChatlist = new AdapterChatlist(getContext(),userList);
                          recyclerView.setAdapter(adapterChatlist);
                          for(int i=0;i<userList.size();i++){
                              lastMessage(userList.get(i).getUid());
                          }
                      }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
            }
        });

        return view;
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thelastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender==null || receiver==null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentUser.getUid())&& chat.getSender().equals(userId) || chat.getReceiver().equals(userId)&& chat.getSender().equals(currentUser.getUid())){
                        thelastMessage = chat.getMessage();


                    }
                }
                adapterChatlist.setLastMessageMap(userId,thelastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for (ModelChatlist chatlist:chatlistList) {
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())) {
                            userList.add(user);
                            break;
                        }
                    }
                    adapterChatlist = new AdapterChatlist(getContext(),userList);
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for (int i=0;i<userList.size();i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        //hide addpost icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id== R.id.action_logout){

            firebaseAuth.signOut();
            CheckUserStatus();
        }
        else  if(id==R.id.action_settings){
            startActivity(new Intent(getActivity() , SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}