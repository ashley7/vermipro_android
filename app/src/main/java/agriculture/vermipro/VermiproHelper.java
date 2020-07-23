package agriculture.vermipro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class VermiproHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="vermipro_market";

    public static final String URL = "https://market.vermiproug.com/api/";

    public static final String IMAGE_URL = "https://market.vermiproug.com/";

    public static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public VermiproHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE user(id INTEGER PRIMARY KEY,phone_number VARCHAR(20) NOT NULL,verified VARCHAR(2))");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS user");
        // Create tables again
        onCreate(db);
    }

    public Cursor readData() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cu = db.rawQuery("SELECT * FROM user",null);

        return  cu;
    }


    public void saveUser(String phone_number){

        deleteSingleRow();//delete old data

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", ""+phone_number);
        values.put("verified","0");
        db.insert("user", null, values);
        Log.d("Response","User created");
    }

    public void deleteSingleRow()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user", null, null);
    }

    public void updateRecord(String phone_number) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE user SET verified = "+"'"+1+"' "+ "WHERE phone_number = "+"'"+phone_number+"'");

    }

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("id")
    public String id;

    @SerializedName("price")
    public String price;

    @SerializedName("description")
    public String description;

    @SerializedName("RAVE_PUBLIC_KEY")
    public String RAVE_PUBLIC_KEY;

    @SerializedName("RAVE_ENCRYPTION_KEY")
    public String RAVE_ENCRYPTION_KEY;

    @SerializedName("transaportCost")
    public String transaportCost;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("status")
    public String status;

    @SerializedName("unit")
    public String unit;

    @SerializedName("date")
    public String date;

}
