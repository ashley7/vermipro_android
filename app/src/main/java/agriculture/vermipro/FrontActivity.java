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

    private TextView livestockAll,poultryAll,vegetables_crops,farm_management,fish_farming;
    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private GridView livestock,poultry_grid,vegetables_crops_grid,farm_management_grid,fish_farming_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        Toolbar toolbar =    findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        livestock = findViewById(R.id.livestock);
        poultry_grid = findViewById(R.id.poultry_grid);
        vegetables_crops_grid = findViewById(R.id.vegetables_crops_grid);
        farm_management_grid = findViewById(R.id.farm_management_grid);
        fish_farming_grid = findViewById(R.id.fish_farming_grid);

        livestockAll = findViewById(R.id.all_livestock);
        poultryAll = findViewById(R.id.all_poultry);
        vegetables_crops = findViewById(R.id.all_vegetables_crops);
        farm_management = findViewById(R.id.all_farm_management);
        fish_farming = findViewById(R.id.all_fish_farming);


        loadProducts(""+1, livestock);
        loadProducts(""+2, poultry_grid);
        loadProducts(""+3, fish_farming_grid);
        loadProducts(""+4, vegetables_crops_grid);
        loadProducts(""+5, farm_management_grid);

        livestockAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                intent.putExtra("category_id", ""+1);
                intent.putExtra("category_name", "LIVESTOCK FARMING");
                startActivity(intent);

            }
        });

        poultryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                intent.putExtra("category_id", ""+2);
                intent.putExtra("category_name", "POULTRY FARMING");
                startActivity(intent);

            }
        });

        vegetables_crops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                intent.putExtra("category_id", ""+4);
                intent.putExtra("category_name", "VEGETABLES AND CROPS");
                startActivity(intent);

            }
        });

        farm_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                intent.putExtra("category_id", ""+5);
                intent.putExtra("category_name", "FARM MANAGEMENT");
                startActivity(intent);

            }
        });

        fish_farming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FrontActivity.this, ProductsActivity.class);
                intent.putExtra("category_id", ""+3);
                intent.putExtra("category_name", "FISH FARMING");
                startActivity(intent);

            }
        });
    }



    private void loadProducts(String category_id, final GridView gridView){

        HashMap hashMap = new HashMap();
        hashMap.put("category_id",""+category_id);

        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(FrontActivity.this, hashMap,false,
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

                            FunDapter<VermiproHelper> fundupter = new FunDapter<>(FrontActivity
                                    .this, vermiproHelperArrayListroHelper, R.layout.front_products_layout, dict);
                            gridView.setAdapter(fundupter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    VermiproHelper category = vermiproHelperArrayListroHelper.get(position);
                                    Intent intent = new Intent(FrontActivity.this, ProductDetailActivity.class);
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
