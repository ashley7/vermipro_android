package agriculture.vermipro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.ALPHA_NUMERIC_STRING;
import static agriculture.vermipro.VermiproHelper.IMAGE_URL;
import static agriculture.vermipro.VermiproHelper.URL;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView name,price,description,additional_charge;
    private ImageView image;
    private Intent intent;
    private String product_name, product_price,product_image,product_description,product_id,paymentMethod,quantityToBuy,customerPhone,unit;
    private Button buy_now;
    private EditText delivery_address,quantity,buyer_description;

    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private RadioGroup mRadioGroup;
    private VermiproHelper vermiproHelper;
    private Cursor cursor;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        intent = getIntent();

        image = findViewById(R.id.image);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        additional_charge = findViewById(R.id.additional_charge);

        buy_now = findViewById(R.id.buy_now);

        delivery_address = findViewById(R.id.delivery_address);
        quantity = findViewById(R.id.quantity);
        buyer_description = findViewById(R.id.buyer_description);

        product_id = intent.getStringExtra("product_id");
        product_price = intent.getStringExtra("product_price");
        product_name = intent.getStringExtra("product_name");
        product_image = intent.getStringExtra("product_image");
        product_description = intent.getStringExtra("product_description");
        unit = intent.getStringExtra("product_unit");

        mRadioGroup = findViewById(R.id.radiogroup);
        mRadioGroup.check(R.id.mobile_money);

        getSupportActionBar().setTitle("Order for "+product_name);

        vermiproHelper = new VermiproHelper(ProductDetailActivity.this);

        cursor = vermiproHelper.readData();

        Picasso.get().load(IMAGE_URL+"/product_images/" + product_image)
                .placeholder(R.drawable.placeholder)
                .resize(700,500)
                .error(R.drawable.placeholder).into(image);

        name.setText(product_name);
        price.setText("Ush "+product_price+" @ "+unit);
        description.setText(product_description);

        quantityToBuy = quantity.getText().toString();


        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(ProductDetailActivity.this, false,
                new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                additional_charge.setText("Transport charge = Ush "+s);
            }
        });

        postResponseAsyncTask.execute(URL+"delivery_fee");

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethod = selectedPaymentType();

                if (paymentMethod.equals("Pay with mobile money")) {



                    HashMap hashMap = new HashMap();
                    hashMap.put("phone_number",""+userPhone());

                    PostResponseAsyncTask postResponseAsyncTask = new
                            PostResponseAsyncTask(ProductDetailActivity.this, hashMap,
                            new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    try {
                                        LayoutInflater li = LayoutInflater.from(ProductDetailActivity.this);
                                        View promptsView = li.inflate(R.layout.payment_layout, null);

                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                ProductDetailActivity.this);

                                        alertDialogBuilder.setView(promptsView);

                                        final EditText email_address = promptsView.findViewById(R.id.customerEmail);

                                        vermiproHelperArrayListroHelper = new JsonConverter<VermiproHelper>().toArrayList(s, VermiproHelper.class);

                                        for (final VermiproHelper result : vermiproHelperArrayListroHelper) {

                                            alertDialogBuilder.setCancelable(false).setPositiveButton("Submit",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            if (email_address.getText().toString().equals("") && quantity.getText().toString().equals("")){
                                                                Toast.makeText(ProductDetailActivity.this, "Please Provide an email address and Quantity", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }

                                                            new RavePayManager(ProductDetailActivity.this)
                                                                    .setAmount((Double.parseDouble(product_price) + Double.parseDouble(result.transaportCost))* Integer.parseInt(quantity.getText().toString()))
                                                                    .setCountry("NG")
                                                                    .setCurrency("UGX")
                                                                    .setEmail(email_address.getText().toString())
                                                                    .setfName(""+result.name)
                                                                    .setlName("")
                                                                    .setNarration("Vermipro LTD payment")
                                                                    .setPublicKey(result.RAVE_PUBLIC_KEY)
                                                                    .setEncryptionKey(result.RAVE_ENCRYPTION_KEY)
                                                                    .setTxRef("Vermipro-"+ randomAlphaNumeric(32))
                                                                    .acceptAccountPayments(true)
                                                                    .acceptCardPayments(true)
                                                                    .acceptAchPayments(false)
                                                                    .acceptMpesaPayments(false)
                                                                    .acceptGHMobileMoneyPayments(false)
                                                                    .acceptUgMobileMoneyPayments(true)
                                                                    .onStagingEnv(false)
                                                                    .isPreAuth(true)
                                                                    .shouldDisplayFee(true)
                                                                    .initialize();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    finish();
                                                                    startActivity(getIntent());
                                                                    dialog.cancel();
                                                                }
                                                            });

                                            // create alert dialog
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            // show it
                                            alertDialog.show();

                                        }

                                    }catch(Exception e){}
                                }});

                    postResponseAsyncTask.execute(URL+"rave_public_Keys");

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

                }else if(paymentMethod.equals("I will Pay on delivery")){

                    if (quantityToBuy.equals(""))
                        quantityToBuy = "1";

                    HashMap hashMap = new HashMap();
                    hashMap.put("product_id", "" + product_id);
                    hashMap.put("quantity", "" + quantity.getText().toString());
                    hashMap.put("payment_method", "cash");
                    hashMap.put("customer_phone_number", "" + userPhone());
                    hashMap.put("delivery_address",""+delivery_address.getText().toString());
                    hashMap.put("description",buyer_description.getText().toString());
                    hashMap.put("price",""+product_price);

                    PostResponseAsyncTask postResponseAsyncTask = new
                            PostResponseAsyncTask(ProductDetailActivity.this, hashMap, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {

                            new AlertDialog.Builder(ProductDetailActivity.this)
                                    .setMessage(s)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            Intent intent = new Intent(ProductDetailActivity.this,
                                                    OldOrdersActivity.class);
                                            startActivity(intent);
                                        }
                                    }).show();
                        }
                    });
                    postResponseAsyncTask.execute(URL+"store_order");
                    postResponseAsyncTask.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getApplicationContext(), "Internet connectivity is weak, or the server " +
                                    "delayed to respond. Please try again", Toast.LENGTH_LONG).show();
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
                }
            }//closing button
        });//onlick
    }

    public String selectedPaymentType(){

        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = findViewById(selectId);

        return radioButton.getText().toString();
    }

    public static final String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            String responsemessage = data.getStringExtra("response");

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {

                Toast.makeText(this, "SUCCESS " , Toast.LENGTH_SHORT).show();

                HashMap hashMap = new HashMap();
                hashMap.put("product_id", "" + product_id);
                hashMap.put("quantity", "" + quantity);
                hashMap.put("payment_method", "mobile_money_or_card");
                hashMap.put("customer_phone_number", "" + userPhone());
                hashMap.put("delivery_address",""+delivery_address.getText().toString());
                hashMap.put("description",buyer_description.getText().toString());
                hashMap.put("price",""+product_price);
                hashMap.put("status",""+data.getStringExtra("status"));
                hashMap.put("tx_ref",""+data.getStringExtra("tx_ref"));
                hashMap.put("appfee",""+data.getStringExtra("appfee"));

                PostResponseAsyncTask postResponseAsyncTask = new
                        PostResponseAsyncTask(ProductDetailActivity.this, hashMap, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Toast.makeText(ProductDetailActivity.this, s, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ProductDetailActivity.this,OldOrdersActivity.class));
                    }
                });

                postResponseAsyncTask.execute(URL + "store_order");
                postResponseAsyncTask.setEachExceptionsHandler(new EachExceptionsHandler() {
                    @Override
                    public void handleIOException(IOException e) {
                        Toast.makeText(getApplicationContext(), "You are having a weak connection", Toast.LENGTH_LONG).show();
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
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + responsemessage, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + responsemessage, Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String userPhone(){
        while (cursor.moveToNext()) {
            customerPhone = cursor.getString(1);
        }
        return  customerPhone;
    }
}
