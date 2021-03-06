package ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ahmed.fciibrahem.helwan.edu.eg.zagal.MessageActivity;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Model.User;
import ahmed.fciibrahem.helwan.edu.eg.zagal.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    CircleImageView profileImage;
    TextView Username;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    StorageReference storageReference;
    private static final int IMG_REQIST=1;
    private Uri imageUri;
    private StorageTask UploadTask;
    private Activity mActivity;


    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage=view.findViewById(R.id.profile_image);
        Username=view.findViewById(R.id.username);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Username.setText(user.getUsername());
                if (user.getImageURl().equals("defualt"))
                {
                    profileImage.setImageResource(R.mipmap.ic_launcher);

                }
                else
                {
                    if (mActivity == null) {
                        return;
                    }

                    Glide.with(getContext()).load(user.getImageURl()).into(profileImage);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });
        return view;
    }

    private void OpenImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQIST);


    }
    private String GetFileExtantion(Uri uri)
    {

        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void UploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if(imageUri!=null)
        {

            final StorageReference filerefrance=storageReference.child(System.currentTimeMillis()
                    +"."+GetFileExtantion(imageUri));

            UploadTask=filerefrance.putFile(imageUri);
            UploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filerefrance.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloaduri=task.getResult();
                        String mUri=downloaduri.toString();
                        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imageURl",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();

                        }else{

                        Toast.makeText(getContext(), "Faild !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();


                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();



                }
            });


        }else {


            Toast.makeText(getContext(), "No Image Selected  !", Toast.LENGTH_SHORT).show();


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQIST &&resultCode==RESULT_OK &&data!=null &&data.getData() !=null){
            imageUri=data.getData();
            if(UploadTask !=null &&UploadTask.isInProgress())
            {
                Toast.makeText(getContext(), "Upload in prograss", Toast.LENGTH_SHORT).show();


            }else{

                UploadImage();
            }
        }
    }
}
