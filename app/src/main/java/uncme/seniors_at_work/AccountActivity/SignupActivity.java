package uncme.seniors_at_work.AccountActivity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import uncme.seniors_at_work.Home;
import uncme.seniors_at_work.R;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference usersRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                //Validation to see if email line is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your UNCC email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation to see if password line is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Create your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation to see if password is less than 6
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation if email contains @
                if(!email.contains("@")){
                    Toast.makeText(SignupActivity.this, "This is not a valid email!.. ", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validation if email used contains @uncc.edu
                if(!email.contains("@uncc.edu")){
                    Toast.makeText(SignupActivity.this, "Sorry, only emails with @uncc.edu can use this app..", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    auth = FirebaseAuth.getInstance();
                                    currentUser = auth.getCurrentUser();
                                    currentUserID = auth.getCurrentUser().getUid();
                                    usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


                                    String username = "None";
                                    String userFirst = "None";
                                    String userLast = "None";
                                    String userGender = "Male";
                                    String userAboutMe = "None";
                                    String banned = "false";
                                    String userProfileImage = "https://firebasestorage.googleapis.com/v0/b/seniors-at-work.appspot.com/o/profile%20Images%2Fprofile.png?alt=media&token=f367aa11-c7b5-40eb-a663-fae9074faecf";

                                    //Create user Hashmap then inputting values into firebase database
                                    HashMap userMap = new HashMap();
                                    userMap.put("aboutMe", userAboutMe);
                                    userMap.put("gender", userGender);
                                    userMap.put("profileImage", userProfileImage);
                                    userMap.put("userFirst", userFirst);
                                    userMap.put("userLast", userLast);
                                    userMap.put("username", username);
                                    userMap.put("banned", banned);
                                    usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "Account has been created", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                SendToProfileActivity();
                                            }
                                            else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SignupActivity.this, "Error occured: " + message, Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    //Send user to next activity
    private void SendToProfileActivity() {
        Intent intent = new Intent(SignupActivity.this, Home.class);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

