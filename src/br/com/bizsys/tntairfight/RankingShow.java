package br.com.bizsys.tntairfight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.ServiceHandler;
import classes.TNTAirFight;

@SuppressLint("DefaultLocale")
public class RankingShow extends Activity {
	public String user_id = "";
	public TNTAirFight tnt;
	public ProgressDialog progress_dialog;
	public TextView valor_vitoria;
	public TextView valor_empates;
	public TextView valor_derrotas;
	public TextView pontos;
	public TextView nome;
	public TextView cidade;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking_show);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		tnt = new TNTAirFight(this);
        
		ImageView btn_voltar = (ImageView) findViewById(R.id.btn_voltar);
		btn_voltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Bundle extras = getIntent().getExtras();
		user_id = extras.getString("id");
		
		new GetRanking().execute();
		
		valor_vitoria = (TextView) findViewById(R.id.valor_vitoria);
		valor_vitoria.setText("-");
		
		valor_empates = (TextView) findViewById(R.id.valor_empates);
		valor_empates.setText("-");
		
		valor_derrotas = (TextView) findViewById(R.id.valor_derrotas);
		valor_derrotas.setText("-");
		
		pontos = (TextView) findViewById(R.id.pontos);
		pontos.setText("-");
		
		nome = (TextView) findViewById(R.id.nome);
		nome.setText("-");
		
		cidade = (TextView) findViewById(R.id.cidade);
		cidade.setText("-");
	}
	@SuppressLint("DefaultLocale")
	public class GetRanking extends AsyncTask<Void, Void, Void>{
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(RankingShow.this);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.setCancelable(true);
			progress_dialog.show();
		}
    	
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(Void... params) {
			String url = String.format("%s%s", getString(R.string.url_users), user_id);
			ServiceHandler service = new ServiceHandler();			
			String retorno_json = service.makeServiceCall(url, ServiceHandler.GET, null, null, null);
			Log.i("newtnt", retorno_json);
			try {
				JSONObject json = new JSONObject(retorno_json);
				JSONArray json_arr = new JSONArray(json.getString("Object"));
				for(int i = 0; i<json_arr.length(); i++)
				{
					final JSONObject json_retorno = new JSONObject(json_arr.get(i).toString());
					final JSONObject user = new JSONObject(json_retorno.getString("user"));
					
					runOnUiThread(new Runnable() {
						@SuppressLint("DefaultLocale")
						@Override
						public void run() {
							try {
								TextView nome = (TextView) findViewById(R.id.nome);
								tnt.setFont(nome);
								nome.setText(user.getString("Nome").toUpperCase());
								
								TextView idade = (TextView) findViewById(R.id.idade);
								tnt.setFont(idade);
								idade.setText(TNTAirFight.calculaIdade(user.getString("dtaNasc")));
								
								pontos.setText(json_retorno.getString("totalLutas").toUpperCase());
								valor_vitoria.setText(json_retorno.getString("vitorias").toUpperCase());								
								valor_derrotas.setText(json_retorno.getString("derrotas").toUpperCase());
								valor_empates.setText(json_retorno.getString("empates").toUpperCase());
								
								if(!String.valueOf(user.getString("PicProfiles")).equals("null"))
								{
									JSONObject picProfile = new JSONObject(String.valueOf(user.getString("PicProfiles")));
									if(!picProfile.getString("photoUrl").equals(""))
									{
										new DownloadImage(picProfile.getString("photoUrl"), RankingShow.this, R.id.img_user, R.id.progress_img_user);
									}
								}else{
									((ProgressBar) findViewById(R.id.progress_img_user)).setVisibility(View.GONE);
								}
								
								cidade.setText(" "+user.getString("cidade").toUpperCase() + ", " + user.getString("estado").toUpperCase());
							} catch (JSONException e) {
								e.printStackTrace();
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
	
	public void onPause(){
		super.onPause();	
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
	}


}
