package classes;

public class Adversario {
	private String nome;
	private String idade;
	private String lutas;
	private String vitorias;
	private String empates;
	private String derrotas;
	private String id;
	private String total_lutas;
	private String cidade;
	private String estado;
	private String image;
	
	public String getCidade(){
		if(this.cidade.equals("null, null")){
			return "Brasil, BR";
		}else{
			return this.cidade;
		}
		
	}
	
	public void setCidade(String cidade){
		this.cidade = cidade;
	}
	
	public String getTotalLutas(){
		return total_lutas;
	}
	
	public void setTotalLutas(String total_lutas){
		this.total_lutas = total_lutas;
	}
	
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public String getLutas() {
		return lutas;
	}
	public void setLutas(String lutas) {
		this.lutas = lutas;
	}
	public String getVitorias() {
		if(vitorias == null || vitorias.equalsIgnoreCase("null")){
			return "0";
		}else{
			return vitorias;
		}
		
	}
	public void setVitorias(String vitorias) {
		this.vitorias = vitorias;
	}
	public String getEmpates() {
		if(empates == null || empates.equalsIgnoreCase("null")){
			return "0";
		}else{
			return empates;
		}
	}
	public void setEmpates(String empates) {
		this.empates = empates;
	}
	public String getDerrotas() {
		if(derrotas == null || derrotas.equalsIgnoreCase("null")){
			return "0";
		}else{
			return derrotas;
		}
		
	}
	public void setDerrotas(String derrotas) {
		this.derrotas = derrotas;
	}

	public String getEstado() {
		if(estado == null || estado.equalsIgnoreCase("null")){
			return "Br";
		}
		return estado;
	}
	
	public void setEstado(String estado){
		this.estado = estado;
	}
	
	public void setImage(String image){
		this.image = image;
	}
	
	public String getImage(){
		return image;
	}
}
