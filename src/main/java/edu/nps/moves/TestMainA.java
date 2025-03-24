package edu.nps.moves;

import edu.nps.moves.net.ExternalSignalListener;

import java.net.SocketException;
import java.net.UnknownHostException;

public class TestMainA {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        System.out.println("Hello world!");
        System.out.println("This is the main class for the DIS example application.");
        ExternalSignalListener externalSignalListener = new ExternalSignalListener(30000);
        externalSignalListener.startListening();

//        while(true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//
//            }
//        }
//        externalSignalListener.stopListening();
    }
}
