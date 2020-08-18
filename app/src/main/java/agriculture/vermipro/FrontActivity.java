package agriculture.vermipro;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;

import static agriculture.vermipro.VermiproHelper.IMAGE_URL;
import static agriculture.vermipro.VermiproHelper.URL;

public class FrontActivity extends AppCompatActivity {

    private TextView myorders;
    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private GridView features_products,categories;

    private PostResponseAsyncTask postResponseAsyncTask;
    private FunDapter<VermiproHelper> fundupter;
    private VermiproHelper category;
    private BindDictionary<VermiproHelper> dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        Toolbar toolbar =    findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myorders = findViewById(R.id.myorders);
        categories = findViewById(R.id.categories);
        features_products = findViewById(R.id.features_products);

        myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrontActivity.this,OldOrdersActivity.class));
            }
        });




        postResponseAsyncTask = new PostResponseAsyncTask(FrontActivity.this, true, new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                try {

                    vermiproHelperArrayListroHelper = new JsonConverter<VermiproHelper>().toArrayList(s, VermiproHelper.class);

                    dict = new BindDictionary<>();

                    dict.addStringField(R.id.name, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
                            return "" + item.cat_name;
                        }
                    });

                    fundupter = new FunDapter<>(FrontActivity
                            .this, vermiproHelperArrayListroHelper, R.layout.front_products_layout, dict);

                    categories.setAdapter(fundupter);

                    categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            category = vermiproHelperArrayListroHelper.get(position);
                            Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                            intent.putExtra("category_id", ""+category.cat_id);
                            intent.putExtra("category_name", ""+category.cat_name);
                            startActivity(intent);

                        }
                    });


                }catch (Exception e){}

            }
        });
        postResponseAsyncTask.execute(URL+"categories");


        postResponseAsyncTask = new PostResponseAsyncTask(FrontActivity.this, false,
                new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {

                        Log.d("DATA",s);

                        try {

                            vermiproHelperArrayListroHelper =  new JsonConverter<VermiproHelper>().toArrayList(s, VermiproHelper.class);

                            dict = new BindDictionary<>();

                            dict.addStringField(R.id.name, new StringExtractor<VermiproHelper>() {
                                @Override
                                public String getStringValue(VermiproHelper item, int position) {
                                    return ""+item.name;
                                }
                            });

                            dict.addStringField(R.id.price, new StringExtractor<VermiproHelper>() {
                                @Override
                                public String getStringValue(VermiproHelper item, int position) {
                                    return "Ush "+item.price;
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
                                            .resize(180,180)
                                            .error(R.drawable.placeholder).into(view);
                                }
                            });

                            fundupter = new FunDapter<>(FrontActivity
                                    .this, vermiproHelperArrayListroHelper, R.layout.front_products_layout, dict);

                            features_products.setAdapter(fundupter);

                            features_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    category = vermiproHelperArrayListroHelper.get(position);

                                    Intent intent = new Intent(FrontActivity.this, ProductDetailActivity.class);

                                    Log.d("DATA",category.id);

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

        postResponseAsyncTask.execute(URL+"category_products_limit_eight");
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
