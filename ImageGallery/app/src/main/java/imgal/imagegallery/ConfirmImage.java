package imgal.imagegallery;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ConfirmImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image);

        String path = "sdcard/camera_app/TestImage.jpg";

        ImageView imageView = (ImageView) findViewById(R.id.imageCamera);
        imageView.setImageDrawable(Drawable.createFromPath(path));

    }
}
