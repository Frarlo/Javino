package gov.ismonnet.server;

import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class ClientInformation{
    private final InetAddress ClientIp;
    private final int ClientPort;
    //TODO: How should I set ClientName? Maybe we don't need ClientName
    private String ClientName;

    public ClientInformation(){
        this.ClientIp = null;
        this.ClientPort = 0;
        this.ClientName = "";
    }

    ClientInformation(final InetAddress ip, final int port){
        this.ClientPort = port;
        this.ClientIp = ip;
        this.ClientName = "";
    }

    InetAddress getClientIp() {
        return ClientIp;
    }

    int getClientPort() {
        return ClientPort;
    }

    boolean equals(ClientInformation temp){
        if (this.ClientIp != null) {
            return this.ClientIp.equals(temp.ClientIp) && this.ClientPort == temp.ClientPort;
        } else
            return false;
    }
}

public class ServerManager {
    private final List<ClientInformation> clients = new ArrayList<>();
    private final int receivePort;

    private DatagramSocket receiveSocket;
    private DatagramPacket packet;
    private byte[] bufferIN;
    private Thread receiveThread;

    public ServerManager(int receivePort) {
        this.receivePort = receivePort;
    }

    public void bind() throws SocketException {
        receiveSocket = new DatagramSocket(receivePort);
        packet = new DatagramPacket(bufferIN, bufferIN.length);
        receiveThread = new Thread(() -> {
            //TODO: Break this cycle
            while(true){
                bufferIN = new byte[256];
                try {
                    receiveSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connectClient(packet.getAddress(), packet.getPort());
                final String cmd = UdpUtils.getInfoReceivedPacket(packet);

                clients.forEach(client -> {
                    try {
                        send(cmd, client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        receiveThread.start();
    }

    private void send(String msg, ClientInformation client) throws IOException {
        receiveSocket.send(UdpUtils.getPacketToSend(msg, client.getClientIp(), client.getClientPort()));
    }

    private void connectClient(InetAddress newClientIp,int newClientPort){
        ClientInformation temp = new ClientInformation(newClientIp, newClientPort);
        boolean exist = false;

        for (ClientInformation client : clients) {
            if (client.equals(temp)) {
                exist = true;
                break;
            }
        }

        if(!exist)
            clients.add(temp);
    }

}
