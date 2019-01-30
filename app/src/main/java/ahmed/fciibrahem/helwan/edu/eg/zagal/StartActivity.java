package ahmed.fciibrahem.helwan.edu.eg.zagal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Button btn_Sregister,btn_Slogin;
    EditText email,password;
    TextView ResetPass;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser !=null)
        {
            Intent intent=new Intent(StartActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btn_Slogin=findViewById(R.id.btn_Login);
        btn_Sregister=findViewById(R.id.btn_register);
        email=findViewById(R.id.username);
        password=findViewById(R.id.password);
        ResetPass=findViewById(R.id.forget_password);
        ResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,ResetPasswordActivity.class));
            }
        });
        auth=FirebaseAuth.getInstance();

        btn_Slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StartActivity.this, "hhhh", Toast.LENGTH_SHORT).show();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();
                if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(StartActivity.this,"email and password  Are Requird",Toast.LENGTH_SHORT).show();

                }
                else {
                    Login(txt_email,txt_password);

                }






            }
        });
        btn_Sregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(StartActivity.this,RegisterActivity.class));

            }
        });


    }
    private void Login(String email,String password)
    {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(StartActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            Toast.makeText(StartActivity.this, "Authontication Field", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }
}
