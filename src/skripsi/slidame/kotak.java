package skripsi.slidame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.TextView;

public class kotak  {
	private TileLocation current_lokasi;
	private final TileLocation correct_lokasi;
	private Bitmap bitmap;
	
	public kotak(Bitmap bitmap, short baris, short kolom) {
		this.bitmap=bitmap;
		current_lokasi=TileLocation.getInstance(baris, kolom);
		correct_lokasi=TileLocation.getInstance(baris, kolom);
	}
	
	public TileLocation getCurrent_lokasi() {
	      return current_lokasi;
	   }
	
	public TileLocation getCorrect_lokasi() {
	      return correct_lokasi;
	   }

	public boolean isCorrect() {
	      return current_lokasi.equals(correct_lokasi);
	   }
	
	public void setCurrent_lokasi(TileLocation location) {
	      this.current_lokasi = location;
	   }
	public Bitmap getBitmap() {
		      return bitmap;
	   }
	public void freeBitmap() {
		      bitmap.recycle();
		      bitmap = null;
	   }
}
