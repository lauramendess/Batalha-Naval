import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaPosicionamento extends JFrame {

    private final Tabuleiro tabuleiroJogador = new Tabuleiro();
    private TabuleiroPainel painelTabuleiro;
    private JLabel rotuloInstrucao;
    private JLabel rotuloOrientacao;
    private JButton botaoIniciar;
    private JButton botaoRotacionar;

    private int indiceNavioAtual = 0;
    private boolean horizontal = true;

    private static final String CAMINHO_ICONE_MENU = "lib/Menu.png";
    private static final String CAMINHO_FUNDO_TEXTO = "lib/FundoTexto.png";

    // Cache de imagens estáticas pré-carregadas
    private static Image imagemFundoTexto = null;
    private static ImageIcon iconeMenu = null;

    static {
        // Carrega as imagens em background assim que a classe é referenciada, sem travar a EDT
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        imagemFundoTexto = toolkit.getImage(CAMINHO_FUNDO_TEXTO);

        Image imgMenu = toolkit.getImage(CAMINHO_ICONE_MENU);
        iconeMenu = new ImageIcon(imgMenu.getScaledInstance(40, 40, Image.SCALE_FAST));
    }

    public TelaPosicionamento() {
        setTitle("Batalha Naval - Posicione sua frota");
        setSize(1000, 780);
        setMinimumSize(new Dimension(760, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PainelFundo fundo = new PainelFundo();
        fundo.setLayout(new BorderLayout());
        setContentPane(fundo);

        // --- PAINEL TOPO ---
        int alturaDesejada = 120;

        JPanel topo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundoTexto != null) {
                    int imgWidth = imagemFundoTexto.getWidth(this);
                    int imgHeight = imagemFundoTexto.getHeight(this);

                    if (imgWidth > 0 && imgHeight > 0) {
                        int larguraBruta = (int) (((double) imgWidth / imgHeight) * alturaDesejada * 3.8);
                        int larguraDesejada = Math.min(larguraBruta, 620);
                        int x = (getWidth() - larguraDesejada) / 2;
                        int y = (getHeight() - alturaDesejada) / 2;

                        // Renderiza dinamicamente usando o ImageObserver do próprio painel
                        g.drawImage(imagemFundoTexto, x, y, larguraDesejada, alturaDesejada, this);
                    }
                }
            }
        };
        topo.setOpaque(false);
        topo.setLayout(new GridBagLayout());
        topo.setPreferredSize(new Dimension(1000, alturaDesejada + 5));

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel painelTextos = new JPanel();
        painelTextos.setOpaque(false);
        painelTextos.setLayout(new BoxLayout(painelTextos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("POSICIONE SUA FROTA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        rotuloInstrucao = new JLabel("", SwingConstants.CENTER);
        rotuloInstrucao.setFont(new Font("Arial", Font.BOLD, 15));
        rotuloInstrucao.setForeground(Color.YELLOW);
        rotuloInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotuloInstrucao.setBorder(new EmptyBorder(2, 0, 0, 0));

        rotuloOrientacao = new JLabel("", SwingConstants.CENTER);
        rotuloOrientacao.setFont(new Font("Arial", Font.PLAIN, 12));
        rotuloOrientacao.setForeground(Color.WHITE);
        rotuloOrientacao.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotuloOrientacao.setBorder(new EmptyBorder(1, 0, 0, 0));

        painelTextos.add(titulo);
        painelTextos.add(rotuloInstrucao);
        painelTextos.add(rotuloOrientacao);

        // --- Botão Menu ---
        JButton botaoMenu = new JButton(iconeMenu);
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

        // Layout Topo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 55, 0, 0);
        topo.add(painelTextos, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 20);
        topo.add(botaoMenu, gbc);

        // --- PAINEL CENTRAL (TABULEIRO) ---
        painelTabuleiro = new TabuleiroPainel(tabuleiroJogador, true);
        painelTabuleiro.setListenerClique(this::tentarPosicionar);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(5, 20, 5, 20));
        centro.add(painelTabuleiro);

        // --- PAINEL INFERIOR (BOTÕES) ---
        botaoRotacionar = new BotaoMetalico("GIRAR (H/V)");
        botaoRotacionar.setPreferredSize(new Dimension(190, 50));
        botaoRotacionar.addActionListener(e -> {
            horizontal = !horizontal;
            atualizarPreviewEstado();
        });

        JButton botaoAleatorio = new BotaoMetalico("ALEATÓRIO");
        botaoAleatorio.setPreferredSize(new Dimension(190, 50));
        botaoAleatorio.addActionListener(e -> posicionarRestanteAleatorio());

        JButton botaoReiniciar = new BotaoMetalico("REINICIAR");
        botaoReiniciar.setPreferredSize(new Dimension(190, 50));
        botaoReiniciar.addActionListener(e -> reiniciarPosicionamento());

        botaoIniciar = new BotaoMetalico("INICIAR BATALHA");
        botaoIniciar.setPreferredSize(new Dimension(220, 50));
        botaoIniciar.setEnabled(false);
        botaoIniciar.addActionListener(e -> iniciarBatalha());

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        inferior.setOpaque(false);
        inferior.add(botaoRotacionar);
        inferior.add(botaoAleatorio);
        inferior.add(botaoReiniciar);
        inferior.add(botaoIniciar);

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
            rotuloOrientacao.setText("Posição inválida (fora do tabuleiro ou encostando em outro navio).");
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