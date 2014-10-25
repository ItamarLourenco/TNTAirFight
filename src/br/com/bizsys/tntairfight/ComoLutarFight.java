package br.com.bizsys.tntairfight;

import org.json.JSONException;
import org.json.JSONObject;

import sessions.AdvSession;
import sessions.FightSession;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import br.com.bizsys.tntairflight.R;
import classes.ServiceHandler;
import classes.TNTAirFight;

@SuppressLint("NewApi")
public class ComoLutarFight extends Activity {
	public MySession my_session;
	public AdvSession adv_session;
	public FightSession fight_session;
	public TNTAirFight tnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_como_lutar_fight);
		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        my_session = new MySession(this);
		adv_session = new AdvSession(this);
		fight_session = new FightSession(this);
		tnt = new TNTAirFight(this);
        
        ImageView img = (ImageView)findViewById(R.id.loading_dot);
        img.setBackgroundResource(R.drawable.loading_dot);
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
        frameAnimation.start();
        
        
        if(fight_session.getPlayer1()){
	        new Thread(new Runnable() {
		        public void run() {
		        	TNTAirFight.sleep();
		        	runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new SendMatch().execute();
						}
		        	});
		        }
		    }).start();
        }else{
        	new Thread(new Runnable() {
		        public void run() {
		        	TNTAirFight.sleep();
		        	runOnUiThread(new Runnable() {
						@Override
						public void run() {
							startActivity(new Intent(ComoLutarFight.this, Preparados.class));
							finish();
						}
		        	});
		        }
		    }).start();
        }
	}
	

	
	public class SendMatch extends AsyncTask<String, Void, Void>{
		@Override
		protected Void doInBackground(String... params) {
			JSONObject json = new JSONObject();
			try {
				if(fight_session.getPlayer1()){
					json.put("pOneUserId", my_session.getId());
					json.put("pTwoUserId", adv_session.getId());
				}else{
					json.put("pOneUserId", adv_session.getId());
					json.put("pTwoUserId", my_session.getId());
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ServiceHandler service = new ServiceHandler();
			final String json_retorno = service.makeServiceCall(getString(R.string.url_match), ServiceHandler.POST, String.valueOf(json), null, null);
			try {
				JSONObject match = new JSONObject(json_retorno);
				String match_id = new JSONObject(match.getString("Object")).getString("Id");
				fight_session.setMatchId(match_id);
				
				startActivity(new Intent(ComoLutarFight.this, Preparados.class));
				finish();				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}

	
	@Override
	public void onBackPressed() {}
	
	public void onPause(){
		super.onPause();	
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
	}

}
