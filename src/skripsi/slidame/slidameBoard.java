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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.renderscript.Font;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The game class that consists of the tiles (and views) and determines if the
 * user has correctly solved the puzzle.
 * TileViews are stationary.  Tiles move around and are given to TileViews to 
 * display.
 * @author wadechatam
 *
 */
public final class slidameBoard  {

   private static slidameBoard board = null; // Singleton instance - can be 
                                 // changed by calling
                                 // createGameBoard class method
   private List<Tile> tiles = null;
   private List<TileView> tileViews = null;
   private List<TableRow> tableRow = null;
   private TextView vb;
   private Tile theBlankTile; // The empty square
   private Bitmap bitmap; // Picture used for puzzle
   private TableLayout parentLayout;
   private int gridSize;
   private Context context;
   public static final int GAME_DELAY= 2500;
   public static final int ANIM_DELAY= 1050;
   private MediaPlayer mp,mp2;
   private ImageView mCompleteView;
   private int boardWidth,boardHeight,fin;
   private short blankRow,blankColumn;
   private long timer;
   public static String DB_NAME = "scoreDB.db";
   public SQLiteDatabase highsocreDB;
   private Chronometer crono;
   private PuzzleActivity puz;
   private int permutations; 
   private int moveCount; 

   /* (non-Javadoc)
    * Private constructor to force access to class instance through 
    * createGameBoard method.
    */
   private slidameBoard(Context context, 
                 Bitmap bitmap, 
                 TableLayout parentLayout, 
                 int width, 
                 int height,
                 int gridSize) {      
      this.context = context;
      this.boardWidth = width;
      this.boardHeight = height;
      this.bitmap = Bitmap.createScaledBitmap(bitmap, 
            this.boardWidth, 
            this.boardHeight, 
            true);
      this.moveCount = 0;      
      this.parentLayout = parentLayout;
      this.gridSize = gridSize;
      init();
   }

   /**
    * Creates an instance of GameBoard.
    * 
    * @param context
    * @param bitmap The picture to be used for the puzzle
    * @param parentLayout The primary table layout for storing TileViews
    * @param width The board width in pixels
    * @param height The board height in pixels
    * @param gridSize The row and column count. (3 = 3x3, 4 = 4x4, etc.)
    * @return an instance of the GameBoard that will be used for game play.
    */
   public static slidameBoard createGameBoard(Context context, 
                                 Bitmap bitmap, 
                                 TableLayout parentLayout,
                                 int width,
                                 int height,
                                 int gridSize) {

      board = new slidameBoard(context, 
                       bitmap, 
                       parentLayout, 
                       width, 
                       height, 
                       gridSize);
       
      return board;
   }
   
