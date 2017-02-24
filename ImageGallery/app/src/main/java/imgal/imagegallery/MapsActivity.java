package imgal.imagegallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.net.Uri;

import static android.graphics.Bitmap.CompressFormat.JPEG;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    DatabaseImgal mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    double lng,lat;

    private Marker marker;
    private GoogleMap mMap;
    byte[] img = null;
    static final int CAM_REQUEST = 1777;

    int checkdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lat = getIntent().getDoubleExtra("lat",13.0);
        lng = getIntent().getDoubleExtra("lng",100.0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ImageButton camerabtn = (ImageButton) findViewById(R.id.camerabtn);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent,CAM_REQUEST);

            }
        });


        Toast.makeText(MapsActivity.this,
                "Click icon to view image.",
                Toast.LENGTH_LONG).show();

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mHelper = new DatabaseImgal(this);
        mDb = mHelper.getWritableDatabase();
        if (lat==13 && lng ==100) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 5));
        }else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));

        }


        try {
            mCursor = mDb.rawQuery("SELECT * FROM " + DatabaseImgal.TABLE_NAME, null);
            ArrayList<Marker> arr_list = new ArrayList<Marker>();
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LATITUDE)),
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LONGTITUDE))))
                        .title(mCursor.getString(mCursor.getColumnIndex(DatabaseImgal.COL_NAME))));
                marker.setTag(mCursor.getInt(mCursor.getColumnIndex(DatabaseImgal.COL_ID)));


                img = mCursor.getBlob(mCursor.getColumnIndex(DatabaseImgal.COL_IMAGE));

                Bitmap b1 = BitmapFactory.decodeByteArray(img, 0, img.length);
                Bitmap resized = Bitmap.createScaledBitmap(b1, 149, 149, true);
                Bitmap resized2 = Bitmap.createScaledBitmap(b1, 135, 135, true);
                Canvas canvas = new Canvas(resized);
                canvas.drawColor(Color.LTGRAY);
                canvas.drawBitmap(resized2, 7, 7, null);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resized));
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.imageicon));
                arr_list.add(marker);
                mCursor.moveToNext();
            }
        }catch (Exception e){

        }


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
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

//        mMap.setInfoWindowAdapter(new MapItemAdapter(this));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
    }





    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }





    @Override
    public void onInfoWindowClick(Marker marker) {

        Integer id = (Integer) marker.getTag();
        String name = marker.getTitle();


        Intent intent = new Intent(MapsActivity.this, ViewImageActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name",name);
        startActivity(intent);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == CAM_REQUEST) {
                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                    Bitmap imageB = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    imageB.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    img = bos.toByteArray();

                    ContentValues v = new ContentValues();

                    v.put(DatabaseImgal.COL_NAME, currentDateTimeString);
                    v.put(DatabaseImgal.COL_LATITUDE, lat);
                    v.put(DatabaseImgal.COL_LONGTITUDE, lng);
                    v.put(DatabaseImgal.COL_IMAGE, img);


                    mDb.insert(DatabaseImgal.TABLE_NAME, null, v);
                    updateMarker();


                }
            }
        }catch (Exception e){

        }

    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void updateMarker(){

        mMap.clear();

        mHelper = new DatabaseImgal(this);
        mDb = mHelper.getWritableDatabase();

        try {
            mCursor = mDb.rawQuery("SELECT * FROM " + DatabaseImgal.TABLE_NAME, null);
            ArrayList<Marker> arr_list = new ArrayList<Marker>();
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LATITUDE)),
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LONGTITUDE))))
                        .title(mCursor.getString(mCursor.getColumnIndex(DatabaseImgal.COL_NAME))));
                marker.setTag(mCursor.getInt(mCursor.getColumnIndex(DatabaseImgal.COL_ID)));

                img = mCursor.getBlob(mCursor.getColumnIndex(DatabaseImgal.COL_IMAGE));



                Bitmap b1 = BitmapFactory.decodeByteArray(img, 0, img.length);
                Bitmap resized = Bitmap.createScaledBitmap(b1, 149, 149, true);
                Bitmap resized2 = Bitmap.createScaledBitmap(b1, 135, 135, true);
                Canvas canvas = new Canvas(resized);
                canvas.drawColor(Color.LTGRAY);
                canvas.drawBitmap(resized2, 7, 7, null);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resized));

//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.imageicon));
                arr_list.add(marker);
                mCursor.moveToNext();
            }
        }catch (Exception e){

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(mMap == null)){
            updateMarker();
            checkdc = 0;
        }
    }

//
//public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {
//
//    TextView tv;
//    ImageView imgview;
//
//    public MapItemAdapter(Context context) {
//        imgview = new ImageView(context);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        imgview.setPadding(10,10,10,10);
//        imgview.setBackgroundColor(Color.WHITE);
//    }
//
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        try {
//            int id = (int) marker.getTag();
//
//            mCursor = mDb.rawQuery("SELECT " + DatabaseImgal.COL_IMAGE +
//                    " FROM " + DatabaseImgal.TABLE_NAME +
//                    " WHERE " + DatabaseImgal.COL_ID + "=" + id, null);
//
//            if (mCursor != null) {
//                mCursor.moveToFirst();
//                do {
//                    img = mCursor.getBlob(mCursor.getColumnIndex(DatabaseImgal.COL_IMAGE));
//                } while (mCursor.moveToNext());
//                Bitmap b1 = BitmapFactory.decodeByteArray(img, 0, img.length);
//                Bitmap resized = Bitmap.createScaledBitmap(b1, 300, 300, true);
//
//                imgview.setImageBitmap(resized);
//            }
//        }catch (Exception e){
//
//        }
//        return imgview;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
//}
}
