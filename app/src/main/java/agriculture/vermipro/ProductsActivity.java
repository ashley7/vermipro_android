package agriculture.vermipro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.IMAGE_URL;
import static agriculture.vermipro.VermiproHelper.URL;

public class ProductsActivity extends AppCompatActivity {

    private Intent intent;
    private String category_id, category_name;

    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        category_id = intent.getStringExtra("category_id");
        category_name = intent.getStringExtra("category_name");

        getSupportActionBar().setTitle(category_name);

        HashMap hashMap = new HashMap();
        hashMap.put("category_id",""+category_id);

        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(ProductsActivity.this, hashMap,
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
                            return "Ush "+item.price+" @ "+item.unit;
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
                                    .resize(380,250)
                                    .error(R.drawable.placeholder).into(view);
                        }
                    });

                    listView = findViewById(R.id.product_list);

                    FunDapter<VermiproHelper> fundupter = new FunDapter<>(ProductsActivity
                            .this, vermiproHelperArrayListroHelper, R.layout.product_layout, dict);
                    listView.setAdapter(fundupter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            VermiproHelper category = vermiproHelperArrayListroHelper.get(position);
                            Intent intent = new Intent(ProductsActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product_id", category.id);
                            intent.putExtra("product_name", category.name);
                            intent.putExtra("product_price", category.price);
                            intent.putExtra("product_image", category.image);
                            intent.putExtra("product_unit", category.unit);
                            intent.putExtra("product_description", category.description);
                            startActivity(intent);

                        }
                    });

                }catch (Exception e){}

            }
        });

        postResponseAsyncTask.execute(URL+"category_products");
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

}
