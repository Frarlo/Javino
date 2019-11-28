package gov.ismonnet.client;

import gov.ismonnet.shared.Commands;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class GuiClient extends JFrame implements Listener {

    private final ClientManager cm;

    private final BufferedImage onImage;
    private final BufferedImage offImage;

    public GuiClient(ClientManager cm) {
        this.cm = cm;

        try {
            this.onImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("Led_on.png")));
            this.offImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("Led_off.png")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        initComponents();
    }

    private void buttonActionPerformed(ActionEvent e) {
        cm.send(Commands.PRESS_BUTTON);
    }

    @Override
    public void receive(Commands msg) {
        if (Commands.TURN_ON_LED == msg) {
            ((ImagePanel) imgPanel).setImage(onImage);
        } else if (Commands.TURN_OFF_LED == msg) {
            ((ImagePanel) imgPanel).setImage(offImage);
        } else if (Commands.DISCONNECT == msg) {
            System.exit(0);
        }
    }

    private void createUIComponents() {
        imgPanel = new ImagePanel();

        ((ImagePanel) imgPanel).setImage(offImage);
    }

    @SuppressWarnings("ALL")
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        createUIComponents();

        JButton button = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 0 0",
            // columns
            "[grow,center]",
            // rows
            "[grow,top]" +
            "[fill]"));

        //======== imgPanel ========
        {
            imgPanel.setBorder(null);
            imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
        }
        contentPane.add(imgPanel, "cell 0 0,grow");

        //---- button ----
        button.setText("clicca qua che si fanno magie");
        button.addActionListener(e -> buttonActionPerformed(e));
        contentPane.add(button, "cell 0 1");
        setSize(345, 420);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel imgPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    static class ImagePanel extends JPanel {

        private Image image;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(!(g instanceof Graphics2D))
                return;

            final Graphics2D g2d = (Graphics2D) g;

            final double widthRation = (double) getWidth() / image.getWidth(null);
            final double heightRation = (double) getHeight() / image.getHeight(null);
            final double scaleRatio = Math.min(widthRation, heightRation);

            final int x = (getWidth() - (int) (image.getWidth(null) * scaleRatio)) / 2;
            final int y = (getHeight() - (int) (image.getHeight(null) * scaleRatio)) / 2;

            g2d.translate(getX() + x, getY() + y);
            g2d.scale(scaleRatio, scaleRatio);

            g2d.drawImage(image, 0, 0, this);

            g2d.scale(1 / scaleRatio, 1 / scaleRatio);
            g2d.translate(-getX() - x, -getY() - y);
        }

        public void setImage(Image image) {
            this.image = image;
            repaint();
        }

        public Image getImage() {
            return image;
        }
    }
}
