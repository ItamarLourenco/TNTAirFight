package fragments;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.bizsys.tntairflight.R;
import classes.Adversario;
import classes.DownloadImage;
import classes.TNTAirFight;


@SuppressLint({ "ValidFragment", "ValidFragment", "ValidFragment", "DefaultLocale" })
public class AdversariosFragment extends Fragment {
	public int position = 0;
	public ArrayList<Adversario> lista_adversario = new ArrayList<Adversario>();
	public Activity act = null;
	public TNTAirFight tnt;
	public ViewPager mPager;
	
	public AdversariosFragment(ArrayList<Adversario> lista_adversario, FragmentActivity act, int position, ViewPager mPager){
		tnt = new TNTAirFight(act, false);
		this.position = position;
		this.lista_adversario = lista_adversario;
		this.act = act;
		this.mPager = mPager;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_view_adversarios, container, false);
		
		final Adversario adv = lista_adversario.get(position);
		
		
		ImageView seta_menos = (ImageView) rootView.findViewById(R.id.seta_menos);
		seta_menos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(position - 1);				
			}
		});
		ImageView seta_mais = (ImageView) rootView.findViewById(R.id.seta_mais);
		seta_mais.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(position + 1);
			}
		});
		
		
				
		TextView nome = (TextView) rootView.findViewById(R.id.nome);
		nome.setText(TNTAirFight.espacamento(adv.getNome().toUpperCase()));
		tnt.setFont(nome);
		
		TextView valor_vitoria = (TextView) rootView.findViewById(R.id.valor_vitoria);
		valor_vitoria.setText(String.valueOf(adv.getVitorias()));
		tnt.setFont(valor_vitoria);
		
		TextView valor_empates = (TextView) rootView.findViewById(R.id.valor_empates);
		valor_empates.setText(String.valueOf(adv.getEmpates()));
		tnt.setFont(valor_empates);
		
		TextView valor_derrotas = (TextView) rootView.findViewById(R.id.valor_derrotas);
		valor_derrotas.setText(String.valueOf(adv.getDerrotas()));
		tnt.setFont(valor_derrotas);
		
		TextView total_lutas = (TextView) rootView.findViewById(R.id.pontos);
		total_lutas.setText(String.valueOf(adv.getTotalLutas()));		
		tnt.setFont(total_lutas);
		
		TextView cidade = (TextView) rootView.findViewById(R.id.cidade);
		cidade.setText(TNTAirFight.espacamento(String.format(" %s, %s", adv.getCidade(), adv.getEstado()).toUpperCase()));
		tnt.setFont(cidade);
		
		TextView idade = (TextView) rootView.findViewById(R.id.idade);
		idade.setText(classes.TNTAirFight.calculaIdade(adv.getIdade()));
		tnt.setFont(idade);
		
		
		final ImageView img_user = (ImageView) rootView.findViewById(R.id.img_user);
		final ProgressBar progress_img_user = (ProgressBar) rootView.findViewById(R.id.progress_img_user);
		
		if(!adv.getImage().equals(""))
		{
			new AsyncTask<Void, Void, Void>()
			{
				@Override
				protected Void doInBackground(Void... arg0) {
					try {
						HttpClient http = new DefaultHttpClient();
						HttpGet get = new HttpGet(adv.getImage());
						HttpResponse response = http.execute(get);
						
						byte[] bytes = EntityUtils.toByteArray(response.getEntity());					
						final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						
						act.runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                        	img_user.setImageBitmap(DownloadImage.formatImg(bitmap));
	                        	img_user.setVisibility(View.VISIBLE);
	                        	progress_img_user.setVisibility(View.GONE);
	                        }
	                    });
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
				
			}.execute();
		}else{
			progress_img_user.setVisibility(View.GONE);
		}
		
		
		
        return rootView;
    }

}
