package skripsi.slidame;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import skripsi.slidame.R.anim;

import android.R.color;
import android.R.layout;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.*;

public class GameBoard extends Activity  {
	private Bitmap bit,gam;
	private int layar,lebar_board,grid;
	//private float x,y,z;
	TextView cek;
	private Context context;
	private GamePiece emptyGamePiece;
	private GamePiece tempGamePiece;
	private GamePiece tempGamePiece2;
	private SensorManager sensorManager;
	private static GameBoard board=null;
	private TableLayout gameLayout;	
	private List<GamePiece> gamePieces = null;
	private List<TableRow> tableRow = null;
	private int tr,tc;
	
	GameBoard(Context context, Bitmap bit, TableLayout gLayout,int lebar,int grid) {
		this.context =context;
		this.lebar_board=lebar;
		this.bit = Bitmap.createScaledBitmap(bit, this.lebar_board,
				this.lebar_board, true);
		this.gameLayout = gLayout;
		this.grid=grid;
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {

		cek= new TextView(context);
		initializeLists();
		createGamePieces();
		addToGameScreen();
		tr=emptyGamePiece.getCurrentRow();
		tc=emptyGamePiece.getCurrentColumn();
		
		testAccelometer();
	}

	private void testAccelometer(){
		cek.setText(tr+"COBA"+tc);
		gameLayout.addView(cek);
	}
	
	private void initializeLists() {
		if (gamePieces == null) {
			gamePieces = new ArrayList<GamePiece>(grid*grid);
		} else {
			for (int i = 0; i < gamePieces.size(); i++) {
				gamePieces.get(i).getBitmap().recycle();
				gamePieces = new ArrayList<GamePiece>(grid*grid);
			}
		}

		tableRow = new ArrayList<TableRow>(grid);

		for (int row = 0; row < (grid*grid); row++) {
			tableRow.add(new TableRow(context));
		}
	}
	
	private void createGamePieces() {
		int gamePiece_width = bit.getWidth() / grid;
		int gamePiece_height = bit.getHeight() / grid;

		for (int row = 0; row < grid; row++) {
			for (int column = 0; column < grid; column++) {
				Bitmap bitm = Bitmap.createBitmap(bit, column
						* gamePiece_width, row * gamePiece_height,
						gamePiece_width, gamePiece_height);
				if ((row == grid - 1) && (column == grid - 1)) {
					bitm = Bitmap.createBitmap(gamePiece_width, gamePiece_height,
							bitm.getConfig());
					bitm.eraseColor(Color.BLACK);
					emptyGamePiece = new GamePiece(context, bitm, row, column,"empty");
					gamePieces.add(emptyGamePiece);
				} else {
					tempGamePiece = new GamePiece(context, bitm, row,
							column, ""+((grid*row)+column));
					gamePieces.add(tempGamePiece);
				}
			} // end column
		}// end row
		bit.recycle();
	}
	
	public void addToGameScreen() {
		
		Iterator<GamePiece> it = (shuffleGamePieces()).iterator();
		for (int row = 0; row < grid; row++) {
			for (int column = 0; column < grid; column++) {
				tableRow.get(row).addView(it.next());
			} // end column
			gameLayout.addView(tableRow.get(row));
		} // end row
		
	}
	
	public List<GamePiece> shuffleGamePieces() {
		Collections.shuffle(gamePieces);
		gamePieces.remove(emptyGamePiece);
		gamePieces.add(emptyGamePiece);

		for (int row = 0; row < grid; row++) {
			for (int column = 0; column < grid; column++) {
				gamePieces.get(grid * row + column).setCurrent(row, column);
			}
		}
		//numberOfMoves = 0;
		return gamePieces;
	}

	public static void createGameBoard(Context context, Bitmap bit,
			TableLayout gLayout, int lebar, int grid) {
		board = new GameBoard(context,bit,gLayout,lebar,grid);
	}
	
	
	void LEFT(){
		/*
		// emptyGamePiece needs to maintain blackBG and emptyGamePiece state but switch with GamePiece
		emptyGamePiece = gp;
		emptyGamePiece.setCurrent(gp);
		// GamePiece needs to switch with emptyGamePiece
		gp = emptyPiece;
		gp.setCurrent(emptyPiece);
		*/
		//tr=gamePieces.get((grid*tr)+(tc)).getCurrentRow();
		//tc=gamePieces.get((grid*tr)+(tc)).getCurrentColumn();
		
		tr=emptyGamePiece.getCurrentRow();
		tc=emptyGamePiece.getCurrentColumn();
		GamePiece emptyPiece = emptyGamePiece;
		GamePiece colorPiece = gamePieces.get((grid*tr)+(tc-1));
		cek.setTextColor(Color.GREEN);
		cek.setText(emptyGamePiece.getCurrentRow()+"-"+emptyGamePiece.getCurrentColumn());
		if(tc>0){
			//emptyGamePiece = colorPiece;
			emptyPiece.setCurrent(colorPiece);
			//colorPiece = emptyPiece;
			colorPiece.setCurrent(emptyGamePiece);
			/*
			gamePieces.get((grid*tr)+(tc)).swap(gamePieces.get(grid*tr+(tc-1)));
			gamePieces.get((grid*tr)+(tc)).setBG(gamePieces.get(grid*tr+(tc-1)).getBitmap());
			gamePieces.get((grid*tr)+(tc)).startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.left_animation));
			gamePieces.get((grid*tr)+(tc-1)).gpBG(emptyGamePiece);
			gamePieces.get((grid*tr)+(tc)).setCurrent(tr, tc-1);
			*/
		}
	}
	void RIGHT(){
		tr=gamePieces.get((grid*tr)+(tc)).getCurrentRow();
		tc=gamePieces.get((grid*tr)+(tc)).getCurrentColumn();
		cek.setText(emptyGamePiece.getCurrentRow()+"-"+emptyGamePiece.getCurrentColumn());
		if(tc<(grid-1)){
			gamePieces.get((grid*tr)+(tc)).swap(gamePieces.get((grid*tr)+(tc+1)));
			gamePieces.get((grid*tr)+(tc)).setBG(gamePieces.get(grid*tr+(tc)).getBitmap());
			gamePieces.get(grid*tr+(tc)).startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.right_animation));
			gamePieces.get((grid*tr)+(tc+1)).gpBG(emptyGamePiece);
			gamePieces.get((grid*tr)+(tc)).setCurrent(tr, tc+1);
		}
	}
	void UP(){
		cek.setTextColor(Color.CYAN);
		if(tr>0){
			gamePieces.get(grid*tr+(tc)).setCurrent(gamePieces.get(grid*(tr-1)+tc));
			//gamePieces.get(grid*(tr-1)+tc).setCurrent(emptyGamePiece);
			gamePieces.get(grid*(tr-1)+tc).setBG(emptyGamePiece.getBitmap());
			tr=tr-1;
			tc=tc;	
		}
	}
	void DOWN(){
		cek.setTextColor(Color.CYAN);
		if(tr<(grid-1)){
			gamePieces.get(grid*tr+(tc)).setCurrent(gamePieces.get(grid*(tr)+tc));
			//gamePieces.get(grid*(tr+1)+tc).setCurrent(emptyGamePiece);
			gamePieces.get(grid*(tr+1)+tc).setBG(emptyGamePiece.getBitmap());
			tr=tr+1;
			tc=tc;	
		}
	}
	


	/*
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	float[] values = event.values;
		    // Movement
		    x = (int)values[0];
		    y = (int)values[1];
		    z = (int)values[2];
		}
	}
	*/




}
