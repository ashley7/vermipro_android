package agriculture.vermipro;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

public class OneTimeLoginActivity extends AppCompatActivity {

    private VermiproHelper vermiproHelper;

    private Cursor result;

    private String varified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vermiproHelper = new VermiproHelper(OneTimeLoginActivity.this);

        result = vermiproHelper.readData();

        if(result.getCount() > 0){

            while (result.moveToNext()) {

                varified = result.getString(2);

                if(varified.equals("1")){

                    Intent intent = new Intent(OneTimeLoginActivity.this, CategoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }else{

                    Toast.makeText(this, "Please verify your account", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OneTimeLoginActivity.this,VerificationActivity.class));
                    finish();

                }
            }
        }else if(result.getCount() == 0){

            startActivity(new Intent(OneTimeLoginActivity.this,RegisterActivity.class));
            finish();

        }
    }
}
