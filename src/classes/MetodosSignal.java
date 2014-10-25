package classes;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.zsoft.SignalA.Hubs.HubOnDataCallback;

public abstract class MetodosSignal extends SignalConexao {
	public MetodosSignal(){
		SignalConexao.hub.On("OnlineStatus", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				Log.i("newtnt", json.toString());
				OnlineStatus(json);
			}
		});
		SignalConexao.hub.On("disconnected", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				sendGetAllOnlineStatus();
			}
		});
		SignalConexao.hub.On("enters", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				sendGetAllOnlineStatus();
			}
		});
		SignalConexao.hub.On("match", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				try {
					getMatch(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		SignalConexao.hub.On("differentName", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				sendGetAllOnlineStatus();
			}
		});
		SignalConexao.hub.On("broadcastMessage", new HubOnDataCallback(){
			@Override
			public void OnReceived(JSONArray json) {
				try {
					broadcastMessage(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});			
	}
	
	public abstract void getMatch(JSONArray json) throws JSONException;
	public abstract void OnlineStatus(JSONArray json);
	public abstract void broadcastMessage(JSONArray json) throws JSONException;

}
