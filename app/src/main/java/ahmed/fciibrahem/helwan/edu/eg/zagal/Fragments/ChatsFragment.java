package ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ahmed.fciibrahem.helwan.edu.eg.zagal.Adapters.UsersAdapter;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.Chat;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.ChatList;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Token;
import ahmed.fciibrahem.helwan.edu.eg.zagal.R;


public class ChatsFragment extends Fragment {
    private UsersAdapter usersAdapter;
    private RecyclerView chatsRcycalrView;
    private List<User> users;
    private List<ChatList> usersList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        chatsRcycalrView = view.findViewById(R.id.chats_recyclar_view);
        chatsRcycalrView.setHasFixedSize(true);
        chatsRcycalrView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ChatList chatList=snapshot.getValue(ChatList.class);
                    usersList.add(chatList);


                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateToken(FirebaseInstanceId.getInstance().getToken());






//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                usersList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getSender().equals(firebaseUser.getUid())) {
//                        usersList.add(chat.getReciver());
//                    }
//                    if (chat.getReciver().equals(firebaseUser.getUid())) {
//                        usersList.add(chat.getSender());
//                    }
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        return view;


    }
    private void UpdateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);



    }

    private void chatList() {
        users=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);
                    for (ChatList chatList:usersList){
                        assert user != null;
                        if(user.getId().equals(chatList.getId())){
                            users.add(user);
                        }

                    }

                }
                usersAdapter=new UsersAdapter(getContext(),users,true);
                chatsRcycalrView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}


//    private void GetChats()
//    {
//        users=new ArrayList<>();
//        reference=FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                    users.clear();
//                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
//                        User user = snapshot.getValue(User.class);
//                        Log.i("chat fag" , "f3ind id  " + user.getId());
//                        for (String id : usersList) {
//                            Log.i("chat fag" , "id  " + id);
//                            if(user.getId().equals(id)){
//                                Log.i("chat fag" , "== fnd f3nd" );
////                                if(users.size() != 0){
//                                    Log.i("chat fag" , "f3nd lst > 0");
//                                    int flag=0;
//                                    for (User user1 : users){
//                                        Log.i("chat fag" , "xist  " + user1.getId());
//                                        if(user.getId().equals(user1.getId())){
//                                           // users.add(user);
//                                            flag=1;
//                                            Log.d("chat fag", "not "+user);
//
//                                        }
//
//                                    }
//                                    if(flag==0)
//                                    {
//                                        Log.d("chat fag", "n f flag");
//
//                                        users.add(user);
//                                    }
//
//
//
////                                }
////                                else{
//////                                    users.add(user);
////                                }
//
//                            }
//                        }
//
//                    }
//
//                    Log.i("tttl usrrss",""+users);
//                usersAdapter=new UsersAdapter(getContext(),users);
//                chatsRcycalrView.setAdapter(usersAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }