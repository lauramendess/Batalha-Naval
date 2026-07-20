import javax.swing.*;
import java.awt.*;

public class TelaDerrota extends JFrame {

    // Largura desejada para os botões (a altura será calculada automaticamente mantendo a proporção exata)
    private static final int LARGURA_BOTAO = 230;

    public TelaDerrota() {
        setTitle("Batalha Naval - Derrota");
        setSize(1120, 800);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Image imagemFundo = new ImageIcon("lib/Derrota.png").getImage();

        JPanel fundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
            }
        };

        fundo.setLayout(new GridBagLayout());
        setContentPane(fundo);

        // Carrega as imagens com proporção nativa e resolução máxima
        ImageIcon imgJogarNovamente = carregarImagemNitida("lib/JogarNovamente.png", LARGURA_BOTAO);
        ImageIcon imgMenu = carregarImagemNitida("lib/MenuPrincipal.png", LARGURA_BOTAO);

        JButton botaoJogarNovamente = new JButton(imgJogarNovamente);
        JButton botaoMenu = new JButton(imgMenu);

        configurarEstiloBotao(botaoJogarNovamente);
        configurarEstiloBotao(botaoMenu);

        botaoJogarNovamente.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaPosicionamento().setVisible(true));
        });

        botaoMenu.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
        });

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoJogarNovamente);
        painelBotoes.add(botaoMenu);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 70, 0); // Espaçamento harmônico da borda inferior
        fundo.add(painelBotoes, gbc);
    }

    private ImageIcon carregarImagemNitida(String caminho, int larguraDesejada) {
        ImageIcon iconOriginal = new ImageIcon(caminho);

        int largOriginal = iconOriginal.getIconWidth();
        int altOriginal = iconOriginal.getIconHeight();

        // Evita divisão por zero caso a imagem não exista
        if (largOriginal <= 0 || altOriginal <= 0) {
            return iconOriginal;
        }

        // Calcula a altura perfeita proporcional à arte original
        int alturaCalculada = (int) (((double) altOriginal / largOriginal) * larguraDesejada);

        Image imgRedimensionada = iconOriginal.getImage().getScaledInstance(
                larguraDesejada,
                alturaCalculada,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imgRedimensionada);
    }

    private void configurarEstiloBotao(JButton botao) {
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajusta o tamanho do botão exatamente ao do ícone dimensionado
        ImageIcon icon = (ImageIcon) botao.getIcon();
        if (icon != null) {
            botao.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        }
    }
}