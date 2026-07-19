import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tabuleiro {
    public static final int TAMANHO = 10;

    /** Definicao padrao da frota: nome + tamanho de cada navio. */
    public static final String[] FROTA_NOMES = {
            "Porta-aviões", "Navio de Guerra", "Cruzador", "Submarino", "Destroidor"
    };
    public static final int[] FROTA_TAMANHOS = {5, 4, 3, 3, 2};

    public enum Estado { AGUA, NAVIO, ACERTO, ERRO }

    private final Estado[][] grade = new Estado[TAMANHO][TAMANHO];
    private final List<Navio> navios = new ArrayList<>();
    private final Random random = new Random();

    public Tabuleiro() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                grade[i][j] = Estado.AGUA;
            }
        }
    }

    // ---------- Posicionamento aleatorio (usado pelo computador) ----------

    public void posicionarFrotaAleatoria() {
        for (int i = 0; i < FROTA_TAMANHOS.length; i++) {
            posicionarNavioAleatorio(FROTA_NOMES[i], FROTA_TAMANHOS[i]);
        }
    }

    private void posicionarNavioAleatorio(String nome, int tamanho) {
        boolean posicionado = false;
        while (!posicionado) {
            boolean horizontal = random.nextBoolean();
            int linha = random.nextInt(TAMANHO);
            int coluna = random.nextInt(TAMANHO);
            if (podePosicionar(linha, coluna, tamanho, horizontal)) {
                posicionarNavio(nome, tamanho, linha, coluna, horizontal);
                posicionado = true;
            }
        }
    }

    // ---------- Posicionamento manual (usado pelo jogador) ----------

    /** Verifica se um navio cabe e nao encosta em outro, sem alterar o tabuleiro. */
    public boolean podePosicionar(int linha, int coluna, int tamanho, boolean horizontal) {
        for (int k = 0; k < tamanho; k++) {
            int l = horizontal ? linha : linha + k;
            int c = horizontal ? coluna + k : coluna;
            if (l < 0 || l >= TAMANHO || c < 0 || c >= TAMANHO) return false;
            if (haVizinhoOcupado(l, c)) return false;
        }
        return true;
    }

    /** Posiciona efetivamente um navio (assume que podePosicionar ja foi checado). */
    public Navio posicionarNavio(String nome, int tamanho, int linha, int coluna, boolean horizontal) {
        Navio navio = new Navio(nome, tamanho);
        for (int k = 0; k < tamanho; k++) {
            int l = horizontal ? linha : linha + k;
            int c = horizontal ? coluna + k : coluna;
            grade[l][c] = Estado.NAVIO;
            navio.adicionarPosicao(l, c);
        }
        navios.add(navio);
        return navio;
    }

    /** Remove todos os navios e limpa o tabuleiro (para reiniciar o posicionamento). */
    public void limpar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                grade[i][j] = Estado.AGUA;
            }
        }
        navios.clear();
    }

    private boolean haVizinhoOcupado(int linha, int coluna) {
        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                int l = linha + dl;
                int c = coluna + dc;
                if (l >= 0 && l < TAMANHO && c >= 0 && c < TAMANHO) {
                    if (grade[l][c] == Estado.NAVIO) return true;
                }
            }
        }
        return false;
    }

    // ---------- Ataques ----------

    /** Retorna true se acertou algum navio. */
    public boolean atacar(int linha, int coluna) {
        if (grade[linha][coluna] == Estado.NAVIO) {
            grade[linha][coluna] = Estado.ACERTO;
            for (Navio n : navios) {
                if (n.contemPosicao(linha, coluna)) {
                    n.atingir(linha, coluna);
                    break;
                }
            }
            return true;
        } else if (grade[linha][coluna] == Estado.AGUA) {
            grade[linha][coluna] = Estado.ERRO;
        }
        return false;
    }

    public boolean jaAtacado(int linha, int coluna) {
        return grade[linha][coluna] == Estado.ACERTO || grade[linha][coluna] == Estado.ERRO;
    }

    public Navio navioAfundadoEm(int linha, int coluna) {
        for (Navio n : navios) {
            if (n.contemPosicao(linha, coluna) && n.afundado()) return n;
        }
        return null;
    }

    public boolean todosNaviosAfundados() {
        for (Navio n : navios) {
            if (!n.afundado()) return false;
        }
        return true;
    }

    public Estado getEstado(int linha, int coluna) {
        return grade[linha][coluna];
    }

    public List<Navio> getNavios() {
        return navios;
    }

    public int getQuantidadeNaviosFrota() {
        return FROTA_TAMANHOS.length;
    }
}