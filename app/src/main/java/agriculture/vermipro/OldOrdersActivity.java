package agriculture.vermipro;

import android.database.Cursor;
import android.os.Bundle;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.IMAGE_URL;
import static agriculture.vermipro.VermiproHelper.URL;

public class OldOrdersActivity extends AppCompatActivity {

    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private VermiproHelper vermiproHelper;
    private Cursor cursor;
    private String customerPhone;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_orders);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        vermiproHelper = new VermiproHelper(OldOrdersActivity.this);
        cursor = vermiproHelper.readData();
        HashMap hashMap = new HashMap();
        hashMap.put("phone_number", ""+userPhone());

        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(OldOrdersActivity.this, hashMap,
                new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                try {
                    vermiproHelperArrayListroHelper =  new JsonConverter<VermiproHelper>().toArrayList(s, VermiproHelper.class);
                    BindDictionary<VermiproHelper> dict = new BindDictionary<>();

                    dict.addStringField(R.id.name, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return ""+item.name;
                        }
                    });

                    dict.addStringField(R.id.price, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            double totalAmount = Double.parseDouble(item.quantity) * Double.parseDouble(item.price);
                            return "Total amount Ush "+totalAmount;
                        }
                    });

                    dict.addStringField(R.id.quantity, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return "Quantity: "+item.quantity;
                        }
                    });

                    dict.addStringField(R.id.date, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return "Date: "+item.date;
                        }
                    });


                    dict.addStringField(R.id.status, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return "Status: "+item.status;
                        }
                    });

                    dict.addDynamicImageField(R.id.image, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return item.image;
                        }
                    }, new DynamicImageLoader() {
                        @Override
                        public void loadImage(String url, ImageView view) {
                            Picasso.get().load(IMAGE_URL+"/product_images/" + url)
                                    .placeholder(R.drawable.placeholder)
                                    .resize(380,380)
                                    .error(R.drawable.placeholder).into(view);
                        }
                    });

                    listView = findViewById(R.id.orders_list);

                    FunDapter<VermiproHelper> fundupter = new FunDapter<>(OldOrdersActivity
                            .this, vermiproHelperArrayListroHelper, R.layout.order_layout, dict);
                    listView.setAdapter(fundupter);

                }catch (Exception e){}

            }
        });
        postResponseAsyncTask.execute(URL+"customer_orders");
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
    }

    private String userPhone(){
        while (cursor.moveToNext()) {
            customerPhone = cursor.getString(1);
        }
        return  customerPhone;
    }

}
