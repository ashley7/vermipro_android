package agriculture.vermipro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static agriculture.vermipro.VermiproHelper.IMAGE_URL;
import static agriculture.vermipro.VermiproHelper.URL;

public class CategoryActivity extends AppCompatActivity {

    private ArrayList<VermiproHelper> vermiproHelperArrayListroHelper;
    private GridView gridView;
    private TextView myorders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        myorders = findViewById(R.id.myorders);

        myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this,OldOrdersActivity.class));
            }
        });

        PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(CategoryActivity.this, new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                try{
                    vermiproHelperArrayListroHelper =  new JsonConverter<VermiproHelper>().toArrayList(s, VermiproHelper.class);
                    BindDictionary<VermiproHelper> dict = new BindDictionary<>();

                    dict.addStringField(R.id.name, new StringExtractor<VermiproHelper>() {
                        @Override
                        public String getStringValue(VermiproHelper item, int position) {
//                          return ""+item.name;
                            return "";
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
                            Picasso.get().load(IMAGE_URL+"product_images/" + url)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder).into(view);
                        }
                    });

                    gridView = findViewById(R.id.category_list);

                    FunDapter<VermiproHelper> fundupter = new FunDapter<>(CategoryActivity
                            .this, vermiproHelperArrayListroHelper, R.layout.category_layout, dict);
                    gridView.setAdapter(fundupter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            VermiproHelper category = vermiproHelperArrayListroHelper.get(position);
                            Intent intent = new Intent(CategoryActivity.this, ProductsActivity.class);
                            intent.putExtra("category_id", category.id);
                            intent.putExtra("category_name", category.name);
                            startActivity(intent);

                        }
                    });

                }catch(Exception e){}

            }
        });
        postResponseAsyncTask.execute(URL+"categories");
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
