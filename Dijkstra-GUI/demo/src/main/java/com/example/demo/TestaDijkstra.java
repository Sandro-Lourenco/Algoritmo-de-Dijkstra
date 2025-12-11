// Localização: Spring/demo/src/main/java/com/example/demo/TestaDijkstra.java
package com.example.demo;

import java.util.List;

public class TestaDijkstra {

    // Mapeamento: A=0, B=1, C=2, D=3, E=4, F=5
    private static final String[] VERTICES = {"A", "B", "C", "D", "E", "F"};
    private static final int ORIGEM = 0;  // A
    private static final int DESTINO = 5; // F

    public static void main(String[] args) {

        // 1. Criar grafo com 6 vértices
        Grafo grafo = new Grafo(6);

        try {
            System.out.println("--- Inicializando Grafo da Imagem (A-F) ---");

            // ============================================================
            //  🚀 ARESTAS EXATAS DA IMAGEM ENVIADA (GRAFO NÃO DIRIGIDO)
            //  Inserimos cada aresta em ambos os sentidos (v1->v2 e v2->v1)
            // ============================================================

            // A ↔ C (2)
            grafo.insereAresta(0, 2, 2);
            grafo.insereAresta(2, 0, 2);

            // A ↔ B (4)
            grafo.insereAresta(0, 1, 4);
            grafo.insereAresta(1, 0, 4);

            // C ↔ B (1)
            grafo.insereAresta(2, 1, 1);
            grafo.insereAresta(1, 2, 1);

            // C ↔ E (10)
            grafo.insereAresta(2, 4, 10);
            grafo.insereAresta(4, 2, 10);

            // C ↔ D (8)
            grafo.insereAresta(2, 3, 8);
            grafo.insereAresta(3, 2, 8);

            // B ↔ D (5)
            grafo.insereAresta(1, 3, 5);
            grafo.insereAresta(3, 1, 5);

            // D ↔ E (2)
            grafo.insereAresta(3, 4, 2);
            grafo.insereAresta(4, 3, 2);

            // E ↔ F (2)
            grafo.insereAresta(4, 5, 2);
            grafo.insereAresta(5, 4, 2);

            // F ↔ D (6)
            grafo.insereAresta(5, 3, 6);
            grafo.insereAresta(3, 5, 6);

            System.out.println("Grafo carregado com sucesso (não dirigido).");
            System.out.println("Arestas carregadas exatamente como a imagem enviada.");
            System.out.println("----------------------------------------------------------");

            // 2. Executar o Dijkstra
            // ✅ **IMPORTANTE**: Certifique-se de ter aplicado a correção no
            // Dijkstra.obterArvoreCMC para atualizar this.p[v] ao diminuir chave,
            // conforme discutido antes.
            Dijkstra dijkstra = new Dijkstra(grafo);
            dijkstra.obterArvoreCMC(ORIGEM);

            // 3. Obter custo e caminho
            double custo = dijkstra.peso(DESTINO);
            List<Integer> path = dijkstra.getSequentialPath(ORIGEM, DESTINO);

            System.out.println("\n--- Resultado do Dijkstra (A → F) ---");

            if (path.isEmpty() || custo == Double.MAX_VALUE) {
                System.out.println("Nao existe caminho entre A e F.");
            } else {
                StringBuilder rota = new StringBuilder();
                for (int i = 0; i < path.size(); i++) {
                    rota.append(VERTICES[path.get(i)]);
                    if (i < path.size() - 1) rota.append(" -> ");
                }

                System.out.println("Caminho mínimo encontrado: " + rota.toString());
                System.out.println("Custo total: " + (int)custo);

                System.out.println("\nCaminho ótimo esperado (confirmado):");
                System.out.println("A -> C -> B -> D -> E -> F (custo total = 12)");
            }

            System.out.println("----------------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Erro ao processar o grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
