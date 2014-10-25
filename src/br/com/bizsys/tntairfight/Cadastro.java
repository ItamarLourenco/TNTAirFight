package br.com.bizsys.tntairfight;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.DownloadImage;
import classes.ServiceHandler;
import classes.TNTAirFight;

@SuppressLint({ "NewApi", "ValidFragment" })
public class Cadastro extends Activity {
	ProgressDialog progress_dialog = null;
	
	public Calendar c = Calendar.getInstance();
    public int startYear = c.get(Calendar.YEAR);
    public int startMonth = c.get(Calendar.MONTH);
    public int startDay = c.get(Calendar.DAY_OF_MONTH);
    public TextView data = null;
    public TNTAirFight tnt;
    public MySession my_session;
    public ImageView camera = null;
    public String pathPhoto = "";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        tnt = new TNTAirFight(this);
        my_session = new MySession(this);
        
        UUID uuid = UUID.randomUUID();  
        String myRandom = uuid.toString();  
        System.out.println(myRandom.substring(0,20));  
        
        LinearLayout add_image = (LinearLayout) findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = {"Tirar uma Foto", "Escolher Foto"};
				AlertDialog.Builder builder = new AlertDialog.Builder(Cadastro.this);
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
        
        
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        ImageView btn_voltar = (ImageView) findViewById(R.id.btn_voltar);
        btn_voltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        data = (TextView) findViewById(R.id.data);
        data.setLongClickable(false);
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					DialogFragment dialogFragment = new StartDatePicker();
				    dialogFragment.show(getFragmentManager(), "start_date_picker");
				}
			}
		});
        data.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment dialogFragment = new StartDatePicker();
			    dialogFragment.show(getFragmentManager(), "start_date_picker");
			}
		});
        
        final TextView nome = (TextView) findViewById(R.id.nome);
        nome.setHint(TNTAirFight.espacamento("NOME"));
        final TextView login = (TextView) findViewById(R.id.login);
        login.setHint(TNTAirFight.espacamento("E-MAIL"));
        final TextView senha = (TextView) findViewById(R.id.senha);
        senha.setHint(TNTAirFight.espacamento("SENHA"));
        final TextView confirme_senha = (TextView) findViewById(R.id.comfirme_senha);
        confirme_senha.setHint(TNTAirFight.espacamento("CONFIRMAR SENHA"));
        
        final Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject json_send = new JSONObject();
				try {					
					if(nome.getText().toString().equalsIgnoreCase("")){
						tnt.AlertTNT("Por favor, preecha o nome.", Cadastro.this);
						return;
					}
					
					if(login.getText().toString().equalsIgnoreCase("")){
						tnt.AlertTNT("Por favor, preecha o e-mail.", Cadastro.this);
						return;
					}
					
					if(senha.getText().toString().equalsIgnoreCase("")){
						tnt.AlertTNT("Por favor, preecha a senha.", Cadastro.this);
						return;
					}
					
					if(! senha.getText().toString().equals(confirme_senha.getText().toString())){
						tnt.AlertTNT("As senhas não conferem!", Cadastro.this);
						return;
					}
					
					String data = String.format("%s-%s-%s", startYear, startMonth,startDay);					
					
					json_send.put("Nome", nome.getText().toString());
					json_send.put("Email", login.getText().toString());
					json_send.put("Password", senha.getText().toString());
					json_send.put("dtaNasc", data);
					json_send.put("latitude", my_session.getLatitude());
					json_send.put("longitude", my_session.getLogintude());
					json_send.put("cidade", my_session.getCidade());
					json_send.put("estado", my_session.getEstado());
					json_send.put("hasFarGame", true);
					
					if(!tnt.verificaConexao()){
						tnt.AlertTNT("Sem conexão", Cadastro.this);
					}else{
						new CadastroWs().execute(String.valueOf(json_send));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		});
    }
    
    public class CadastroWs extends AsyncTask<String, Void, Void>{
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_dialog = new ProgressDialog(Cadastro.this);
			progress_dialog.setMessage("Aguarde...");
			progress_dialog.setCancelable(true);
			progress_dialog.show();
		}
    	
		@Override
		protected Void doInBackground(String... params) {
			String json = String.valueOf(params[0]);
			
			ServiceHandler service = new ServiceHandler();
			final String retorno_json = service.makeServiceCall(getString(R.string.url_cadastro), ServiceHandler.POST, json, null, null);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject obj_json = new JSONObject(retorno_json);
						Log.i("newtnt", retorno_json);
						if(obj_json.getString("Status").equalsIgnoreCase("Error")){
							tnt.AlertTNT(obj_json.getString("Message"), Cadastro.this);
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
							my_session.setImage(pathPhoto);
														
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
							
							
							startActivity(new Intent(Cadastro.this, Perfil.class));
							finish();
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
    
   

    class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog dialog = new DatePickerDialog(Cadastro.this, this, startYear, startMonth, startDay);
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
            
            //2014-05-21
            data.setText(String.format("%s/%s/%s", mes, dias, ano));
        }
    } 
    
    public void onDetroy(){
    	super.onDestroy();
    	if(TNTAirFight.getInstance().audio_splash_loop != null){
   			TNTAirFight.getInstance().audio_splash_loop.stop();
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
	            
	            pathPhoto = getRealPathFromURI(getImageUri(Cadastro.this, imageBitmap));
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
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
