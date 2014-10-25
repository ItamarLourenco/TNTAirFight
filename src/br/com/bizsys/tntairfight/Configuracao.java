package br.com.bizsys.tntairfight;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import sessions.ConfigSave;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.ServiceHandler;
import classes.TNTAirFight;

import com.facebook.Session;

@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class Configuracao extends Activity {
	
	public boolean toogle_sons = false;
	public boolean toogle_vibracao = false;
	public boolean toogle_notificacao = false;
	public boolean toogle_canhoto_destro = false;
	public ConfigSave config = null;
	public Session session;
	public TNTAirFight tnt;
	public ProgressDialog progress_dialog = null;
	public MySession my_session;
	public ImageView camera = null;
    public String pathPhoto = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	session = Session.getActiveSession();
    	
    	my_session = new MySession(this);

        tnt = new TNTAirFight(this);
        TextView login = (TextView) findViewById(R.id.login);
        login.setText(TNTAirFight.espacamento(my_session.getEmail()));
        login.setEnabled(false);

        
        if(!my_session.getImage().equals(""))
		{
			new DownloadImage(my_session.getImage(), Configuracao.this, R.id.img_user, R.id.progress_img_user);
		}else{
			((ProgressBar) findViewById(R.id.progress_img_user)).setVisibility(View.GONE);
		}
        
        
        config = new ConfigSave(this);
        
        
        
        LinearLayout add_image = (LinearLayout) findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = {"Tirar uma Foto", "Escolher Foto"};
				AlertDialog.Builder builder = new AlertDialog.Builder(Configuracao.this);
				builder.setTitle("Escolha:");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item)
				    {
				    	if(item == 0)
				    	{
				    		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    		    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			    		        startActivityForResult(takePictureIntent, 2);
			    		    }
				    	}else{
				    		
				    		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);				    				 
				    		startActivityForResult(i, 3);
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
        
        
        final TextView senha = (TextView) findViewById(R.id.senha);        
		final TextView confirma_senha = (TextView) findViewById(R.id.confirme_senha);
        
        final ImageView btn_ok = (ImageView) findViewById(R.id.btn_ok);
        btn_ok.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
					case MotionEvent.ACTION_UP:
						btn_ok.setImageResource(R.drawable.btn_ok);
						
						if(senha.getText().length() == 0){
							tnt.AlertTNT("Por favor, preecha a senha!", Configuracao.this);
							return true;
						}
						
						if(!senha.getText().toString().equals(confirma_senha.getText().toString())){
							tnt.AlertTNT("As senhas não conferem!", Configuracao.this);
							return true;
						}
						
						JSONObject send_json = new JSONObject();
						try {
							send_json.put("id", my_session.getId());
							send_json.put("Password", senha.getText().toString());
							send_json.put("Email", my_session.getEmail());
							new ResetSenha().execute(String.valueOf(send_json));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					break;
					
					case MotionEvent.ACTION_DOWN:
						btn_ok.setImageResource(R.drawable.btn_ok_hover);
					break;
					
				}
				return true;
			}
		});
        
        
        
        
        
        final ImageView como_jogar = (ImageView) findViewById(R.id.dica);
        como_jogar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Configuracao.this, ComoJogar.class));
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});
        
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
						overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
					break;
				}
				return true;
			}
		});
        
        
        
        final ImageView check_sons = (ImageView) findViewById(R.id.check_sons);
        if(!config.getSons()){
        	check_sons.setImageResource(R.drawable.switch_off);
        }else{
        	check_sons.setImageResource(R.drawable.switch_on);
        }        
        check_sons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!config.getSons()){
					check_sons.setImageResource(R.drawable.switch_on);
					TNTAirFight.getInstance().audio_splash_loop.start();
					config.setSons(true);					
				}else{
					check_sons.setImageResource(R.drawable.switch_off);
					TNTAirFight.getInstance().audio_splash_loop.pause();
					config.setSons(false);
				}					
			}
		});
        
        final ImageView check_vibracao = (ImageView) findViewById(R.id.check_vibracao);
        if(!config.getVibracao()){
        	check_vibracao.setImageResource(R.drawable.switch_off);
        }else{
        	check_vibracao.setImageResource(R.drawable.switch_on);
        }
        check_vibracao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!config.getVibracao()){
					check_vibracao.setImageResource(R.drawable.switch_on);
					config.setVibracao(true);
				}else{
					check_vibracao.setImageResource(R.drawable.switch_off);
					config.setVibracao(false);
				}
			}
		});
        
        final ImageView check_notificacoes = (ImageView) findViewById(R.id.check_notificacoes);
        if(!config.getNotificacao()){
        	check_notificacoes.setImageResource(R.drawable.switch_off);
        }else{
        	check_notificacoes.setImageResource(R.drawable.switch_on);
        }
        check_notificacoes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!config.getNotificacao()){
					check_notificacoes.setImageResource(R.drawable.switch_on);
					config.setNotificacao(true);
				}else{
					check_notificacoes.setImageResource(R.drawable.switch_off);
					config.setNotificacao(false);
				}
			}
		});
        
        
        final ImageView canhoto_destro = (ImageView) findViewById(R.id.canhoto_destro);
        if(!config.getCanhoto()){
        	canhoto_destro.setImageResource(R.drawable.destro);
        }else{
        	canhoto_destro.setImageResource(R.drawable.canhoto);
        }
        canhoto_destro.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!config.getCanhoto()){
					canhoto_destro.setImageResource(R.drawable.canhoto);
					config.setCanhoto(true);
				}else{
					canhoto_destro.setImageResource(R.drawable.destro);
					config.setCanhoto(false);
				}
			}
		});
        
        ImageView termo_usu = (ImageView) findViewById(R.id.termo_usu);
        termo_usu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Configuracao.this, TermoDeUso.class));
			}
		}); 
        
        ImageView sair = (ImageView) findViewById(R.id.sair);
        sair.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoff();
			}
		});
        ImageView logoff_facebook = (ImageView) findViewById(R.id.logoff_facebook);
        if(!my_session.getFacebook()){
        	logoff_facebook.setVisibility(View.GONE);
        }
        logoff_facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoff();
			}
		});
    }
    
    public void logoff(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(Configuracao.this);
        alert.setTitle("Quer sair mesmo?");
        alert.setMessage("Fazendo o log off, você não receberá alertas de novos desafios.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {
            	my_session.lougout();
                
            	Session session = Session.getActiveSession();
                if (session != null) {
                    if (!session.isClosed()) {
                        session.closeAndClearTokenInformation();
                    }
                } else {
                    session = new Session(Configuracao.this);
                    Session.setActiveSession(session);
                    session.closeAndClearTokenInformation();
                }
                startActivity(new Intent(Configuracao.this, Login.class));
                finish();
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                
            }
        });
        alert.show();
    }
    
    
    public class ResetSenha extends AsyncTask<String, Void, Void>{
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(Configuracao.this);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.setCancelable(true);
			progress_dialog.show();
		}
		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			
			ServiceHandler service = new ServiceHandler();
			final String newJson = service .makeServiceCall(getString(R.string.url_cadastro), ServiceHandler.PUT, json, null, null);
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject json_obj = new JSONObject(newJson);
						tnt.AlertTNT(json_obj.getString("Message"), Configuracao.this);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			
			progress_dialog.dismiss();
			return null;
		}
    	
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    	if(resultCode == RESULT_OK)
    	{
	        if (requestCode == 2)
	        {
	            Bundle extras = data.getExtras();
	            Bitmap imageBitmap = (Bitmap) extras.get("data");	            
	            final Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);	            
	            Intent intent = new Intent("com.android.camera.action.CROP");  
	            intent.setDataAndType(tempUri, "image/*");  
	            intent.putExtra("crop", "true");  
	            intent.putExtra("aspectX", 1);  
	            intent.putExtra("aspectY", 1);  
	            intent.putExtra("outputX", 250);  
	            intent.putExtra("outputY", 250);  
	            intent.putExtra("return-data", true);
	            startActivityForResult(intent, 4);            	            
	        }else if(requestCode == 3){	
	            Intent intent = new Intent("com.android.camera.action.CROP");  
	            intent.setDataAndType(data.getData(), "image/*");  
	            intent.putExtra("crop", "true");  
	            intent.putExtra("aspectX", 1);  
	            intent.putExtra("aspectY", 1);  
	            intent.putExtra("outputX", 250);  
	            intent.putExtra("outputY", 250);  
	            intent.putExtra("return-data", true);
	            startActivityForResult(intent, 4);
	        }else if(requestCode == 4){
	        	camera = (ImageView) findViewById(R.id.img_user);
	        	camera.setVisibility(View.VISIBLE);
	        	Bundle extras = data.getExtras();
	            final Bitmap imageBitmap = (Bitmap) extras.get("data");
	            camera.setImageBitmap(DownloadImage.formatImg(imageBitmap));
	            
	            pathPhoto = getRealPathFromURI(getImageUri(Configuracao.this, imageBitmap));
	            
	            Log.i("newtnt", my_session.getId());
	            
	            new AsyncTask<Void, Void, Void>(){
	    			@Override
	    			protected Void doInBackground(Void... params) {
	    				List<NameValuePair> paramms = new ArrayList<NameValuePair>();
	    	            paramms.add(new BasicNameValuePair("idUser", my_session.getId() ));
	    				paramms.add(new BasicNameValuePair("photoUrl", String.format(getString(R.string.url_upload_photo), my_session.getId(), UUID.randomUUID().toString().substring(0,20))));
	    				paramms.add(new BasicNameValuePair("file", pathPhoto));
	    				post(getString(R.string.upload_photo), paramms);				
	    				return null;
	    			}
	        	}.execute();		
	        }
	        	
    	}
    }
    
    public void post(String url, List<NameValuePair> nameValuePairs) 
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < nameValuePairs.size(); index++) {
                if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                } else {
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);
            httpClient.execute(httpPost, localContext);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getRealPathFromURI(Uri uri) 
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
        return cursor.getString(idx); 
    }
    
    public Uri getImageUri(Context inContext, Bitmap inImage) 
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
