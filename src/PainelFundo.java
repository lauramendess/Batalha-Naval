import javax.swing.*;
import java.awt.*;

public class PainelFundo extends JPanel {

    private Image imagemFundo;

    public PainelFundo() {
        setLayout(null);
        carregarImagem();
    }
    private void carregarImagem() {
        try {
            ImageIcon icon = new ImageIcon("lib/fundo.jpg");

            if (icon.getIconWidth() == -1) {
                throw new Exception("Imagem não encontrada.");
            }

            imagemFundo = icon.getImage();

        } catch (Exception e) {
            System.out.println("Erro ao carregar a imagem de fundo:");
            e.printStackTrace();
            imagemFundo = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (imagemFundo != null) {

            g2.drawImage(
                    imagemFundo,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this);

        } else {

            g2.setColor(new Color(20, 90, 170));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.drawString("Imagem lib/fundo.jpg não encontrada.", 40, 40);

        }
    }
}