package br.com.bizsys.tntairfight;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import br.com.bizsys.tntairflight.R;
import classes.ServiceHandler;
import classes.SignalConexao;
import classes.TNTAirFight;


@SuppressLint("NewApi")
public class Luta extends Activity implements SensorEventListener{
	public static SignalConexao signal_conexao;
	public static MySession my_session;
	public AdvSession adv_session;
	public static FightSession fight_session;
	public TNTAirFight tnt;
	public static ProgressDialog progress_dialog;
	private static Timer timer = null;
    private static TimerTask task = null;
    private final static Handler handler = new Handler();
    private static final int TIPO_SENSOR_ACELEROMETRO = Sensor.TYPE_ACCELEROMETER;
    private SensorManager sensorManager;
    private Sensor sensorAcelerometro;
    private Button btnA, btnB, btnC;
    public long lastDown ;
    public long lastDuration;
    public boolean isHoldingA, isHoldingB, isHoldingC;
    public static int life_height = 160;
    public JSONArray todos_golpes = new JSONArray();
    public static Vibrator vibrator = null;
    public static MathResult mathResult;
    public static FrameLayout life;
    public static Activity act;
    public static ConfigSave config;
    public boolean active;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		config = new ConfigSave(this);
		if(config.getCanhoto()){
			setContentView(R.layout.activity_luta_canhoto);
		}else{
			setContentView(R.layout.activity_luta);
		}
				
		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        progress_dialog = new ProgressDialog(Luta.this);
		progress_dialog.setMessage("Aguarde...");
		progress_dialog.setCancelable(true);
		progress_dialog.show();
		
        my_session = new MySession(this);
		adv_session = new AdvSession(this);
		fight_session = new FightSession(this);
		tnt = new TNTAirFight(this);
		signal_conexao = StartFight.signal_conexao;
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mathResult = new MathResult();
		life = (FrameLayout) Luta.this.findViewById(R.id.life);
		timer = new Timer();
		act = this;
		
		
		
