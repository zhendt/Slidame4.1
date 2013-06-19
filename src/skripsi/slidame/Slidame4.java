package skripsi.slidame;

import java.sql.Savepoint;
import java.util.List;

import android.R.drawable;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.SavedState;
import android.widget.Toast;

public class Slidame4 extends Activity implements SensorEventListener {

	private Bitmap bit,bi,bitm;
	private int x,y,z;
	private TableRow tblrow ;
	private ImageView bimg;
	private kotak hitam;
	private TextView v ;
	private List<kotak> tiles = null;
	private List<TileView> tileViews = null;
	private List<TableRow> tableRow = null;
	private TableLayout tbl;
	public int ukurangrid;
	private SensorManager sensorManager;
	//private int[] sizes;
	private Handler mHandler = new Handler();
	private GameBoard coba;
	private int lebar;
	private Context context;
	public static final int GAME_DELAY= 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources r = getResources();
		
		tbl = (TableLayout)findViewById(R.id.TableLayout1);
		tblrow = new TableRow(this);

		v= new TextView(this);
		bimg = new ImageView(this);		
		//sensor
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
		//lebar
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		lebar = displayMetrics.widthPixels;
		/*
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		lebar = metrics.widthPixels;
		*/
		//lebar = (display.getWidth());		
		setContentView(R.layout.activity_slidame4);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.slidame4, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()) {
		case R.id.hint:
			//double ko =coba.layar;
			
			Toast.makeText(getApplicationContext(), ""+lebar, Toast.LENGTH_SHORT).show();
			break;
		case R.id.highscore:
			
			Toast.makeText(getApplicationContext(), "HIGH SCORE ", 3).show();
			break;
		case R.id.grid3:
			ukurangrid = 3;
			createGameBoard();
			item.setChecked(true);
			break;
		case R.id.grid4:
			ukurangrid = 4;
			createGameBoard();
			item.setChecked(true);
			break;
		case R.id.grid5:
			ukurangrid = 5;
			createGameBoard();
			item.setChecked(true);
			break;

		default:
			return super.onOptionsItemSelected(item);
			
		}
		onResume();
		return false;
				
	}
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		onResume();
		super.onOptionsMenuClosed(menu);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		onPause();
		return super.onMenuOpened(featureId, menu);
	}
	
	public void bukagambar(){
		
		bit = Bitmap.createScaledBitmap(bi, lebar, lebar, true);
		int lebar_tile = bit.getWidth()/ukurangrid;
		for(short baris=0 ;baris<ukurangrid;baris++){
			for(short kolom=0 ;kolom<ukurangrid;kolom++){
				bitm= Bitmap.createBitmap(bit, kolom*lebar_tile, baris*lebar_tile, lebar_tile, lebar_tile);
				bimg.setImageBitmap(bitm);
				tblrow.addView(bimg);
			}
		tbl.addView(tblrow);	
		}
	}

	private final void createGameBoard() {
		TableLayout gLayout;
		gLayout = (TableLayout) findViewById(R.id.TableLayout1);
		gLayout.removeAllViews();
		bi = BitmapFactory.decodeResource(getResources(), R.drawable.gambar);
		coba = new GameBoard(this, bi, gLayout, lebar,ukurangrid);
		bi.recycle();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	  public void onSensorChanged(SensorEvent event) {
		//coba!=null &&  //kirim nilai failed!
		//mHandler.postDelayed(mUpdateTimeTask, 5000);
	    if (coba!=null && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	float[] values = event.values;
		    // Movement
		    x =(int) values[0];
		    y =(int) values[1];
		    z =(int) values[2];
		    if(x>=3){
		    	//coba.RIGHT();
		    }
		    else if(x<=(-3)){
		    	coba.LEFT();
		    }
		    else if(y>=3){
		    	//coba.UP();
		    }
		    else if(y<=(-3)){
		    	//coba.DOWN();
		    }
		    //coba.gerak(x, y, z);
	    }
	  }
	
	private Runnable mUpdateTimeTask = new Runnable() {
	    public void run() {
	        // Do some stuff that you want to do here

	    // You could do this call if you wanted it to be periodic:
	        mHandler.postDelayed(this, 5000 );

	        }
	    };
	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		}
	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
		}
}
