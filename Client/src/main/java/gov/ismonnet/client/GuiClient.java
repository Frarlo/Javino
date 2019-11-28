package gov.ismonnet.client;

import gov.ismonnet.shared.Commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiClient implements Listener {
    private JButton cliccaQuaCheSiButton;
    private JPanel panel1;
    private ClientManager cm;

    public GuiClient(ClientManager cm) {
        this.cm = cm;

        cliccaQuaCheSiButton.addActionListener(actionEvent -> {
            cm.send(Commands.PRESS_BUTTON);
        });
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
}
