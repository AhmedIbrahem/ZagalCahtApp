package ahmed.fciibrahem.helwan.edu.eg.zagal.Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdServices extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refresToken= FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser !=null){
            UpdateToken(refresToken);

        }
    }

    private void UpdateToken(String refresToken) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(refresToken);
        reference.child(firebaseUser.getUid()).setValue(token);




    }
}