		if(config.getSons()){
			if(fight_session.getPlayer1() || my_session.getDistancia()){
				TNTAirFight.inicio_luta = MediaPlayer.create(Luta.this, R.raw.prontos_fight_clip01);
				TNTAirFight.inicio_luta.start();
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
							params.add(Preparados.my_session.getId());
							params.add("FIGHT");
							params.add(Preparados.adv_session.getId());
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
							params.add(Preparados.my_session.getId());
							params.add("START");
							params.add(Preparados.adv_session.getId());
							StartFight.signal_conexao.SendToSpecific(params);
							Luta.ativaContagem();
							Luta.progress_dialog.dismiss();
						}
		        	});
		        }
		    }).start();
		}
		
		
        btnA = (Button)findViewById(R.id.btnA);
        btnB = (Button)findViewById(R.id.btnB);
        btnC = (Button)findViewById(R.id.btnC);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensorAcelerometro = sensorManager.getDefaultSensor(TIPO_SENSOR_ACELEROMETRO);
        if(sensorAcelerometro == null){
            Toast.makeText(this, "Sensor não disponivel", Toast.LENGTH_LONG).show();
        }
        this.addActionToButtons();        
	}
	
	//Ativada em StartFight
	public void START(){
		Luta.ativaContagem();
		Luta.progress_dialog.dismiss();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void addActionToButtons(){
        btnA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldingA = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isHoldingA = false;
                        break;
                }
                return false;
            }
        });


        btnB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldingB = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isHoldingB = false;
                        break;
                }
                return false;
            }
        });

        btnC.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldingC = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isHoldingC = false;
                        break;
                }
                return false;
            }
        });

       

    }
	
	@Override
    protected void onResume(){
        super.onResume();
        comecaAMagica();        
    }
	
	@Override
	public void onPause(){
		super.onPause();	
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
	}
	
	public void onSensorChanged(SensorEvent event){
		float acX = 0.0f;
        float acY  = 0.0f;
        float acZ = 0.0f;

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER ){
        	acX=event.values[0];
            acY=event.values[1];
            acZ=event.values[2];
        }
        if((acZ > -10.6 && acZ < -8.0)){
            if (isHoldingA && isHoldingB){
                capturaGolpe("Chute Frontal");
            }else if(isHoldingA && isHoldingC){
                capturaGolpe("Arm Lock");
            }else if(isHoldingC){
                capturaGolpe("Upper");
            }
        }else if ((acY > 7.8 && acY < 10.5) && acZ> 0.9){
            if(isHoldingA && isHoldingB){
                capturaGolpe("Chute Alto");
            }else if(isHoldingA && isHoldingC){
                capturaGolpe("Mata Leão");
            }else if (isHoldingC){
                capturaGolpe("Cruzado");
            }
        } else if (acX < -7.7 || acX> 7.7){
            if (isHoldingA && isHoldingB){
                capturaGolpe("Joelhada");
            }else if(isHoldingA && isHoldingC){
                capturaGolpe("Guilhotina");
            }else if (isHoldingC){
                capturaGolpe("Jab");
            }
        }

    }


    public void comecaAMagica(){
        if (sensorAcelerometro != null){
            sensorManager.registerListener(this, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public  void terminaAMagica(){
        sensorManager.unregisterListener(this);
    }
	
	
	int total_golpes = 0;
	protected void capturaGolpe(String golpe) {
		if(active == true)
		{
			if(segundos == 0 || segundos == 3 || segundos == 6 || segundos == 9
			   || segundos == 12 || segundos == 15 || segundos == 18 || segundos == 21
			   || segundos == 24 || segundos == 27 || segundos == 30)
			{
				
				Log.i("newtnt", "socou em "+segundos);
				
				total_golpes++;		
				if(total_golpes == 10){
					if(config.getSons()){
						TNTAirFight.boa_garoto = MediaPlayer.create(Luta.act, R.raw.boa_garoto);
						TNTAirFight.boa_garoto.start();
					}
		    	}
				
				ArrayList<Object> golpes = new ArrayList<Object>(1);
				golpes.add(my_session.getId());
				golpes.add(golpe);
				golpes.add(adv_session.getId());
				signal_conexao.SendToSpecific(golpes);
				
				JSONObject add_golpe = new JSONObject();
				try {
					add_golpe.put("hitString", golpe);
					add_golpe.put("intervalo", "0");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				todos_golpes.put(add_golpe);
				
				Log.i("tnt", String.format("GOLPE = %s TOTAL DE GOLPE = %d", golpe, total_golpes));
			}
		}
	}
	
	public static int total_golpes_levados = 0;
	protected void levandoGolpe(String golpe){
		if(config.getVibracao()){
			vibrator.vibrate(500);
		}
		
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    	params.setMargins(35, life_height, 35, 25);    	
    	life_height += 10;
    	life.setLayoutParams(params);
    	
    	if(total_golpes_levados == 10){
	  		Random rand = new Random();	
	  	    int randomNum = rand.nextInt((1 - 0) + 1) + 0;	  		
	  	    if(randomNum == 0){
	  	    	if(config.getSons()){
	  	    		TNTAirFight.vamos_po = MediaPlayer.create(Luta.act, R.raw.vamos_po);
	  	    		TNTAirFight.vamos_po.start();
	  	    	}
	  	    }else{
	  	    	if(config.getSons()){
	  	    		TNTAirFight.levanta = MediaPlayer.create(Luta.act, R.raw.levanta);
	  	    		TNTAirFight.levanta.start();
	  	    	}
	  	    }
	  	}
    	
    	Log.i("tnt", "Levando Golpes = " + golpe);
    	total_golpes_levados++;
	}

	
	public static int segundos = 0;
	private static void ativaContagem(){
    task = new TimerTask() {
        public void run() {
           handler.post(new Runnable() {
                public void run() {
                	segundos++;
                	if(segundos == 30){ 
                		timer.cancel();
                		timer = null;
                		task = null;
                		total_golpes_levados = 0;
                		segundos = 0;
                				
                		TNTAirFight.sleep();
            			Luta.mathResult.execute();
            			
            			if(Luta.progress_dialog != null){
            				Luta.progress_dialog.show();
            			}
                		
                		
                		
                		if(fight_session.getPlayer1() || my_session.getDistancia()){
	                		if(config.getSons()){
	                			TNTAirFight.gongo = MediaPlayer.create(Luta.act, R.raw.sino_final_clip01);
	                			TNTAirFight.gongo.start();
	                		}
                		}
                	}else if(segundos == 19){
                		if(fight_session.getPlayer1() || my_session.getDistancia()){
                			if(config.getSons()){
                				TNTAirFight.final_audio = MediaPlayer.create(Luta.act, R.raw.gongo_clip01);
                				TNTAirFight.final_audio.start();
                			}
                		}
                	}
                	
                	Log.i("tnt", String.format("Segundo de luta = %s", segundos));
                }
           });
        }};           
        timer.schedule(task, 1000, 1000); 
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}	
	
	public class MathResult extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {        	
			JSONObject send_json = new JSONObject();
			try {
				send_json.put("userId", my_session.getId());
				send_json.put("matchId", fight_session.getMatchId());
				send_json.put("hitString", todos_golpes.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			
			
			ServiceHandler service =  new ServiceHandler();
			String json_retorno = service.makeServiceCall(getString(R.string.url_hit), ServiceHandler.POST, String.valueOf(send_json), null, null);
			try {
				JSONObject ok = new JSONObject(json_retorno);			
				if(ok.getString("Status").equals("Ok")){
					TNTAirFight.sleep();
		        	signal_conexao.leave();
		        	signal_conexao.conexao.Stop();
		        	
		        	startActivity(new Intent(Luta.this, Estatisticas.class));
		        	finish();
		        	
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	progress_dialog.dismiss();
			return null;
		}
	}

	public void onDetroy(){
		super.onDestroy();
		timer = null;
		task = null;
	}
	
	@Override
    public void onStart() {
       super.onStart();
       active = true;
    } 

    @Override
    public void onStop() {
       super.onStop();
       active = false;
    }
	
	@Override
	public void onBackPressed() {}
	
}
