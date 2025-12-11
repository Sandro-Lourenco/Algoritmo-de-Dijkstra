package com.example.demo;

public class FPHeapMinIndireto {

    private double p[];
    private int n;
    private int pos[];
    private int fp[];

    /*
       p = vetor de pesos (distâncias)
       v = vetor com os vértices dentro do heap: fp[1..n]
    */
    public FPHeapMinIndireto(double p[], int v[]) {
        this.p = p;
        this.fp = v;
        this.n = this.fp.length - 1;

        // pos deve comportar os vértices (0..n-1)
        this.pos = new int[this.n];

        // Mapeia fp[i] -> posição i
        for (int i = 1; i <= this.n; i++) {
            this.pos[this.fp[i]] = i;
        }
    }

    public void refaz(int esq, int dir) {
        int j = esq * 2;
        int x = this.fp[esq];

        while (j <= dir) {
            if (j < dir && p[fp[j]] > p[fp[j + 1]])
                j++;

            if (p[x] <= p[fp[j]])
                break;

            this.fp[esq] = this.fp[j];
            this.pos[this.fp[j]] = esq;

            esq = j;
            j = esq * 2;
        }

        this.fp[esq] = x;
        this.pos[x] = esq;
    }

    public void constroi() {
        int esq = n / 2;
        while (esq >= 1) {
            refaz(esq, n);
            esq--;
        }
    }

    public int retiraMin() throws Exception {
        if (this.n < 1)
            throw new Exception("Heap vazio");

        int minimo = this.fp[1];

        this.fp[1] = this.fp[this.n];
        this.pos[this.fp[this.n]] = 1;

        this.n--;
        refaz(1, this.n);

        return minimo;
    }

    public void diminuiChave(int vertice, double novoValor) {
        int i = this.pos[vertice];
        this.p[vertice] = novoValor;

        int pai = i / 2;
        while (i > 1 && p[fp[pai]] > p[fp[i]]) {
            int aux = fp[pai];
            fp[pai] = fp[i];
            pos[fp[i]] = pai;

            fp[i] = aux;
            pos[aux] = i;

            i = pai;
            pai = i / 2;
        }
    }

    public boolean vazio() {
        return this.n == 0;
    }
}
