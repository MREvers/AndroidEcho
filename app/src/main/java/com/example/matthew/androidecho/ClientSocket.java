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
    Socket m_Socket;
    DataOutputStream OutputStream;
    DataInputStream InputStream;

    public ClientSocket() throws IOException {
        // 10.0.2.2 is the loopback on local HOST machine.
        // Don't forget to include Internet permissions in the manifest.
        m_Socket = new Socket("10.0.2.2", 6881);

        OutputStream = new DataOutputStream(m_Socket.getOutputStream());
        InputStream = new DataInputStream(m_Socket.getInputStream());
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

        InputStream.read(buf, 0, 512);
        szRetval += new String(buf);

        return szRetval;
    }

}
