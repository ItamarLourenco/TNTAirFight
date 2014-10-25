package br.com.bizsys.tntairfight;

import java.util.Timer;
import java.util.TimerTask;

import s.s;
import sessions.ConfigSave;
import sessions.MySession;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;
import br.com.bizsys.tntairflight.R;
import classes.TNTAirFight;

import com.appsflyer.AppsFlyerLib;

@SuppressLint({ "NewApi" })
public class Splash extends Activity {
	public ConfigSave config;
	public boolean fechar = false;
	private Timer timerAtual = new Timer();
    private TimerTask task;
    private final Handler handler = new Handler();
    public MySession my_session;
    public TNTAirFight tnt;
    public ImageView splash = null;
    public VideoView videoView = null;
	public boolean open = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        AppsFlyerLib.setAppsFlyerKey("jxNFtBYNsuJAYscTBhpyta");
        AppsFlyerLib.sendTracking(this);
        
        this.ativarMusica();      
        tnt = new TNTAirFight(this);
        config = new ConfigSave(this);
        my_session = new MySession(this);
        
        
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash));
		videoView.setOnErrorListener(new OnErrorListener(){
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				splash = (ImageView) findViewById(R.id.splash);				
				splash.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.GONE);				
				
				FrameLayout video = (FrameLayout) findViewById(R.id.video);
    	        video.setOnClickListener(new View.OnClickListener() {
    				@Override
    				public void onClick(View v) {    					
    					if(my_session.check()){
    						startActivity(new Intent(Splash.this, Perfil.class));
    						
    					}else{
    						startActivity(new Intent(Splash.this, Login.class));
    						
    					}
    				}
    			});
				return true;
			}
		});
		
		videoView.start();
               
        videoView.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer mp) {    	        
		        FrameLayout video = (FrameLayout) findViewById(R.id.video);
		        video.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {    					
						if(my_session.check()){
							startActivity(new Intent(Splash.this, Perfil.class));
							
						}else{
							startActivity(new Intent(Splash.this, Login.class));
							
						}
					}
				});
			}
        });
        
        new s();
    }
	

	public void onResume(){
		super.onResume();
		if(open == false){
			open = true;
		}else{
			finish();
		}
	}
	
	public void playLooping(){
		TNTAirFight.getInstance().audio_splash_loop = MediaPlayer.create(Splash.this, R.raw.audio_splash_loop);
		TNTAirFight.getInstance().audio_splash_loop.setLooping(true);
		if(config.getSons()){
			TNTAirFight.getInstance().audio_splash_loop.start();
		}
	}
	
	public int tempo = 0;
	private void ativarMusica(){
    task = new TimerTask() {
        public void run() {
           handler.post(new Runnable() {
                public void run() {
                	timerAtual.cancel();
                	playLooping();
                }
           });
        }};           
        timerAtual.schedule(task, 8500, 8500); 
    }
	
	public void onDestroy(){
		super.onDestroy();
		if(TNTAirFight.getInstance().audio_splash_loop != null){
   			TNTAirFight.getInstance().audio_splash_loop.stop();
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
