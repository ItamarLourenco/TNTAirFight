package classes;

import java.util.ArrayList;

import sessions.MySession;
import android.app.Activity;
import android.content.OperationApplicationException;
import android.util.Log;
import br.com.bizsys.tntairflight.R;

import com.zsoft.SignalA.Hubs.HubConnection;
import com.zsoft.SignalA.Hubs.HubInvokeCallback;
import com.zsoft.SignalA.Hubs.IHubProxy;
import com.zsoft.SignalA.Transport.StateBase;
import com.zsoft.SignalA.transport.longpolling.LongPollingTransport;

public class SignalConexao{
	public static Activity act;
	public static MySession my_session = null;
	public HubConnection conexao = null;
	public static IHubProxy hub = null;
	final public String NAME_HUB = "FightHub";
		
	public void Conexao(Activity act) {
		my_session = new MySession(act);
		conexao = new HubConnection(act.getString(R.string.url_signal), act, new LongPollingTransport()){
			@Override
			public void OnMessage(String message) {
				if(message.length() > 200){
					Log.i("signal", String.format("OnMessage = %s", message.substring(0, 200)));
				}else{
					Log.i("signal", String.format("OnMessage = %s", message));
				}
						
			}
			@Override
			public void OnStateChanged(StateBase oldState, StateBase newState) {
				switch(newState.getState()){
					case Disconnected:
						Log.d("DEBUG", "Disconnected");
					break;
					case Connected:
						Log.d("DEBUG", "Connected");
						sendNotify();
					break;
					default:
						Log.d("DEBUG", "Disconnected!");
					break;
				}
			}
			@Override
			public void OnError(Exception exception) {
	            Log.e("signal", String.format("Erro Sockets = %s", exception.getMessage()));
			}
		};
		
		try {
    		hub = conexao.CreateHubProxy(NAME_HUB);
		} catch (OperationApplicationException e) {
			Log.i("signal", e.getMessage());
		}
		conexao.Start();
	}
	

	public void sendNotify(){
		ArrayList<Object> id = new ArrayList<Object>();
		id.add(my_session.getId());

		hub.Invoke("Notify", id, new HubInvokeCallback(){
			@Override
			public void OnError(Exception arg0) {}
			@Override
			public void OnResult(boolean send_getAllUsers, String arg1) {
				if(send_getAllUsers){
					sendGetAllOnlineStatus();
				}
			}
		});
	}


	protected void sendGetAllOnlineStatus() {
		ArrayList<Object> id = new ArrayList<Object>();
		id.add(my_session.getId());
		id.add(my_session.getLatitude());
		id.add(my_session.getLogintude());
	
		
		hub.Invoke("GetAllOnlineStatus", id, new HubInvokeCallback(){
			@Override
			public void OnError(Exception arg0) {}
			@Override
			public void OnResult(boolean send_getAllUsers, String arg1) {}
		});
	}


	public void leave() {
		ArrayList<Object> id = new ArrayList<Object>();
		id.add(my_session.getId());		
		hub.Invoke("Leave", id, new HubInvokeCallback(){
			@Override
			public void OnError(Exception arg0) {}
			@Override
			public void OnResult(boolean send_getAllUsers, String arg1) {
				conexao.Stop();
			}
		});
	}

	
	public void SendToSpecific(ArrayList<Object> params){
		hub.Invoke("SendToSpecific", params, new HubInvokeCallback(){
			@Override
			public void OnError(Exception erro) {}
			@Override
			public void OnResult(boolean enviado, String retorno) {}
		});
	}
	
	public void GetMatch(ArrayList<Object> params){
		hub.Invoke("GetMatch", params, new HubInvokeCallback(){
			@Override
			public void OnError(Exception arg0) {}

			@Override
			public void OnResult(boolean arg0, String arg1) {}							
		});
				
	}	
}


