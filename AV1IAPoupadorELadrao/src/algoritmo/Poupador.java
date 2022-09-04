package algoritmo;

import java.util.*;
import java.util.stream.Collectors;


public class Poupador extends ProgramaPoupador {	
	private boolean grafoInicializado = false;
	Map<String, Integer> adjacentes;
	List<Integer> visao;
	List<Integer> rastroPoupador;
	List<Integer> rastroLadrao;
	//String: conteudo, Integer: Valor correspondente no mapa
	Map<String, Integer> TabelaMapa;
	//Integer: Posicao no vetor visao, Integer: Valor da direcao a ser seguida
	Map<Integer, Integer> TabelaDirecao;
	Map<String, Integer> AcaoValor;
	List<Integer> PosicoesPossiveis;
	Grafo grafo;
	
	
	int decisao;
	@Override
	public int acao()
	{
		Init();
		//System.out.println(TabelaMapa.values());
		atualizaGrafo();
		//grafo.printGrafo(); //impressão do grafo
		decisao = InstintoDeFuga();
		decisao = correrProBanco(grafo);
		if (decisao == 0) decisao = SeuSiriguejo();
		//return LidarAdjacentes();
		
		return decisao;
	}
	
	// ------------------------------------------------------------
	//Metodos auxiliares
	
	/**
	 * Responsavel por inicializar todas as estruturas de dados necessarias
	 */
	private void Init() 
	{
		//List<Integer> visaoLista = Arrays.stream(sensor.getVisaoIdentificacao()).boxed().toList();
		
				visao =  Arrays.stream( sensor.getVisaoIdentificacao() ).boxed().collect(Collectors.toList());
				rastroPoupador = Arrays.stream( sensor.getAmbienteOlfatoPoupador() ).boxed().collect(Collectors.toList());
				rastroLadrao = Arrays.stream( sensor.getAmbienteOlfatoLadrao() ).boxed().collect(Collectors.toList());

				//preencher adjacentes (precisa ser atualizado todo turno)
				adjacentes = new HashMap<String, Integer>();
				adjacentes.put("cima",visao.get(7));
				adjacentes.put("esquerda",visao.get(11));
				adjacentes.put("direita",visao.get(12));
				adjacentes.put("baixo",visao.get(16));
				
				TabelaMapa = new HashMap<String, Integer>();
				//preencher tabela-mapa
				TabelaMapa.put("semvisao", -2);
				TabelaMapa.put("foraDoMapa", -1);
				TabelaMapa.put("vazio", 0);
				TabelaMapa.put("parede", 1);
				TabelaMapa.put("banco", 3);
				TabelaMapa.put("moeda", 4);
				TabelaMapa.put("poder", 5);
				TabelaMapa.put("poupador", 100);
				TabelaMapa.put("ladrao1", 200);
				TabelaMapa.put("ladrao2", 210);
				TabelaMapa.put("ladrao3", 220);
				TabelaMapa.put("ladrao4", 230);
				
				AcaoValor = new HashMap<String, Integer>();
				
				AcaoValor.put("parado", 0);
				AcaoValor.put("cima", 1);
				AcaoValor.put("baixo", 2);
				AcaoValor.put("direita", 3);
				AcaoValor.put("esquerda", 4);

				PosicoesPossiveis = new ArrayList<>();

				PosicoesPossiveis.add(AcaoValor.get("cima"));
				PosicoesPossiveis.add(AcaoValor.get("baixo"));
				PosicoesPossiveis.add(AcaoValor.get("direita"));
				PosicoesPossiveis.add(AcaoValor.get("esquerda"));
				
				TabelaDirecao = new HashMap<Integer, Integer>();
				//preencher tabela-direcao
				TabelaDirecao.put(0, AcaoValor.get("esquerda"));
				TabelaDirecao.put(1, AcaoValor.get("esquerda"));
				TabelaDirecao.put(5, AcaoValor.get("esquerda"));
				TabelaDirecao.put(6, AcaoValor.get("esquerda"));
				TabelaDirecao.put(10, AcaoValor.get("esquerda"));
				TabelaDirecao.put(11, AcaoValor.get("esquerda"));
				TabelaDirecao.put(14, AcaoValor.get("esquerda"));
				TabelaDirecao.put(15, AcaoValor.get("esquerda"));
				TabelaDirecao.put(19, AcaoValor.get("esquerda"));
				TabelaDirecao.put(20, AcaoValor.get("esquerda"));
				
				TabelaDirecao.put(3, AcaoValor.get("direita"));
				TabelaDirecao.put(4, AcaoValor.get("direita"));
				TabelaDirecao.put(8, AcaoValor.get("direita"));
				TabelaDirecao.put(9, AcaoValor.get("direita"));
				TabelaDirecao.put(12, AcaoValor.get("direita"));
				TabelaDirecao.put(13, AcaoValor.get("direita"));
				TabelaDirecao.put(17, AcaoValor.get("direita"));
				TabelaDirecao.put(18, AcaoValor.get("direita"));
				TabelaDirecao.put(22, AcaoValor.get("direita"));
				TabelaDirecao.put(23, AcaoValor.get("direita"));
				
				TabelaDirecao.put(2, AcaoValor.get("cima"));
				TabelaDirecao.put(7, AcaoValor.get("cima"));
				TabelaDirecao.put(16, AcaoValor.get("baixo"));
				TabelaDirecao.put(21, AcaoValor.get("baixo"));		
				
				initGrafo();
				
	}
	
