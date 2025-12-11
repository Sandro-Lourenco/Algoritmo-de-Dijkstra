package com.example.demo;

public class Grafo {
    public static class Aresta {
        private int v1, v2, peso;

        public Aresta(int v1, int v2, int peso) {
            this.v1 = v1;
            this.v2 = v2;
            this.peso = peso;
        }

        public int peso() {
            return this.peso;
        }

        public int v1() {
            return this.v1;
        }

        public int v2() {
            return this.v2;
        }
    }

    private static class Celula {
        int vertice, peso;

        public Celula(int v, int p) {
            this.vertice = v;
            this.peso = p;
        }

        public boolean equals(Object obj) {
            Celula item = (Celula) obj;
            return (this.vertice == item.vertice);
        }
    }

    private Lista adj[];
    private int numVertices;

    public Grafo (int numVertices) {
        this.adj = new Lista[numVertices]; this.numVertices = numVertices;
        for (int i = 0; i < this.numVertices; i++) this.adj[i] = new Lista();
    }
    public void insereAresta (int v1, int v2, int peso) {
        Celula item = new Celula (v2, peso);
        this.adj[v1].insere (item);
    }
    public boolean existeAresta (int v1, int v2) {
        Celula item = new Celula (v2, 0);
        return (this.adj[v1].pesquisa (item) != null);
    }
    public boolean listaAdjVazia (int v) {
        return this.adj[v].vazia ();
    }
    public Aresta primeiroListaAdj (int v) {
        // Retorna a primeira aresta que o vértice v participa ou
        // null se a lista de adjacência de v for vazia
        Celula item = (Celula) this.adj[v].primeiro ();
        return item != null ? new Aresta (v, item.vertice, item.peso): null;
    }
    public Aresta proxAdj (int v) {
        // Retorna a próxima aresta que o vértice v participa ou
        // null se a lista de adjacência de v estiver no fim
        Celula item = (Celula) this.adj[v].proximo ();
        return item != null ? new Aresta (v, item.vertice, item.peso): null;
    }
    public Aresta retiraAresta (int v1, int v2) throws Exception {
        Celula chave = new Celula (v2, 0);
        Celula item = (Celula) this.adj[v1].retira (chave);
        return item != null ? new Aresta (v1, v2, item.peso): null;
    }
    public void imprime () {
        for (int i = 0; i < this.numVertices; i++) {
            System.out.println ("Vertice " + i + ":");
            Celula item = (Celula) this.adj[i].primeiro ();
            while (item != null) {
                System.out.println ("  " + item.vertice + " (" +item.peso+ ")");
                item = (Celula) this.adj[i].proximo ();
            }
        }
    }
    public int numVertices () {
        return this.numVertices;
    }
}
