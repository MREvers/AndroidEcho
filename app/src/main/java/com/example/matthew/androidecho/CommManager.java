package com.example.matthew.androidecho;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Created by Matthew on 8/16/2017.
 */

public class CommManager {
    LinkedList<String> MessageBuffer = new LinkedList<String>(){};
    CommThread   commThread;
    CommOutput   commOutput;
    Semaphore    SocketMutex;

    String IPAddress;
    int    Port;

    public CommManager(TextView SnackPanel){
        IPAddress = "10.0.2.2";
        Port = 6881;
        commOutput = new CommOutput(SnackPanel);
        SocketMutex = new Semaphore(1);
    }

    public void Begin(){
        // Set the default address.
        // Start the new thread
        commThread = new CommThread(new ClientSocket(IPAddress, Port),
                                    MessageBuffer, commOutput, SocketMutex);
        commThread.start();

    }

    public boolean ChangeAddr(String aIPAddress, int aPort){
        boolean bRetval = false;
        if (!IPAddress.equals(aIPAddress) || Port != aPort){
            MessageBuffer.clear();

            commThread.SetSocket(new ClientSocket(aIPAddress, aPort));
            IPAddress = aIPAddress;
            Port = aPort;
            bRetval = true;
        }
        else{
            bRetval = true;
        }

        return bRetval;
    }

    public void SendMessage(String IPAddress, int Port, String MSG){
        if (ChangeAddr(IPAddress, Port)){
            SendMessage(MSG);
        }
    }

    public void SendMessage(String MSG){
        MessageBuffer.add(MSG);
    }

    public void CommListener(){

    }

    private void snackShow(String aMSG){
        commOutput.Send(aMSG);
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
