package br.com.bizsys.tntairfight;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sessions.AdvSession;
import sessions.ConfigSave;
import sessions.FightSession;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.TNTAirFight;

import com.zsoft.SignalA.Hubs.HubOnDataCallback;


@SuppressLint("DefaultLocale")
public class Preparados extends Activity {
	public static MySession my_session;
	public static AdvSession adv_session;
	public FightSession fight_session;
	public TNTAirFight tnt;
	public HubOnDataCallback hubOnDataCallback;
	public static Activity act;	
	public static ConfigSave config;
	
	
	@SuppressLint({ "NewApi", "DefaultLocale" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preparados);
		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       
		my_session = new MySession(this);		
		adv_session = new AdvSession(this);
		fight_session = new FightSession(this);
		tnt = new TNTAirFight(this); 
		act = this;
		config = new ConfigSave(this);
		
		if(config.getSons()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
				TNTAirFight.torcida = MediaPlayer.create(Preparados.this, R.raw.torcidade);
				TNTAirFight.torcida.start();
			}
		}
		
		if(fight_session.getPlayer1()){
			new Thread(new Runnable() {
		        public void run() {
		        	TNTAirFight.sleep();
		        	runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ArrayList<Object> params = new ArrayList<Object>();
							params.add(my_session.getId());
							params.add("MATCH");
							params.add(adv_session.getId());
							StartFight.signal_conexao.SendToSpecific(params);
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
							ArrayList<Object> params = new ArrayList<Object>();
							params.add(my_session.getId());
							params.add("FIGHT");
							params.add(adv_session.getId());
							StartFight.signal_conexao.SendToSpecific(params);
						}
		        	});
		        }
		    }).start();
		}
		
		
		
        TextView nome = (TextView) findViewById(R.id.nome);
		nome.setText(my_session.getNome().toUpperCase());
		String text_my_vitorias = TNTAirFight.espacamento(String.format("%s VITÓRIAS", my_session.getVitorias())).toString();
		String text_my_idade =  TNTAirFight.calculaIdade(my_session.getIdade());
		TextView idade = (TextView) findViewById(R.id.idade);
		idade.setText(String.format("%s,  %s", text_my_idade, text_my_vitorias));
		TextView cidade = (TextView) findViewById(R.id.cidade);
		cidade.setText(TNTAirFight.espacamento(String.format(" %s, %s", my_session.getCidade().toUpperCase(), my_session.getEstado().toUpperCase())));
		if(!my_session.getImage().equals(""))
		{
			new DownloadImage(my_session.getImage(), act, R.id.img_user, R.id.progress_img_user);
		}
		
		
		
		
		TextView adv_nome = (TextView) findViewById(R.id.adv_nome);
		adv_nome.setText(adv_session.getNome().toUpperCase());
		TextView adv_idade = (TextView) findViewById(R.id.adv_idade);
		String text_adv_vitorias = TNTAirFight.espacamento(String.format("%s VITÓRIAS", adv_session.getVitorias())).toString();
		String text_adv_idade =  TNTAirFight.calculaIdade(adv_session.getIdade());
		adv_idade.setText(String.format("%s,  %s", text_adv_idade, text_adv_vitorias));
		TextView adv_cidade = (TextView) findViewById(R.id.adv_cidade);
		adv_cidade.setText(TNTAirFight.espacamento(String.format(" %s, %s", adv_session.getCidade().toUpperCase(), adv_session.getEstado().toUpperCase())));
		
		if(!adv_session.getImage().equals(""))
		{
			new DownloadImage(adv_session.getImage(), act, R.id.img_user_adv, R.id.progress_img_user_adv);
		}
		
	}
	
	//Ativada em StartFight
	public void FIGHT(){		
		act.startActivity(new Intent(act, Luta.class));		
		if(TNTAirFight.torcida != null){
			TNTAirFight.torcida.stop();
		}
		act.finish();
	}
	
	public void onDestroy(){
		super.onDestroy();
		if(TNTAirFight.torcida != null){
			TNTAirFight.torcida.stop();
		}		
	}
	
	public void onPause(){
		super.onPause();	
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
	}
	
	@Override
	public void onBackPressed() {}

}
