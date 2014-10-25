package sessions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MySession {
	public Activity act;
	public SharedPreferences session;
	
	public static final String MY_PREFERENCES = "MySession";
	public static final String id = "id";
	public static final String idade = "idade";
	public static final String cidade = "cidade";
	public static final String estado = "estado";
	public static final String logintude = "logintude";
	public static final String latitude = "latitude";
	public static final String lutas = "lutas";
	public static final String vitorias = "vitorias";
	public static final String nome = "nome";
	public static final String email = "email";
	public static final String facebook = "facebook";
	public static final String image = "image";
	public static final String distante = "distante"; 
	
	
	public MySession(Activity act){
		this.act = act;
		session = act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		MostrarAll();
	}
	
	public String getId(){
		return session.getString(id, "");
	}
	public void setId(String setId){
		Editor editor = session.edit();
		editor.putString(id, setId);
		editor.commit();
	}
	
	
	public String getVitorias(){
		return session.getString(vitorias, "");
	}
	public void setVitorias(String set_vitorias){
		Editor editor = session.edit();
		editor.putString(vitorias, set_vitorias);
		editor.commit();
	}
	
	
	public String getIdade(){
		return session.getString(idade, "");
	}
	public void setIdade(String setIdade){
		Editor editor = session.edit();
		editor.putString(idade, setIdade);
		editor.commit();
	}
	
	public String getCidade(){
		if(session.getString(cidade, "").equals("")){
			return "Brasil";
		}
		return session.getString(cidade, "");
	}
	public void setCidade(String setCidade){
		Editor editor = session.edit();
		editor.putString(cidade, setCidade);
		editor.commit();
	}
	
	public String getEstado(){
		if(session.getString(estado, "").equals("")){
			return "BR";
		}
		return session.getString(estado, "");
	}
	public void setEstado(String setEstado){
		Editor editor = session.edit();
		editor.putString(estado, setEstado);
		editor.commit();
	}
	
	public String getLogintude(){
		return session.getString(logintude, "");
	}
	public void setLogintude(String setLogintude){
		Editor editor = session.edit();
		editor.putString(logintude, setLogintude);
		editor.commit();
	}
	
	public String getLatitude(){
		return session.getString(latitude, "");
	}
	public void setLatitude(String setLatitude){
		Editor editor = session.edit();
		editor.putString(latitude, setLatitude);
		editor.commit();
	}
	
	
	public String getLutas(){
		return session.getString(lutas, "");
	}
	
	public void setLutas(String setLutas){
		Editor editor = session.edit();
		editor.putString(lutas, setLutas);
		editor.commit();
	}
	
	public String getNome(){
		return session.getString(nome, "");
	}
	public void setNome(String setNome){
		Editor editor = session.edit();
		editor.putString(nome, setNome);
		editor.commit();
	}
	
	public String getEmail(){
		return session.getString(email, "");
	}
	public void setEmail(String setEmail){
		Editor editor = session.edit();
		editor.putString(email, setEmail);
		editor.commit();
	}
	
	public boolean getFacebook(){
		return session.getBoolean(facebook, false);
	}
	public void setFacebook(boolean setFacebook){
		Editor editor = session.edit();
		editor.putBoolean(facebook, setFacebook);
		editor.commit();
	}
	
	public String getImage(){
		return session.getString(image, "");
	}
	
	public void setImage(String setImage){
		Editor editor = session.edit();
		editor.putString(image, setImage);
		editor.commit();
	}
	
	
	public boolean getDistancia(){
		return session.getBoolean(distante, false);
	}
	
	public void setDistancia(boolean setDistante){
		Editor editor = session.edit();
		editor.putBoolean(distante, setDistante);
		editor.commit();
	}
	
	
	
	public boolean check(){
		if(session.getString(id, "").equals("")){
			return false;
		}else{
			return true;
		}
	}
	
	public void lougout(){
		Editor editor = session.edit();
		editor.clear();
	    editor.commit();
	}
	
	public void MostrarAll(){
		Log.d("tnt", String.format("ID = %s \n Idade = %s \n cidade = %s \n estado = %s \n logintude = %s \n latitude = %s \n lutas = %s \n nome = %s \n email = %s \n facebook = %s", 
				session.getString(id, ""), session.getString(idade, ""), session.getString(cidade, ""), session.getString(estado, ""), session.getString(logintude, ""),
				session.getString(latitude, ""), session.getString(lutas, ""), session.getString(nome, ""), session.getString(email, ""), session.getBoolean(facebook, false)));
	}
	
}
