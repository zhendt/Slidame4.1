/*
Copyright (C) 2011  Wade Chatam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package skripsi.slidame;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment.SavedState;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the primary @class Activity for the PhotoGaffe application.  It
 * creates the game board and handles any menu items selected.
 * @author wadechatam
 *
 */
public final class PuzzleActivity extends Activity implements SensorEventListener {
   
	public  int START_DELAY = 2000;
   public static final int INTERVAL = 1000;
   private int ukurangrid,lebar,x,y,z;
   private slidameBoard board;
   private Bitmap bitmap; 
   private ListView list_score;
   private ArrayAdapter<String> aa;
   private long timer;
   private int grid,move;
   private SQLiteDatabase highscoreDB;
   private boolean numbersVisible = true; 
   private SensorManager sensorManager;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //db
      String DB_PATH= getApplicationContext().getFilesDir().getPath();
      highscoreDB = SQLiteDatabase.openDatabase(DB_PATH+slidameBoard.DB_NAME,null,SQLiteDatabase.CREATE_IF_NECESSARY);
      new countleft(START_DELAY, INTERVAL);
      //sensor
      sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
      //lebar
      DisplayMetrics displayMetrics = new DisplayMetrics();
      WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
      wm.getDefaultDisplay().getMetrics(displayMetrics);
      lebar = displayMetrics.widthPixels;
      setContentView(R.layout.activity_slidame4);
   }    

   private final void createGameBoard() {
      TableLayout tableLayout;
      tableLayout = (TableLayout) findViewById(R.id.TableLayout1);    
      tableLayout.removeAllViews();
      bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gambar);
      board = slidameBoard.createGameBoard(this, 
            bitmap, 
            tableLayout,
            lebar,
            lebar,
            ukurangrid);
     board.setNumbersVisible(numbersVisible);
     bitmap.recycle(); 
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.slidame4, menu);
      return true;
   }
   //TODO Replace this with ActionBar support for IceCream Sandwich (4.0)
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
		case R.id.hint:
			Toast.makeText(getApplicationContext(), ""+getApplicationContext().getFilesDir().getPath()+slidameBoard.DB_NAME, 
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.highscore:
			highscore();
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
		}onResume();
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
	

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}

@Override
public void onSensorChanged(SensorEvent event) {
	//delay = new countleft(START_DELAY, INTERVAL);
	if (board!=null && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    	float[] values = event.values;
	    // Movement
	    x =(int) Math.floor(values[0]);
	    y =(int) Math.floor(values[1]);
	    z =(int) Math.floor(values[2]);
	    //delay = new countleft(START_DELAY, INTERVAL);
	    //delay.start();
		    if(x>=3){
		    	board.right();
		    }
		    else if(x<=(-3)){
		    	board.left();
		    }
		    else if(y>=3){
		    	board.up();
		    }
		    else if(y<=(-3)){
		    	board.down();
		    }
    }
	
  }


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


class countleft extends CountDownTimer{
	public countleft(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void onFinish() {
		if(x>=3){
	    	board.right();
	    }
	    else if(x<=(-3)){
	    	board.left();
	    }
	    else if(y>=3){
	    	board.up();
	    }
	    else if(y<=(-3)){
	    	board.down();
	    }//START_DELAY = 2000;
	}
	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), ""+millisUntilFinished, 
				Toast.LENGTH_SHORT).show();
	}
}
	public void highscore(){
		list_score=(ListView)findViewById(R.id.score_list);//listview
		TableLayout tbl = (TableLayout)findViewById(R.id.table_score);
		TableRow tblrow = new TableRow(this);
		TextView tblview = new TextView(this);
		ArrayList<String> INscore = new ArrayList();
		ArrayList<ScoreItem> scores = new ArrayList<ScoreItem>();
		//get DB
		for(int i=3;i<6;i++){
			highscoreDB.execSQL("CREATE TABLE IF NOT EXISTS HIGHSCORE"+i+"(" +
			   		"TIME long(9),MOVE integer(4),GRID integer(2));"
				   );
			
			Cursor gethighscorealter=highscoreDB.rawQuery("SELECT*FROM HIGHSCORE3"+
					   " ORDER BY TIME asc, MOVE asc ;"
					   , null);
				if(gethighscorealter.getCount()>0){
					gethighscorealter.moveToFirst();
					timer= gethighscorealter.getLong(gethighscorealter.getColumnIndex("TIME"));	
					move = gethighscorealter.getInt(gethighscorealter.getColumnIndex("MOVE"));
					grid = gethighscorealter.getInt(gethighscorealter.getColumnIndex("GRID"));
						
				}
				
		}
		
		highscoreview();
	}
	
	public void highscore2(){
		
		
	}
	
	public void highscoreview2(){
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.score);
		ListView lv = (ListView ) dialog.findViewById(R.id.score_list);
		dialog.setCancelable(true);
		dialog.setTitle("ListView");
		dialog.show();	
}
	
	public void highscoreview(){
			AlertDialog.Builder showhighscore =new AlertDialog.Builder(this);
			LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.score, null);
			showhighscore.setCancelable(false);
			showhighscore.setTitle(" ");
			showhighscore.setView(layout);
			showhighscore.setIcon(R.drawable.menu_icon);
			showhighscore.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					onResume();
				}
			});
			   showhighscore.show();
		
	}
	

	/*
    private class ScoresCancelListener implements OnCancelListener {
        public void onCancel(DialogInterface dialog) {
            mTimerView.setBase(SystemClock.elapsedRealtime() - mTime);
            if (!mTileView.isSolved()) {
                mTimerView.start();
            }        
        }
    }
    
    private class ConfirmDeleteListener implements OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton ) {
            if (whichButton == AlertDialog.BUTTON1) {
                ScoreUtil.getInstance(SlidePuzzleActivity.this).clearScores();                
            }
            showHighScoreListDialog();
        }
    }
	
	/**/
	/*
	    private void showHighScoreListDialog() {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.high_score_list, null);
        ListView listView = (ListView) layout.findViewById(R.id.score_list);
        ScoresListener listener = new ScoresListener();
        long[] times = ScoreUtil.getInstance(this).getAllScores();
        String[] sizes = getResources().getStringArray(R.array.pref_entries_size);
        int len = sizes.length;
        
        ArrayList<ScoreItem> scores = new ArrayList<ScoreItem>();
        for (int i = 0; i < len; i++) {
            scores.add(new ScoreItem(sizes[i], times[i]));
        }
        listView.setAdapter(new HighScoreListAdapter(this, scores));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        //builder.setIcon();
        builder.setTitle("Highscore");
        builder.setCancelable(true);
        builder.setView(layout);
        //builder.setAdapter(new HighScoreListAdapter(this, scores), null);
        builder.setPositiveButton("Reset", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
        builder.setNegativeButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
        
        builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
			}
		});
        builder.show();        
    }
	 /* */

   /* (non-Javadoc)
    * Return a 'congratulatory' message that also contains the number of moves.
    */
   /*
   private String createCompletionMessage() {
      String completeMsg = 
            getResources().getString(R.string.congratulations) + " " 
            + String.valueOf(board.getMoveCount());
      String[] insults = getResources().getStringArray(R.array.insults);
      completeMsg += "\n";
      int insultIndex = (int) Math.floor(Math.random() * insults.length); 
      completeMsg += insults[insultIndex];
      
      return completeMsg;
   }*/
}