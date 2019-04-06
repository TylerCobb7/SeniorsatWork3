package uncme.seniors_at_work;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;


public class UserSignUp extends AppCompatActivity {

    private EditText UserName, UserEmail, UserPass, UserDOB;
    private Button createAcc;
    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        UserName = (EditText) findViewById(R.id.etUsername);
        UserEmail = (EditText) findViewById(R.id.etUserEmail);
        UserPass = (EditText) findViewById(R.id.etUserPass);
        createAcc = (Button) findViewById(R.id.btnCreateAccount);
        displayDate = (TextView) findViewById(R.id.etUserDOB);

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountInformation();
            }
        });


        //Date of Birth selector
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UserSignUp.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = month + "/" + day + "/" + year;
                displayDate.setText(date);
            }
        };
    }

    //Save User information function
    private void SaveAccountInformation() {

        String username = UserName.getText().toString();
        String useremail = UserEmail.getText().toString();
        String userpass = UserPass.getText().toString();
        String userdob = displayDate.getText().toString();


        //Checks for blank input
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Username field is empty", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(useremail)){
            Toast.makeText(this, "Email Address field is empty", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userpass)){
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userdob)){
            Toast.makeText(this, "Date of Birth field is empty", Toast.LENGTH_SHORT).show();
        }
        if((!(TextUtils.isEmpty(username)) && !(TextUtils.isEmpty(useremail)) && !(TextUtils.isEmpty(userpass)) && !(TextUtils.isEmpty(userdob))) == true){
            //Saves Users information if there is no blank input
            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("useremail", useremail);
            userMap.put("userpass", userpass);
            userMap.put("userdob", userdob);

            //New activity is directed when the information is saved.
            Intent intent = new Intent(UserSignUp.this, AccountCreated.class);
            startActivity(intent);
        }
    }
}
