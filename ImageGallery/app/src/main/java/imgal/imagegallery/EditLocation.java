package imgal.imagegallery;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class EditLocation extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener{
    DatabaseImgal mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    private GoogleMap mMap;

    private Marker emarker,marker;

    private int id;

    private int checkdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = getIntent().getIntExtra("id", 0);

        Button savebtn = (Button) findViewById(R.id.savebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseImgal.COL_NAME, emarker.getTitle()); //These Fields should be your String values of actual column names
                cv.put(DatabaseImgal.COL_LATITUDE, emarker.getPosition().latitude);
                cv.put(DatabaseImgal.COL_LONGTITUDE, emarker.getPosition().longitude);

                mDb.update(DatabaseImgal.TABLE_NAME, cv, DatabaseImgal.COL_ID + "=" + id, null);
                Toast.makeText(EditLocation.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(EditLocation.this,
                "Click Red marker title to change name, \nHold to drag.",
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
        mDb = mHelper.getReadableDatabase();

        try {
            mCursor = mDb.rawQuery("SELECT * FROM " + DatabaseImgal.TABLE_NAME +
                    " WHERE " + DatabaseImgal.COL_ID + "=" + id, null);
            mCursor.moveToFirst();
            emarker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                    mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LATITUDE)),
                    mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LONGTITUDE))))
                    .title(mCursor.getString(mCursor.getColumnIndex(DatabaseImgal.COL_NAME))).draggable(true));
            emarker.setTag(mCursor.getInt(mCursor.getColumnIndex(DatabaseImgal.COL_ID)));
        }catch (Exception e){

        }

        try {
            mCursor = mDb.rawQuery("SELECT * FROM " + DatabaseImgal.TABLE_NAME +
                    " WHERE " + DatabaseImgal.COL_ID + "!=" + id, null);
            ArrayList<Marker> arr_list = new ArrayList<Marker>();
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LATITUDE)),
                        mCursor.getDouble(mCursor.getColumnIndex(DatabaseImgal.COL_LONGTITUDE))))
                        .title(mCursor.getString(mCursor.getColumnIndex(DatabaseImgal.COL_NAME))));
                marker.setTag(mCursor.getInt(mCursor.getColumnIndex(DatabaseImgal.COL_ID)));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                arr_list.add(marker);
                mCursor.moveToNext();
            }
        }catch (Exception e){

        }





        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emarker.getPosition(), 10));


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
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
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        this.emarker.setPosition(marker.getPosition());

        Toast.makeText(EditLocation.this,
                "Latitude: "+marker.getPosition().latitude + "\nLongtitude: " + marker.getPosition().longitude,
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int check = (int) marker.getTag();
        if (check == id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Image:"+emarker.getTitle()+"\n\n");

            // Set up the input
            final EditText input = new EditText(this);

            input.setText(emarker.getTitle());
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emarker.setTitle(input.getText().toString());}
                });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

            builder.show();
        }


    }
}
