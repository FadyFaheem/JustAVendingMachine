package com.fadyfaheem;

import com.fazecast.jSerialComm.SerialPort;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ArduinoConnection {

    static SerialPort ardAccess;
    public static void connectToArd(){
        SerialPort[] portNames = SerialPort.getCommPorts(); // Gets all serial ports available
        ArrayList<String> ports = new ArrayList<>(); // array to store port non descriptive port names
        for (SerialPort port : portNames) { // Takes all ports and parses them into Arraylist String
            ports.add(port.getSystemPortName());
        }
        for (String port : ports) { // This gets a little messy. Takes all ports against serial port descriptive name and looks for Arduino specfic port.
            // I have no clue if this will work or not. however I do hope so It does.
            for (SerialPort serPort: portNames) {
                if (serPort.getDescriptivePortName().equals("Arduino Mega 2560 ("+ port + ")")){
                    ardAccess = SerialPort.getCommPort(port);
                    ardAccess.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    if (ardAccess.openPort()) {
                        System.out.println("Connected to arduino!");
                    }
                }
            }
        }
    }

    public static void arduinoWrite(String a){ // Writes to arduino code. Arduino takes number and proceeds to hold relay open for 2sec
        try{Thread.sleep(5);} catch(Exception ignored){}
        PrintWriter send = new PrintWriter(ardAccess.getOutputStream());
        send.print(a);
        send.flush();
    }

}
