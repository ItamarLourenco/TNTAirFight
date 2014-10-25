package br.com.bizsys.tntairfight;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.ConfigSave;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import br.com.bizsys.tntairflight.R;
import classes.GPSTracker;
import classes.ServiceHandler;
import classes.TNTAirFight;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


@SuppressLint({ "NewApi", "ValidFragment" })
public class Login extends Activity {
	public ProgressDialog progress_dialog = null;
	public TNTAirFight tnt; 
	private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "user_birthday", "user_friends", "email");
	public GPSTracker gps;
	double log = 0.0;
	double lat = 0.0;
	public MySession my_session;
	private UiLifecycleHelper uiHelper;
	public Calendar c = Calendar.getInstance();
    public int startYear = c.get(Calendar.YEAR);
    public int startMonth = c.get(Calendar.MONTH);
    public int startDay = c.get(Calendar.DAY_OF_MONTH);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tnt = new TNTAirFight(this);
        uiHelper = new UiLifecycleHelper(Login.this, callback);
        uiHelper.onCreate(savedInstanceState);
        progress_dialog = new ProgressDialog(Login.this);
		progress_dialog.setMessage("Aguarde...");
		progress_dialog.setCancelable(true);
		
        
        final ImageView criar_conta = (ImageView) findViewById(R.id.criar_conta);
        criar_conta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toCadastro();
			}
		});        
        
        final ImageView esqueci_senha = (ImageView) findViewById(R.id.esqueci_senha);
        esqueci_senha.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toEsqueciSenha();
			}
		});
        
        final EditText login = (EditText) findViewById(R.id.login);
        login.setHint(TNTAirFight.espacamento("E-MAIL"));
        final EditText senha = (EditText) findViewById(R.id.senha);
        senha.setHint(TNTAirFight.espacamento("SENHA"));
        final Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(!tnt.verificaConexao()){
						tnt.AlertTNT("Sem conexão", Login.this);
					}else{
						realizaLogin(login, senha);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("tnt", "Erro Login JSON");
				}				
			}
		});
        
        
        
        final LoginButton authButton = (LoginButton) findViewById(R.id.login_button);
        authButton.setReadPermissions(PERMISSIONS);
        
        final Button btn_facebook = (Button) findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authButton.callOnClick();
			}
		});
    }    
    
    @SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("tnt", "Facebook Logado");
            progress_dialog.show();
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                       try {
                    	   JSONObject json = new JSONObject(user.getInnerJSONObject().toString());
                    	                       	   
                    	   my_session.setNome(json.getString("name"));
                    	   my_session.setEmail(json.getString("email"));
                    	   my_session.setImage("http://graph.facebook.com/"+json.getString("id")+"/picture?type=large");
                    	   
                    	   AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
	               	        alert.setTitle("TNTAirFight");
	               	        alert.setMessage("Por favor, entre com sua data de Nascimento");
	               	        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               	            public void onClick (DialogInterface dialog, int id) {
	               	            	DialogFragment dialogFragment = new StartDatePicker();
	       						    dialogFragment.show(getFragmentManager(), "start_date_picker");
	               	            }
	               	        });
	               	        alert.show();
                    	   
                    	   
						} catch (JSONException e) {
							e.printStackTrace();
						}
                    }
                }
            });
        } else if (state.isClosed()) {
            Log.e("tnt", "Facebook Deslogado");
            if (session != null) {
                if (!session.isClosed()) {
                    session.closeAndClearTokenInformation();
                }
            } else {
                session = new Session(Login.this);
                Session.setActiveSession(session);
                session.closeAndClearTokenInformation();
            }
        }
    }
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    
	protected void realizaLogin(EditText login, EditText senha) throws JSONException {		
		String login_text = login.getText().toString();
		String senha_text = senha.getText().toString();
		if(login_text.equalsIgnoreCase("")){
			tnt.AlertTNT("Por favor, entre com o seu E-mail", Login.this);
		}else{
			if(senha_text.equalsIgnoreCase("")){
				tnt.AlertTNT("Por favor, entre com o sua Senha", Login.this);
			}else{
				//Criando JSON para o Login
				JSONObject json = new JSONObject();
				json.put("Email", login_text);
				json.put("Password", senha_text);
				json.put("latitude", lat);
				json.put("longitude", log);
				json.put("hasFarGame", true);
				
				new RealizaLogin().execute(String.valueOf(json));
			}
		}
	}


	public void toCadastro(){
    	startActivity(new Intent(Login.this, Cadastro.class));
    }
    
    public void toEsqueciSenha(){
    	startActivity(new Intent(Login.this, EsqueciSenha.class));
    }    
    
    public class GetCity extends  AsyncTask<Double, Void, Void>{
		@Override
		protected Void doInBackground(Double... params) {
			double log = params[0];
			double lat = params[1];			
						
			String cidade = "Brasil";
			String estado = "BR";
			
			ServiceHandler service = new ServiceHandler();			
			String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + ","+ log + "&sensor=true";
			String json = service.makeServiceCall(url, ServiceHandler.GET, null, null, null);
			try {
				JSONObject jsonObj = new JSONObject(json);
				String Status = jsonObj.getString("status");
				if (Status.equalsIgnoreCase("OK")) {
	                JSONArray Results = jsonObj.getJSONArray("results");
	                JSONObject zero = Results.getJSONObject(0);
	                JSONArray address_components = zero.getJSONArray("address_components");

	                for (int i = 0; i < address_components.length(); i++) {
	                    JSONObject zero2 = address_components.getJSONObject(i);
	                    String long_name = zero2.getString("long_name");
	                    JSONArray mtypes = zero2.getJSONArray("types");
	                    String Type = mtypes.getString(0);

	                    if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
	                        if (Type.equalsIgnoreCase("street_number")) {
	                        	// Address1 = long_name + " ";
	                        } else if (Type.equalsIgnoreCase("route")) {
	                        	// Address1 = Address1 + long_name;
	                        } else if (Type.equalsIgnoreCase("sublocality")) {
	                            // Address2 = long_name;
	                        } else if (Type.equalsIgnoreCase("locality")) {
	                            // Address2 = Address2 + long_name + ", ";
	                        	cidade = long_name;
	                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
	                        	// estado = long_name;
	                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
	                        	estado = zero2.getString("short_name");
	                        } else if (Type.equalsIgnoreCase("country")) {
	                        	// Country = long_name;
	                        } else if (Type.equalsIgnoreCase("postal_code")) {
	                        	// PIN = long_name;
	                        }
	                    }

	                }
	            }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			my_session.setCidade(cidade);
            my_session.setEstado(estado);
            my_session.setLatitude(String.valueOf(lat));
            my_session.setLogintude(String.valueOf(log));
			
			return null;
		}
    }
    
    public class RealizaLogin extends AsyncTask<String, Void, Void>{
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			ServiceHandler service = new ServiceHandler();
			final String json_retorno = service.makeServiceCall(getString(R.string.url_login), ServiceHandler.POST, json, null, null);
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(json_retorno != null)
						{
							JSONObject obj_json = new JSONObject(json_retorno);
							if(obj_json.getString("Status").equalsIgnoreCase("Error")){
								tnt.AlertTNT(obj_json.getString("Message"), Login.this);
							}else if (obj_json.getString("Status").equalsIgnoreCase("Ok")){
								JSONObject obj_json_obj = new JSONObject(obj_json.getString("Object"));
								
								my_session.setId(obj_json_obj.getString("Id"));
								my_session.setEmail(obj_json_obj.getString("Email"));
								my_session.setNome(obj_json_obj.getString("Nome"));
								my_session.setIdade(obj_json_obj.getString("dtaNasc"));
								my_session.setCidade(obj_json_obj.getString("cidade"));
								my_session.setEstado(obj_json_obj.getString("estado"));
								my_session.setLogintude(obj_json_obj.getString("longitude"));
								my_session.setLatitude(obj_json_obj.getString("latitude"));
								my_session.setFacebook(false);
								
								startActivity(new Intent(Login.this, Perfil.class));
								finish();
							}
						}else{
							tnt.AlertTNT("Sem conexão com o servidor, tente novamente mais tarde", Login.this);
						}						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			progress_dialog.dismiss();
			return null;
		}
    }
    
    public void CadastroFacebook(){
    	try {
    		final JSONObject json = new JSONObject();
        	json.put("Nome", my_session.getNome());
        	json.put("Email", my_session.getEmail());
        	json.put("Password", "");
        	json.put("dtaNasc", classes.TNTAirFight.dateToEua(my_session.getIdade()));
        	json.put("isFacebook", "true");
        	json.put("cidade", my_session.getCidade());
			json.put("estado", my_session.getEstado());
			json.put("latitude", lat);
			json.put("hasFarGame", true);
			json.put("longitude", log);						
			
			new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... params) {
					ServiceHandler service = new ServiceHandler();
					final String json_retorno = service.makeServiceCall(getString(R.string.url_cadastro), ServiceHandler.POST, String.valueOf(json), null, null);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(json_retorno != null)
							{
								try {
									JSONObject obj_json = new JSONObject(json_retorno);
									if(obj_json.getString("Status").equalsIgnoreCase("Error")){
										tnt.AlertTNT(obj_json.getString("Message"), Login.this);
									}else if (obj_json.getString("Status").equalsIgnoreCase("Ok")){
										JSONObject obj_json_obj = new JSONObject(obj_json.getString("Object"));
										my_session.setId(obj_json_obj.getString("Id"));
										my_session.setEmail(obj_json_obj.getString("Email"));
										my_session.setNome(obj_json_obj.getString("Nome"));
										my_session.setIdade(obj_json_obj.getString("dtaNasc"));
										my_session.setCidade(obj_json_obj.getString("cidade"));
										my_session.setEstado(obj_json_obj.getString("estado"));
										my_session.setLogintude(obj_json_obj.getString("longitude"));
										my_session.setLatitude(obj_json_obj.getString("latitude"));
										my_session.setFacebook(true);
										
										uploadPhoto();
										
										startActivity(new Intent(Login.this, Perfil.class));
										finish();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								cadastro_facebook = false;
								progress_dialog.dismiss();
							}else{
								tnt.AlertTNT("Sem conexão com o servidor, tente novamente mais tarde", Login.this);
							}
						}
					});
					return null;
				}				
			}.execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
		if(!TNTAirFight.isRunningInForeground()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				TNTAirFight.getInstance().audio_splash_loop.stop();
			}	
		}
    	
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        ConfigSave config = new ConfigSave(this);
    	if(config.getSons()){
			if(TNTAirFight.getInstance().audio_splash_loop != null){
				if(!TNTAirFight.getInstance().audio_splash_loop.isPlaying()){
					TNTAirFight.getInstance().audio_splash_loop.start();
				}
			}
		}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    public void onResume(){
    	super.onResume();
    	uiHelper.onResume();
    	gps = new GPSTracker(Login.this); 
    	my_session = new MySession(this);
    	
    	my_session.setImage("");
    	if(gps.canGetLocation()){
    		log = gps.getLongitude();
        	lat = gps.getLatitude();        	
        	new GetCity().execute(log, lat);
        }else{
        	gps.showSettingsAlert();
        }
    	
    }
    
    
    
    public boolean cadastro_facebook = false;
    class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog dialog = new DatePickerDialog(Login.this, this, startYear, startMonth, startDay);
			return dialog;
        }
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear + 1;
            startDay = dayOfMonth;
            
            String dias = String.valueOf(startDay);            
            if(startDay < 10){
            	dias = "0"+dias;
            }
            String mes = String.valueOf(startMonth);            
            if(startMonth < 10){
            	mes = "0"+mes;
            }            
            String ano = String.valueOf(startYear);
            
            String data_final = String.format("%s/%s/%s", dias, mes, ano);
            my_session.setIdade(data_final);
            
            if(cadastro_facebook == false){
            	CadastroFacebook();	
            	cadastro_facebook = true;
            }
        }
    } 
    
    
    public void uploadPhoto()
    {    	
    	new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				ServiceHandler service = new ServiceHandler();
    	    	
		    	List<NameValuePair> paramms = new ArrayList<NameValuePair>();
		    	paramms.add(new BasicNameValuePair("idUser", my_session.getId()));
		    	paramms.add(new BasicNameValuePair("photoUrl", my_session.getImage()));
		    	
				service.makeServiceCall(getString(R.string.upload_photo), ServiceHandler.POST, null, paramms, null, false);				
				return null;
			}
    	}.execute();
    }
    
}