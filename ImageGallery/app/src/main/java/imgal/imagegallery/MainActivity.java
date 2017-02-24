package imgal.imagegallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DatabaseImgal mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());



    LocationManager locationManager;
    Handler mHandler;
//
    double longGPS,latGPS;
    double longNet, latNet;
    double lat,lng;
    private static int RESULT_LOAD_IMG = 20;
    String imgDecodableString;
    byte[] img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mHandler = new Handler();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 5, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 1000, 5, locationListenerNetwork);

        Button startbtn = (Button) findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    if (latNet == 0 && longNet == 0) {
                        if (latGPS==0&&longGPS==0){
                            Toast.makeText(MainActivity.this, "Open location or Wait a sec and Try again.", Toast.LENGTH_SHORT).show();
                        }else {

                            intent.putExtra("lat", latGPS);
                            intent.putExtra("lng", longGPS);
                            startActivity(intent);
                        }

                    } else {
                        intent.putExtra("lat", latNet);
                        intent.putExtra("lng", longNet);
                        startActivity(intent);
                    }


            }
        });

        Button thaibtn = (Button) findViewById(R.id.thaibtn);
        thaibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("lat", 13);
                intent.putExtra("lng", 100);
                startActivity(intent);

            }
        });


        Button addbtn = (Button) findViewById(R.id.addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (latNet == 0 && longNet == 0) {
                    if (latGPS==0&&longGPS==0){
                        lat = 13;
                        lng = 100;
                    }else {
                        lat = latGPS;
                        lng = longGPS;
                    }

                } else {
                    lat = latNet;
                    lng = longNet;
                }
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                Bitmap imageB = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                img = bos.toByteArray();


                mHelper = new DatabaseImgal(this);
                mDb = mHelper.getWritableDatabase();


                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                ContentValues v = new ContentValues();

                v.put(DatabaseImgal.COL_NAME, currentDateTimeString);
                v.put(DatabaseImgal.COL_LATITUDE, lat);
                v.put(DatabaseImgal.COL_LONGTITUDE, lng);
                v.put(DatabaseImgal.COL_IMAGE, img);


                mDb.insert(DatabaseImgal.TABLE_NAME, null, v);
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longGPS = location.getLongitude();
            latGPS = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };
//
//
//
    protected final LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longNet = location.getLongitude();
            latNet = location.getLatitude();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


}
