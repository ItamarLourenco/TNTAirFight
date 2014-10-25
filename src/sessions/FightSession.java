package sessions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FightSession {
	public Activity act;
	public SharedPreferences session;
	
	public static final String MY_PREFERENCES = "FightSession";
	public static final String match_id = "match_id";
	public static final String player_1 = "player_1";
	public static final String golpes = "golpes";
	
	public FightSession(Activity act){
		this.act = act;
		session = act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		//MostrarAll();
	}
	
	
	public void setPlayer1(boolean set_player_1){
		Editor editor = session.edit();
		editor.putBoolean(player_1, set_player_1);
		editor.commit();
	}
	
	public boolean getPlayer1(){
		return session.getBoolean(player_1, false);
	}
	
	public void setMatchId(String set_match_id){
		Editor editor = session.edit();
		editor.putString(match_id, set_match_id);
		editor.commit();
	}
	
	public String getMatchId(){
		return session.getString(match_id, "");
	}
	
	public String getGolpes(){
		return session.getString(golpes, "");
	}
	
	public void setGolpes(String set_golpes){
		Editor editor = session.edit();
		editor.putString(golpes, set_golpes);
		editor.commit();
	}
	
	public void lougout(){
		Editor editor = session.edit();
		editor.clear();
	    editor.commit();
	}

//	private void MostrarAll() {
//		Log.i("tnt", String.format("MATCHID = %s, PLAYER1 = %s", getMatchId(), getPlayer1()));
//	}

}
