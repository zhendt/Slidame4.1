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

import android.graphics.Bitmap;

/**
 * This is the basic class for storing part of the picture that makes up the 
 * game board.  It is "mobile" in the sense that it moves around the game board
 * by being passed to different @class TileView objects that will display its
 * image.
 * @author wadechatam
 *
 */
public final class Tile {
   private short row,column;
   private TileLocation currentLocation; // current spot on game board
   private final TileLocation correctLocation; // where it's trying to go
   private Bitmap bitmap; // the partial picture that when combined with
                         // the other Tiles will create the original
                                // picture
   
   /**
    * @param bitmap the partial picture
    * @param correctRow objective row (0-based index)
    * @param correctColumn objective column (0-based index)
    */
   public Tile(Bitmap bitmap, short correctRow, short correctColumn) {
      this.bitmap = bitmap;
      currentLocation = TileLocation.getInstance(correctRow, correctColumn);
      correctLocation = TileLocation.getInstance(correctRow, correctColumn);
   }
   
   /**
    * Accessor method for the tile's current location.
    * @return the current location of the tile on the game board
    */
   public TileLocation getCurrentLocation() {
      return currentLocation;
   }
   
   /**
    * Accessor method for the tile's final location.
    * @return the final locaiton of the tile on the game board
    */
   public TileLocation getCorrectLocation() {
      return correctLocation;
   }
   
   /**
    * Is this tile in the location such that it would correctly display the
    * image if combined with the other tiles in their correct locations?
    * @return true if the tile is in its correct location
    */
   public boolean isCorrect() {
      return currentLocation.equals(correctLocation);
   }
   
   /**
    * The tile has been moved, and its location should be updated.
    * @param location the new current location of the tile
    */
   public void setCurrentLocation(TileLocation location) {
      this.currentLocation = location;
   }
   
   /**
    * The part of the picture that this tile contains
    * @return the partial picture contained within this tile
    */
   public Bitmap getBitmap() {
      return bitmap;
   }
   public short getRow() {
	   return row;
	   }
   public short getColumn() {
	   return column;
	   }
	   
   
   /**
    * Clear the memory used by the bitmap.  This should only be called when
    * the tile is no longer needed as future calls to getBitmap will return
    * null.
    */
   public void freeBitmap() {
      bitmap.recycle();
      bitmap = null;
   }
}
