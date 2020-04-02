package agriculture.vermipro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, phone_number,email,address;
    private String userName, phoneNumber,userEmail,userAddress;
    private Button register;
    private VermiproHelper vermiproHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email_address);
        address = findViewById(R.id.address);

        register = findViewById(R.id.register);

        vermiproHelper = new VermiproHelper(RegisterActivity.this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = name.getText().toString();
                phoneNumber = phone_number.getText().toString();
                userEmail = email.getText().toString();
                userAddress = email.getText().toString();

                if (userName.equals("") && phoneNumber.equals("") && userEmail.equals("") && userAddress.equals("")){
                    messgae("All the fields are required");
                    return;
                }

                HashMap hashMap = new HashMap();
                hashMap.put("name",""+userName);
                hashMap.put("phone_number",""+phoneNumber);
                hashMap.put("email",""+userEmail);
                hashMap.put("address",""+userAddress);

                PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(RegisterActivity.this,hashMap, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {

                        if (s.equals("SUCCESS")){
                            //save local user
                            vermiproHelper.saveUser(phoneNumber);
                            startActivity(new Intent(RegisterActivity.this,VerificationActivity.class));
                        }else{
                            messgae(s);
                        }
                    }
                });

                postResponseAsyncTask.execute(URL+"register");
            }
        });
    }

    private void messgae(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(""+message).setCancelable(true);
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

}
