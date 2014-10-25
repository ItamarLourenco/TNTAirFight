package br.com.bizsys.tntairfight;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sessions.AdvSession;
import sessions.FightSession;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.SignalConexao;
import classes.TNTAirFight;

import com.zsoft.SignalA.Hubs.HubOnDataCallback;


@SuppressLint("NewApi")
public class Espera extends Activity {
	public static AdvSession adv_session;
	public static MySession my_session;
	public TNTAirFight tnt;
	public static SignalConexao signal_conexao;
	private static Timer timer = null;
    private static TimerTask task;
    private final Handler handler = new Handler();
    public HubOnDataCallback hubOnDataCallback;
    public FightSession fight_session;
    public static Activity act;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_espera);	
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        my_session = new MySession(this);
        adv_session = new AdvSession(this);
        tnt = new TNTAirFight(this);
        signal_conexao = StartFight.signal_conexao;
        fight_session = new FightSession(this);
        act = this;
        timer = new Timer();
        
        ativaVolta();        
        
        TextView nome = (TextView) findViewById(R.id.nome);
        nome.setText(my_session.getNome());
        
        TextView cidade = (TextView) findViewById(R.id.cidade);
        cidade.setText(String.format(" %s, %s", my_session.getCidade(), my_session.getEstado()));
        
        TextView idade = (TextView) findViewById(R.id.idade);
        idade.setText(String.format("%s, %s VITÓRIAS", classes.TNTAirFight.calculaIdade(my_session.getIdade()), my_session.getVitorias()));
       
        
        ImageView loading = (ImageView)findViewById(R.id.loading);
        loading.setBackgroundResource(R.drawable.loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) loading.getBackground();
        frameAnimation.start();
        
        
        if(fight_session.getPlayer1()){
            ArrayList<Object> params = new ArrayList<Object>();
            params.add(my_session.getId());
            if(my_session.getDistancia() == true){
            	params.add("Fight, true");
            }else{
            	params.add("Fight, false");
            }
            
            params.add(adv_session.getId());
            signal_conexao.SendToSpecific(params);
        }      
	}
	
	//Ativada em StartFight
	public void Negado(){
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle("Bora pra outro!");
        alert.setMessage("A conexão do seu adversário caiu ou ele ficou com medo.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {     
            	timer.cancel();
            	timer = null;
        		task = null;
            	act.finish();
            }
        });
        alert.show();
	}
	
	
	//Ativada em StartFight
	public void Aceito() {
		new Thread(new Runnable() {
	        public void run() {
	        	TNTAirFight.sleep();
	        	act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						timer.cancel();
						timer = null;
                		task = null;
						act.startActivity(new Intent(act, ComoLutarFight.class));
						act.finish();
						act.overridePendingTransition(R.anim.top_down_in, R.anim.top_down_out);
					}
	        	});
	        }
	    }).start();
	}
	
	
	//Ativada em StartFight
	public void MATCH() {
		new Thread(new Runnable() {
	        public void run() {
	        	TNTAirFight.sleep();
	        	act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						timer.cancel();
						timer = null;
                		task = null;
                		
                		ArrayList<Object> params = new ArrayList<Object>();			            
			            params.add(Espera.adv_session.getId());
			            params.add(Espera.my_session.getId());
			            params.add(Espera.my_session.getId());
			            Espera.signal_conexao.GetMatch(params);
						
						act.startActivity(new Intent(act, ComoLutarFight.class));
						act.finish();
						act.overridePendingTransition(R.anim.top_down_in, R.anim.top_down_out);
					}
	        	});
	        }
	    }).start();
	}
	
	
	private void ativaVolta(){
		task = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
                public void run() {
                	AlertDialog.Builder alert = new AlertDialog.Builder(Espera.this);
                    alert.setTitle("Bora pra outro!");
                    alert.setMessage("A conexão do seu adversário caiu ou ele ficou com medo.");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick (DialogInterface dialog, int id) {     
                        	timer.cancel();
                        	timer = null;
                    		task = null;
                        	act.finish();
                        }
                    });
                    alert.show();
                }
           });
        }};           
        timer.schedule(task, 15000, 15000); 
    }	
	
	public void onDetroy(){
		super.onDestroy();
		timer = null;
		task = null;
	}
	
	public void onStop(){
		super.onStop();
		timer = null;
		task = null;
	}
	public void onPause(){
		super.onPause();
		timer = null;
		task = null;
		
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
