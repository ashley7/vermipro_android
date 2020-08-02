package agriculture.vermipro;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.URL;


public class VerificationActivity extends AppCompatActivity {

    private Button verify;
    private EditText verification_code;
    private Cursor result;
    private VermiproHelper vermiproHelper;
    private String phone_number;
    private TextView resend_code;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();

        phone_number = intent.getStringExtra("phone_number");

        vermiproHelper = new VermiproHelper(VerificationActivity.this);

        verify = findViewById(R.id.verify);

        resend_code = findViewById(R.id.resend_code);

        verification_code = findViewById(R.id.verification_code);

        result = vermiproHelper.readData();

        resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(result.getCount() > 0) {

//                   while (result.moveToNext()) {

//                        phone_number = result.getString(1);

                        HashMap hashMap = new HashMap();
                        hashMap.put("phone_number",""+phone_number);

                        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(VerificationActivity.this, hashMap,
                                new AsyncResponse() {
                            @Override
                            public void processFinish(String s) {
                                messgae(s);
                                return;
                            }
                        });
                        postResponseAsyncTask.execute(URL+"resend_code");
                        postResponseAsyncTask.setEachExceptionsHandler(new EachExceptionsHandler() {
                            @Override
                            public void handleIOException(IOException e) {
                                Toast.makeText(getApplicationContext(), "Internet connectivity is weak.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleMalformedURLException(MalformedURLException e) {
                                Toast.makeText(getApplicationContext(), "The URL is not well specified.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleProtocolException(ProtocolException e) {
                                Toast.makeText(getApplicationContext(), "Issue with protocol.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                                Toast.makeText(getApplicationContext(), "Text encoding is not proper.", Toast.LENGTH_LONG).show();
                            }
                        });
//                    }
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //read user phone_number

                if (verification_code.getText().toString().equals("")){
                    messgae("Please enter the verification code");
                    return;
                }


                if(result.getCount() > 0) {

//                    while (result.moveToNext()) {
//                    phone_number = result.getString(1);
                        HashMap hashMap = new HashMap();
                        hashMap.put("phone_number",""+phone_number);
                        hashMap.put("verification_code",""+verification_code.getText().toString());
                        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(VerificationActivity.this,hashMap,
                                new AsyncResponse() {
                            @Override
                            public void processFinish(String s) {

                                if (s.equals("SUCCESS")){
                                    //set verification code to 1
                                    vermiproHelper.updateRecord(phone_number);
                                    startActivity(new Intent(VerificationActivity.this,OneTimeLoginActivity.class));
                                    finish();

                                }else{
                                    messgae(s);
                                    return;
                                }
                            }
                        });

                        postResponseAsyncTask.execute(URL+"verify_user");
                        postResponseAsyncTask.setEachExceptionsHandler(new EachExceptionsHandler() {
                            @Override
                            public void handleIOException(IOException e) {
                                Toast.makeText(getApplicationContext(), "Internet connectivity is weak.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleMalformedURLException(MalformedURLException e) {
                                Toast.makeText(getApplicationContext(), "The URL is not well specified.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleProtocolException(ProtocolException e) {
                                Toast.makeText(getApplicationContext(), "Issue with protocol.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                                Toast.makeText(getApplicationContext(), "Text encoding is not proper.", Toast.LENGTH_LONG).show();
                            }
                        });

//                    }
                }else{
                    Toast.makeText(VerificationActivity.this, "No user is registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerificationActivity.this,RegisterActivity.class));
                    finish();
                }
            }
        });

    }

    private void messgae(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
        builder.setMessage(""+message).setCancelable(true);
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

}