	private void initGrafo() {
		if(grafoInicializado) return;

		grafoInicializado = true;
		
		grafo = new Grafo();
		
		int x = 30; //número de colunas do mapa do arquivo .txt
		int y = 30; //número de linhas do mapa do arquivo .txt
		
		for(int i=0; i < y; i++) {
			for(int j=0; j < x; j++) {
				grafo.addVertice(j, i, false, 0);
				
				//vizinho cima
				int posVizinhoCima = i-1;
				if(posVizinhoCima >= 0) {
					grafo.addVertice(j, posVizinhoCima, false, 0);
					grafo.addAresta(j, i, j, posVizinhoCima);
				}
				//vizinho esquerda
				int posVizinhoEsquerda = j-1;
				if(posVizinhoEsquerda >= 0) {
					grafo.addVertice(posVizinhoEsquerda, i, false, 0);
					grafo.addAresta(j, i, posVizinhoEsquerda, i);
				}
				//vizinho direita
				int posVizinhoDireita = j+1;
				if(posVizinhoDireita <= 29) {
					grafo.addVertice(posVizinhoDireita, i, false, 0);
					grafo.addAresta(j, i, posVizinhoDireita, i);
				}
				//vizinho baixo
				int posVizinhoBaixo = i+1;
				if(posVizinhoBaixo <= 29) {
					grafo.addVertice(j, posVizinhoBaixo, false, 0);
					grafo.addAresta(j, i, j, posVizinhoBaixo);
				}
			}
		}
	}
	
	private void atualizaGrafo() {
		int xCorrente = sensor.getPosicao().x;
		int yCorrente = sensor.getPosicao().y;
		Vertice verticeCorrente = grafo.getVertice(xCorrente, yCorrente);
		verticeCorrente.setVisitado(true);
		grafo.setVerticeCorrente(verticeCorrente);
		
		
		int xAnalisado = sensor.getPosicao().x - 2;
		int yAnalisado = sensor.getPosicao().y - 2;
		int[] visao = sensor.getVisaoIdentificacao();
		
		//Atualiza grafo
		int visaoIndex = -1;
		int k = 0;
		for(int i=0; i < 5; i++) {
			for(int j=0; j < 5; j++) {
				visaoIndex++;
				if(i == 2 && visaoIndex == 12) j++;
				if(i == 3) k = 0;
				if((i == 4 && j == 4) || 
						visao[visaoIndex] == TabelaMapa.get("foraDoMapa") ||
						(xAnalisado + j) > 29 ||
						(xAnalisado + j) < 0 ||
						(yAnalisado + i) > 29 ||
						(yAnalisado + i) < 0) continue;
				
				Vertice verticeAux = this.grafo.getVertice(xAnalisado + j, yAnalisado + i);
				verticeAux.setValor(visao[visaoIndex]);

				//posicao do banco
				Vertice Posicao_Banco = this.grafo.getVertice(7, 7);
				Posicao_Banco.setValor(3);
				
				int kl = 0;
				
			}
		}		
	}
	
