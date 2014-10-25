package br.com.bizsys.tntairfight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.ConfigSave;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.GPSTracker;
import classes.ServiceHandler;
import classes.TNTAirFight;

public class Perfil extends Activity {
	public MySession my_session;
	public TNTAirFight tnt;
	public ProgressDialog progress_dialog;
	public boolean stop_musica = true;
	public ProgressBar progress_user_image = null;
	public ConfigSave config = null;

	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfil);
		getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		my_session = new MySession(this);
		tnt = new TNTAirFight(this);
		config = new ConfigSave(this);
		
		if(!tnt.verificaConexao()){
			tnt.AlertTNT("Sem conexão.", this);
		}else{
			new getStatus().execute();
		}
		
		
		final ImageView btn_como_jogar = (ImageView) findViewById(R.id.btn_como_jogar);
		btn_como_jogar.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						btn_como_jogar.setImageResource(R.drawable.como_jogar_hover);
					break;
					case MotionEvent.ACTION_UP:
						btn_como_jogar.setImageResource(R.drawable.como_jogar);
						startActivity(new Intent(Perfil.this, ComoJogar.class));
					break;
				}
				return true;
			}
		});
		
		final ImageView btn_ver_ranking = (ImageView) findViewById(R.id.btn_ranking);
		btn_ver_ranking.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						btn_ver_ranking.setImageResource(R.drawable.ver_ranking_hover);
					break;
					case MotionEvent.ACTION_UP:
						btn_ver_ranking.setImageResource(R.drawable.ver_ranking);
						startActivity(new Intent(Perfil.this, RankingGeral.class));
					break;
				}
				return true;
			}
		});
        
		
        final ImageView configuracoes = (ImageView) findViewById(R.id.configuracoes);
        configuracoes.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						configuracoes.setImageResource(R.drawable.ico_opcoes_hover);
					break;
					case MotionEvent.ACTION_UP:
						configuracoes.setImageResource(R.drawable.ico_opcoes);					
						startActivity(new Intent(Perfil.this, Configuracao.class));
						overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
					break;
				}
				return true;
			}
		});
        
        
        
        
        
        Button btn_procurar = (Button) findViewById(R.id.btn_procurar);
        btn_procurar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Perfil.this, StartFight.class));
				stop_musica = false;
			}
		});
	}

	public void onResume(){
		super.onResume();
		
		if(!my_session.check()){
			finish();
		}
		
		stop_musica = true;
		if(tnt.verificaConexao()){ 
			new getStatus().execute();
		}
		
		GPSTracker gps = new GPSTracker(Perfil.this); 
    	my_session = new MySession(this);
    	if(gps.canGetLocation()){
//    		my_session.setLogintude(String.valueOf(gps.getLongitude()));
//    		my_session.setLatitude(String.valueOf(gps.getLatitude()));
    		my_session.setLogintude(String.valueOf(-23.505474));
    		my_session.setLatitude(String.valueOf(-46.886899));
    		
        }else{
        	gps.showSettingsAlert();
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
	
	public boolean load = false;
	@SuppressLint("DefaultLocale")
	public class getStatus extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(load == false){
				progress_dialog = new ProgressDialog(Perfil.this);
				progress_dialog.setCancelable(true);
				progress_dialog.setMessage("Aguarde...");
				progress_dialog.show();				
				load = true;
			}
		}
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(String... params) {
			ServiceHandler service = new ServiceHandler();
			final String json_retorno = service.makeServiceCall(getString(R.string.url_status)+my_session.getId(), ServiceHandler.GET, "", null, null);
			
			if(json_retorno == null){
				tnt.AlertTNT("Sem Conexão", Perfil.this);
				return null;
			}else{				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject obj_json = new JSONObject(json_retorno);
							if(obj_json.getString("Status").equalsIgnoreCase("Error")){
								tnt.AlertTNT(obj_json.getString("Message"), Perfil.this);
							}else if (obj_json.getString("Status").equalsIgnoreCase("Ok")){
								JSONArray obj_json_array = new JSONArray(obj_json.getString("Object"));
								JSONObject obj_dados = new JSONObject(String.valueOf(obj_json_array.get(0)));
								JSONObject user = new JSONObject(String.valueOf(obj_dados.getString("user")));
								
								if(!String.valueOf(user.getString("PicProfiles")).equals("null"))
								{
									JSONObject picProfile = new JSONObject(String.valueOf(user.getString("PicProfiles")));
									if(!picProfile.getString("photoUrl").equals(""))
									{
										new DownloadImage(picProfile.getString("photoUrl"), Perfil.this, R.id.img_user, R.id.progress_img_user);
										my_session.setImage(picProfile.getString("photoUrl"));
									}else{
										((ProgressBar) findViewById(R.id.progress_img_user)).setVisibility(View.GONE);
									}
								}else{
									if(!my_session.getImage().equals(""))
									{
										new DownloadImage(my_session.getImage(), Perfil.this, R.id.img_user, R.id.progress_img_user);
									}else{
										((ProgressBar) findViewById(R.id.progress_img_user)).setVisibility(View.GONE);
									}
								}								
								
								TextView valor_vitoria = (TextView) findViewById(R.id.valor_vitoria);
								valor_vitoria.setText(obj_dados.getString("vitorias"));
								my_session.setVitorias(obj_dados.getString("vitorias"));
								
								TextView valor_empates = (TextView) findViewById(R.id.valor_empates);
								valor_empates.setText(obj_dados.getString("empates"));
								
								TextView valor_derrotas = (TextView) findViewById(R.id.valor_derrotas);
								valor_derrotas.setText(obj_dados.getString("derrotas"));
								
								TextView pontos = (TextView) findViewById(R.id.pontos);
								pontos.setText(obj_dados.getString("totalLutas"));
								my_session.setLutas(obj_dados.getString("totalLutas"));
								
								TextView nome = (TextView) findViewById(R.id.nome);
								nome.setText(TNTAirFight.espacamento(user.getString("Nome").toUpperCase()));
								my_session.setNome(user.getString("Nome").toUpperCase());
								
								TextView idade = (TextView) findViewById(R.id.idade);
								idade.setText(TNTAirFight.calculaIdade(user.getString("dtaNasc")));
								my_session.setIdade(user.getString("dtaNasc"));
								
								
								
								TextView cidade = (TextView) findViewById(R.id.cidade);
								cidade.setText(TNTAirFight.espacamento(String.format(" %s, %s", user.getString("cidade").toUpperCase(), user.getString("estado").toUpperCase()), 3));
								my_session.setCidade(user.getString("cidade").toUpperCase());
								my_session.setEstado(user.getString("estado").toUpperCase());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
			progress_dialog.dismiss();			
			return null;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        Log.d("Test", "Back button pressed!");
	    }
	    else if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	        Log.d("Test", "Home button pressed!");
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("TNTAirFight");
        alert.setMessage("Deseja realmente sair do aplicativo?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
            	finish();
            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			};
		});
        alert.show();
	}
	
	
}
