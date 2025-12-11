document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('route-canvas');
    const ctx = canvas.getContext('2d');
    const origemSelect = document.getElementById('origem-select');
    const destinoSelect = document.getElementById('destino-select');
    const btnOptimize = document.getElementById('btn-optimize');
    const resultInfo = document.getElementById('result-info');

    // Layout visual (mesmo que antes)
    const CITIES_LAYOUT = {
        0: { name: "Luziânia", x: 100, y: 100 },
        1: { name: "Cristalina", x: 200, y: 50 },
        2: { name: "Vianópolis", x: 350, y: 150 },
        3: { name: "Silvânia", x: 300, y: 200 },
        4: { name: "Orizona", x: 400, y: 250 },
        5: { name: "Pires do Rio", x: 500, y: 200 },
        6: { name: "Santa Cruz", x: 550, y: 150 },
        7: { name: "Morrinhos", x: 650, y: 250 },
        8: { name: "Caldas Novas", x: 750, y: 200 },
        9: { name: "Ipameri", x: 600, y: 300 },
        10: { name: "Goiandira", x: 550, y: 350 },
        11: { name: "Urutaí", x: 450, y: 350 },
        12: { name: "Corumbaíba", x: 700, y: 350 },
        13: { name: "Marzagão", x: 750, y: 300 },
        14: { name: "Rio Quente", x: 780, y: 150 },
        15: { name: "Campo Alegre", x: 650, y: 450 },
        16: { name: "Nova Aurora", x: 600, y: 400 },
        17: { name: "Três Ranchos", x: 500, y: 400 },
        18: { name: "Catalão", x: 400, y: 400 },
        19: { name: "Palmelo", x: 550, y: 250 }
    };

    const EDGES = [
        [0,1],[1,2],[2,5],[5,7],[7,8],[5,9],[9,18],
        [5,19],[5,6],[8,14],[9,10],[10,11],[11,4],[4,3],[3,2],
        [8,13],[13,12],[18,17],[17,16],[16,15]
    ];

    let currentPathLayoutIds = []; // path em ids do layout (canvas)
    let backendCities = []; // array de {id, name} vindo do backend
    let backendIdToLayoutId = {}; // mapeamento backendId -> layoutId (ou null)

    // ---------- UTIL: nome normalizado (lowercase, sem acentos minimal) ----------
    function normalizeName(s) {
        if (!s) return s;
        return s.toString().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }

    // ---------- CARREGA CIDADES (robusto para diferentes formatos) ----------
    async function loadCities() {
        try {
            const resp = await fetch('/api/cities');
            if (!resp.ok) throw new Error(`Status ${resp.status}`);
            const data = await resp.json();
            // data pode ser: ["Luziânia","Cristalina",...] ou [{id:0,name:"Luziânia"},...]
            backendCities = [];

            if (Array.isArray(data) && data.length > 0 && typeof data[0] === 'string') {
                // simples array de nomes -> assumimos ids = índices
                data.forEach((name, idx) => backendCities.push({ id: idx, name }));
            } else if (Array.isArray(data) && typeof data[0] === 'object') {
                // array de objetos - tente encontrar id/name
                data.forEach((obj, idx) => {
                    if (obj.id !== undefined && obj.name !== undefined) {
                        backendCities.push({ id: obj.id, name: obj.name });
                    } else if (obj.name !== undefined) {
                        backendCities.push({ id: idx, name: obj.name });
                    } else {
                        // fallback
                        backendCities.push({ id: idx, name: String(obj) });
                    }
                });
            } else {
                throw new Error('Formato inesperado em /api/cities');
            }

            // Prepara selects, usando o ID real do backend
            origemSelect.innerHTML = '';
            destinoSelect.innerHTML = '';
            backendCities.forEach(c => {
                const opt1 = document.createElement('option');
                opt1.value = c.id;
                opt1.textContent = `[${c.id}] ${c.name}`;
                origemSelect.appendChild(opt1);

                const opt2 = opt1.cloneNode(true);
                destinoSelect.appendChild(opt2);
            });

            // Mapeia backend ids para layout ids procurando pelo nome
            backendIdToLayoutId = {};
            backendCities.forEach(c => {
                const norm = normalizeName(c.name);
                let foundLayoutId = null;
                for (const [layoutId, info] of Object.entries(CITIES_LAYOUT)) {
                    if (normalizeName(info.name) === norm) {
                        foundLayoutId = parseInt(layoutId);
                        break;
                    }
                }
                backendIdToLayoutId[c.id] = foundLayoutId; // pode ser null => não mapeado
            });

            console.log('backendCities:', backendCities);
            console.log('backendIdToLayoutId:', backendIdToLayoutId);

            // opcional: selecionar exemplo
            // seleciona por backend id se existir
            const caldas = backendCities.find(c => normalizeName(c.name) === normalizeName('Caldas Novas'));
            const rio = backendCities.find(c => normalizeName(c.name) === normalizeName('Rio Quente'));
            if (caldas && rio) {
                origemSelect.value = caldas.id;
                destinoSelect.value = rio.id;
            } else {
                origemSelect.selectedIndex = 0;
                destinoSelect.selectedIndex = backendCities.length-1;
            }

            drawGraph();

        } catch (err) {
            console.error('Erro ao carregar /api/cities:', err);
            resultInfo.textContent = 'Erro ao carregar cidades. Veja console.';
        }
    }

    // ---------- FUNÇÕES DE DESENHO ----------
    function drawNode(layoutId, color, isHighlighted) {
        const node = CITIES_LAYOUT[layoutId];
        if (!node) return;
        ctx.beginPath();
        ctx.arc(node.x, node.y, 10, 0, Math.PI*2);
        ctx.fillStyle = color;
        ctx.fill();
        ctx.strokeStyle = isHighlighted ? '#000' : '#666';
        ctx.lineWidth = isHighlighted ? 3 : 1;
        ctx.stroke();
        ctx.fillStyle = '#333';
        ctx.font = '10px Inter';
        ctx.textAlign = 'center';
        ctx.fillText(node.name, node.x, node.y - 15);
    }

    function drawEdge(a, b, color, highlight) {
        const A = CITIES_LAYOUT[a];
        const B = CITIES_LAYOUT[b];
        if (!A || !B) return;
        ctx.beginPath();
        ctx.moveTo(A.x, A.y);
        ctx.lineTo(B.x, B.y);
        ctx.strokeStyle = color;
        ctx.lineWidth = highlight ? 4 : 1;
        ctx.stroke();
    }

    function drawGraph() {
        ctx.clearRect(0,0,canvas.width,canvas.height);
        EDGES.forEach(([a,b]) => drawEdge(a,b,'#eee',false));
        // path
        for (let i=0;i<currentPathLayoutIds.length-1;i++){
            drawEdge(currentPathLayoutIds[i], currentPathLayoutIds[i+1], 'purple', true);
        }
        // nodes
        Object.keys(CITIES_LAYOUT).forEach(k => {
            const lid = parseInt(k);
            const isPath = currentPathLayoutIds.includes(lid);
            // determine if selected origem/dest correspond to this layout id
            const origBackendId = parseBackendIdFromSelect(origemSelect.value);
            const destBackendId = parseBackendIdFromSelect(destinoSelect.value);
            const isOrig = backendIdToLayoutId[origBackendId] === lid;
            const isDest = backendIdToLayoutId[destBackendId] === lid;
            let color = '#007bff';
            if (isOrig) color = '#28a745';
            else if (isDest) color = '#dc3545';
            else if (isPath) color = 'purple';
            drawNode(lid, color, isPath);
        });
    }

    function parseBackendIdFromSelect(val) {
        // select value may be string; keep as number or string depending on backend ids (but treat keys uniformly)
        // We'll use exactly the raw value as key for backendIdToLayoutId
        return val;
    }

    // ---------- ANIMAÇÃO ----------
    function animatePathLayout(layoutPath) {
        if (!layoutPath || layoutPath.length === 0) return;
        let step = 0;
        currentPathLayoutIds = [];
        const delay = 500;
        function next() {
            if (step < layoutPath.length) {
                currentPathLayoutIds = layoutPath.slice(0, step+1);
                drawGraph();
                step++;
                setTimeout(next, delay);
            }
        }
        next();
    }

    // ---------- BOTÃO OTIMIZAR ----------
    btnOptimize.addEventListener('click', async () => {
        resultInfo.textContent = 'Calculando...';
        currentPathLayoutIds = [];
        drawGraph();

        // pega ids reais do backend (value do select)
        const origemBackendId = origemSelect.value;
        const destinoBackendId = destinoSelect.value;

        // validar
        if (origemBackendId === destinoBackendId) {
            resultInfo.textContent = 'Origem e destino devem ser diferentes.';
            return;
        }

        // Verifica se backendId existe no mapa
        if (!backendCities.find(c => String(c.id) === String(origemBackendId)) ||
            !backendCities.find(c => String(c.id) === String(destinoBackendId))) {
            resultInfo.textContent = 'IDs enviados não existem no backend (verifique /api/cities).';
            console.warn('Origem/destino não encontrados em backendCities:', origemBackendId, destinoBackendId, backendCities);
            return;
        }

        try {
            const resp = await fetch('/api/optimize-route', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ origemId: origemBackendId, destinoId: destinoBackendId })
            });

            if (!resp.ok) {
                const text = await resp.text();
                console.error('Erro do backend:', resp.status, text);
                resultInfo.textContent = `Erro do servidor (${resp.status}). Veja console.`;
                return;
            }

            const result = await resp.json();
            console.log('Resposta optimize-route:', result);

            // result.path provavelmente é array de backendIds
            if (!result.path || !Array.isArray(result.path) || result.path.length === 0) {
                resultInfo.textContent = 'Nenhuma rota encontrada pelo backend.';
                return;
            }

            // Converte cada backendId da resposta -> layoutId via backendIdToLayoutId
            const layoutPath = [];
            const missing = [];
            result.path.forEach(bid => {
                // bid pode ser number or string; ensure key match
                const key = String(bid);
                // backendIdToLayoutId keys in this code are the raw c.id (as returned); we used them as original types
                let layout = backendIdToLayoutId[bid];
                // try string key as fallback
                if (layout === undefined) layout = backendIdToLayoutId[key];
                if (layout === null || layout === undefined) {
                    missing.push(bid);
                } else {
                    layoutPath.push(layout);
                }
            });

            if (missing.length > 0) {
                resultInfo.innerHTML = `Rota calculada, mas não foi possível desenhar ${missing.length} nó(s) porque não existem no layout visual:<br><strong>${missing.join(', ')}</strong><br>Verifique nomes em /api/cities e CITIES_LAYOUT.`;
                console.warn('IDs sem layout:', missing, 'backendCities:', backendCities);
                // ainda assim tente desenhar os nós que temos
                if (layoutPath.length > 1) animatePathLayout(layoutPath);
                return;
            }

            // sucesso: exibir custo e rota (nomes do layout)
            const cost = result.cost !== undefined ? result.cost : (result.totalWeight !== undefined ? result.totalWeight : null);
            const names = layoutPath.map(id => CITIES_LAYOUT[id].name);
            resultInfo.innerHTML = `Custo: <strong>${cost !== null ? Number(cost).toFixed(2) + ' km' : 'N/A'}</strong><br>Rota: ${names.join(' → ')}`;

            animatePathLayout(layoutPath);

        } catch (err) {
            console.error('Erro na requisição de otimização:', err);
            resultInfo.textContent = 'Erro de rede ao contatar backend.';
        }
    });

    // ---------- INICIALIZAÇÃO ----------
    loadCities();
});
