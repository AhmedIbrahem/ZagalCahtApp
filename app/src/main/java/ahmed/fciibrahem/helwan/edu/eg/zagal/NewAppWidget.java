package ahmed.fciibrahem.helwan.edu.eg.zagal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.ChatList;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;

/**
 * Implementation of App Widget functionality.
 */

public class NewAppWidget extends AppWidgetProvider {
     List<User> users;
     List<ChatList> usersList;
    static FirebaseUser firebaseUser;
    DatabaseReference reference;
    String UserChat,Imageurl;

    void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                         final int appWidgetId) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.getUid() !=null) {
            usersList = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatList chatList = snapshot.getValue(ChatList.class);
                        Log.d("chatlost", "onDataChange: " + chatList.getId());
                        usersList.add(chatList);

                    }

                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            users = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                users.clear();
                                User user = snapshot.getValue(User.class);
                                for (ChatList chatList : usersList) {
                                    assert user != null;
                                    if (user.getId().equals(chatList.getId())) {
                                        users.add(user);
                                        Log.d("Datacahnge", "onDataChange: " + users.size());

                                    }

                                }

                            }
                            int usersize = users.size();
                            UserChat = users.get(usersize - 1).getUsername();
                            Log.d("userchat", "onDataChange: " + UserChat);
                            // Construct the RemoteViews object
                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                            views.setTextViewText(R.id.widusername, UserChat);
                            appWidgetManager.updateAppWidget(appWidgetId, views);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //Log.d("user00000", "onDataChange: "+ users.size());


        }

        // Instruct the widget manager to update the widget


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent=new Intent(context,MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.new_app_widget);
            views.setOnClickPendingIntent(R.id.widusername,pendingIntent);
            views.setOnClickPendingIntent(R.id.widprofile_image,pendingIntent);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId,views);

        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

