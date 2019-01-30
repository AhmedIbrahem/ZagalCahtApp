package ahmed.fciibrahem.helwan.edu.eg.zagal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText sendEmail;
    Button btn_reset;
    FirebaseAuth  firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        sendEmail=findViewById(R.id.send_mail);
        btn_reset=findViewById(R.id.btn_reset);
        firebaseAuth=FirebaseAuth.getInstance();
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=sendEmail.getText().toString();
                if(email.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "All filed Are Requird", Toast.LENGTH_SHORT).show();
                }
                else {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Plase Chack your Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,StartActivity.class));


                            }else{
                                String error=task.getException().toString();
                                Toast.makeText(ResetPasswordActivity.this, ""+error, Toast.LENGTH_SHORT).show();



                            }

                        }
                    });
                }

            }
        });
    }
}
