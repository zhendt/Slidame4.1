package skripsi.slidame;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Splash extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashmenu);
	
		new Handler().postDelayed(new Thread(){
		@Override
		public void run() {
			
			Intent mainMenu = new Intent(Splash.this,PuzzleActivity.class);
			Splash.this.startActivity(mainMenu);
			Splash.this.finish();
			overridePendingTransition(R.anim.right_animation, R.anim.left_animation);
			}
		},slidameBoard.GAME_DELAY);
	 		
	}
}
