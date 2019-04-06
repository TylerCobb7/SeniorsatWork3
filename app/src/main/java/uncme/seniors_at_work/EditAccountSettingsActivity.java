package uncme.seniors_at_work;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uncme.seniors_at_work.AccountActivity.LoginActivity;
import uncme.seniors_at_work.AccountActivity.SignupActivity;

public class EditAccountSettingsActivity extends AppCompatActivity {

    String SERIALIZE_DATA = "user";
    User user;
    EditText editTextUserName;
    EditText editTextFirstName;
    EditText editTextLastName;
    Button goBackButton;
    Button saveSettingsButton;
    Button deleteAccountButton;
    FirebaseAuth userAuth;
    FirebaseUser currentUser;
    DrawerLayout drawerLayout;
    DatabaseReference usersRef;
    RadioButton btnMale;
    RadioButton btnFemale;
    EditText aboutMe;
    CircleImageView profileImage;

    final static int Gallery_Pick = 1;


    String currentUserID;


    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_settings);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = userAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        progressBar = new ProgressBar(this);

        user  = (User) getIntent().getSerializableExtra("serializedata");

        editTextFirstName = (EditText) findViewById(R.id.edit_text_firstName);
        editTextLastName = (EditText) findViewById(R.id.edit_text_lastName);
        editTextUserName = (EditText) findViewById(R.id.edit_text_userName);
        deleteAccountButton = (Button) findViewById(R.id.btnRemoveAcc);
        goBackButton = (Button) findViewById(R.id.goback_button);
        saveSettingsButton = (Button) findViewById(R.id.save_settings_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        btnFemale = (RadioButton)findViewById(R.id.btnFemale);
        btnMale = (RadioButton)findViewById(R.id.btnMale);
        aboutMe = (EditText) findViewById(R.id.etAboutMe);
        profileImage = (CircleImageView) findViewById(R.id.profilePicture);


        //save settings button grabs all text input in fields, updates the user object
        // and sends user object back to homepage
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserInfo();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        //back button goes back to homepage, ignoring changes
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditAccountSettingsActivity.this, HomePage.class);
                startActivity(i);
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                if (currentUser != null) {
                    currentUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditAccountSettingsActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditAccountSettingsActivity.this, SignupActivity.class));
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(EditAccountSettingsActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void SaveUserInfo() {

        String username = editTextUserName.getText().toString();
        String userFirst = editTextFirstName.getText().toString();
        String userLast = editTextLastName.getText().toString();
        String userMale = btnMale.getText().toString();
        String userFemale = btnMale.getText().toString();
        String userAboutMe = aboutMe.getText().toString();
        String userGender = "";

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Username field is empty", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(userFirst)){
            Toast.makeText(this, "First name field is empty", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(userLast)){
            Toast.makeText(this, "Last name field is empty", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(userMale) && !(TextUtils.isEmpty(userFemale))){
            userGender = userFemale;
        }

        if(!(TextUtils.isEmpty(userMale)) && (TextUtils.isEmpty(userFemale))){
            userGender = userMale;
        }

        if((!(TextUtils.isEmpty(username)) && !(TextUtils.isEmpty(userFirst)) && !(TextUtils.isEmpty(userLast))) == true){

            progressBar.setVisibility(View.VISIBLE);

            //Saves Users information if there is no blank input
            /*
            user.setUsername(editTextUserName.getText().toString());
            user.setFirstName(editTextFirstName.getText().toString());
            user.setLastName(editTextLastName.getText().toString());
            */

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("userFirst", userFirst);
            userMap.put("userLast", userLast);
            userMap.put("age", userGender);
            userMap.put("aboutMe", userAboutMe);
            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditAccountSettingsActivity.this, "Account Infomation has been saved", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        SendToProfileActivity();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(EditAccountSettingsActivity.this, "Error occured: " + message, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

            //SendToProfileActivity();
            Intent i = new Intent(EditAccountSettingsActivity.this, HomePage.class);
            i.putExtra(SERIALIZE_DATA, user);
            startActivity(i);
        }
    }

    private void SendToProfileActivity() {

        Intent intent = new Intent(EditAccountSettingsActivity.this, HomePage.class);
        startActivity(intent);
        finish();

    }
}
