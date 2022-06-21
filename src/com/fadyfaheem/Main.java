package com.fadyfaheem;

import com.fazecast.jSerialComm.SerialPort;
import com.pyramidacceptors.ptalk.api.PyramidAcceptor;
import com.pyramidacceptors.ptalk.api.PyramidDeviceException;
import com.pyramidacceptors.ptalk.api.event.CreditEvent;
import com.pyramidacceptors.ptalk.api.event.Events;
import com.pyramidacceptors.ptalk.api.event.PTalkEvent;
import com.pyramidacceptors.ptalk.api.event.PTalkEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Main extends JFrame implements PTalkEventListener, ActionListener {
    private JLabel moneyCounterLabel, selectLabel, vendingPendingLabel;
    private JButton aButton, bButton, cButton, dButton,
            eButton, numOneButton, numTwoButton,
            numThreeButton, numFourButton, numFiveButton,
            numSixButton, numSevenButton, numEightButton,
            numNineButton, clearButton, vendButton;
    private int dollarAvailable = 0;
    private boolean letterAdded = false;
    private String selectionString = "";
    private final String[][] optionsAvailableArray = new String[][]
            {{"A1", "2"},
                    {"A2", "2"},
                    {"A3", "2"},
                    {"A4", "2"},
                    {"A5", "2"},
                    {"B1", "2"},
                    {"B2", "2"},
                    {"B3", "2"},
                    {"B4", "2"},
                    {"B5", "2"},
                    {"B6", "2"},
                    {"C1", "2"},
                    {"C2", "2"},
                    {"C3", "2"},
                    {"C4", "2"},
                    {"C5", "2"},
                    {"C6", "2"},
                    {"D1", "2"},
                    {"D2", "2"},
                    {"D3", "2"},
                    {"D4", "2"},
                    {"D5", "2"},
                    {"D6", "2"},
                    {"D7", "2"},
                    {"D8", "2"},
                    {"D9", "2"},
                    {"E1", "2"},
                    {"E2", "2"},
                    {"E3", "2"},
                    {"E4", "2"},
                    {"E5", "2"},
                    {"E6", "2"},
                    {"E7", "2"},
                    {"E8", "2"},
                    {"E9", "15"}
            };

    PyramidAcceptor acceptor;
    static SerialPort ardAccess;

    public Main() {
        guiSetup();
        connectBillAcceptor();
        connectToArd();
    }

    public void connectToArd(){
        SerialPort[] portNames = SerialPort.getCommPorts();
        ArrayList<String> ports = new ArrayList<>();
        for (SerialPort port : portNames) {
            System.out.println(port.getDescriptivePortName());
            ports.add(port.getSystemPortName());
        }
        for (String port : ports) {
            for (SerialPort serPort: portNames) {
                if (serPort.getDescriptivePortName().equals("Arduino Mega 2560 ("+ port + ")")){
                    ardAccess = SerialPort.getCommPort(port);
                    ardAccess.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    if (ardAccess.openPort()) {
                        System.out.println("Connected to arduino!");
                    } else {
                        System.out.println("Failed to Connect");
                    }
                }
            }
        }
    }

    public void arduinoWrite(String a){
        try{Thread.sleep(5);} catch(Exception ignored){}
        PrintWriter send = new PrintWriter(ardAccess.getOutputStream());
        send.print(a);
        send.flush();
    }

    public static void showOnScreen( int screen, JFrame frame )
    {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        if( screen > -1 && screen < gs.length )
        {
            gs[screen].setFullScreenWindow( frame );
        }
        else if( gs.length > 0 )
        {
            gs[0].setFullScreenWindow( frame );
        }
        else
        {
            throw new RuntimeException( "No Screens Found" );
        }
    }

    public void guiSetup(){

        // Main Panel Setup

        Container mainWindow = getContentPane(); //need this to make JFrame work well
        mainWindow.setLayout(null); //do not use any layout managers
        mainWindow.setBackground(Color.decode("#212426")); //make the background of the window dark gray
        setDefaultCloseOperation(EXIT_ON_CLOSE); //actually end the program when clicking the close button
        setTitle("Vending Machine");//text for the window's title bar
        setResizable(true);//don't allow the user to resize the window
        setSize(1080,1920);//set the size of the window to half the screen width and half the screen height//where to position the top left corner of the window

        // TOP BAR

        moneyCounterLabel = GUI.labelSetup("$0", 100, 200,75,200,200,true);
        mainWindow.add(moneyCounterLabel);

        selectLabel = GUI.labelSetup("", 100, 650, 75,200,200,true);
        mainWindow.add(selectLabel);

        // ROW 1

        aButton = GUI.buttonSetup("A", 100, 75, 350, 200,200,this, true);
        mainWindow.add(aButton);

        numOneButton = GUI.buttonSetup("1", 100, 340, 350, 200,200,this, true);
        mainWindow.add(numOneButton);

        numTwoButton = GUI.buttonSetup("2", 100, 570, 350, 200,200,this, true);
        mainWindow.add(numTwoButton);

        numThreeButton = GUI.buttonSetup("3", 100, 800, 350, 200,200,this, true);
        mainWindow.add(numThreeButton);

        // ROW 2

        bButton = GUI.buttonSetup("B", 100, 75, 580, 200,200,this, true);
        mainWindow.add(bButton);

        numFourButton = GUI.buttonSetup("4", 100, 340, 580, 200,200,this, true);
        mainWindow.add(numFourButton);

        numFiveButton = GUI.buttonSetup("5", 100, 570, 580, 200,200,this, true);
        mainWindow.add(numFiveButton);

        numSixButton = GUI.buttonSetup("6", 100, 800, 580, 200,200,this, true);
        mainWindow.add(numSixButton);

        // ROW 3

        cButton = GUI.buttonSetup("C", 100, 75, 810, 200,200,this, true);
        mainWindow.add(cButton);

        numSevenButton = GUI.buttonSetup("7", 100, 340, 810, 200,200,this, true);
        mainWindow.add(numSevenButton);

        numEightButton = GUI.buttonSetup("8", 100, 570, 810, 200,200,this, true);
        mainWindow.add(numEightButton);

        numNineButton = GUI.buttonSetup("9", 100, 800, 810, 200,200,this, true);
        mainWindow.add(numNineButton);

        // ROW 4

        dButton = GUI.buttonSetup("D", 100, 75, 1040, 200,200,this, true);
        mainWindow.add(dButton);

        clearButton = GUI.buttonSetup("Clear", 100, 340, 1040, 660,200,this, true);
        mainWindow.add(clearButton);

        // ROW 5

        eButton = GUI.buttonSetup("E", 100, 75, 1270, 200,200,this, true);
        mainWindow.add(eButton);

        vendButton = GUI.buttonSetup("Vend", 100, 340, 1270, 660,200,this, true);
        mainWindow.add(vendButton);


        // VENDING SCREEN //

        vendingPendingLabel = GUI.labelSetup("Vending...", 200, 0,760,1080,400, true);
        vendingPendingLabel.setVisible(false);
        mainWindow.add(vendingPendingLabel);
    }

    public void addLetterNumber(boolean isLetter, String LetterNumber){
        if (selectionString.length() < 2) {
            if (!letterAdded && isLetter) {
                selectionString += LetterNumber;
                letterAdded = true;
                selectLabel.setText(selectionString);
            }
            if (letterAdded && !isLetter) {
                selectionString += LetterNumber;
                selectLabel.setText(selectionString);
            }
        }

        if (selectionString.length() == 2) {
            selectLabel.setForeground(Color.red);
            for (String[] strings : optionsAvailableArray) {
                if (strings[0].equals(selectionString)) {
                    selectLabel.setForeground(Color.white);
                }
            }
        }
    }

    public void clearSelect(){
        letterAdded = false;
        selectionString = "";
        selectLabel.setForeground(Color.white);
        selectLabel.setText(selectionString);
    }

    public void addDollarBill() {
        dollarAvailable++;
        moneyCounterLabel.setText("$" + dollarAvailable);
    }

    public void mainScreenVisibility(boolean isVisible) {
        moneyCounterLabel.setVisible(isVisible);
        selectLabel.setVisible(isVisible);
        aButton.setVisible(isVisible);
        bButton.setVisible(isVisible);
        cButton.setVisible(isVisible);
        dButton.setVisible(isVisible);
        eButton.setVisible(isVisible);
        numOneButton.setVisible(isVisible);
        numTwoButton.setVisible(isVisible);
        numThreeButton.setVisible(isVisible);
        numFourButton.setVisible(isVisible);
        numFiveButton.setVisible(isVisible);
        numSixButton.setVisible(isVisible);
        numSevenButton.setVisible(isVisible);
        numEightButton.setVisible(isVisible);
        numNineButton.setVisible(isVisible);
        clearButton.setVisible(isVisible);
        vendButton.setVisible(isVisible);
    }

    public void vendingPendingVisibility(){
        vendingPendingLabel.setVisible(true);
        ActionListener task = evt -> {
            vendingPendingLabel.setVisible(false);
            mainScreenVisibility(true);
        };
        Timer countdown = new Timer(5000 ,task);
        countdown.setRepeats(false);
        countdown.start();

    }




    public void connectBillAcceptor() {
        try {
            acceptor = PyramidAcceptor.valueOfRS232();
            acceptor.connect();
            acceptor.addChangeListener(this);
        } catch (PyramidDeviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeEventReceived(PTalkEvent evt) {
        if (evt.getId() == Events.Credit){
            if (((CreditEvent) evt).getBillName().name().equals("Bill1")) {
                addDollarBill();
            }

        }
    }


    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);
        showOnScreen(1, bob);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == aButton) {
            addLetterNumber(true, "A");
        }
        if (e.getSource() == bButton) {
            addLetterNumber(true, "B");
        }
        if (e.getSource() == cButton) {
            addLetterNumber(true, "C");
        }
        if (e.getSource() == dButton) {
            addLetterNumber(true, "D");
        }
        if (e.getSource() == eButton) {
            addLetterNumber(true, "E");
        }
        if (e.getSource() == numOneButton) {
            addLetterNumber(false, "1");
        }
        if (e.getSource() == numTwoButton) {
            addLetterNumber(false, "2");
        }
        if (e.getSource() == numThreeButton) {
            addLetterNumber(false, "3");
        }
        if (e.getSource() == numFourButton) {
            addLetterNumber(false, "4");
        }
        if (e.getSource() == numFiveButton) {
            addLetterNumber(false, "5");
        }
        if (e.getSource() == numSixButton) {
            addLetterNumber(false, "6");
        }
        if (e.getSource() == numSevenButton) {
            addLetterNumber(false, "7");
        }
        if (e.getSource() == numEightButton) {
            addLetterNumber(false, "8");
        }
        if (e.getSource() == numNineButton) {
            addLetterNumber(false, "9");
        }
        if (e.getSource() == clearButton) {
            clearSelect();
        }
        if (e.getSource() == vendButton) {
            if (selectionString.length() == 2) {
                if (selectLabel.getForeground() == Color.white) {
                    if (dollarAvailable >= 1) {
                        dollarAvailable--;
                        moneyCounterLabel.setText("$" + dollarAvailable);
                        for (String[] strings : optionsAvailableArray) {
                            if (selectLabel.getText().equals(strings[0])) {
                                arduinoWrite(strings[1]);
                            }
                        }
                        clearSelect();
                        mainScreenVisibility(false);
                        vendingPendingVisibility();
                    }
                }
            }
        }


    }
}
