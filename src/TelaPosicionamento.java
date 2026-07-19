import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaPosicionamento extends JFrame {

    private final Tabuleiro tabuleiroJogador = new Tabuleiro();
    private TabuleiroPainel painelTabuleiro;
    private JLabel rotuloInstrucao;
    private JLabel rotuloOrientacao;
    private JButton botaoIniciar;
    private JButton botaoRotacionar;

    private int indiceNavioAtual = 0;
    private boolean horizontal = true;

    public TelaPosicionamento() {
        setTitle("Batalha Naval - Posicione sua frota");
        setSize(1000, 780);
        setMinimumSize(new Dimension(760, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PainelFundo fundo = new PainelFundo();
        fundo.setLayout(new BorderLayout());
        setContentPane(fundo);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBorder(new EmptyBorder(18, 20, 8, 25));

        JLabel titulo = new JLabel("POSICIONE SUA FROTA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        rotuloInstrucao = new JLabel("", SwingConstants.CENTER);
        rotuloInstrucao.setFont(new Font("Arial", Font.BOLD, 20));
        rotuloInstrucao.setForeground(Color.YELLOW);
        rotuloInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotuloInstrucao.setBorder(new EmptyBorder(6, 0, 0, 0));

        rotuloOrientacao = new JLabel("", SwingConstants.CENTER);
        rotuloOrientacao.setFont(new Font("Arial", Font.PLAIN, 17));
        rotuloOrientacao.setForeground(Color.WHITE);
        rotuloOrientacao.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotuloOrientacao.setBorder(new EmptyBorder(2, 0, 0, 0));

        topo.add(titulo);
        topo.add(rotuloInstrucao);
        topo.add(rotuloOrientacao);

        painelTabuleiro = new TabuleiroPainel(tabuleiroJogador, true);
        painelTabuleiro.setListenerClique(this::tentarPosicionar);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(10, 30, 10, 30));
        centro.add(painelTabuleiro);

        botaoRotacionar = new BotaoMetalico("GIRAR (H/V)");
        botaoRotacionar.setPreferredSize(new Dimension(190, 55));
        botaoRotacionar.addActionListener(e -> {
            horizontal = !horizontal;
            atualizarPreviewEstado();
        });

        JButton botaoAleatorio = new BotaoMetalico("ALEATÓRIO");
        botaoAleatorio.setPreferredSize(new Dimension(190, 55));
        botaoAleatorio.addActionListener(e -> posicionarRestanteAleatorio());

        JButton botaoReiniciar = new BotaoMetalico("REINICIAR");
        botaoReiniciar.setPreferredSize(new Dimension(190, 55));
        botaoReiniciar.addActionListener(e -> reiniciarPosicionamento());

        botaoIniciar = new BotaoMetalico("INICIAR BATALHA");
        botaoIniciar.setPreferredSize(new Dimension(220, 55));
        botaoIniciar.setEnabled(false);
        botaoIniciar.addActionListener(e -> iniciarBatalha());

        JButton botaoMenu = new BotaoMetalico("MENU");
        botaoMenu.setPreferredSize(new Dimension(140, 55));
        botaoMenu.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
        });

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        inferior.setOpaque(false);
        inferior.add(botaoRotacionar);
        inferior.add(botaoAleatorio);
        inferior.add(botaoReiniciar);
        inferior.add(botaoIniciar);
        inferior.add(botaoMenu);

        fundo.add(topo, BorderLayout.NORTH);
        fundo.add(centro, BorderLayout.CENTER);
        fundo.add(inferior, BorderLayout.SOUTH);

        atualizarPreviewEstado();
    }

    private void atualizarPreviewEstado() {
        if (indiceNavioAtual >= Tabuleiro.FROTA_NOMES.length) {
            painelTabuleiro.desativarPreview();
            rotuloInstrucao.setText("Frota completa! Clique em INICIAR BATALHA.");
            rotuloOrientacao.setText("");
            botaoIniciar.setEnabled(true);
            botaoRotacionar.setEnabled(false);
            return;
        }
        String nome = Tabuleiro.FROTA_NOMES[indiceNavioAtual];
        int tamanho = Tabuleiro.FROTA_TAMANHOS[indiceNavioAtual];
        rotuloInstrucao.setText("Posicione: " + nome + " (" + tamanho + " casas)");
        rotuloOrientacao.setText("Orientação atual: " + (horizontal ? "HORIZONTAL" : "VERTICAL")
                + "  -  clique no tabuleiro para posicionar");
        painelTabuleiro.ativarPreview(tamanho, horizontal);
    }

    private void tentarPosicionar(int linha, int coluna) {
        if (indiceNavioAtual >= Tabuleiro.FROTA_NOMES.length) return;

        int tamanho = Tabuleiro.FROTA_TAMANHOS[indiceNavioAtual];
        String nome = Tabuleiro.FROTA_NOMES[indiceNavioAtual];

        if (!tabuleiroJogador.podePosicionar(linha, coluna, tamanho, horizontal)) {
            rotuloOrientacao.setText("Posição invalida (fora do tabuleiro ou encostando em outro navio).");
            return;
        }

        tabuleiroJogador.posicionarNavio(nome, tamanho, linha, coluna, horizontal);
        indiceNavioAtual++;
        painelTabuleiro.repaint();
        atualizarPreviewEstado();
    }

    private void posicionarRestanteAleatorio() {
        java.util.Random random = new java.util.Random();
        while (indiceNavioAtual < Tabuleiro.FROTA_NOMES.length) {
            int tamanho = Tabuleiro.FROTA_TAMANHOS[indiceNavioAtual];
            String nome = Tabuleiro.FROTA_NOMES[indiceNavioAtual];
            boolean posicionado = false;
            while (!posicionado) {
                boolean h = random.nextBoolean();
                int l = random.nextInt(Tabuleiro.TAMANHO);
                int c = random.nextInt(Tabuleiro.TAMANHO);
                if (tabuleiroJogador.podePosicionar(l, c, tamanho, h)) {
                    tabuleiroJogador.posicionarNavio(nome, tamanho, l, c, h);
                    posicionado = true;
                }
            }
            indiceNavioAtual++;
        }
        painelTabuleiro.repaint();
        atualizarPreviewEstado();
    }

    private void reiniciarPosicionamento() {
        tabuleiroJogador.limpar();
        indiceNavioAtual = 0;
        botaoRotacionar.setEnabled(true);
        botaoIniciar.setEnabled(false);
        painelTabuleiro.repaint();
        atualizarPreviewEstado();
    }

    private void iniciarBatalha() {
        dispose();
        SwingUtilities.invokeLater(() -> new TelaJogo(tabuleiroJogador).setVisible(true));
    }
}
