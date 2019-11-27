package gov.ismonnet.server;

import gov.ismonnet.shared.UdpUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class ClientInformation{
    private final InetAddress ClientIp;
    private final int ClientPort;
    private String ClientName;

    public ClientInformation(){
        this.ClientIp = null;
        this.ClientPort = 0;
        this.ClientName = "";
    }

    public ClientInformation(final InetAddress ip, final int port){
        this.ClientPort = port;
        this.ClientIp = ip;
        this.ClientName = "";
    }

    public final InetAddress getClientIp() {
        return ClientIp;
    }

    public final int getClientPort() {
        return ClientPort;
    }

    public final String getClientName() {
        return ClientName;
    }
    //TODO: How should I set ClientName? Maybe we don't need ClientName
    public void setClientName(String ClientName) { this.ClientName = ClientName; }

    public boolean equals(ClientInformation temp){
        if(this.ClientIp.equals(temp.ClientIp) && this.ClientPort == temp.ClientPort)
            return true;
        else
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

    private final void send(String msg, ClientInformation client) throws IOException {
        receiveSocket.send(UdpUtils.getPacketToSend(msg, client.getClientIp(), client.getClientPort()));
    }

    private final String read() throws IOException {
        byte[] buffer = new byte[256];

        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        receiveSocket.receive(pkt);

        return UdpUtils.getInfoReceivedPacket(pkt);
    }

    private final void connectClient(InetAddress newClientIp,int newClientPort){
        ClientInformation temp = new ClientInformation(newClientIp, newClientPort);
        boolean exist = false;

        for(int ini = 0; ini < clients.size(); ini++){
            if(clients.get(ini).equals(temp)){
                exist =  true;
                break;
            }
        }

        if(!exist)
            clients.add(temp);
    }

}
