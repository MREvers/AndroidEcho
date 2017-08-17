package com.example.matthew.androidecho;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Matthew on 8/16/2017.
 */

public class ClientSocket {
    String IPAddress;
    int Port;
    Socket m_Socket;
    DataOutputStream OutputStream;
    DataInputStream InputStream;

    public ClientSocket(String aIPAddress, int aPort) {
        IPAddress = aIPAddress;
        Port = aPort;
        m_Socket = null;
    }

    public boolean Connect() {
        try{
            if (m_Socket == null && Port < Short.MAX_VALUE*2-1){
                // 10.0.2.2 is the loopback on local HOST machine.
                // Don't forget to include Internet permissions in the manifest.
                SocketAddress socketAddress = new InetSocketAddress(IPAddress, Port);

                m_Socket = new Socket();
                m_Socket.setSoTimeout(200);
                m_Socket.connect(socketAddress, 200);

                OutputStream = new DataOutputStream(m_Socket.getOutputStream());
                InputStream = new DataInputStream(m_Socket.getInputStream());
            }
            return m_Socket != null && (m_Socket.isConnected() && !m_Socket.isClosed());
        }
        catch (IOException e){
        }

        return false;
    }

    public boolean Release(){
        boolean bRetval = false;
        if (m_Socket != null && !m_Socket.isClosed()){
            try{
                m_Socket.close();
                bRetval = true;
            }
            catch (IOException e){
                bRetval = false;
            }
        }

        return bRetval;
    }

    public void Send(String msg)  throws IOException {
        // Looks like strings in JAVA store characters as 00 xx.
        // So "abc" looks like 00 61 00 62 00 63 (dec). But we
        // want 61, 62, 63
        OutputStream.write(msg.getBytes());
    }

    public String Recv() throws IOException{
        String szRetval = new String();
        byte[] buf = new byte[512];

        if (InputStream.available() > 0){
            InputStream.read(buf, 0, 512);
            szRetval += new String(buf);
        }

        return szRetval;
    }

}
