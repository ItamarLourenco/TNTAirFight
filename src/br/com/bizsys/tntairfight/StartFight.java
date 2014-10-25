package br.com.bizsys.tntairfight;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.AdvSession;
import sessions.ConfigSave;
import sessions.FightSession;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import br.com.bizsys.tntairflight.R;
import classes.Adversario;
import classes.MetodosSignal;
import classes.ServiceHandler;
import classes.SignalConexao;
import classes.TNTAirFight;
import fragments.AdversariosFragment;

public class StartFight extends FragmentActivity {
	public TNTAirFight tnt;
	public MySession my_session;
	public AdvSession adv_session;
	public FightSession fight_session;
	public static SignalConexao signal_conexao;
	public ArrayList<Adversario> lista_adversario;
	public ViewPager mPager;
	public ScreenSlidePagerAdapter mPagerAdapter;
	public ProgressDialog progress_dialog = null;	
	public Espera espera = null;
	public Preparados preparados = null;
	public Luta luta = null;
	public boolean on_activity = true;
	public static ConfigSave config;
	public static boolean countdown = false;  
	public static int timecountdown = 15000;
	public static boolean foipraluta = false;
	public static boolean active = true;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar_adversarios);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        tnt = new TNTAirFight(this);
        my_session = new MySession(this);
        adv_session = new AdvSession(this);
        fight_session = new FightSession(this);
        progress_dialog = new ProgressDialog(StartFight.this);
        on_activity = true;
		progress_dialog.setCancelable(true);
		progress_dialog.setMessage("Aguarde...");
		progress_dialog.show();
		config = new ConfigSave(this);
		
		espera = new Espera();
		preparados = new Preparados();
		luta = new Luta();
		
		signal_conexao = new SignalConexao();
		signal_conexao.Conexao(this);
		
		//Gerencia todas as classes
		new MetodosSignal(){
			@Override
			public void OnlineStatus(JSONArray json){
				createOnlineStatus(json);
			}

			@Override
			public void broadcastMessage(final JSONArray json) throws JSONException {
				String id = json.getString(0);
				String tipo = "";
				
				if(json.getString(1).contains(","))
				{
					String[] tipo_array = json.getString(1).split(",");
					tipo = tipo_array[0];
					
					if(tipo_array[1].equals("true")){
						my_session.setDistancia(true);
					}else{
						my_session.setDistancia(false);
					}
				}else{
					tipo = json.getString(1);
				}
				
				
				
				if(on_activity == true)
				{ 
					switch(tipo)
					{
						/**
						 * class StartFight
						 */
						case "Fight":
							if(config.getNotificacao()){
								conviteLuta(id);
							}else{
								Toast.makeText(StartFight.this, "Convite negado, para aceitar veja as configurações.", Toast.LENGTH_SHORT).show();
							}							
						break;
					}
				}
				
				if(on_activity == false)
				{
					switch(tipo)
					{	
						case "Fight":
						break;
					
						/**
						 * class Espera
						 */
						case "Negado!":
							espera.Negado();
						break;
						
						case "Aceito!":
							espera.Aceito();
						break;
						
						case "MATCH":
							espera.MATCH();
						break;
												
						/**
						 * class Preparados
						 */
						case "FIGHT":
							preparados.FIGHT();
						break;
						/**
						 * class Luta
						 */
						case "START":
							luta.START();
						break;
						
						/**
						 * class Luta
						 * Defaul é qualquer golpe recebido 
						 */
						default:
							luta.levandoGolpe(tipo);
						break;
					}
				}	
			}

			//Atualiza MatchId para o player 2
			@Override
			public void getMatch(JSONArray json) throws JSONException {
				String json_open = json.getString(1);
				JSONObject json_obj = new JSONObject(json_open);
				String id_math = new JSONObject(json_obj.getString("Object")).getString("Id");			 
				fight_session.setMatchId(id_math);
			}
		};
		
        
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());        
        
        FrameLayout fechar = (FrameLayout) findViewById(R.id.fechar);
        fechar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				countdown = false;
				foipraluta = false;
				signal_conexao.leave();
				finish();
			}
		});
        Button lutar_agora = (Button) findViewById(R.id.lutar_agora);
        lutar_agora.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lista_adversario.size() > 0)
				{
					Adversario adversario = lista_adversario.get(mPager.getCurrentItem());
					adv_session.setId(adversario.getId());
					adv_session.setNome(adversario.getNome());
					adv_session.setCidade(adversario.getCidade());
					adv_session.setEstado(adversario.getEstado());
					adv_session.setLutas(adversario.getLutas());
					adv_session.setIdade(adversario.getIdade());
					adv_session.setVitorias(adversario.getVitorias());
					Log.i("newtnt", "lutar_agora = "+ adversario.getImage());
					adv_session.setImage(adversario.getImage());
					
					fight_session.setPlayer1(true);
					startActivity(new Intent(StartFight.this, Espera.class));
					foipraluta = true;
					countdown = false;
					finish();
				}else{
					Toast.makeText(StartFight.this, "Nenhum jogador online, por favor tente novamente mais tarde!", Toast.LENGTH_LONG).show();
					foipraluta = true;
					countdown = false;
					finish();
				}
				
				
			}
		});
    }


	protected void conviteLuta(final String id_adversario) {
		AlertDialog.Builder alert = new AlertDialog.Builder(StartFight.this);
        alert.setTitle("Nova Luta");
        alert.setMessage("Estão te convidando para uma luta, você topa?");
        alert.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
            	adv_session.setId(id_adversario);
            	fight_session.setPlayer1(false);
            	for(int i=0; i<lista_adversario.size(); i++){
					Adversario get_adv =  lista_adversario.get(i);
					if(get_adv.getId().equals(id_adversario)){
						adv_session.setNome(get_adv.getNome());
						adv_session.setIdade(get_adv.getIdade());
						adv_session.setCidade(get_adv.getCidade());
						adv_session.setEstado(get_adv.getEstado());
						adv_session.setLutas(get_adv.getLutas());
						adv_session.setVitorias(get_adv.getVitorias());
						adv_session.setImage(get_adv.getImage());
					}
				}            	
            	
            	new Thread(new Runnable() {
        	        public void run() {
        	        	
        	        	startActivity(new Intent(StartFight.this, Espera.class));
        	        	foipraluta = true;
        	        	countdown = false;
		        		finish();
        	        	
        	        	TNTAirFight.sleep();
        	        	runOnUiThread(new Runnable() {
        					@Override
        					public void run() {
        						ArrayList<Object> params = new ArrayList<Object>(1);
        						params.add(my_session.getId());
        						if(my_session.getDistancia() == true){
        							params.add("Aceito!, true");
        						}else{
        							params.add("Aceito!, false");
        						}
        		        		params.add(adv_session.getId());
        		        		signal_conexao.SendToSpecific(params);
        					}
        	        	});
        	        }
        	    }).start();
            }
        });
        alert.setNegativeButton("Fugir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	ArrayList<Object> params = new ArrayList<Object>(1);
				params.add(my_session.getId());
        		if(my_session.getDistancia() == true){
					params.add("Negado!, true");
				}else{
					params.add("Negado!, false");
				}
        		params.add(adv_session.getId());
        		signal_conexao.SendToSpecific(params);
            }
        });
        alert.show();
	}


	protected void createOnlineStatus(JSONArray json) {
		if(on_activity)
		{
			lista_adversario = new ArrayList<Adversario>();
			try {
				JSONObject obj_users = new JSONObject(json.getString(1));
				if(obj_users.getString("Status").equalsIgnoreCase("Ok")){
					JSONArray users_array = new JSONArray(obj_users.getString("Object"));
					if(users_array.length() > 0)
					{
						for(int i = 0; i<users_array.length(); i++)
						{
							JSONObject status = new JSONObject(String.valueOf(users_array.get(i)));
							
							Adversario adversario = new Adversario();
							adversario.setVitorias(status.getString("vitorias"));
							adversario.setEmpates(status.getString("empates"));
							adversario.setDerrotas(status.getString("derrotas"));
							adversario.setTotalLutas(status.getString("totalLutas"));
							
							JSONObject user = new JSONObject(status.getString("user"));
							adversario.setId(user.getString("Id"));
							adversario.setNome(user.getString("Nome"));
							adversario.setIdade(user.getString("dtaNasc"));
							adversario.setCidade(user.getString("cidade"));
							adversario.setEstado(user.getString("estado"));						
							
							if(!String.valueOf(user.getString("PicProfiles")).equals("null"))
							{
								JSONObject picProfile = new JSONObject(String.valueOf(user.getString("PicProfiles")));
								adversario.setImage(picProfile.getString("photoUrl"));
							} else{
								adversario.setImage("");
							}
							
							
							lista_adversario.add(adversario);
						}
						
						mPager.setAdapter(null);
						mPager.setAdapter(mPagerAdapter);
						progress_dialog.dismiss();
					}else{
						if(countdown == false)
						{
							new CountDownTimer(timecountdown, 1000) {
								public void onTick(long millisUntilFinished) {
								}
								
								public void onFinish() {
									askPlayerPertos();
								}
							}.start();
						
							countdown = true;
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {        	
            return new AdversariosFragment(lista_adversario, StartFight.this, position, mPager);
        }

        @Override
        public int getCount() {
            return lista_adversario.size();
        }
    }

	public void onResume(){
		super.onResume();
		on_activity = true;
		
	}
	public void onPause(){
		super.onPause();
		on_activity = false;
			
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
		
	}
	
	
	
	public void askPlayerPertos()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(StartFight.this);
        alert.setTitle("TNTAirFight");
        alert.setMessage("Não foi encontrado nenhum amigo próximo, deseja realizar uma luta aleatória?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
            	progress_dialog.show();
            	my_session.setDistancia(true);
            	new AsyncTask<Void, Void, Void>(){            		
					@Override
					protected Void doInBackground(Void... arg0) {						
		            	ServiceHandler service = new ServiceHandler();
		            	String json = service.makeServiceCall(getString(R.string.url_buscas) + my_session.getId(), ServiceHandler.GET, null, null, null);
		            	
		            	
		            	try {
							JSONObject obj_json = new JSONObject(json);
							JSONArray users_array = new JSONArray(obj_json.getString("Object"));
							if(users_array.length() > 0)
							{
								final String json_final = String.format("[\"644ac8a1-3bdb-4c14-8916-429b3bd03018\",%s]", json);
			            		runOnUiThread(new Runnable() {
									@Override
									public void run() {									
										try {
											createOnlineStatus(new JSONArray(json_final));
										} catch (JSONException e) {
											e.printStackTrace();
										}
										progress_dialog.dismiss();
									}
			            		});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {	
										AlertDialog.Builder alert = new AlertDialog.Builder(StartFight.this);
								        alert.setTitle("TNTAirFight");
								        alert.setMessage("Não foi encontrado nenhum usuário online!");
								        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								            public void onClick (DialogInterface dialog, int id) {
								            	countdown = false;
								            	foipraluta = true;
												finish();
												progress_dialog.dismiss();
								            }
								        });
								        alert.show();
									}
			            		});
								
							}
							
						} catch (JSONException e1) {
							e1.printStackTrace();
						}	
						return null;
					}
            	}.execute();
            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	countdown = false;
            	signal_conexao.leave();
            	foipraluta = true;
				finish();
            }
        });
        if(active == true && on_activity == true){
        	alert.show();
        }else{
        	finish();
        }
	}
	
	
	public void onDestroy(){
		super.onDestroy();
		on_activity = false;
	}
	public void onStop(){
		super.onStop();
		on_activity = false;
		active = false;
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		if(!isScreenOn){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}
		}
	}
	
	@Override
    public void onStart() {
       super.onStart();
       active = true;
    } 
	
	
}
