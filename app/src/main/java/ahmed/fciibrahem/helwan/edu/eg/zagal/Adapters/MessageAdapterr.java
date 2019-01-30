package ahmed.fciibrahem.helwan.edu.eg.zagal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ahmed.fciibrahem.helwan.edu.eg.zagal.MessageActivity;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.Chat;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import ahmed.fciibrahem.helwan.edu.eg.zagal.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapterr  extends RecyclerView.Adapter<MessageAdapterr.MessageViewHolder>{
    public static final int MSG_TYPE_SEND=0;
    public static final int MSG_TYPE_RECEVEE=1;

    FirebaseUser firebaseUser;
    Context context;
    List<Chat> chats;
    String imageurl;
    public MessageAdapterr(Context context,List<Chat> chats,String imageurl)
    {
        this.context=context;
        this.chats=chats;
        this.imageurl=imageurl;


    }


    @NonNull
    @Override
    public MessageAdapterr.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType==MSG_TYPE_RECEVEE)
        {

            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_reciver,viewGroup,false);
            return new MessageAdapterr.MessageViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_sender,viewGroup,false);
            return new MessageAdapterr.MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterr.MessageViewHolder messageViewHolder, int i) {
        Chat chat=chats.get(i);
        messageViewHolder.showMessage.setText(chat.getMessage());
        if (imageurl.equals("defualt"))
        {


            messageViewHolder.profleimage.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {

            Glide.with(context).load(imageurl).into(messageViewHolder.profleimage);
        }
        if(i==chats.size()-1){
            if(chat.isIsseen()){
                messageViewHolder.text_seen.setText("Seen");
            }else {
                messageViewHolder.text_seen.setText("Delvierd");

            }

        }else {

            messageViewHolder.text_seen.setVisibility(View.GONE);
        }






    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView showMessage;
        public CircleImageView profleimage;
        public  TextView text_seen;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage=itemView.findViewById(R.id.show_message);
            profleimage=itemView.findViewById(R.id.profile_image);
            text_seen=itemView.findViewById(R.id.txt_seen);


        }




    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid()))
        {

            return MSG_TYPE_SEND;

        }
        else
        {
            return MSG_TYPE_RECEVEE;
        }

    }
}
