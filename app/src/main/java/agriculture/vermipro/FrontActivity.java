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
    private GridView categories;
    private PostResponseAsyncTask postResponseAsyncTask;
    private FunDapter<VermiproHelper> fundupter;

    private BindDictionary<VermiproHelper> dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        Toolbar toolbar =    findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myorders = findViewById(R.id.myorders);
        categories = findViewById(R.id.categories);


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
                            .this, vermiproHelperArrayListroHelper, R.layout.category_layout, dict);

                    categories.setAdapter(fundupter);

                    categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            VermiproHelper category  = vermiproHelperArrayListroHelper.get(position);

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

        }

}