	//iterar em posicoes adjacentes e ir em direcao da primeira moeda encontrada
	private int CorrerEmMoedaAdjacente() 
	{

		if(adjacentes.get("cima") == TabelaMapa.get("moeda"))
		{
			return AcaoValor.get("cima");
		}
		else if(adjacentes.get("baixo") == TabelaMapa.get("moeda"))
		{
			return AcaoValor.get("baixo");
		}
		else if(adjacentes.get("esquerda") == TabelaMapa.get("moeda"))
		{
			return AcaoValor.get("esquerda");
		}
		else if(adjacentes.get("direita") == TabelaMapa.get("moeda"))
		{
			return AcaoValor.get("direita");
		}
		
		return 0;
	}
	
	private int SeuSiriguejo() 
	{
		
		int decisao;
		decisao = CorrerEmMoedaAdjacente();
		if(decisao != 0) return decisao;
		
		for(int i =0; i < visao.size(); i++) 
		{
			if(visao.get(i) == TabelaMapa.get("moeda"))
			{
				System.out.print("dinheiro");
				return TabelaDirecao.get(i);
			}
		}
		//System.out.println("aleatorio");
		return direcaoAleatoria(PosicoesPossiveis);
		
	}
	
	private int InstintoDeFuga() 
	{
		int fugir = 0;
		//good iteration example for java maps
		for(Map.Entry<String, Integer> entry : adjacentes.entrySet()) 
		{
			//System.out.println(entry.getKey() + " = " + entry.getValue());
			if(entry.getValue().equals(TabelaMapa.get("ladrao1"))||
					entry.getValue().equals(TabelaMapa.get("ladrao2"))	||
					entry.getValue().equals(TabelaMapa.get("ladrao3"))||
					entry.getValue().equals(TabelaMapa.get("ladrao4"))	)
			{
				System.out.print("foge!\n");
				
				switch(entry.getKey()) 
				{
					case "cima":
						fugir = AcaoValor.get("baixo");
						//System.out.println("eita! fugir para baixo");	
						break;
					case "baixo": 
						fugir = AcaoValor.get("cima");
						//System.out.println("eita! fugir para cima");
						break;
					case "direita":
						fugir = AcaoValor.get("esquerda");
						//System.out.println("eita! fugir para esquerda");
						break;
					case "esquerda":
						fugir = AcaoValor.get("direita");
						//System.out.println("eita! fugir para direita");
						break;
					default:
						break;
				}
				return fugir;
			}	
		}	
		return 0;
	}

	private int correrProBanco(Grafo grafo)
	{
		//posicao do poupador
		int posicao_x = (int) sensor.getPosicao().getX();
		int posicao_y = (int) sensor.getPosicao().getY();

		//adcionando uma lista de vertices visitados e uma fila de vertices para ver na bfs
		Set<Vertice> visited = new LinkedHashSet<Vertice>();
		Queue<Vertice> queue = new LinkedList<Vertice>();

		queue.add(grafo.getVertice(posicao_x, posicao_y));
		visited.add(grafo.getVertice(posicao_x, posicao_y)); 

		while(!queue.isEmpty()) {
			Vertice vertice = queue.poll();
			posicao_x = vertice.x;
			posicao_y = vertice.y;
			//cima

			 for (Vertice adjacente : grafo.getVizinhos(posicao_x,posicao_y)) {
				if (!visited.contains(adjacente)) {
					//TODO: colocar parede (add visao do usuario)
					//System.out.println(adjacente.getValor());
					visited.add(adjacente);
					queue.add(adjacente);
					if(adjacente.x == 7 && adjacente.y == 7){
						System.out.println("ENCONTROU BANCO");
					}
				}
			 }
		}
		//grafo.addVertice(j, posVizinhoCima, false, 0);
		//grafo.addAresta(j, i, j, posVizinhoCima);
		return 0;
	}

