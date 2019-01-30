package ahmed.fciibrahem.helwan.edu.eg.zagal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ahmed.fciibrahem.helwan.edu.eg.zagal.Adapters.MessageAdapterr;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments.APIService;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.Chat;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Client;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Data;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.MyResponse;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Sender;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Token;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profle_chat_image;
    TextView ChatUSername;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;
    ImageButton btn_sent;
    EditText text_sent;
    MessageAdapterr messageAdapterr;
    List<Chat> chats;
    RecyclerView chatRecyclarView;
    ValueEventListener seenListener;
     String userid;
     APIService apiService;
     boolean notfiy=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        profle_chat_image=findViewById(R.id.profile_imageview);
        ChatUSername=findViewById(R.id.username);
        btn_sent=findViewById(R.id.btn_sent);
        text_sent=findViewById(R.id.txt_sent);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        apiService= Client.getclient("https://fcm.googleapis.com/").create(APIService.class);


        chatRecyclarView=findViewById(R.id.chats_recyclar_view);
        chatRecyclarView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclarView.setLayoutManager(linearLayoutManager);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        intent=getIntent();
        userid=intent.getStringExtra("userid");
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notfiy=true;
                String message=text_sent.getText().toString();
                if(!message.equals(""))
                {
                    SentMessage(firebaseUser.getUid(),userid,message);

                }
                else
                {
                    Toast.makeText(MessageActivity.this, "You Must sent any thing", Toast.LENGTH_SHORT).show();

                }
                text_sent.setText("");

            }
        });




        reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ChatUSername.setText(user.getUsername());
                if (user.getImageURl().equals("defualt"))
                {
                    profle_chat_image.setImageResource(R.mipmap.ic_launcher);

                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageURl()).into(profle_chat_image);
                }

                GetMessages(firebaseUser.getUid(),userid,user.getImageURl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        SeenMessage(userid);


    }
    private void SeenMessage(final String userid){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);




                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
     private  void SentMessage(String Sender, final String Reciver, String Message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",Sender);
        hashMap.put("reciver",Reciver);
        hashMap.put("Message",Message);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);
         final DatabaseReference chatref=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);
        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final  String message=Message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(notfiy){
                sentNtification(Reciver,user.getUsername(),message);}
                notfiy=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    private void  sentNtification(String receiver, final String username, final String message)
    { DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void GetMessages(final String myId, final String userId, final String urlimage)
    {
        chats=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(myId) &&chat.getSender().equals(userId) || chat.getReciver().equals(userId) &&chat.getSender().equals(myId))
                    {
                        chats.add(chat);
                    }
                }
                messageAdapterr=new MessageAdapterr(MessageActivity.this,chats,urlimage);

                chatRecyclarView.setAdapter(messageAdapterr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void CurrentUser(String userid)
    {
        SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();



    }
    private void status(String ststus)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",ststus);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        CurrentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(seenListener!=null) {
            reference.removeEventListener(seenListener);

        }status("offline");
        CurrentUser("none");
    }

}
