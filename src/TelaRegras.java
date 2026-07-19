import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class TelaRegras extends JDialog {

    public TelaRegras(JFrame pai) {

        super(pai, "Regras da Batalha Naval", true);

        setSize(600, 550);
        setLocationRelativeTo(pai);
        setResizable(false);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(new Color(15, 40, 75));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("REGRAS DO JOGO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        String texto = """
                <html>
                <body style='font-family:Arial; font-size:14px; color:white; padding:10px;'>

                <h2 style='text-align:center;'>BATALHA NAVAL</h2>

                <b>Objetivo:</b><br>
                Afundar toda a frota do computador antes que ele afunde a sua.<br><br>

                <b>Frota de cada jogador:</b><br>
                • 1 Porta-aviões (5 casas)<br>
                • 1 Navio de Guerra (4 casas)<br>
                • 1 Cruzador (3 casas)<br>
                • 1 Submarino (3 casas)<br>
                • 1 Destroidor (2 casas)<br><br>

                <b>Regras:</b><br><br>

                1. Os navios são posicionados no início da partida.<br><br>

                2. O jogador e a máquina atacam alternadamente.<br><br>

                3. Cada ataque pode resultar em:
                <ul>
                    <li>Água (erro)</li>
                    <li>Acerto</li>
                    <li>Navio afundado</li>
                </ul>

                4. O jogador não pode atacar a mesma posição duas vezes.<br><br>

                5. A máquina também evita repetir ataques já realizados.<br><br>

                6. Vence quem destruir todos os navios adversários primeiro.

                </body>
                </html>
                """;

        JEditorPane corpo = new JEditorPane();
        corpo.setContentType("text/html");
        corpo.setText(texto);
        corpo.setEditable(false);
        corpo.setOpaque(false);
        corpo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // Faz abrir no início do texto
        SwingUtilities.invokeLater(() -> corpo.setCaretPosition(0));

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Barra de rolagem fina
        JScrollBar barra = scroll.getVerticalScrollBar();
        barra.setPreferredSize(new Dimension(8, 0));

        barra.setUI(new BasicScrollBarUI() {

            @Override
            protected void configureScrollBarColors() {
                // Cor da parte que desliza
                thumbColor = Color.WHITE;

                // Cor do fundo da barra
                trackColor = new Color(15, 40, 75);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }

        });

        JButton voltar = new JButton("VOLTAR");
        voltar.setPreferredSize(new Dimension(140, 40));
        voltar.setFont(new Font("Arial", Font.BOLD, 14));
        voltar.setBackground(Color.WHITE);
        voltar.setForeground(Color.BLACK);
        voltar.setFocusPainted(false);
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> dispose());

        JPanel painelBotao = new JPanel();
        painelBotao.setOpaque(false);
        painelBotao.add(voltar);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(painelBotao, BorderLayout.SOUTH);

        setContentPane(painel);
    }
}