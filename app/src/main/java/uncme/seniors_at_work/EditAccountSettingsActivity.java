package uncme.seniors_at_work;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditAccountSettingsActivity extends AppCompatActivity {

    String SERIALIZE_DATA = "user";
    User user;
    EditText editTextUserName;
    EditText editTextFirstName;
    EditText editTextLastName;
    Button goBackButton;
    Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_settings);

        user  = (User) getIntent().getSerializableExtra("serializedata");

        editTextFirstName = findViewById(R.id.edit_text_firstName);
        editTextLastName = findViewById(R.id.edit_text_lastName);
        editTextUserName = findViewById(R.id.edit_text_userName);

        goBackButton = findViewById(R.id.goback_button);
        saveSettingsButton = findViewById(R.id.save_settings_button);

        //save settings button grabs all text input in fields, updates the user object
        // and sends user object back to homepage
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextUserName.getText().toString() != "") {
                    user.setUsername(editTextUserName.getText().toString());
                }
                if (editTextFirstName.getText().toString() != "") {
                    user.setFirstName(editTextFirstName.getText().toString());
                }
                if (editTextLastName.getText().toString() != "") {
                    user.setLastName(editTextLastName.getText().toString());
                }

                Intent i = new Intent(EditAccountSettingsActivity.this, HomePage.class);
                i.putExtra(SERIALIZE_DATA, user);
                startActivity(i);
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

    }
}