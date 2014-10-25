package classes;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class DownloadImage {	
	public DownloadImage(){
		
	}
	
	private Bitmap bmp;

	public DownloadImage(final String src, final Activity act, final int img_id, final int progress_bar_id)
	{

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					HttpClient http = new DefaultHttpClient();
					HttpGet get = new HttpGet(src);
					HttpResponse response = http.execute(get);
					
					byte[] bytes = EntityUtils.toByteArray(response.getEntity());					
					final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					bmp = bitmap;
					act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	ProgressBar progress_bar = (ProgressBar) act.findViewById(progress_bar_id);
                        	progress_bar.setVisibility(View.GONE);
                        	ImageView image_user = (ImageView) act.findViewById(img_id);
                        	
        					image_user.setImageBitmap(formatImg(bitmap));
        					image_user.setVisibility(View.VISIBLE);
                        }
                    });
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	
	
	public DownloadImage(final String src)
	{

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					HttpClient http = new DefaultHttpClient();
					HttpGet get = new HttpGet(src);
					HttpResponse response = http.execute(get);
					
					byte[] bytes = EntityUtils.toByteArray(response.getEntity());					
					final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					bmp = bitmap;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	
	public Bitmap getBitmap(){
		return bmp;
	}

	
	public static Bitmap formatImg(Bitmap bmpOriginal)
	{        
		if(bmpOriginal != null)
		{
			int width, height;
		    height = bmpOriginal.getHeight();
		    width = bmpOriginal.getWidth();

		    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		    Canvas c = new Canvas(bmpGrayscale);
		    Paint paint = new Paint();
		    ColorMatrix cm = new ColorMatrix();
		    cm.setSaturation(0);
		    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		    paint.setColorFilter(f);
		    c.drawBitmap(bmpOriginal, 0, 0, paint);
		    return recorte(ThumbnailUtils.extractThumbnail(bmpGrayscale, 200, 200));
		}else{
			return null;
		}
	   
	}
	public static Bitmap recorte(Bitmap bitmapimg)
	{
		if(bitmapimg != null)
		{
			Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(), bitmapimg.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(), bitmapimg.getWidth());
	
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawCircle(bitmapimg.getWidth() / 2, bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmapimg, rect, rect, paint);
	        
	        return output;
		}else{
			return null;
		}
	}
	
	
}
