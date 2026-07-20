import javax.swing.*;
import java.awt.*;

public class TelaVitoria extends JFrame {

    public TelaVitoria() {
        setTitle("Batalha Naval - Vitória");
        setSize(1120, 800);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Image imagem = new ImageIcon("lib/Vitoria.png").getImage();

        JPanel fundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagem, 0, 0, getWidth(), getHeight(), this);
            }
        };

        fundo.setLayout(new GridBagLayout());

        setContentPane(fundo);

        ImageIcon imgJogarNovamente = new ImageIcon(
                new ImageIcon("lib/JogarNovamente.png")
                        .getImage()
                        .getScaledInstance(240, 80, Image.SCALE_SMOOTH));

        ImageIcon imgMenu = new ImageIcon(
                new ImageIcon("lib/MenuPrincipal.png")
                        .getImage()
                        .getScaledInstance(240, 80, Image.SCALE_SMOOTH));

        JButton botaoJogarNovamente = new JButton(imgJogarNovamente);
        JButton botaoMenu = new JButton(imgMenu);

        configurarBotao(botaoJogarNovamente);
        configurarBotao(botaoMenu);

        botaoJogarNovamente.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaPosicionamento().setVisible(true));
        });

        botaoMenu.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
        });

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoJogarNovamente);
        painelBotoes.add(botaoMenu);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 0, 60, 0);
        fundo.add(painelBotoes, gbc);
    }

    private void configurarBotao(JButton botao) {

        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = (ImageIcon) botao.getIcon();

        int largura = 320;
        int altura = 100;

        Image imagem = icon.getImage().getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH);

        botao.setIcon(new ImageIcon(imagem));
        botao.setPreferredSize(new Dimension(largura, altura));
    }
}