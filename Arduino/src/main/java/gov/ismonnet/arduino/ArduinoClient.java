package gov.ismonnet.arduino;

import gov.ismonnet.shared.Shared;

import java.net.*;

public class ArduinoClient {
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress ip;


    //pass port and ip from main
    public ArduinoClient (InetAddress ip) throws SocketException {
        this.receiveSocket = new DatagramSocket(Shared.ARDUINO_MAIN_RECEIVE_PORT);
        this.sendSocket = new DatagramSocket();
        this.ip = ip;
    }

    public void send(String msg){
        DatagramPacket pkt;

        pkt = new DatagramPacket(msg.getBytes(),msg.getBytes().length,ip,)
    }

    public String read(){

    }

}