	private int ronaldinhoGaucho()
	{
		//TO DO implement
		return 0;
	}
	
	
	private int direcaoAleatoria(List<Integer>posiveisPosicoes) 
	{
		Random rand = new Random();
		int randomElement = posiveisPosicoes.get(rand.nextInt(posiveisPosicoes.size()));
		return  randomElement;
	}

	private void DebugLog(String input)
	{
		int numeroPoupador = 120381;
		System.out.println("Poupador " + numeroPoupador + input);
	}

}

class Vertice {
	Integer x;
	Integer y;
	Integer valor;
	boolean visitado;
	
	public Vertice(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public Vertice(Integer x, Integer y, Integer valor) {
		this.x = x;
		this.y = y;
		this.valor = valor;
	}
	
	public Vertice(Integer x, Integer y, boolean visitado, Integer valor) {
		this.x = x;
		this.y = y;
		this.visitado = visitado;
		this.valor = valor;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public boolean isVisitado() {
		return visitado;
	}

	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertice other = (Vertice) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
	
}

class Grafo {
	private Map<Vertice, Set<Vertice>> adjacentes = new HashMap<Vertice, Set<Vertice>>();
	
	private Vertice verticeCorrente;
	
	public void addVertice(Integer x, Integer y) {
		adjacentes.putIfAbsent(new Vertice(x, y), new HashSet<>());
	}
	
	
	public void addVertice(Integer x, Integer y, Integer valor) {
		adjacentes.putIfAbsent(new Vertice(x, y, valor), new HashSet<>());
	}
	
	public void addVertice(Integer x, Integer y, boolean visitado, Integer valor) {
		Optional<Vertice> v = adjacentes.keySet().stream().filter(new Vertice(x,y)::equals).findFirst();
		
		if(v.isPresent() && v.get().isVisitado() == false) {
			v.get().setVisitado(visitado);
		}else {
			adjacentes.putIfAbsent(new Vertice(x, y, visitado, valor), new HashSet<>());
		}
			
	}
	
	public void addVisao() {
		
	}
	
	public void removeVertice(Integer x, Integer y, Integer valor) {
		Vertice v = new Vertice(x, y, valor);
		adjacentes.values().stream().forEach(e -> e.remove(v));
		adjacentes.remove(v);
	}
	
	public void addAresta(Integer x1, Integer y1, Integer x2, Integer y2) {
		Vertice v1 = new Vertice(x1, y1);
		Vertice v2 = new Vertice(x2, y2);
		adjacentes.get(v1).add(v2);
		adjacentes.get(v2).add(v1);
	}


	public Vertice getVerticeCorrente() {
		return verticeCorrente;
	}


	public void setVerticeCorrente(Vertice verticeCorrente) {
		this.verticeCorrente = verticeCorrente;
	}	
	
	public Map<Vertice, Set<Vertice>> getAdjacentes(){
		return this.adjacentes;
	}
	
	
	/**
	 * Retorna os vértices vizinho do vértice na coordneada (x,y).
	 * @param x
	 * @param y
	 * @return
	 */
	public Set<Vertice> getVizinhos(Integer x, Integer y) {
		return this.adjacentes.get(new Vertice(x,y));
	}
	
	public Vertice getVertice(Integer x, Integer y) {
		return this.adjacentes.keySet().stream()
				.filter(new Vertice(x,y)::equals)
				.findFirst().get();
	}
	
	public void printGrafo() {
		int colunas = 30;
		int linhas = 30;
		
		for(int i=0; i < linhas; i++) {
			System.out.print("Linha " + (i+1) + ": ");
			for(int j=0; j < colunas; j++) {
				if(verticeCorrente.getY().equals(i) && verticeCorrente.getX().equals(j))
					System.out.print("X ");
				else
					System.out.print(getVertice(j, i) + " ");
			}
			System.out.println("\n");
		}
	}
}

final class MapaUtils{
	private MapaUtils() {
		
	}
	
//	public static Point getPointFromIndice(Integer indice) {
//		Point point = new Point();
//		
//		
//	}
}
