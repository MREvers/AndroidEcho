package com.example.matthew.androidecho;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Created by Matthew on 8/16/2017.
 */

public class CommThread extends Thread {
    private LinkedList<String> MessegeQueue = new LinkedList<>();
    private ClientSocket ClientSocket;
    private CommOutput   OutputMessenger;
    private Semaphore    SocketMutex;

    public CommThread(ClientSocket clientSocket, LinkedList<String> aMsgBuffer,
                      CommOutput aMsger, Semaphore aMutex){
        MessegeQueue = aMsgBuffer;
        OutputMessenger = aMsger;
        ClientSocket = clientSocket;
        SocketMutex = aMutex;
    }

    public void SetSocket(ClientSocket aNewSocket){
        acquireClient();
        if (CloseSocket()){
            ClientSocket = aNewSocket;
        }
        releaseClient();
    }

    public boolean CloseSocket(){
        if (ClientSocket != null){
            if(!ClientSocket.Release()){
                ClientSocket = null;
                snackShow("*Could Not Release Socket");
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        boolean bRecover = true;
        while(bRecover){
            bRecover = simpleSendRecvComm();
        }
    }

    private boolean simpleSendRecvComm(){
        boolean bCanRecover = false;
        try{
            snackShow("*Comm Thread Running");
            while(true){
                acquireClient();
                if (ClientSocket != null && ClientSocket.Connect()){
                    while(MessegeQueue.size() > 0){
                        ClientSocket.Send(MessegeQueue.getFirst());
                        snackShow("Send: " + MessegeQueue.getFirst());
                        MessegeQueue.removeFirst();
                        if (!sleep(25)){releaseClient(); break;}
                    }
                    String szResp = ClientSocket.Recv();
                    if (szResp.length() > 0){
                        snackShow("Recv: " + szResp);
                    }
                }
                releaseClient();
                if (!sleep(200)){continue;}
            }
        }catch (IOException e){
            releaseClient();
            snackShow(e.getMessage());
            SetSocket(null);
            bCanRecover = true;
        }

        return bCanRecover;
    }

    private boolean sleep(int aiMSecs){
        try {
            Thread.sleep(aiMSecs);
            return true;
        } catch (InterruptedException e) {
            // Interrupted exception will occur if
            // the Worker object's interrupt() method
            // is called. interrupt() is inherited
            // from the Thread class.
            return false;
        }
    }

    private void snackShow(String aMSG){
        OutputMessenger.Send(aMSG);
    }

    private void acquireClient(){
        try{
            SocketMutex.acquire();
        }
        catch (InterruptedException e){
        }
    }

    private void releaseClient(){
        SocketMutex.release();
    }
}