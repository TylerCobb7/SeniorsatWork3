package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FlagActivity extends AppCompatActivity {

    String current_user_id;
    DatabaseReference usersRef, postsRef, flagsRef;
    FirebaseAuth mAuth;
    Button sexBtn, violentBtn, hateBtn;
    ProgressBar progressBar;
    String Post_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);

        Post_Key = getIntent().getExtras().get("Postkey").toString();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        sexBtn = (Button) findViewById(R.id.sexContentBtn);
        violentBtn = (Button)findViewById(R.id.vioRepBtn);
        hateBtn = (Button)findViewById(R.id.hateBtn);
        progressBar =(ProgressBar)findViewById(R.id.progressBar2);

        sexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "Sexual Content";
                sendFlagInformation(info);
            }
        });

        violentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "Violent or repulsive content";
                sendFlagInformation(info);
            }
        });

        hateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "Hateful or abusive content";
                sendFlagInformation(info);
            }
        });


    }

    private void sendFlagInformation(String info) {
        progressBar.setVisibility(View.VISIBLE);


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());


        final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Flagged").child(RandomKey);

        HashMap flagsMap = new HashMap();
            flagsMap.put("reason", info);
            flagsMap.put("reported by", current_user_id);
            flagsMap.put("reported post", Post_Key);
            flagsMap.put("date", saveCurrentDate);
            flagsMap.put("time", saveCurrentTime);
            postsRef.updateChildren(flagsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key);
                        HashMap setFlag = new HashMap();
                        setFlag.put("flagged", "true");
                        postsRef.updateChildren(setFlag);

                        Toast.makeText(FlagActivity.this, "Thank you for letting us know.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        SendToHomeActivity();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(FlagActivity.this, message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
    }

    private void SendToHomeActivity() {
        Intent intent = new Intent(FlagActivity.this, Home.class);
        startActivity(intent);
    }
}
