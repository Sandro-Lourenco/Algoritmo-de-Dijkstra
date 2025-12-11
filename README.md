Aqui está **exatamente o seu texto convertido para Markdown**, organizado, estruturado e com formatação correta:

---

# Análise e Documentação: Algoritmo de Dijkstra para Otimização de Rotas

Este documento detalha a implementação do Algoritmo de Dijkstra em Java, utilizando uma arquitetura Spring Boot para expor os resultados via uma interface web interativa.
O objetivo é calcular o caminho de menor custo (distância) entre dois pontos em um grafo ponderado.

---

## 1. Contexto e Modelagem do Problema

O projeto modela um conjunto de cidades e rotas rodoviárias como um **Grafo Ponderado**:

* **Não-Direcionado**: no cenário das 20 cidades da Estrada de Ferro
* **Dirigido**: no cenário de teste A–F

### Elementos do Grafo

* **Vértices (V)**: Representam as cidades (ex.: Luziânia, Rio Quente, A, B, C).
* **Arestas (E)**: Representam as rotas/estradas entre as cidades.
* **Pesos**: Distância em quilômetros (km).

O algoritmo central, implementado na classe `Dijkstra.java`, resolve o problema de **Caminho Mais Curto de Fonte Única (SSSP)**.

---

## 2. Arquitetura da Solução (Estrutura Java)

A solução é dividida em três camadas principais:

1. Representação do grafo
2. Algoritmo de busca
3. Infraestrutura web

Todas as classes do algoritmo estão no pacote `com.example.demo`.

---

### 2.1. Representação do Grafo (`Grafo.java` e `Lista.java`)

A estrutura utiliza uma **Lista de Adjacência**, que garante eficiência.

#### **Grafo.java**

* Mantém o array de listas de adjacências: `adj[]`
* Arestas armazenadas como objetos `Grafo.Aresta`, contendo:

  * `v2`: vértice destino
  * `peso`: valor da aresta

#### **Lista.java**

* Implementação de lista encadeada genérica
* Usada para armazenar as células de adjacência (`Grafo.Celula`)

---

### 2.2. O Algoritmo de Dijkstra (`Dijkstra.java`)

Classe principal responsável pelo cálculo do SSSP.

#### **Método chave**

`obterArvoreCMC(int raiz)`

#### **Estruturas Utilizadas**

* `p[]`: guarda o menor peso conhecido da raiz até cada vértice `u`
* `antecessor[]`: armazena o vértice anterior no caminho encontrado

#### **Processo**

1. Extrai o vértice com o menor peso (`retiraMin`)
2. Para cada vizinho, aplica **relaxamento**:

   ```
   p[vizinho] > p[u] + peso(u, vizinho)
   ```
3. Atualiza pesos no heap

#### **Retorno**

* `getSequentialPath(int origem, int v)`: reconstrói o caminho a partir do array `antecessor`

---

### 2.3. Fila de Prioridade Otimizada (`FPHeapMinIndireto.java`)

Para eficiência, usa-se um **Heap Mínimo Indireto**.

* O heap armazena **IDs dos vértices** (`fp[]`), não os pesos
* Os pesos são consultados no array `p[]` em `Dijkstra.java`
* O array auxiliar `pos[]` permite atualização rápida em **O(1)**

---

## 3. Análise de Complexidade

A eficiência do algoritmo depende das operações de heap.

### Tabela de Complexidade

| Operação                         | Complexidade (Heap Binário) | Ocorrências |
| -------------------------------- | --------------------------- | ----------- |
| Construção                       | `O(V)`                      | 1           |
| Extração do mínimo (`retiraMin`) | `O(log V)`                  | `V` vezes   |
| Diminuir chave (`diminuiChave`)  | `O(log V)`                  | `E` vezes   |

### Complexidade total

[
\mathbf{O}((V \cdot \log V) + (E \cdot \log V))}
]

Que resulta em:

[
\mathbf{O((V + E) \cdot \log V)}
]

Essa é a complexidade clássica de Dijkstra com lista de adjacência + heap binário.

---

## 4. Interface Gráfica e Infraestrutura Web (Spring Boot)

### Backend – Spring Boot

O sistema expõe o algoritmo via API REST.

#### **RouteController.java**

* Recebe POST em `/api/optimize-route`
* Inicializa o grafo (modelo A–F ou 20 cidades)
* Executa o Dijkstra com origem/destino fornecidos
* Retorna JSON contendo:

  * `path`: lista ordenada de vértices
  * `cost`: custo total da rota

### Frontend – HTML & JavaScript

#### **index.html**

* Exibe seleção de origem e destino

#### **script.js**

* Busca lista de cidades em `/api/cities`
* Envia origem/destino via POST para `/api/optimize-route`
* Desenha o grafo em um `<canvas>`
* Destaca o caminho mínimo em **roxo**

---

## 5. Cenário de Teste (A → F)

Um cenário especial foi criado para validação do algoritmo.

### **Vértices**

A, B, C, D, E, F (IDs 0–5)

### **Rota esperada**

[
A \rightarrow C \rightarrow B \rightarrow D \rightarrow E \rightarrow F
]

### **Custo Total**

**12.00**

---

Se quiser, posso gerar:

📌 versão PDF
📌 versão README.md
📌 versão com diagrama do grafo
📌 versão com Mermaid (diagramas em Markdown)

É só pedir!
