package br.com.bizsys.tntairfight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.TNTAirFight;

public class TermoDeUso extends Activity {

	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termo_de_uso);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TNTAirFight tnt = new TNTAirFight(this);
        tnt.setFont((TextView) findViewById(R.id.termo));
        
        final ImageView btn_voltar = (ImageView) findViewById(R.id.btn_voltar);
        btn_voltar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						btn_voltar.setImageResource(R.drawable.btn_voltar_hover);
					break;
					case MotionEvent.ACTION_UP:
						btn_voltar.setImageResource(R.drawable.btn_voltar);
						
						finish();
						overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
					break;
				}
				return true;
			}
		});
        
		
	}

	public void onPause(){
		super.onPause();	
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
	}
}
