package ahmed.fciibrahem.helwan.edu.eg.zagal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments.ChatsFragment;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments.ProfileFragment;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments.UsersFragment;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.Chat;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView profileImag;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
      Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profileImag=findViewById(R.id.profile_imageview);
        username=findViewById(R.id.username);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Log.d("onDataChange", ""+user.getImageURl()+"----"+user.getUsername()+"---"+user.getId());




                if (user.getImageURl().equals("defualt"))
                {
                    Log.d("onDataChange", "in fff ");
                    profileImag.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Log.d("onDataChange", "in sslll ");

                    Glide.with(getApplicationContext()).load(user.getImageURl()).into(profileImag);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final TabLayout tabLayout=findViewById(R.id.tablayout);
        final ViewPager viewPager=findViewById(R.id.viewpager);
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapteer viewPagerAdapteer=new ViewPagerAdapteer(getSupportFragmentManager());
                int unread=0;
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(firebaseUser.getUid())&& !chat.isIsseen()){
                        unread++;
                    }


                }
                if(unread==0){
                    viewPagerAdapteer.Addfragment(new ChatsFragment(),"Chats");




                }else {

                    viewPagerAdapteer.Addfragment(new ChatsFragment(),"("+unread+")Chats");

                }
                viewPagerAdapteer.Addfragment(new UsersFragment(),"Users");
                viewPagerAdapteer.Addfragment(new ProfileFragment(),"Profile");
                viewPager.setAdapter(viewPagerAdapteer);
                tabLayout.setupWithViewPager(viewPager);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            return true;

        }


return false;
    }

    class ViewPagerAdapteer extends FragmentPagerAdapter
    {


        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapteer(FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }


        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public  void Addfragment(Fragment fragment,String title)
        {
            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
