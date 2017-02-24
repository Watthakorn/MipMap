package imgal.imagegallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IceCoCo-PC on 10-Dec-16.
 */

public class DatabaseImgal extends SQLiteOpenHelper {
    private static final String DB_NAME = "ImageGallery";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "Image";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGTITUDE = "longtitude";
    public static final String COL_IMAGE = "image";

    public DatabaseImgal(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+ TABLE_NAME + " ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_NAME+ " TEXT,"+ COL_LATITUDE+" DOUBLE,"+COL_LONGTITUDE+" DOUBLE,"+COL_IMAGE+" BLOB);");

//
//        Bitmap b= BitmapFactory.decodeFile("drawable/noimg.png");
//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        b.compress(Bitmap.CompressFormat.PNG, 100, bos);
//        byte[] img=bos.toByteArray();

//        ContentValues v = new ContentValues();
//
//
//        v.put(COL_NAME,"TEST-NEW");
//        v.put(COL_LATITUDE,13.0);
//        v.put(COL_LONGTITUDE,100.0);
//        v.put(COL_IMAGE,img);

//        db.insert(TABLE_NAME,null,v);



    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS"+ TABLE_NAME+";");
        onCreate(db);


    }
}