   /* (non-Javadoc)
    * Create tiles and views. Then shuffle.
    */
   private void init() {
      initializeLists();   
      createTiles();
      createTileViews();
      shuffleTiles();
      check();
      mCompleteView = new ImageView(context);
      bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gambar);
	  mCompleteView.setImageBitmap(bitmap);
   }
   
   private void check(){
	   fin=0;
	   //chronometer
	   crono = new Chronometer(context);
	   crono.setBase(SystemClock.elapsedRealtime());
	   crono.start();
	   crono.setGravity(Gravity.CENTER_HORIZONTAL);
	   crono.setTypeface(Typeface.DEFAULT_BOLD);
	   
	   //Data
	   String DB_PATH = context.getFilesDir().getPath();
	   highsocreDB = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME,null,SQLiteDatabase.CREATE_IF_NECESSARY);
	   highsocreDB.execSQL("CREATE TABLE IF NOT EXISTS HIGHSCORE"+gridSize+"(" +
			   		"TIME long(9),MOVE integer(4),GRID integer(2));"
				   );
	   
	   //
	   vb = new TextView(context);
	   vb.setGravity(Gravity.CENTER_HORIZONTAL);
	   vb.setTextColor(Color.CYAN);
	   parentLayout.addView(crono);
	   mp = MediaPlayer.create(context,R.raw.warn);
	   mp2 = MediaPlayer.create(context,R.raw.applause);
   }

   /* (non-Javadoc)
    * Creates new objects for tiles, tile views, and table rows.
    */
   private void initializeLists() {
      if (tiles == null) {
         tiles = new ArrayList<Tile> (gridSize * gridSize);
      } else {
         // Be sure to clean up old tiles
         for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).freeBitmap();
            tiles = new ArrayList<Tile> (gridSize * gridSize);
         }
      }
      tileViews = new ArrayList<TileView> (gridSize * gridSize);
      tableRow = new ArrayList<TableRow> (gridSize);

      for (int row = 0; row < gridSize; row++) {
         tableRow.add(new TableRow(context));            
      }
   }

   /* (non-Javadoc)
    * Cut the picture into pieces and assign it to tiles.
    */
   private void createTiles() {
      int tile_width = boardWidth / gridSize;
      int tile_height = boardHeight / gridSize;

      for (short row = 0; row < gridSize; row++) {
         for (short column = 0; column < gridSize; column++) {
            Bitmap bm = Bitmap.createBitmap(bitmap,
                  column * tile_width,
                  row * tile_height,
                  tile_width,
                  tile_height);

            // if final, Tile -> blank
            if ((row == gridSize - 1) && (column == gridSize - 1)) {
               bm = Bitmap.createBitmap(tile_width, 
                                  tile_height, 
                                  bm.getConfig());
               bm.eraseColor(Color.BLACK);
               theBlankTile = new Tile(bm, row, column);
               tiles.add(theBlankTile);
            } else {
               tiles.add(new Tile(bm, row, column));
            }            
         } // end column         
      } // end row
      bitmap.recycle();
   }   

   /* (non-Javadoc)
    * Initialize the tile views and add them to the table layout.
    */
   private void createTileViews() {      
      for (short row = 0; row < gridSize; row++) {
         for (short column = 0; column < gridSize; column++) {
            TileView tv = new TileView(context, row, column);
            tileViews.add(tv);             
            tableRow.get(row).addView(tv);
         } // end column
         parentLayout.addView(tableRow.get(row));
      } // end row
   }

   /**
    * Re-arrange the tiles into a solvable puzzle.
    */
   public void shuffleTiles() {
      do {
         Collections.shuffle(tiles);
         //tiles.remove(theBlankTile);
         //tiles.add(theBlankTile);
         for (short row = 0; row < gridSize; row++) {
            for (short column = 0; column < gridSize; column++) {
               tileViews.get(row * gridSize + column).setCurrentTile(
                     tiles.get(row * gridSize + column));
            }
         }
      } while (!isSolvable());
      moveCount = 0;
   }
   /**
    * Notifies the game board that a tile view has been touched.  Typically
    * only called by the TileViews.
    * @param tv the TileView that was touched.
    */
   public static void notifyTileViewUpdate(TileView tv) {
      board.tileViewUpdate(tv);
   }
   private void tileViewUpdate(TileView tv) {
      swapTileWithBlank(tv);
   }
   /**
    * Get the current "score"
    * @return the number of tile moves
    */
   public int getMoveCount() {
      return moveCount;
   }
   
   private boolean isSolvable() {
      permutations = 0; // the number of incorrect orderings of tiles
      short currentTileViewLocationScore;
      short subsequentTileViewLocationScore;
      // Start at the first tile
      for (int i = 0; i < tiles.size() - 2; i++) {
         Tile tile = tiles.get(i);
         // Determine the tile's location value
         currentTileViewLocationScore = computeLocationValue(tile.getCorrectLocation());
         // Compare the tile's location score to all of the tiles that
         // follow it
         for (int j = i + 1; j < tiles.size() - 1; j++) {
            Tile tSub = tiles.get(j);
            
            subsequentTileViewLocationScore = computeLocationValue(tSub.getCorrectLocation());
               
            // If a tile is found to be out of order, increment the number
            // of permutations.
            if (currentTileViewLocationScore>subsequentTileViewLocationScore) {
               permutations++;
            }
         }
      }
      // return whether number of permutations is even
      return permutations % 2 == 0;
   }

   /* (non-Javadoc)
    * Determine if the entire board is correctly solved by all of the tiles
    * being in the correct location.
    */
   private boolean isCorrect() {
      // if a single tile is incorrect, return false
      for (Tile tile : tiles) {
         if (!tile.isCorrect()) {
            return false;
         }
      }
      return true;
   }

   /* (non-Javadoc)
    * Determine if the tile view clicked is adjacent to the blank tile. If so,
    * swap their locations. If this swap solves the puzzle, congratulate the
    * user on being the smartest person in the world (or insult them for taking
    * so many moves).  
    */
   
   public void right(){
	   if(fin!=2){
		   blankColumn =theBlankTile.getCurrentLocation().getColumn();
		   blankRow =theBlankTile.getCurrentLocation().getRow();
		   if(blankColumn<(gridSize-1)){
			   swap(tileViews.get((blankRow * gridSize + (blankColumn+1))));
			   tileViews.get((blankRow * gridSize + (blankColumn+1))).startAnimation(AnimationUtils.loadAnimation(
		                  this.context, R.anim.left_animation));
		   }else{
			   mp.start();
		   }   
	   }
	   
   }
   public void left(){
	   if(fin!=2){
		   blankColumn =theBlankTile.getCurrentLocation().getColumn();
		   blankRow =theBlankTile.getCurrentLocation().getRow();
		   if(blankColumn>0){
			   swap(tileViews.get((blankRow * gridSize + (blankColumn-1))));
			   tileViews.get((blankRow * gridSize + (blankColumn-1))).startAnimation(AnimationUtils.loadAnimation(
		                  this.context, R.anim.right_animation));
			   
		   }else{
			   mp.start();
		   }   
	   }
	   
   }
   public void up(){
	   if(fin!=2){
		   blankColumn =theBlankTile.getCurrentLocation().getColumn();
		   blankRow =theBlankTile.getCurrentLocation().getRow();
		   if(blankRow>0){
			   swap(tileViews.get(((blankRow-1) * gridSize + blankColumn)));
			   tileViews.get(((blankRow-1) * gridSize + blankColumn)).startAnimation(AnimationUtils.loadAnimation(
		                  this.context, R.anim.down_animation));
		   }else{
			  mp.start();
		   }   
	   }
	   
   }
   public void down(){
	   if(fin!=2){
		   blankColumn =theBlankTile.getCurrentLocation().getColumn();
		   blankRow =theBlankTile.getCurrentLocation().getRow();
		   if(blankRow<(gridSize-1)){
			   swap(tileViews.get(((blankRow+1) * gridSize + blankColumn)));
			   tileViews.get(((blankRow+1) * gridSize + blankColumn)).startAnimation(AnimationUtils.loadAnimation(
		                  this.context, R.anim.up_animation));
		   }else{
			   mp.start();
		   }   
	   }
	   
   }
   
   //SWAP
   private void swap(TileView tv){
	   Tile tile = tv.getCurrentTile();
	   TileView theBlankTileView = tileViews.get(computeLocationValue(theBlankTile.getCurrentLocation()));
	   theBlankTileView.setCurrentTile(tile);
       tv.setCurrentTile(theBlankTile);
       moveCount++;
       if(isCorrect()){
    	   finish();
       }
   }
   
   private void finish(){
	   fin = 2;
	   mp2.start();
	   timer = SystemClock.elapsedRealtime()-crono.getBase();
	   crono.stop();
	   timer = timer/1000;
	   parentLayout.removeAllViews();
	   parentLayout.addView(mCompleteView, boardWidth, boardWidth);
	   parentLayout.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.fadein));
	   //finish alert
	   Toast mToast = Toast.makeText(context,"CELAMADH EAGH "+moveCount+"|"+timer,3);
       mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0,0);
       mToast.show();
       checkhighscore(timer,moveCount,gridSize);
       
       /*
	   AlertDialog.Builder alert = new AlertDialog.Builder(context);
	   alert.setMessage("CELAMADH EAGH "+moveCount+"|"+timer);
	   
	   alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	});
	   alert.show();*/
	   //Toast.makeText(context, "CELAMADH EAGH ||"+moveCount, Toast.LENGTH_SHORT).show();
	   
   }
   
   //cek highscore
   private void checkhighscore(long timer, int moveCount,int gridSize){
	   //insert
	   highsocreDB.execSQL("INSERT INTO HIGHSCORE"+gridSize+" VALUES("+timer+
			   ","+moveCount+","+gridSize+");"
			   );
	   /*
	   Cursor gethighscorealter = highsocreDB.rawQuery("SELECT MIN(TIME) FROM HIGHSCORE"+gridSize+";"
			   , null);
	   /*
	   Cursor gethighscore=highsocreDB.rawQuery("SELECT*FROM HIGHSCORE"+gridSize+
			   " ORDER BY TIME;"
			   , null);
		gethighscore.moveToFirst();
			   
	   gethighscorealter.moveToFirst();
	   */
	   
   }
   
   //swap
   private void swapTileWithBlank(TileView tv) {
      Tile tile = tv.getCurrentTile();
      
      TileView theBlankTileView = tileViews.get(
            computeLocationValue(theBlankTile.getCurrentLocation()));
      

      if (tile.getCurrentLocation().isAdjacent(
            theBlankTile.getCurrentLocation())) {
         
         // Animate tile movement
         if (tile.getCurrentLocation().getColumn() < 
            theBlankTile.getCurrentLocation().getColumn()) {
            theBlankTileView.bringToFront();
            //LEFT
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.left_animation));
            
         } else if (tile.getCurrentLocation().getColumn() > 
                  theBlankTile.getCurrentLocation().getColumn()) {
            theBlankTileView.bringToFront();
            //RIGHT
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.right_animation));
            
         } else if (tile.getCurrentLocation().getRow() < 
                  theBlankTile.getCurrentLocation().getRow()) {
            theBlankTileView.bringToFront();
            //UP            
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.up_animation));
            
         } else if (tile.getCurrentLocation().getRow() > 
                  theBlankTile.getCurrentLocation().getRow()) {
            theBlankTileView.bringToFront();
            //DOWN
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.down_animation));
         }         
         theBlankTileView.setCurrentTile(tile);
         tv.setCurrentTile(theBlankTile);
         moveCount++;
      }            

   }

   /* (non-Javadoc)
    * Return the location on the board for the given row and column, in the
    * range 0 to gridSize-1.  For instance, on a 4x4 grid the 2nd row 2nd
    * column should have the value 5.
    */
   private short computeLocationValue(short row, short column) {
      return (short) (gridSize * row + column);
   }
   
   /* (non-Javadoc)
    * Return the location on the board for the given row and column, in the
    * range 0 to gridSize-1.  For instance, on a 4x4 grid the 2nd row 2nd
    * column should have the value 5.
    */
   private short computeLocationValue(TileLocation location) {
      return computeLocationValue(location.getRow(), location.getColumn());
   }
   
   /**
    * Sets the visibility of the titles for the tiles.
    * @param visible True if the tile's correct location should be displayed.
    * False, otherwise.
    */
   public void setNumbersVisible(boolean visible) {
      for (TileView tv : tileViews) {
         tv.setNumbersVisible(visible);
      }
   }
   
   /**
    * Returns the number of rows and columns in this instance of the game board
    * @return number of rows and columns
    */
   public int getGridSize() {
      return gridSize;
   }
}

//database
/*
class data extends SQLiteOpenHelper{
	public static String DB_PATH = "/data/data/skripsi/databases/";
	public static String DB_NAME = "scoreDB";
	public SQLiteDatabase highsocreDB;
	
	public data(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		highsocreDB = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME,null,SQLiteDatabase.CREATE_IF_NECESSARY);
		highsocreDB.execSQL("CREATE TABLE IF NOT EXISTS HIGHSCORE"+gridSize+"(" +
			   		"TIME INT(9),MOVE INT(4),GRID INT(2);"
				   );
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
*/
