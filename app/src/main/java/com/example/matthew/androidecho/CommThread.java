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
    LinkedList<String> MessegeQueue = new LinkedList<>();
    ClientSocket ClientSocket;
    View SnackBarAnchor;
    Semaphore SocketMutex;

    public CommThread(ClientSocket clientSocket, LinkedList<String> aMsgBuffer,
                      View aViewAnchor, Semaphore aMutex){
        MessegeQueue = aMsgBuffer;
        SnackBarAnchor = aViewAnchor;
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
                snackShow("Could Not Release Socket", "CT:Close");
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        try{
            snackShow("Comm Thread Running", "Confirm");
            while(true){
                acquireClient();
                if (ClientSocket != null && ClientSocket.Connect()){
                    while(MessegeQueue.size() > 0){
                        ClientSocket.Send(MessegeQueue.getFirst());
                        snackShow("Sent: " + MessegeQueue.getFirst(), "Verify");
                        MessegeQueue.removeFirst();

                        if (!sleep(100)){releaseClient(); continue;}
                    }
                }
                releaseClient();
                if (!sleep(200)){continue;}
            }
        }catch (IOException e){
            snackShow(e.getMessage(), "IOFail");
        }
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

    private void snackShow(String aMSG, String aActName){
        Snackbar.make(SnackBarAnchor, aMSG, Snackbar.LENGTH_LONG)
                .setAction(aActName, null).show();
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