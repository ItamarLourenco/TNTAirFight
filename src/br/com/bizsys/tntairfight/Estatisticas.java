package br.com.bizsys.tntairfight;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.AdvSession;
import sessions.ConfigSave;
import sessions.FightSession;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.ServiceHandler;
import classes.TNTAirFight;

@SuppressLint("NewApi")
public class Estatisticas extends Activity {
	public FightSession fight_session;
	ProgressDialog progress_dialog = null;
	public boolean player_1 = false;
	public TNTAirFight tnt;
	public boolean ganhou = false;
	public TextView p1_armlock_valor;
	public TextView p1_chute_alto_valor;
	public TextView p1_cruzado_valor;
	public TextView p1_guilhotinha_valor;
	public TextView p1_jab_valor;
	public TextView p1_joelhada_valor;
	public TextView p1_mata_leao_valor;
	public TextView p1_gancho_valor;
	public TextView p2_armlock_valor;
	public TextView p2_chute_alto_valor;
	public TextView p2_cruzado_valor;
	public TextView p2_guilhotinha_valor;
	public TextView p2_jab_valor;
	public TextView p2_joelhada_valor;
	public TextView p2_mata_leao_valor;
	public TextView p2_gancho_valor;
	public ImageView won;
	public ImageView btn_ok;
	public TextView ver_ranking;
	public TextView pOne;
	public TextView pTwo;
    public TextView p1_chute_frontal_valor;
    public TextView p2_chute_frontal_valor;
    public boolean empate_p1 = false;
    public boolean empate_p2 = false;
    public MySession my_session;
    public static ConfigSave config;
    public AdvSession adv_session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_estatisticas);
		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		tnt = new TNTAirFight(this);
		fight_session = new FightSession(this);
		my_session = new MySession(this);
		config = new ConfigSave(this);
		adv_session = new AdvSession(this);
		
		
		if(config.getSons()){
			TNTAirFight.vencedor  = MediaPlayer.create(Estatisticas.this, R.raw.vencedoreh);
			TNTAirFight.vencedor.start();
		}
        
        
        String math_id = fight_session.getMatchId();
        String my_id = my_session.getId();
        player_1 = fight_session.getPlayer1();
		  
		p1_armlock_valor = (TextView) findViewById(R.id.p1_armlock_valor); 

		p1_chute_frontal_valor = (TextView) findViewById(R.id.p1_chute_frontal_valor); 
		p1_chute_alto_valor = (TextView) findViewById(R.id.p1_chute_alto_valor); 
		p1_cruzado_valor = (TextView) findViewById(R.id.p1_cruzado_valor);
		p1_guilhotinha_valor = (TextView) findViewById(R.id.p1_guilhotinha_valor);
		p1_jab_valor = (TextView) findViewById(R.id.p1_jab_valor);
		p1_joelhada_valor = (TextView) findViewById(R.id.p1_joelhada_valor);
		p1_mata_leao_valor = (TextView) findViewById(R.id.p1_mata_leao_valor);
		p1_gancho_valor = (TextView) findViewById(R.id.p1_gancho_valor);
		p2_armlock_valor = (TextView) findViewById(R.id.p2_armlock_valor);
		p2_chute_alto_valor = (TextView) findViewById(R.id.p2_chute_alto_valor);
		p2_chute_frontal_valor = (TextView) findViewById(R.id.p2_chute_frontal_valor);
		p2_cruzado_valor = (TextView) findViewById(R.id.p2_cruzado_valor);
		p2_guilhotinha_valor = (TextView) findViewById(R.id.p2_guilhotinha_valor); 
		p2_jab_valor = (TextView) findViewById(R.id.p2_jab_valor);
		p2_joelhada_valor = (TextView) findViewById(R.id.p2_joelhada_valor); 
		p2_mata_leao_valor = (TextView) findViewById(R.id.p2_mata_leao_valor);
		p2_gancho_valor = (TextView) findViewById(R.id.p2_gancho_valor); 
        won = (ImageView) findViewById(R.id.won); 
        btn_ok = (ImageView) findViewById(R.id.btn_ok);
        ver_ranking = (TextView) findViewById(R.id.ver_ranking);
        pOne = (TextView) findViewById(R.id.pOne);
        pTwo = (TextView) findViewById(R.id.pTwo);

        
        btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				if(config.getSons()){
					if(TNTAirFight.getInstance().audio_splash_loop != null){
						TNTAirFight.getInstance().audio_splash_loop = MediaPlayer.create(Estatisticas.this, R.raw.audio_splash_loop);
						TNTAirFight.getInstance().audio_splash_loop.setLooping(true);
						TNTAirFight.getInstance().audio_splash_loop.start();
					}
				}
			}
		});
        
        ver_ranking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Estatisticas.this, RankingGeral.class));
				if(config.getSons()){
					if(TNTAirFight.getInstance().audio_splash_loop != null){
						TNTAirFight.getInstance().audio_splash_loop = MediaPlayer.create(Estatisticas.this, R.raw.audio_splash_loop);
						TNTAirFight.getInstance().audio_splash_loop.setLooping(true);
						TNTAirFight.getInstance().audio_splash_loop.start();
					}
				}
				finish();
			}
		});
        
        new MatchResult().execute(math_id, my_id);
	}
	
	public class MatchResult extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(Estatisticas.this);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.show();
		}
		
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(String... params) {
			String math_id = params[0];
			String my_id = params[1];
			
	        TNTAirFight.sleep(5000);
			
			
			JSONObject json = new JSONObject();
			try {
				json.put("idMatch", math_id);
				json.put("isPlayerOne", player_1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			

			
			ServiceHandler service = new ServiceHandler();
			String json_retorno = service.makeServiceCall(getString(R.string.url_macth), ServiceHandler.PUT, String.valueOf(json), null, null);
						
			try {						
				JSONObject obj_json = new JSONObject(json_retorno);
				if(obj_json.getString("Status").equalsIgnoreCase("Error")){
					tnt.AlertTNT(obj_json.getString("Message"), Estatisticas.this);
				}else if (obj_json.getString("Status").equalsIgnoreCase("Ok")){
					JSONArray json_arr = new JSONArray(obj_json.getString("Object").toString());
					for(int i =0; i<json_arr.length(); i++){
						final JSONObject json_obj = new JSONObject(json_arr.get(i).toString());
						if(i == 0){
							String id_user = json_obj.getString("idUser");
							String won = json_obj.getString("won");
							
							if(won.equals("true")){
								empate_p1 = true;
							}
							
							if(my_id.equals(id_user)){
								if(won.equals("true")){
									ganhou = true;
								}else{
									ganhou = false;
								}
							}
							
							
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										pOne.setText(json_obj.getString("pOne").toUpperCase());
										
										p1_armlock_valor.setText( (json_obj.getString("totalArmLock").equals("null") ? "0" : json_obj.getString("totalArmLock")) );
										p1_chute_alto_valor.setText((json_obj.getString("totalChuteAlto").equals("null") ? "0" : json_obj.getString("totalChuteAlto")) );
										p1_chute_frontal_valor.setText((json_obj.getString("totalChuteFrontal").equals("null") ? "0" : json_obj.getString("totalChuteFrontal")) );
										p1_cruzado_valor.setText((json_obj.getString("totalCruzado").equals("null") ? "0" : json_obj.getString("totalCruzado")) );
										p1_guilhotinha_valor.setText((json_obj.getString("totalGuilhotina").equals("null") ? "0" : json_obj.getString("totalGuilhotina")) );
										p1_jab_valor.setText((json_obj.getString("totalJab").equals("null") ? "0" : json_obj.getString("totalJab")) );
										p1_joelhada_valor.setText((json_obj.getString("totalJoelhada").equals("null") ? "0" : json_obj.getString("totalJoelhada")) );
										p1_mata_leao_valor.setText((json_obj.getString("totalMataLeao").equals("null") ? "0" : json_obj.getString("totalMataLeao")) );
										p1_gancho_valor.setText((json_obj.getString("totalUpper").equals("null") ? "0" : json_obj.getString("totalUpper")) );
										
										if(player_1 == true)
										{
											if(!my_session.getImage().equals(""))
											{
												new DownloadImage(my_session.getImage(), Estatisticas.this, R.id.img_user, R.id.progress_img_user);
											}
											
											if(!adv_session.getImage().equals(""))
											{
												new DownloadImage(adv_session.getImage(), Estatisticas.this, R.id.img_user_adv, R.id.progress_img_user_adv);
											}
										}else{
											if(!adv_session.getImage().equals(""))
											{
												new DownloadImage(adv_session.getImage(), Estatisticas.this, R.id.img_user, R.id.progress_img_user);
											}
											
											if(!my_session.getImage().equals(""))
											{
												new DownloadImage(my_session.getImage(), Estatisticas.this, R.id.img_user_adv, R.id.progress_img_user_adv);
											}
										}
										
										
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
							
							
						}else{
							String id_user = json_obj.getString("idUser");
							String won = json_obj.getString("won");
							
							if(won.equals("true")){
								empate_p2 = true;
							}
							
							if(my_id.equals(id_user)){
								if(won.equals("true")){
									ganhou = true;
								}else{
									ganhou = false;
								}
							}							
							
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										pTwo.setText(json_obj.getString("pTwo").toUpperCase());
										
										p2_armlock_valor.setText( (json_obj.getString("totalArmLock").equals("null") ? "0" : json_obj.getString("totalArmLock")) );
										p2_chute_alto_valor.setText((json_obj.getString("totalChuteAlto").equals("null") ? "0" : json_obj.getString("totalChuteAlto")) );
										p2_chute_frontal_valor.setText((json_obj.getString("totalChuteFrontal").equals("null") ? "0" : json_obj.getString("totalChuteFrontal")) );
										p2_cruzado_valor.setText((json_obj.getString("totalCruzado").equals("null") ? "0" : json_obj.getString("totalCruzado")) );
										p2_guilhotinha_valor.setText((json_obj.getString("totalGuilhotina").equals("null") ? "0" : json_obj.getString("totalGuilhotina")) );
										p2_jab_valor.setText((json_obj.getString("totalJab").equals("null") ? "0" : json_obj.getString("totalJab")) );
										p2_joelhada_valor.setText((json_obj.getString("totalJoelhada").equals("null") ? "0" : json_obj.getString("totalJoelhada")) );
										p2_mata_leao_valor.setText((json_obj.getString("totalMataLeao").equals("null") ? "0" : json_obj.getString("totalMataLeao")) );
										p2_gancho_valor.setText((json_obj.getString("totalUpper").equals("null") ? "0" : json_obj.getString("totalUpper")) );
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});							
						}
					}
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(empate_p1 == true && empate_p2 == true){
								won.setImageResource(R.drawable.img_final_empate);
								
								if(config.getSons()){
									TNTAirFight._vencedor  = MediaPlayer.create(Estatisticas.this, R.raw.venceu);
									TNTAirFight._vencedor.start();
								}
							}else{
							
								if(ganhou){
									//USER GANHOU
									won.setImageResource(R.drawable.detonou);
									
									if(config.getSons()){
										TNTAirFight._vencedor  = MediaPlayer.create(Estatisticas.this, R.raw.venceu);
										TNTAirFight._vencedor.start();
									}
								}else{
									//USER PERDEU									
									won.setImageResource(R.drawable.que_surra);
									
									if(config.getSons()){ 
										TNTAirFight._vencedor  = MediaPlayer.create(Estatisticas.this, R.raw.perdeu);
										TNTAirFight._vencedor.start();
									}
								}
							}
							
						}
					});
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			progress_dialog.dismiss();
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
