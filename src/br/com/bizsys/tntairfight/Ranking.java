package br.com.bizsys.tntairfight;

import android.graphics.Bitmap;

public class Ranking {
	private String nome;
	private String pontos;
	private String id; 
	private String image;
	private Bitmap bitmap;
	
	
	public Ranking(String id, String nome, String pontos, String image, Bitmap bitmap){
		this.id = id;
		this.nome = nome;
		this.pontos = pontos;
		this.image = image;
		this.bitmap = bitmap;
	}
	
	public String getNome(){
		return this.nome;
	}
	public void setNome(String nome){
		this.nome = nome;
		
	}
	
	
	public String getPontos(){
		return this.pontos;
	}
	public void setPontos(String pontos){
		this.pontos = pontos;
	}
	
	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getImage(){
		return this.image;
	}
	public void setImage(String image){
		this.image = image;
	}
	
	public Bitmap getBitmap(){
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
}
