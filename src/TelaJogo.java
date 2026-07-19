import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class TelaJogo extends JFrame {

    private final Tabuleiro tabuleiroJogador;
    private final Tabuleiro tabuleiroComputador = new Tabuleiro();
    private TabuleiroPainel painelJogador;
    private TabuleiroPainel painelComputador;
    private final Random random = new Random();

    private JLabel rotuloStatus;
    private boolean turnoJogador = true;
    private boolean jogoTerminado = false;

    /** Inicia a partida com um tabuleiro de jogador ja posicionado manualmente. */
    public TelaJogo(Tabuleiro tabuleiroJogador) {
        this.tabuleiroJogador = tabuleiroJogador;
        tabuleiroComputador.posicionarFrotaAleatoria();
        montarInterface();
    }

    /** Construtor de conveniencia: posiciona a frota do jogador aleatoriamente tambem. */
    public TelaJogo() {
        this.tabuleiroJogador = new Tabuleiro();
        this.tabuleiroJogador.posicionarFrotaAleatoria();
        tabuleiroComputador.posicionarFrotaAleatoria();
        montarInterface();
    }

    private void montarInterface() {
        setTitle("Batalha Naval");
        setSize(1220, 820);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PainelFundo fundo = new PainelFundo();
        fundo.setLayout(new BorderLayout());
        setContentPane(fundo);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);
        painelTopo.setBorder(new EmptyBorder(15, 20, 5, 20));

        JLabel titulo = new JLabel("BATALHA NAVAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);

        rotuloStatus = new JLabel("Sua vez: escolha uma casa no tabuleiro inimigo", SwingConstants.CENTER);
        rotuloStatus.setFont(new Font("Arial", Font.BOLD, 16));
        rotuloStatus.setForeground(Color.YELLOW);

        painelTopo.add(titulo, BorderLayout.NORTH);
        painelTopo.add(rotuloStatus, BorderLayout.SOUTH);

        painelJogador = new TabuleiroPainel(tabuleiroJogador, true);
        painelComputador = new TabuleiroPainel(tabuleiroComputador, false);
        painelComputador.setListenerClique(this::jogadorAtaca);

        JPanel blocoJogador = criarBloco("SEU TABULEIRO", painelJogador);
        JPanel blocoComputador = criarBloco("TABULEIRO INIMIGO", painelComputador);

        JPanel painelTabuleiros = new JPanel(new GridLayout(1, 2, 30, 0));
        painelTabuleiros.setOpaque(false);
        painelTabuleiros.setBorder(new EmptyBorder(10, 30, 30, 30));
        painelTabuleiros.add(blocoJogador);
        painelTabuleiros.add(blocoComputador);

        JButton botaoVoltar = new BotaoMetalico("MENU");
        botaoVoltar.setPreferredSize(new Dimension(140, 45));
        botaoVoltar.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
        });
        JPanel painelInferior = new JPanel();
        painelInferior.setOpaque(false);
        painelInferior.add(botaoVoltar);

        fundo.add(painelTopo, BorderLayout.NORTH);
        fundo.add(painelTabuleiros, BorderLayout.CENTER);
        fundo.add(painelInferior, BorderLayout.SOUTH);
    }

    private JPanel criarBloco(String rotulo, TabuleiroPainel painel) {
        JPanel bloco = new JPanel(new BorderLayout(0, 8));
        bloco.setOpaque(false);

        JLabel titulo = new JLabel(rotulo, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(painel);

        bloco.add(titulo, BorderLayout.NORTH);
        bloco.add(container, BorderLayout.CENTER);
        return bloco;
    }

    private void jogadorAtaca(int linha, int coluna) {
        if (jogoTerminado || !turnoJogador) return;
        if (tabuleiroComputador.jaAtacado(linha, coluna)) return;

        boolean acertou = tabuleiroComputador.atacar(linha, coluna);
        painelComputador.repaint();

        if (acertou) {
            Navio afundado = tabuleiroComputador.navioAfundadoEm(linha, coluna);
            if (afundado != null) {
                rotuloStatus.setText("Você afundou o " + afundado.getNome() + " inimigo!");
            } else {
                rotuloStatus.setText("Acertou! Jogue novamente.");
            }
            if (tabuleiroComputador.todosNaviosAfundados()) {
                fimDeJogo(true);
            }
        } else {
            rotuloStatus.setText("Água! Vez do computador...");
            turnoJogador = false;
            Timer timer = new Timer(700, e -> turnoComputador());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void turnoComputador() {
        if (jogoTerminado) return;
        int linha, coluna;
        do {
            linha = random.nextInt(Tabuleiro.TAMANHO);
            coluna = random.nextInt(Tabuleiro.TAMANHO);
        } while (tabuleiroJogador.jaAtacado(linha, coluna));

        boolean acertou = tabuleiroJogador.atacar(linha, coluna);
        painelJogador.repaint();

        if (acertou) {
            Navio afundado = tabuleiroJogador.navioAfundadoEm(linha, coluna);
            if (afundado != null) {
                rotuloStatus.setText("O computador afundou seu " + afundado.getNome() + "!");
            } else {
                rotuloStatus.setText("O computador acertou seu navio! Jogando novamente...");
            }
            if (tabuleiroJogador.todosNaviosAfundados()) {
                fimDeJogo(false);
                return;
            }
            Timer timer = new Timer(700, e -> turnoComputador());
            timer.setRepeats(false);
            timer.start();
        } else {
            rotuloStatus.setText("O computador errou. Sua vez!");
            turnoJogador = true;
        }
    }

    private void fimDeJogo(boolean venceuJogador) {
        jogoTerminado = true;
        String mensagem = venceuJogador
                ? "Parabens! Voce afundou toda a frota inimiga!"
                : "Fim de jogo! O computador afundou toda a sua frota.";
        rotuloStatus.setText(mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Batalha Naval",
                venceuJogador ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}