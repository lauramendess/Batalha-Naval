import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class TelaJogo extends JFrame {

    private final Tabuleiro tabuleiroJogador;
    private final Tabuleiro tabuleiroComputador = new Tabuleiro();
    private TabuleiroPainel painelJogador;
    private TabuleiroPainel painelComputador;
    private final Random random = new Random();

    private Point primeiroAcerto = null;
    private Point ultimoAcerto = null;

    private int dirLinha = 0;
    private int dirColuna = 0;

    private boolean direcaoDefinida = false;
    private boolean invertendoDirecao = false;

    private JLabel rotuloStatus;
    private boolean turnoJogador = true;
    private boolean jogoTerminado = false;

    private static final String CAMINHO_ICONE_MENU = "lib/Menu.png";
    private static final String CAMINHO_FUNDO_TEXTO = "lib/FundoTexto.png";

    // Cache estático compartilhado de imagens
    private static Image imgFundoTextoOriginal;
    private static ImageIcon iconeBotaoMenu;

    static {
        // Carrega os recursos uma única vez na inicialização da classe
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        imgFundoTextoOriginal = toolkit.getImage(CAMINHO_FUNDO_TEXTO);

        Image imgMenu = toolkit.getImage(CAMINHO_ICONE_MENU);
        iconeBotaoMenu = new ImageIcon(imgMenu.getScaledInstance(45, 45, Image.SCALE_FAST));
    }

    /** Inicia a partida com um tabuleiro de jogador já posicionado manualmente. */
    public TelaJogo(Tabuleiro tabuleiroJogador) {
        this.tabuleiroJogador = tabuleiroJogador;
        tabuleiroComputador.posicionarFrotaAleatoria();
        montarInterface();
    }

    /**
     * Construtor de conveniência: posiciona a frota do jogador aleatoriamente
     * também.
     */
    public TelaJogo() {
        this.tabuleiroJogador = new Tabuleiro();
        this.tabuleiroJogador.posicionarFrotaAleatoria();
        tabuleiroComputador.posicionarFrotaAleatoria();
        montarInterface();
    }

    // --- Subclasse da Placa do Título (Ajustada para renderização dinâmica rápida)
    // ---
    static class PainelTituloPlaca extends JPanel {

        public PainelTituloPlaca(String texto) {
            setOpaque(false);
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(260, 55));

            JLabel label = new JLabel(texto, SwingConstants.CENTER);
            label.setFont(new Font("Arial Black", Font.BOLD, 15));
            label.setForeground(Color.WHITE);
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imgFundoTextoOriginal != null) {
                int imgWidth = imgFundoTextoOriginal.getWidth(this);
                int imgHeight = imgFundoTextoOriginal.getHeight(this);

                if (imgWidth > 0 && imgHeight > 0) {
                    // Desenha diretamente ajustando ao tamanho preferido do painel sem travar
                    int larg = 260;
                    int alt = 50;
                    int x = (getWidth() - larg) / 2;
                    int y = (getHeight() - alt) / 2;

                    g.drawImage(imgFundoTextoOriginal, x, y, larg, alt, this);
                }
            }
        }
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

        // --- PAINEL DO TOPO ---
        JPanel painelTopo = new JPanel(new GridBagLayout());
        painelTopo.setOpaque(false);
        painelTopo.setBorder(new EmptyBorder(15, 20, 5, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel painelTextosTopo = new JPanel();
        painelTextosTopo.setOpaque(false);
        painelTextosTopo.setLayout(new BoxLayout(painelTextosTopo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("BATALHA NAVAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        rotuloStatus = new JLabel("Sua vez: escolha uma casa no tabuleiro inimigo", SwingConstants.CENTER);
        rotuloStatus.setFont(new Font("Arial", Font.BOLD, 16));
        rotuloStatus.setForeground(Color.YELLOW);
        rotuloStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTextosTopo.add(titulo);
        painelTextosTopo.add(Box.createVerticalStrut(5));
        painelTextosTopo.add(rotuloStatus);

        // --- BOTÃO DE ÍCONE DO MENU ---
        JButton botaoMenu = new JButton(iconeBotaoMenu);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFocusPainted(false);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoMenu.setToolTipText("Voltar ao Menu");

        Insets bordaNormal = new Insets(0, 0, 4, 4);
        Insets bordaHover = new Insets(4, 4, 0, 0);
        botaoMenu.setBorder(new EmptyBorder(bordaNormal));

        botaoMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botaoMenu.setBorder(new EmptyBorder(bordaHover));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botaoMenu.setBorder(new EmptyBorder(bordaNormal));
            }
        });

        botaoMenu.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
        });

        // Posicionamento no Topo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 50, 0, 0);
        painelTopo.add(painelTextosTopo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        painelTopo.add(botaoMenu, gbc);

        // --- TABULEIROS E PLACAS ---
        painelJogador = new TabuleiroPainel(tabuleiroJogador, true);
        painelComputador = new TabuleiroPainel(tabuleiroComputador, false);
        painelComputador.setListenerClique(this::jogadorAtaca);

        // Blocos dos Tabuleiros com Placas de Título
        JPanel blocoJogador = criarBlocoTab(new PainelTituloPlaca("SEU TABULEIRO"), painelJogador);
        JPanel blocoComputador = criarBlocoTab(new PainelTituloPlaca("TABULEIRO INIMIGO"), painelComputador);

        JPanel painelTabuleiros = new JPanel(new GridLayout(1, 2, 40, 0));
        painelTabuleiros.setOpaque(false);
        painelTabuleiros.setBorder(new EmptyBorder(15, 40, 40, 40));
        painelTabuleiros.add(blocoJogador);
        painelTabuleiros.add(blocoComputador);

        fundo.add(painelTopo, BorderLayout.NORTH);
        fundo.add(painelTabuleiros, BorderLayout.CENTER);
    }

    private JPanel criarBlocoTab(PainelTituloPlaca tituloPlaca, TabuleiroPainel painelTabuleiro) {
        JPanel bloco = new JPanel(new BorderLayout(0, 10));
        bloco.setOpaque(false);

        JPanel containerTabuleiro = new JPanel(new GridBagLayout());
        containerTabuleiro.setOpaque(false);
        containerTabuleiro.add(painelTabuleiro);

        bloco.add(tituloPlaca, BorderLayout.NORTH);
        bloco.add(containerTabuleiro, BorderLayout.CENTER);

        return bloco;
    }

    private void jogadorAtaca(int linha, int coluna) {
        if (jogoTerminado || !turnoJogador)
            return;
        if (tabuleiroComputador.jaAtacado(linha, coluna))
            return;

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

        if (jogoTerminado)
            return;

        int linha;
        int coluna;

        while (true) {

            if (primeiroAcerto == null) {

                // Modo busca (xadrez)
                do {
                    linha = random.nextInt(Tabuleiro.TAMANHO);
                    coluna = random.nextInt(Tabuleiro.TAMANHO);
                } while (tabuleiroJogador.jaAtacado(linha, coluna)
                        || (linha + coluna) % 2 != 0);

            } else if (!direcaoDefinida) {

                // Ainda não sabemos a direção do navio
                int[][] lados = {
                        { -1, 0 },
                        { 1, 0 },
                        { 0, -1 },
                        { 0, 1 }
                };

                boolean encontrou = false;

                linha = coluna = -1;

                for (int[] d : lados) {

                    int l = primeiroAcerto.x + d[0];
                    int c = primeiroAcerto.y + d[1];

                    if (l >= 0 && l < Tabuleiro.TAMANHO &&
                            c >= 0 && c < Tabuleiro.TAMANHO &&
                            !tabuleiroJogador.jaAtacado(l, c)) {

                        linha = l;
                        coluna = c;
                        encontrou = true;
                        break;
                    }
                }

                if (!encontrou) {
                    primeiroAcerto = null;
                    continue;
                }

            } else {

                if (!invertendoDirecao) {

                    linha = ultimoAcerto.x + dirLinha;
                    coluna = ultimoAcerto.y + dirColuna;

                } else {

                    linha = primeiroAcerto.x - dirLinha;
                    coluna = primeiroAcerto.y - dirColuna;

                }

                if (linha < 0 ||
                        linha >= Tabuleiro.TAMANHO ||
                        coluna < 0 ||
                        coluna >= Tabuleiro.TAMANHO ||
                        tabuleiroJogador.jaAtacado(linha, coluna)) {

                    if (!invertendoDirecao) {

                        invertendoDirecao = true;
                        continue;

                    } else {

                        primeiroAcerto = null;
                        ultimoAcerto = null;
                        direcaoDefinida = false;
                        invertendoDirecao = false;
                        continue;
                    }
                }
            }

            boolean acertou = tabuleiroJogador.atacar(linha, coluna);

            painelJogador.repaint();

            if (acertou) {

                if (primeiroAcerto == null) {

                    primeiroAcerto = new Point(linha, coluna);
                    ultimoAcerto = primeiroAcerto;

                } else if (!direcaoDefinida) {

                    dirLinha = Integer.compare(linha, primeiroAcerto.x);
                    dirColuna = Integer.compare(coluna, primeiroAcerto.y);

                    direcaoDefinida = true;
                    ultimoAcerto = new Point(linha, coluna);

                } else {

                    ultimoAcerto = new Point(linha, coluna);

                }

                Navio afundado = tabuleiroJogador.navioAfundadoEm(linha, coluna);

                if (afundado != null) {

                    primeiroAcerto = null;
                    ultimoAcerto = null;

                    direcaoDefinida = false;
                    invertendoDirecao = false;

                    dirLinha = 0;
                    dirColuna = 0;

                    rotuloStatus.setText(
                            "O computador afundou seu "
                                    + afundado.getNome() + "!");
                } else {

                    rotuloStatus.setText(
                            "O computador acertou seu navio! Jogando novamente...");
                }

                if (tabuleiroJogador.todosNaviosAfundados()) {
                    fimDeJogo(false);
                    return;
                }

                Timer timer = new Timer(700, e -> turnoComputador());
                timer.setRepeats(false);
                timer.start();

            } else {

                if (direcaoDefinida && !invertendoDirecao) {
                    invertendoDirecao = true;
                }

                rotuloStatus.setText("O computador errou. Sua vez!");
                turnoJogador = true;
            }

            break;
        }
    }

    private void fimDeJogo(boolean venceuJogador) {
        jogoTerminado = true;
        String mensagem = venceuJogador
                ? "Parabéns! Você afundou toda a frota inimiga!"
                : "Fim de jogo! O computador afundou toda a sua frota.";
        rotuloStatus.setText(mensagem);
        Timer timer = new Timer(600, e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                if (venceuJogador)
                    new TelaVitoria().setVisible(true);
                else
                    new TelaDerrota().setVisible(true);
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
}