import java.util.ArrayList;
import java.util.List;

public class Navio {
    private final String nome;
    private final int tamanho;
    private final List<int[]> posicoes = new ArrayList<>();
    private final List<Boolean> atingido = new ArrayList<>();

    public Navio(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public void adicionarPosicao(int linha, int coluna) {
        posicoes.add(new int[]{linha, coluna});
        atingido.add(false);
    }

    public boolean contemPosicao(int linha, int coluna) {
        for (int[] p : posicoes) {
            if (p[0] == linha && p[1] == coluna) return true;
        }
        return false;
    }

    public void atingir(int linha, int coluna) {
        for (int i = 0; i < posicoes.size(); i++) {
            int[] p = posicoes.get(i);
            if (p[0] == linha && p[1] == coluna) {
                atingido.set(i, true);
                return;
            }
        }
    }

    public boolean afundado() {
        for (boolean b : atingido) {
            if (!b) return false;
        }
        return true;
    }

    public String getNome() { return nome; }
    public int getTamanho() { return tamanho; }
    public List<int[]> getPosicoes() { return posicoes; }
}
