package uncme.seniors_at_work;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountCreated extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_created);

        button = (Button)findViewById(R.id.btnBackToSignIn);

        //Tells users account has been created
        //Button takes users back to sign in page
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountCreated.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
