package gov.ismonnet.client;

import gov.ismonnet.shared.Commands;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GuiClient extends JFrame implements Listener {

    private ClientManager cm;

    public GuiClient(ClientManager cm) {
        this.cm = cm;
        initComponents();
    }

    private void buttonActionPerformed(ActionEvent e) {
        cm.send(Commands.PRESS_BUTTON);
    }

    @Override
    public void receive(Commands msg) {
        //Accende o spegne la lampèadina
        if (Commands.TURN_ON_LED == msg) {
            //accendo la lampada
        } else if (Commands.TURN_OFF_LED == msg) {

        } else if (Commands.DISCONNECT == msg) {
            System.exit(0);
        }
    }

    @SuppressWarnings("ALL")
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        JLabel imgLabel = new JLabel();
        JButton button = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new VerticalLayout(5));
        contentPane.add(imgLabel);

        //---- button ----
        button.setText("clicca qua che si fanno magie");
        button.addActionListener(e -> buttonActionPerformed(e));
        contentPane.add(button);
        pack();
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}
