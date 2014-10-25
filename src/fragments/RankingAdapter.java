package fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.bizsys.tntairfight.Ranking;
import br.com.bizsys.tntairflight.R;
import classes.TNTAirFight;

public class RankingAdapter extends BaseAdapter {
	private Context context = null;
	private ArrayList<Ranking> list_ranking= new ArrayList<Ranking>();
	private TNTAirFight tnt = null;
	private Activity act;
	private LayoutInflater layoutInflater;
	
	public RankingAdapter(Context context, Activity act,  ArrayList<Ranking> list_ranking){
		this.act = act;
		this.context = context;
		this.tnt = new TNTAirFight(act);
		this.list_ranking = list_ranking;
		
		layoutInflater = LayoutInflater.from(context);
	}
	

	@Override
	public int getCount() {
		return list_ranking.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	public Ranking ranking = null;
	public View list_view = null;
	public ViewHolder holder = null;
	@SuppressLint({ "DefaultLocale", "ViewHolder", "InflateParams", "SetJavaScriptEnabled" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_view_ranking, null);
			
			holder = new ViewHolder();
			holder.nome = (TextView) convertView.findViewById(R.id.nome);
			holder.pontos = (TextView) convertView.findViewById(R.id.pontos);
			holder.img = (ImageView) convertView.findViewById(R.id.img_user);
			//holder.webView = (WebView) convertView.findViewById(R.id.webView);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ranking = list_ranking.get(position);
		
		holder.nome.setText(ranking.getNome().toUpperCase());		
		holder.pontos.setText(ranking.getPontos());
		tnt.setFont(holder.nome);
		tnt.setFont(holder.pontos);
		tnt.setFont((TextView) convertView.findViewById(R.id.pts));
		
		if(ranking.getBitmap() != null){
			holder.img.setImageBitmap(ranking.getBitmap());
		}else{
			holder.img.setImageBitmap(null);
		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView nome;
		TextView pontos;
		WebView webView;
		ImageView img;
	}
	
	
}
