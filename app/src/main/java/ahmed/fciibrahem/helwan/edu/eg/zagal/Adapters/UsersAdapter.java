package ahmed.fciibrahem.helwan.edu.eg.zagal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ahmed.fciibrahem.helwan.edu.eg.zagal.MessageActivity;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.Chat;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import ahmed.fciibrahem.helwan.edu.eg.zagal.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    Context context;
    List<User> users;
    private boolean isChat;
    String thelastMessage;

    public UsersAdapter(Context context,List<User> users,boolean isChat)
    {
        this.context=context;
        this.users=users;
        this.isChat=isChat;


    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.user_item,viewGroup,false);
        return new UsersAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        final User user=users.get(i);
        userViewHolder.username.setText(user.getUsername());
        if(user.getImageURl().equals("defualt"))
        {
            userViewHolder.profleimage.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(context).load(user.getImageURl()).into(userViewHolder.profleimage);

        }
        if(isChat){
            getLastMessage(user.getId(),userViewHolder.Last_message);

        }else {
            userViewHolder.Last_message.setVisibility(View.GONE);
        }
        if(isChat){
            if(user.getStatus().equals("online")){
                userViewHolder.imageOn.setVisibility(View.VISIBLE);
                userViewHolder.ImageOff.setVisibility(View.GONE);


            }else {
                userViewHolder.imageOn.setVisibility(View.GONE);
                userViewHolder.ImageOff.setVisibility(View.VISIBLE);

            }

        }else {

            userViewHolder.imageOn.setVisibility(View.GONE);
            userViewHolder.ImageOff.setVisibility(View.GONE);
        }


        userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+user.getUsername(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder
    {

      public   TextView username;
      public   CircleImageView profleimage;
      private ImageView imageOn,ImageOff;
      public TextView Last_message;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            profleimage=itemView.findViewById(R.id.proifle_image);
            imageOn=itemView.findViewById(R.id.img_on);
            ImageOff=itemView.findViewById(R.id.img_off);
            Last_message=itemView.findViewById(R.id.last_message);


        }




    }
    private void getLastMessage(final String userid, final TextView lastmassag){
        thelastMessage="defualt";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(firebaseUser !=null){
                    if(chat.getReciver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)
                            ||chat.getReciver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid())){
                        thelastMessage=chat.getMessage();

                    }}


                }
                switch (thelastMessage){
                    case "defualt" :
                        lastmassag.setText("No messages");
                        break;
                        default:
                            lastmassag.setText(thelastMessage);
                            break;


                }
                thelastMessage="defualt";



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
