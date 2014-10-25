package sessions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigSave{
	public Activity act;
	public static final String MY_PREFERENCES = "TNT_CONFIG";
	public static final String SONS = "sons";
	public static final String VIBRACAO = "vibracao";
	public static final String NOTIFICACAO = "notificacao";
	public static final String CANHOTO = "canhoto";
	public SharedPreferences sharedpreferences;
	
	public ConfigSave(Activity act)
	{
		this.act = act;
		sharedpreferences = act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	public void setSons(boolean set){
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(SONS, set);
		editor.commit();
	}
	public boolean getSons(){
		return sharedpreferences.getBoolean(SONS, true);
	}
	
	public void setVibracao(boolean set){
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(VIBRACAO, set);
		editor.commit();
	}
	public boolean getVibracao(){
		return sharedpreferences.getBoolean(VIBRACAO, true);
	}
	
	public void setNotificacao(boolean set){
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(NOTIFICACAO, set);
		editor.commit();
	}
	public boolean getNotificacao(){
		return sharedpreferences.getBoolean(NOTIFICACAO, true);
	}
	
	public void setCanhoto(boolean set){
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(CANHOTO, set);
		editor.commit();
	}
	
	public boolean getCanhoto(){
		return sharedpreferences.getBoolean(CANHOTO, false);
	}
	
		
	public void clearConfiguracoes()
	{
		Editor editor = sharedpreferences.edit();
		editor.clear();
	    editor.commit();
	}
	
}
