package uncme.seniors_at_work;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button SignUp;
    private int counter = 5;
    String SERIALIZE_DATA = "serializedata";
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        Info = (TextView)findViewById(R.id.tvInfo);
        Login = (Button)findViewById(R.id.btnLogin);
        SignUp = (Button)findViewById(R.id.btnSignup);


        SignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserSignUp.class);
                startActivity(intent);
            }
        });


        Info.setText("No of attempts remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });


    }
    private void validate(String userName, String userPassword){
        if((userName.equals("")) && (userPassword.equals(""))){

            //create a new user object with the user credentials
            user = new User(Name.getText().toString());
            user.setUsername("default test");
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            //include the user object to pass to the next activity
            intent.putExtra(SERIALIZE_DATA, user);
            startActivity(intent);
        }else{
            counter--;

            Info.setText("No of attempts remaining " + String.valueOf(counter));

            if(counter == 0){
                Login.setEnabled(false);
            }
        }
    }
}
