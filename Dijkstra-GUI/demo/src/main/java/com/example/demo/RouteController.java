package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {

    // Lista fixa (20 cidades)
    private static final String[] CIDADES = {
        "Luziânia", "Cristalina", "Vianópolis", "Silvânia", "Orizona",
        "Pires do Rio", "Santa Cruz", "Morrinhos", "Caldas Novas", "Ipameri",
        "Goiandira", "Urutaí", "Corumbaíba", "Marzagão", "Rio Quente",
        "Campo Alegre", "Nova Aurora", "Três Ranchos", "Catalão", "Palmelo"
    };

    private static final int NUM_VERTICES = CIDADES.length;

    // ========= MODELOS ==========
    static class RouteRequest {
        public int origemId;
        public int destinoId;
    }

    static class PathResponse {
        public boolean found;
        public List<Integer> path;
        public double cost;

        public PathResponse(boolean found, List<Integer> path, double cost) {
            this.found = found;
            this.path = path;
            this.cost = cost;
        }
    }
    // =============================


    // =======================================
    // 🔧 MÉTODO DE CRIAÇÃO E CONFIGURAÇÃO DO GRAFO
    // =======================================
    private Grafo initializeGraph() {

        Grafo grafo = new Grafo(NUM_VERTICES);

        // Mapa nome -> índice
        Map<String, Integer> id = new HashMap<>();
        for (int i = 0; i < NUM_VERTICES; i++) {
            id.put(CIDADES[i], i);
        }

        Consumer<String[]> edge = data -> {
            int v1 = id.get(data[0]);
            int v2 = id.get(data[1]);
            int peso = Integer.parseInt(data[2]);
            grafo.insereAresta(v1, v2, peso);
            grafo.insereAresta(v2, v1, peso);
        };

        // ===============================
        // GRAFO 100% REVISADO E CORRIGIDO
        // ===============================

        edge.accept(new String[]{"Luziânia", "Cristalina", "60"});
        edge.accept(new String[]{"Cristalina", "Vianópolis", "180"});
        edge.accept(new String[]{"Vianópolis", "Silvânia", "40"});
        edge.accept(new String[]{"Silvânia", "Orizona", "50"});
        edge.accept(new String[]{"Orizona", "Urutaí", "150"});
        edge.accept(new String[]{"Urutaí", "Goiandira", "90"});
        edge.accept(new String[]{"Goiandira", "Ipameri", "30"});
        edge.accept(new String[]{"Ipameri", "Pires do Rio", "110"});
        edge.accept(new String[]{"Ipameri", "Catalão", "70"});
        edge.accept(new String[]{"Catalão", "Três Ranchos", "40"});
        edge.accept(new String[]{"Três Ranchos", "Nova Aurora", "60"});
        edge.accept(new String[]{"Nova Aurora", "Campo Alegre", "50"});
        edge.accept(new String[]{"Pires do Rio", "Palmelo", "20"});
        edge.accept(new String[]{"Pires do Rio", "Santa Cruz", "30"});
        edge.accept(new String[]{"Pires do Rio", "Morrinhos", "100"});
        edge.accept(new String[]{"Morrinhos", "Caldas Novas", "60"});
        edge.accept(new String[]{"Caldas Novas", "Rio Quente", "30"});
        edge.accept(new String[]{"Caldas Novas", "Marzagão", "70"});
        edge.accept(new String[]{"Marzagão", "Corumbaíba", "50"});

        return grafo;
    }


    // =======================================
    // 📌 ENDPOINT PRINCIPAL: Encontrar Melhor Rota
    // =======================================
    @PostMapping("/api/optimize-route")
    public PathResponse optimizeRoute(@RequestBody RouteRequest req) {

        int origem = req.origemId;
        int destino = req.destinoId;

        // Validação
        if (origem < 0 || origem >= NUM_VERTICES ||
            destino < 0 || destino >= NUM_VERTICES) {

            return new PathResponse(false, new ArrayList<>(), 0);
        }

        try {
            Grafo grafo = initializeGraph();
            Dijkstra dj = new Dijkstra(grafo);
            dj.obterArvoreCMC(origem);

            List<Integer> path = dj.getSequentialPath(origem, destino);
            double custo = dj.peso(destino);

            boolean found = !path.isEmpty() && custo != Double.MAX_VALUE;

            if (!found) {
                System.out.println("Nenhuma rota encontrada entre "
                    + CIDADES[origem] + " e " + CIDADES[destino]);
            } else {
                System.out.println("Rota encontrada!");
                System.out.println("Origem: " + CIDADES[origem]);
                System.out.println("Destino: " + CIDADES[destino]);
                System.out.println("Custo total: " + custo);
                System.out.println("Caminho (IDs): " + path);
            }

            return new PathResponse(found, path, custo);

        } catch (Exception e) {
            System.err.println("\n=== ERRO GRAVE NO DIJKSTRA ===");
            e.printStackTrace();
            return new PathResponse(false, new ArrayList<>(), 0);
        }
    }


    // =======================================
    // 📌 LISTA DE CIDADES PARA O FRONT-END
    // =======================================
    @GetMapping("/api/cities")
    public String[] getCities() {
        return CIDADES;
    }
}
