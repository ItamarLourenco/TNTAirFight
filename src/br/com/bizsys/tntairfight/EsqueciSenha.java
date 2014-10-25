package br.com.bizsys.tntairfight;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.ServiceHandler;
import classes.TNTAirFight;

@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class EsqueciSenha extends Activity {
	public ProgressDialog progress_dialog;
	public TNTAirFight tnt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_esqueci_senha);
		
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
		
		final TextView email = (TextView) findViewById(R.id.email);		
		final ImageView btn_enviar = (ImageView) findViewById(R.id.btn_enviar);
		btn_enviar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_UP:
						btn_enviar.setImageResource(R.drawable.btn_enviar);
						if(!email.getText().toString().equals("")){
							new EsqueciSenhaWs().execute(email.getText().toString());
						}else{
							tnt.AlertTNT(getString(R.string.enter_email), EsqueciSenha.this);
						}
					break;
					case MotionEvent.ACTION_DOWN:
						btn_enviar.setImageResource(R.drawable.btn_enviar_hover);
					break;
				}
				return true;
			}
		});
	}
	
	public class EsqueciSenhaWs extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(EsqueciSenha.this);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.setCancelable(true);
			progress_dialog.show();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			String email = String.valueOf(params[0]);			
			ServiceHandler service = new ServiceHandler();
			
			JSONObject json_email = new JSONObject();
			try {
				json_email.put("email", email);
				String json_envio = String.valueOf(json_email);
				String retorno_esqueci_senha = service.makeServiceCall(getString(R.string.url_login), ServiceHandler.PUT, json_envio, null, null);
				JSONObject msg = new JSONObject(retorno_esqueci_senha);
				
				final String message = msg.getString("Message");
				final String status = msg.getString("Status");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						
						AlertDialog.Builder alert = new AlertDialog.Builder(EsqueciSenha.this);
				        alert.setTitle("TNTAirFight");
				        alert.setMessage(message);
				        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            public void onClick (DialogInterface dialog, int id) {	
				            	if(status.equals("Ok")){
				            		finish();
				            	}
				            }
				        });
				        alert.show();
						
					}
				});
				
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
