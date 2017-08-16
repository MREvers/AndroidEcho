package com.example.matthew.androidecho;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Matthew on 8/16/2017.
 */

public class CommThread extends Thread {
    LinkedList<String> MessegeQueue = new LinkedList<>();
    View SnackBarAnchor;

    public CommThread(LinkedList<String> aMsgBuffer, View aViewAnchor){
        MessegeQueue = aMsgBuffer;
        SnackBarAnchor = aViewAnchor;
    }

    @Override
    public void run() {

        ClientSocket clientSock;
        try{
            clientSock = new ClientSocket();
            snackShow("Connection Established", "Confirm");
            while(true){
                if (MessegeQueue.size() > 0){
                    clientSock.Send(MessegeQueue.getFirst());
                    snackShow("Sent: " + MessegeQueue.getFirst(), "Verify");
                    MessegeQueue.removeFirst();

                    if (!sleep(200)){break;}

                    String szResp = "NoResp";
                    szResp = clientSock.Recv();

                    snackShow("Server Response: " + szResp, "Resp");
                }

                if (!sleep(200)){break;}
            }
        }catch (IOException e){
            snackShow(e.getMessage(), "IOFail");
        }
    }

    private boolean sleep(int aiMSecs){
        try {
            Thread.sleep(200);
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
}