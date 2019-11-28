package gov.ismonnet.client;

import gov.ismonnet.shared.Commands;

public interface Listener {
    void receive(Commands msg);
}
