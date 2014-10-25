package classes;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class TNTAirFight {
	public static Activity act;
	public View view;
	public MediaPlayer audio_splash_loop;
	public static MediaPlayer inicio_luta;
	public static MediaPlayer gongo;
	public static MediaPlayer final_audio;
	public static MediaPlayer boa_garoto;
	public static MediaPlayer vamos_po;
	public static MediaPlayer vencedor;
	public static MediaPlayer _vencedor;
	public static MediaPlayer levanta;
	public static MediaPlayer torcida;
	private static TNTAirFight mInstance= null;
	
	
	public TNTAirFight(Activity act){
		TNTAirFight.act = act;
		view = act.findViewById(android.R.id.content);
		setAllFonts(view);
		
	}
	public TNTAirFight(Activity act, boolean font){
		TNTAirFight.act = act;
		view = act.findViewById(android.R.id.content);
		if(font){
			setAllFonts(view);
		}
	}
	
	public static synchronized TNTAirFight getInstance(){
    	if(null == mInstance){
    		mInstance = new TNTAirFight(act);
    	}
    	return mInstance;
    }
	
	private void setAllFonts(View view) {
	    try {
	        if (view instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) view;
	            for (int i = 0; i < vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                setAllFonts(child);
	         }
	        } else if (view instanceof TextView ) {
	        	TextView v = ((TextView) view);
	            v.setTypeface(Typeface.createFromAsset(TNTAirFight.act.getAssets(), "fonts/Politica.ttf"));
	        }
	    } catch (Exception e) {
	    }
	 }


	public void setFont(TextView tv){
		Typeface tf = Typeface.createFromAsset(TNTAirFight.act.getApplicationContext().getAssets(),"fonts/Politica.ttf");
		tv.setTypeface(tf);
	}
	public void setFont(EditText tv){
		Typeface tf = Typeface.createFromAsset(TNTAirFight.act.getApplicationContext().getAssets(),"fonts/Politica.ttf");
		tv.setTypeface(tf);
	}
	public void setFontBr(EditText tv){
		Typeface tf = Typeface.createFromAsset(TNTAirFight.act.getApplicationContext().getAssets(),"fonts/Politica Bold.ttf");
		tv.setTypeface(tf);
	}
	public void setFontBr(TextView tv){
		Typeface tf = Typeface.createFromAsset(TNTAirFight.act.getApplicationContext().getAssets(),"fonts/Politica Bold.ttf");
		tv.setTypeface(tf);
	}
	
	public void AlertTNT(String msg_alerta, Activity act)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle("TNTAirFight");
        alert.setMessage(msg_alerta);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog, int id) {            	
            }
        });
        alert.show();
	}
	
	public static String dateToEua(String data) {
		String data_eua[] = data.split("/");
		
		return String.format("%s-%s-%s", data_eua[2], data_eua[1], data_eua[0]);
	}
	
	public static String calculaIdade(String dataNasc){
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		int mesAtual = c.get(Calendar.MONTH);
		//int diaAtual = c.get(Calendar.DAY_OF_MONTH);
		
		
		if(dataNasc.contains("T")){
			dataNasc = dataNasc.replace("T", " ");
		}
		
		if(dataNasc.contains("-")){
			String[] data = dataNasc.split("-");
			if(data.length > 0){
				int anoNascimento = Integer.parseInt(data[0]);  
				int mesNascimento = Integer.parseInt(data[1]);	    
			    int idade = anoAtual-anoNascimento;
			    if(mesAtual < mesNascimento){
			    	idade--;
			    }
			    return String.valueOf(String.format("%s %s", idade, espacamento("ANOS")));
			}
		}
		return "ANOS";
    }
	
	
	public static void sleep(){
		try {
    	    Thread.sleep(2000);
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
	}
	
	public static void sleep(int milisegundos){
		try {
    	    Thread.sleep(milisegundos);
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
	}
	public static String getTipo(JSONArray json) {
		String tipo = "";
		try {
			tipo = json.getString(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tipo;
	}
	
	public  boolean verificaConexao() {  
	    boolean conectado;  
	    ConnectivityManager conectivtyManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if (conectivtyManager.getActiveNetworkInfo() != null  
	            && conectivtyManager.getActiveNetworkInfo().isAvailable()  
	            && conectivtyManager.getActiveNetworkInfo().isConnected()) {  
	        conectado = true;  
	    } else {  
	        conectado = false;  
	    }  
	    return conectado;  
	}  
	
	
	public static SpannableString espacamento(String originalText) {
		return espacamento(originalText, 5);
	}
	public static SpannableString espacamento(final String originalText, float letterSpacing) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if(i+1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return finalText;
    }
	
	
	public static boolean isRunningInForeground() {
		Log.i("newtnt", String.valueOf(isAppRunning(act)));
		
		PowerManager powerManager = (PowerManager) act.getSystemService(act.POWER_SERVICE);
		if(powerManager.isScreenOn() == false){
			return false;
		}		
		
	    ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
	    List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
	    if (tasks.isEmpty()) {
	        return false;
	    }

	    
	    
	    
	    String topActivityName = tasks.get(0).topActivity.getPackageName();
	    return topActivityName.equalsIgnoreCase(act.getPackageName());
	}
	
	public static boolean isAppRunning(Context context) {
		Log.i("newtnt", "onPause");
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
	    if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
	        return true;
	    }
	    return false;
	}
	
}
