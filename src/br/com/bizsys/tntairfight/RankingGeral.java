package br.com.bizsys.tntairfight;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.ConfigSave;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.ServiceHandler;
import classes.TNTAirFight;
import fragments.RankingAdapter;

@SuppressLint("NewApi")
public class RankingGeral extends Activity {
	private ArrayList<Ranking> list_ranking = new ArrayList<Ranking>();
	public Activity act;
	public ProgressDialog progress_dialog = null;
	public TNTAirFight tnt = null;
	public ListView lista_ranking = null;
	
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ranking_geral);        
        this.act = this;
        tnt = new TNTAirFight((Activity) this);
        
        lista_ranking = (ListView) findViewById(R.id.lista_ranking);
        lista_ranking.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            	Ranking r =	list_ranking.get(position);
            	Intent i = new Intent(RankingGeral.this, RankingShow.class);
            	i.putExtra("id", r.getId());
            	startActivity(i);
            }
        });
        
        new getRanking().execute();
                
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
						startActivity(new Intent(RankingGeral.this, Configuracao.class));
						overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
					break;
				}
				return true;
			}
		});

    }
    
    public class getRanking extends AsyncTask<String, Void, Void>{
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(RankingGeral.this);
			progress_dialog.setCancelable(true);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.show();
		}
    	
		@Override
		protected Void doInBackground(String... params) {
			ServiceHandler service = new ServiceHandler();
			final String newJson = service.makeServiceCall(getString(R.string.url_rank)+"1" , ServiceHandler.GET, "", null, null);
			try {
				JSONObject obj_json = new JSONObject(newJson);						 
				if(obj_json.getString("Status").equalsIgnoreCase("Error")){
					Toast.makeText(RankingGeral.this, obj_json.getString("Message"), Toast.LENGTH_SHORT).show();
				}else if (obj_json.getString("Status").equalsIgnoreCase("Ok")){
					JSONArray obj_json_array = new JSONArray(obj_json.getString("Object"));
					
					for(int i=0; i<obj_json_array.length(); i++){
						final JSONObject users = new JSONObject(String.valueOf(obj_json_array.get(i)));
						
						Ranking add_rank = null;
						
						if(!users.getString("photoUrl").equals("")){
							Bitmap bmp = DownloadImage.formatImg(DownloadFullFromUrl(users.getString("photoUrl")));
							add_rank = new Ranking(users.getString("idUser"), users.getString("nome"),  String.format("%s ", users.getString("pontos")), users.getString("photoUrl"), bmp);
						}else{
							add_rank = new Ranking(users.getString("idUser"), users.getString("nome"),  String.format("%s ", users.getString("pontos")), users.getString("photoUrl"), null);
						}
						
						
														
						
						list_ranking.add(add_rank);
					}							
			        
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					lista_ranking.setAdapter(new RankingAdapter(act, act, list_ranking));
				}
			});
			
			progress_dialog.dismiss();
			return null;
		}    	
    }

    public Bitmap DownloadFullFromUrl(String imageFullURL) {
    	HttpClient http = new DefaultHttpClient();
		HttpGet get = new HttpGet(imageFullURL);
		HttpResponse response = null;
		try {
			response = http.execute(get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] bytes = null;
		try {
			bytes = EntityUtils.toByteArray(response.getEntity());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
		final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		
		return bitmap;
    }
    
    public void onResume(){
    	super.onResume();
    	    	
    	ConfigSave config = new ConfigSave(this);
    	if(config.getSons()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				if(!TNTAirFight.getInstance().audio_splash_loop.isPlaying()){
					TNTAirFight.getInstance().audio_splash_loop.start();
				}
			}
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