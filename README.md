# Relatório - Algoritmo de Dijkstra

## 1. Visão Geral e Objetivo
O software tem como objetivo calcular o caminho de menor custo (distância) entre um vértice "raiz" (origem) e todos os outros vértices de um grafo. A implementação utiliza a estratégia gulosa do algoritmo de Dijkstra, otimizada com uma fila de prioridade para garantir performance adequada em grafos maiores.

## 2. Estrutura do Sistema (Arquitetura)
O sistema é modularizado nas seguintes classes principais:

* **cap7/listaad/autoreferencia/Grafo**
  Representa o grafo usando Listas de Adjacência. Cada vértice possui uma lista encadeada (Lista) contendo arestas para seus vizinhos. Utiliza a classe auxiliar GrafoAresta para armazenar origem, destino e peso.

* **cap3/autoreferencia/Lista**
  Uma implementação de lista encadeada simples, utilizada para armazenar as arestas de cada vértice do grafo.

* **cap7/FPHeapMinIndireto**
  Uma implementação de Fila de Prioridade (Min-Heap).
  > *Diferencial:* É "Indireta", o que significa que ela mantém um mapeamento (pos) entre o ID do vértice e sua posição no Heap. Isso permite a operação diminuiChave em tempo logarítmico, essencial para a eficiência do Dijkstra.

* **cap7/Dijkstra**
  Contém a lógica do algoritmo. Mantém arrays de antecessor (para reconstruir o caminho) e p (pesos/distâncias acumuladas). O método obterArvoreCMC executa o cálculo das distâncias.

* **cap7/Testa Dijkstra**
  A classe principal (main) que instancia o grafo, insere arestas e executa o teste.

* **cap7/RouteController (Componente Web)**
  Uma classe controladora REST que parece tentar receber um JSON de nós e arestas para calcular rotas. Apresenta erros de compilação/dependências faltantes nos arquivos fornecidos.

## 3. Entrada de Dados
O programa aceita dados de duas formas:

1. **Entrada Estática (Console - Testa Dijkstra):**
   * O grafo é construído programaticamente no método main.
   * São inseridas arestas direcionadas com pesos (custos).
   * O formato lógico insere Aresta (origem, destino, peso).
   * Define-se um vértice raiz para iniciar a busca.

2. **Entrada Dinâmica (Web - RouteController):**
   * Recebe um objeto JSON via POST contendo listas de nodes (nós) e edges (arestas).

## 4. Saída de Dados
A execução principal (Testa Dijkstra) produz as seguintes informações no console:

1. *Estrutura do Grafo:* A representação textual das listas de adjacência (ex: vértices e seus vizinhos com pesos).
2. *Cálculo:* Mensagens de log indicando o início do cálculo a partir da raiz.
3. *Caminhos e Custos:*
   * Para cada vértice destino solicitado, o programa imprime a sequência de vértices a serem percorridos.
   * O custo total acumulado desse caminho.
   * Exemplo: Caminho de 0 ate 3: 0->1->3 (custo: 15.0).

## 5. Análise de Complexidade
A eficiência desta implementação é determinada pelo uso do FPHeapMinIndireto em conjunto com a Lista de Adjacência.

### Complexidade de Tempo
Sendo $V$ o número de vértices e $E$ o número de arestas:

* *Inicialização:* $O(V)$ para configurar os arrays de distância e o heap.
* *Extração do Mínimo:* Ocorre uma vez para cada vértice. Custo: $O(V \log V)$.
* *Relaxamento de Arestas:* Graças ao mapeamento indireto no Heap, a atualização da prioridade custa $O(\log V)$. Total para todas as arestas: $O(E \log V)$.

*Complexidade Total:*
$$O((V+E) \log V)$$

> Para grafos conexos onde $E \ge V-1$, isso simplifica para *$O(E \log V)$*. Isso é significativamente mais rápido que a implementação ingênua com array ($O(V^2)$) para grafos esparsos.

### Complexidade de Espaço
* *Grafo (Listas de Adjacência):* $O(V+E)$.
* *Estruturas Auxiliares:* $O(V)$ (Arrays de peso, antecessor, heap).

*Espaço Total:*
$$O(V+E)$$

## 6. Observações Adicionais e Limitações
* *Pesos Negativos:* O algoritmo de Dijkstra não funciona corretamente com arestas de peso negativo (é uma limitação teórica do algoritmo).
* *Componente Web:* Os arquivos RouteController.class contêm erros de referência (ex: Unresolved compilation problems), indicando ausência de bibliotecas do Spring Framework.
* *Tratamento de Erros:* O código possui exceções básicas para "Heap vazio" ou "Lista vazia".

## 7. Conclusão
O programa é uma implementação acadêmica robusta e eficiente do algoritmo de Dijkstra em Java. O uso de Listas de Adjacência combinado com um Heap Mínimo Indireto demonstra preocupação com a performance ($O(E \log V)$), tornando-o adequado para grafos de médio a grande porte.
