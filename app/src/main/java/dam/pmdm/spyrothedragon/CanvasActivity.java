package dam.pmdm.spyrothedragon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CanvasActivity extends AppCompatActivity {

    private ImageView imageView;
    private Drawable imagen;
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_canvas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagen = this.getResources().getDrawable(R.drawable.spyrominillama);
        imageView = (ImageView) findViewById(R.id.imagenSpyro);
        dibujarSpyro(imageView, imagen);

    }

    private void dibujarSpyro(View view, Drawable img) {
        int vWidth = view.getWidth();
        int vHeight = view.getHeight();
        bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.spyrollama);
        imageView.setImageBitmap(bitmap);
        // Create a Canvas with the bitmap.
        canvas = new Canvas();
        canvas.drawBitmap(bitmap, 0, 0, p);
    }

    protected void onDraw(Canvas canvas) {
        Rect imageBounds = canvas.getClipBounds();  // Adjust this for where you want it
        imagen.setBounds(imageBounds);
        imagen.draw(canvas);

    }
}
