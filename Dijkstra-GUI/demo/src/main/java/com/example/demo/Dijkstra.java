package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dijkstra {
    private int antecessor[];
    private double p[];
    private Grafo grafo;

    public Dijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    public void obterArvoreCMC(int raiz) throws Exception {
        int n = this.grafo.numVertices();
        this.p = new double[n]; // peso dos vértices
        int vs[] = new int[n+1]; // vértices (para o heap, usa índice 1 a n)
        this.antecessor = new int[n];

        for (int u = 0; u < n; u++) {
            this.antecessor[u] = -1;
            p[u] = Double.MAX_VALUE; // Inicializa com infinito
            vs[u + 1] = u;
        }

        p[raiz] = 0; // O peso da raiz é zero

        FPHeapMinIndireto heap = new FPHeapMinIndireto(p, vs);
        heap.constroi();

        while (!heap.vazio()) {
            int u = heap.retiraMin(); // Retira o vértice com menor peso p[u]

            if (!this.grafo.listaAdjVazia(u)) {
                Grafo.Aresta adj = grafo.primeiroListaAdj(u);

                while (adj != null) {
                    int v = adj.v2();
                    // Cálculo do novo peso: p[u] + peso da aresta (u, v)
                    double novoPeso = this.p[u] + adj.peso(); 

                    // Passo de Relaxamento
                    if (this.p[v] > novoPeso) {
                        antecessor[v] = u;

                        // ATUALIZAÇÃO CORRETA DO PESO E DO HEAP
                        this.p[v] = novoPeso;
                        // O método diminuiChave no heap usa o novo peso para reordenar
                        heap.diminuiChave(v, novoPeso); 
                    }

                    adj = grafo.proxAdj(u);
                }
            }
        }
    }

    public int antecessor(int u) {
        return this.antecessor[u];
    }

    public double peso(int u) {
        return this.p[u];
    }

    public void imprimeCaminho(int origem, int v) {
        if (origem == v)
            System.out.println(origem);
        else if (this.antecessor[v] == -1)
            System.out.println("Nao existe caminho de " + origem + " ate " + v);
        else {
            imprimeCaminho(origem, this.antecessor[v]);
            System.out.println(v);
        }
    }

    // MÉTODO ESSENCIAL PARA A WEB/CONTROLLER
    public List<Integer> getSequentialPath(int origem, int v) {
        List<Integer> path = new ArrayList<>();
        int atual = v;

        // Se o destino for inalcançável (peso continua infinito)
        if (this.p[v] == Double.MAX_VALUE) {
            return new ArrayList<>();
        }

        // Reconstrói o caminho de trás para frente (destino para origem)
        while (atual != -1) {
            path.add(atual);
            if (atual == origem) break;
            atual = this.antecessor[atual];
        }

        // Inverte a lista para obter a ordem correta (origem -> destino)
        Collections.reverse(path);
        return path;
    }
}