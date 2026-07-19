import javax.swing.*;
import java.awt.*;

public class TelaMenu extends JFrame {

    public TelaMenu() {
        setTitle("Batalha Naval");
        setSize(1120, 800);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PainelFundo fundo = new PainelFundo();
        setContentPane(fundo);

        Image imagemTitulo = new ImageIcon("lib/Titulo.png").getImage();

        Image imagemRedimensionada = imagemTitulo.getScaledInstance(
                650,
                -1,
                Image.SCALE_SMOOTH);

        JLabel titulo = new JLabel(new ImageIcon(imagemRedimensionada));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon imgJogar = new ImageIcon(
                new ImageIcon("lib/Jogar.png")
                        .getImage()
                        .getScaledInstance(220, 220, Image.SCALE_SMOOTH));

        ImageIcon imgRegras = new ImageIcon(
                new ImageIcon("lib/Regras.png")
                        .getImage()
                        .getScaledInstance(220, 220, Image.SCALE_SMOOTH));

        JButton botaoJogar = new JButton(imgJogar);
        JButton botaoRegras = new JButton(imgRegras);

        botaoJogar.setBorderPainted(false);
        botaoJogar.setContentAreaFilled(false);
        botaoJogar.setFocusPainted(false);
        botaoJogar.setOpaque(false);

        botaoRegras.setBorderPainted(false);
        botaoRegras.setContentAreaFilled(false);
        botaoRegras.setFocusPainted(false);
        botaoRegras.setOpaque(false);

        botaoJogar.setPreferredSize(new Dimension(220, 220));
        botaoRegras.setPreferredSize(new Dimension(220, 220));

        configurarBotao(botaoJogar);
        configurarBotao(botaoRegras);
        
        botaoJogar.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaPosicionamento().setVisible(true));
        });

        botaoRegras.addActionListener(e -> new TelaRegras(this).setVisible(true));

        JPanel painelCentral = new JPanel();
        painelCentral.setOpaque(false);
        painelCentral.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 40, 0, 40);
        painelCentral.add(botaoJogar, gbc);
        gbc.gridx = 1;
        painelCentral.add(botaoRegras, gbc);

        fundo.setLayout(new GridBagLayout());
        GridBagConstraints gTitulo = new GridBagConstraints();
        gTitulo.gridx = 0;
        gTitulo.gridy = 0;
        gTitulo.weightx = 1;
        gTitulo.weighty = 0.45;
        gTitulo.fill = GridBagConstraints.BOTH;
        gTitulo.insets = new Insets(40, 40, 0, 40);
        fundo.add(titulo, gTitulo);

        GridBagConstraints gBotoes = new GridBagConstraints();
        gBotoes.gridx = 0;
        gBotoes.gridy = 1;
        gBotoes.weightx = 1;
        gBotoes.weighty = 0.1;
        fundo.add(painelCentral, gBotoes);
    }

    private void configurarBotao(JButton botao) {

        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = (ImageIcon) botao.getIcon();
        botao.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setSize(
                        botao.getWidth() + 8,
                        botao.getHeight() + 8);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setSize(
                        icon.getIconWidth(),
                        icon.getIconHeight());
            }

        });
    }
}
