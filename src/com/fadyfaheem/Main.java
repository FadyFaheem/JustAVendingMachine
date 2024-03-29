package com.fadyfaheem;

import com.fazecast.jSerialComm.SerialPort;
import com.pyramidacceptors.ptalk.api.event.CreditEvent;
import com.pyramidacceptors.ptalk.api.event.Events;
import com.pyramidacceptors.ptalk.api.event.PTalkEvent;
import com.pyramidacceptors.ptalk.api.event.PTalkEventListener;
import org.omg.CORBA.SystemException;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends JFrame implements ActionListener {

    Container mainWindow;
    private JLabel moneyCounterLabel, selectLabel, vendingPendingLabel,
            adminPasscodeLabel, adminPageLabel,
            changeOfCostRowLabel, changeOfCostPriceLabel,
            updateAllItemNumLabel,
            updateItemRowLabel, updateItemNumLabel,
            updateItemNameRowLabel,
            changeRelayLineRowLabel, changeRelayLineNumLabel, sendTestEmailLabel;

    private JTextField updateItemNameTextField, changeLocationTextField, changeSendingEmailTextField, changeSendingPassTextField, changeReceivingEmailTextField;
    private JButton aButton, bButton, cButton, dButton, // MAIN SCREEN
            eButton, numOneButton, numTwoButton, // MAIN SCREEN
            numThreeButton, numFourButton, numFiveButton, // MAIN SCREEN
            numSixButton, numSevenButton, numEightButton, // MAIN SCREEN
            numNineButton, clearButton, vendButton, // MAIN SCREEN
            adminButton, adminOneButton, adminTwoButton, adminThreeButton, // ADMIN LOGIN
            adminFourButton, adminFiveButton, adminSixButton, // ADMIN LOGIN
            adminSevenButton, adminEightButton, adminNineButton, // ADMIN LOGIN
            adminClearButton, adminZeroButton, adminEnterButton, adminBackButton, // ADMIN LOGIN
            adminOptionOne, adminOptionTwo, adminOptionThree, adminOptionFour, // ADMIN CONTROL
            adminOptionFive, adminControlBack, adminPageBack, adminPageForward, // ADMIN CONTROL
            changeOfCostRowBack, changeOfCostRowForward, changeOfCostPriceForward, // CHANGE OF COST
            changeOfCostPriceBack, changeOfCostSave, changeOfCostBackButton, // CHANGE OF COST
            updateAllItemNumBack, updateAllItemNumForward, updateAllItemBackButton, updateAllItemSave, // CHANGE ALL ITEM
            updateItemNumRowBack, updateItemNumRowForward, updateItemNumBack,  // CHANGE SINGLE ITEM AMOUNT
            updateItemNumForward, updateItemNumSave, updateItemNumBackButton, // CHANGE SINGLE ITEM AMOUNT
            updateItemNameRowBack, updateItemNameRowForward, updateItemNameSave, updateItemNameBackButton,
            changeRelayLineBackButton, changeRelayLineRowBack, changeRelayLineRowForward,
            changeRelayLineNumBack, changeRelayLineNumForward, changeRelayLineSave, changeRelayLineTest,
            changeLocationBackButton, changeLocationSaveButton,
            changeSendingEmailSaveButton, changeSendingEmailBackButton,
            changeReceivingEmailBackButton, changeReceivingEmailSaveButton,
            changeSendingPasswordSaveButton, changeSendingPasswordBackButton,
            sendTestEmailNoButton, sendTestEmailYesButton;

    private int dollarAvailable = 0; // Used for keeping dollars inputted for consumer
    private int adminSwitch = 0;

    private final String[] adminMenuOptions = {"Change Cost of Item", "Update All Item Amount", "Update Item Amount", "Update Item Name", "Update All Item Name", "Change Relay Line", "Change Admin Password", "Change Machine Location", "Change Sending Email", "Change Sending Password", "Change Email Receiver", "Send Test Email"};

    private int onPageNum = 0;
    private int itemAllUpdateInt = 0;
    private int itemAmountIntRow = 0;
    private int relayLineNumInt = 0;
    private int itemNameRowInt = 0;
    private final int maxPageNum = (int) Math.ceil(adminMenuOptions.length / 5.0);
    private final ArrayList<JButton> adminMenuButtons = new ArrayList<>();

    private int changeOfCostInt = 0;
    private final ArrayList<String> row;
    private String adminPass = "";
    private boolean letterAdded = false; // bool to check for input
    private String selectionString = "";

    static SerialPort ardAccess;


    public Main() {
        guiSetup(); // Sets up GUI
        ardAccess = SerialPort.getCommPort("/dev/ttyACM_DEVICE1");
        System.out.println("Open port: " + ardAccess.openPort());
        MySQL.mySQLConnect();
        row = MySQL.rowList();
        //row = new ArrayList<>();
        arduinoCheck.setRepeats(true);
        arduinoCheck.start();


        ExecutorService billAcceptorWatcher = Executors.newCachedThreadPool();

        billAcceptorWatcher.execute(() -> {
            System.out.println("List COM ports");
            SerialPort comPortsTWO = SerialPort.getCommPort("/dev/ttyACM_DEVICE2");
            comPortsTWO.openPort();
            comPortsTWO.setBaudRate(9600);

            try {
                while (true)
                {
                    // read serial port  and display data
                    while (comPortsTWO.bytesAvailable() > 0)
                    {
                        byte[] readBuffer = new byte[comPortsTWO.bytesAvailable()];
                        int numRead = comPortsTWO.readBytes(readBuffer, readBuffer.length);
                        for (int i = 0; i < readBuffer.length; i++) {
                            if ((char)readBuffer[i] == '1') {
                                addDollarBill();
                            }
                        }



                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public static void arduinoWrite(String a){ // Writes to arduino code. Arduino takes number and proceeds to hold relay open for 2sec

        try{
            Thread.sleep(5);
            PrintWriter send = new PrintWriter(ardAccess.getOutputStream());
            send.print(a);
            send.flush();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void appendAdminMenuButtons() {
        adminMenuButtons.add(adminOptionOne);
        adminMenuButtons.add(adminOptionTwo);
        adminMenuButtons.add(adminOptionThree);
        adminMenuButtons.add(adminOptionFour);
        adminMenuButtons.add(adminOptionFive);
    }

    public void guiSetup(){

        // Main Panel Setup

        mainWindow = getContentPane(); //need this to make JFrame work well
        mainWindow.setLayout(null); //do not use any layout managers
        mainWindow.setBackground(Color.decode("#212426")); //make the background of the window dark gray
        setDefaultCloseOperation(EXIT_ON_CLOSE); //actually end the program when clicking the close button
        setTitle("Vending Machine");//text for the window's title bar
        setResizable(true);//don't allow the user to resize the window
        setSize(1080,1920);//set the size of the window to half the screen width and half the screen height//where to position the top left corner of the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
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


        // Admin Login Menu

        //This is an invisible button used to access the menu without being noticed, it is placed within
        // the top right corner of the screen and only works when a6 is typed
        adminButton = GUI.buttonSetup("", 0, 885, -10, 200,200,this, true);
        adminButton.setOpaque(false);
        adminButton.setContentAreaFilled(false);
        adminButton.setBorderPainted(false);
        adminButton.setBorder(BorderFactory.createEmptyBorder());
        mainWindow.add(adminButton);

        adminOneButton = GUI.buttonSetup("1", 100, 200, 550, 200,200,this, true );
        mainWindow.add(adminOneButton);

        adminTwoButton = GUI.buttonSetup("2", 100, 450, 550, 200,200,this, true );
        mainWindow.add(adminTwoButton);

        adminThreeButton = GUI.buttonSetup("3", 100, 700, 550, 200,200,this, true );
        mainWindow.add(adminThreeButton);

        adminFourButton = GUI.buttonSetup("4", 100, 200, 800, 200,200,this, true );
        mainWindow.add(adminFourButton);

        adminFiveButton = GUI.buttonSetup("5", 100, 450, 800, 200,200,this, true );
        mainWindow.add(adminFiveButton);

        adminSixButton = GUI.buttonSetup("6", 100, 700, 800, 200,200,this, true );
        mainWindow.add(adminSixButton);

        adminSevenButton = GUI.buttonSetup("7", 100, 200, 1050, 200,200,this, true );
        mainWindow.add(adminSevenButton);

        adminEightButton = GUI.buttonSetup("8", 100, 450, 1050, 200,200,this, true );
        mainWindow.add(adminEightButton);

        adminNineButton = GUI.buttonSetup("9", 100, 700, 1050, 200,200,this, true );
        mainWindow.add(adminNineButton);

        adminClearButton = GUI.buttonSetup("Clear", 50, 200, 1300, 200,200,this, true );
        mainWindow.add(adminClearButton);

        adminZeroButton = GUI.buttonSetup("0", 100, 450, 1300, 200,200,this, true );
        mainWindow.add(adminZeroButton);

        adminEnterButton = GUI.buttonSetup("Enter", 50, 700, 1300, 200,200,this, true );
        mainWindow.add(adminEnterButton);

        adminBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true );
        mainWindow.add(adminBackButton);

        adminPasscodeLabel = GUI.labelSetup("", 100, 355,275,400,200, true);
        mainWindow.add(adminPasscodeLabel);

        // Admin Control Panel
        adminControlBack = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(adminControlBack);

        adminOptionOne = GUI.buttonSetup("1", 50, 175,325,750, 200,this,true);
        mainWindow.add(adminOptionOne);

        adminOptionTwo = GUI.buttonSetup("2", 50, 175,575,750, 200,this,true);
        mainWindow.add(adminOptionTwo);

        adminOptionThree = GUI.buttonSetup("3", 50, 175,825,750, 200,this,true);
        mainWindow.add(adminOptionThree);

        adminOptionFour = GUI.buttonSetup("4", 50, 175,1075,750, 200,this,true);
        mainWindow.add(adminOptionFour);

        adminOptionFive = GUI.buttonSetup("5", 50, 175,1325,750, 200,this,true);
        mainWindow.add(adminOptionFive);

        adminPageBack = GUI.buttonSetup("←", 100, 175, 1575, 200,200,this, true);
        mainWindow.add(adminPageBack);

        adminPageForward = GUI.buttonSetup("→", 100, 725, 1575, 200,200,this, true);
        mainWindow.add(adminPageForward);

        adminPageLabel = GUI.labelSetup("# of #", 50, 400, 1575, 300,200, true);
        mainWindow.add(adminPageLabel);

        appendAdminMenuButtons();

        // END OF ADMIN PANEL

        // CHANGE OF ROW COST

        changeOfCostBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeOfCostBackButton);

        changeOfCostRowBack = GUI.buttonSetup("←", 100, 175, 500, 200,200,this, true);
        mainWindow.add(changeOfCostRowBack);

        changeOfCostRowForward = GUI.buttonSetup("→", 100, 725, 500, 200,200,this, true);
        mainWindow.add(changeOfCostRowForward);

        changeOfCostRowLabel = GUI.labelSetup("##", 100, 400, 500, 300,200, true);
        mainWindow.add(changeOfCostRowLabel);

        changeOfCostPriceLabel = GUI.labelSetup("$1", 100, 400, 1000, 300,200, true);
        mainWindow.add(changeOfCostPriceLabel);

        changeOfCostPriceBack = GUI.buttonSetup("←", 100, 175, 1000, 200,200,this, true);
        mainWindow.add(changeOfCostPriceBack);

        changeOfCostPriceForward = GUI.buttonSetup("→", 100, 725, 1000, 200,200,this, true);
        mainWindow.add(changeOfCostPriceForward);

        changeOfCostSave = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(changeOfCostSave);

        // END OF CHANGE COST

        // UPDATE ALL ITEM AMOUNT

        updateAllItemNumBack = GUI.buttonSetup("←", 100, 175, 750, 200,200,this, true);
        mainWindow.add(updateAllItemNumBack);

        updateAllItemNumForward = GUI.buttonSetup("→", 100, 725, 750, 200,200,this, true);
        mainWindow.add(updateAllItemNumForward);

        updateAllItemBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(updateAllItemBackButton);

        updateAllItemNumLabel = GUI.labelSetup("##", 100, 400, 750, 300,200, true);
        mainWindow.add(updateAllItemNumLabel);

        updateAllItemSave = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(updateAllItemSave);

        // END OF ALL ITEM AMOUNT

        // UPDATE ITEM AMOUNT

        updateItemNumBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(updateItemNumBackButton);

        updateItemNumRowBack = GUI.buttonSetup("←", 100, 175, 500, 200,200,this, true);
        mainWindow.add(updateItemNumRowBack);

        updateItemNumRowForward = GUI.buttonSetup("→", 100, 725, 500, 200,200,this, true);
        mainWindow.add(updateItemNumRowForward);

        updateItemRowLabel = GUI.labelSetup("###", 100, 400, 500, 300,200, true);
        mainWindow.add(updateItemRowLabel);

        updateItemNumLabel = GUI.labelSetup("1", 100, 400, 1000, 300,200, true);
        mainWindow.add(updateItemNumLabel);

        updateItemNumBack = GUI.buttonSetup("←", 100, 175, 1000, 200,200,this, true);
        mainWindow.add(updateItemNumBack);

        updateItemNumForward = GUI.buttonSetup("→", 100, 725, 1000, 200,200,this, true);
        mainWindow.add(updateItemNumForward);

        updateItemNumSave = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(updateItemNumSave);

        // END OF UPDATE ITEM AMOUNT

        // UPDATE ITEM NAME

        updateItemNameBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(updateItemNameBackButton);

        updateItemNameRowBack = GUI.buttonSetup("←", 100, 175, 500, 200,200,this, true);
        mainWindow.add(updateItemNameRowBack);

        updateItemNameRowForward = GUI.buttonSetup("→", 100, 725, 500, 200,200,this, true);
        mainWindow.add(updateItemNameRowForward);

        updateItemNameSave = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(updateItemNameSave);

        updateItemNameRowLabel = GUI.labelSetup("###", 100, 400, 500, 300,200, true);
        mainWindow.add(updateItemNameRowLabel);

        updateItemNameTextField = GUI.textFieldSetup("####",1,60,155,1000,800,150, true);
        mainWindow.add(updateItemNameTextField);

        // END OF UPDATE ITEM NAME

        // CHANGE RELAY LINE

        changeRelayLineBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeRelayLineBackButton);

        changeRelayLineRowBack = GUI.buttonSetup("←", 100, 175, 500, 200,200,this, true);
        mainWindow.add(changeRelayLineRowBack);

        changeRelayLineRowForward = GUI.buttonSetup("→", 100, 725, 500, 200,200,this, true);
        mainWindow.add(changeRelayLineRowForward);

        changeRelayLineSave = GUI.buttonSetup("Save", 100, 350,1550,400, 200,this,true);
        mainWindow.add(changeRelayLineSave);

        changeRelayLineTest = GUI.buttonSetup("Test", 100, 350,1300,400, 200,this,true);
        mainWindow.add(changeRelayLineTest);

        changeRelayLineRowLabel = GUI.labelSetup("###", 100, 400, 500, 300,200, true);
        mainWindow.add(changeRelayLineRowLabel);

        changeRelayLineNumLabel = GUI.labelSetup("1", 100, 400, 1000, 300,200, true);
        mainWindow.add(changeRelayLineNumLabel);

        changeRelayLineNumBack = GUI.buttonSetup("←", 100, 175, 1000, 200,200,this, true);
        mainWindow.add(changeRelayLineNumBack);

        changeRelayLineNumForward = GUI.buttonSetup("→", 100, 725, 1000, 200,200,this, true);
        mainWindow.add(changeRelayLineNumForward);

        // END OF CHANGE RELAY LINE

        // Change Location
        changeLocationBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeLocationBackButton);

        changeLocationSaveButton = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(changeLocationSaveButton);

        changeLocationTextField = GUI.textFieldSetup("####",1,60,155,750,800,150, true);
        mainWindow.add(changeLocationTextField);

        // END OF CHANGE LOCATION

        // Sender Email
        changeSendingEmailBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeSendingEmailBackButton);

        changeSendingEmailSaveButton = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(changeSendingEmailSaveButton);

        changeSendingEmailTextField = GUI.textFieldSetup("####",1,40,155,750,800,150, true);
        mainWindow.add(changeSendingEmailTextField);

        // END OF SENDING EMAIL

        // Sending Password

        changeSendingPasswordBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeSendingPasswordBackButton);

        changeSendingPasswordSaveButton = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(changeSendingPasswordSaveButton);

        changeSendingPassTextField = GUI.textFieldSetup("####",1,40,155,750,800,150, true);
        mainWindow.add(changeSendingPassTextField);

        // End of Sending Password

        // Receiving Email

        changeReceivingEmailBackButton = GUI.buttonSetup("←", 100, 50, 50, 200,200,this, true);
        mainWindow.add(changeReceivingEmailBackButton);

        changeReceivingEmailSaveButton = GUI.buttonSetup("Save", 100, 350,1400,400, 200,this,true);
        mainWindow.add(changeReceivingEmailSaveButton);

        changeReceivingEmailTextField = GUI.textFieldSetup("####",1,40,155,750,800,150, true);
        mainWindow.add(changeReceivingEmailTextField);

        // End of Receiving Email

        // Send Test Email

        sendTestEmailLabel = GUI.labelSetup("Send Test Email?", 100, 150, 750, 800,150, true);
        mainWindow.add(sendTestEmailLabel);

        sendTestEmailYesButton = GUI.buttonSetup("Yes", 100, 200,1200,300, 200,this,true);
        mainWindow.add(sendTestEmailYesButton);

        sendTestEmailNoButton = GUI.buttonSetup("No", 100, 600,1200,300, 200,this,true);
        mainWindow.add(sendTestEmailNoButton);

        //mainScreenVisibility(false);
        adminLoginVisibility(false);
        adminControlPanelVisibility(false);
        ChangeOfCostVisibility(false);
        UpdateAllItemVisibility(false);
        UpdateItemAmountVisibility(false);
        UpdateItemNameVisibility(false);
        ChangeRelayLineVisibility(false);
        ChangeLocationNameVisibility(false);
        ChangeSendingEmailVisibility(false);
        ChangeSendingPassVisibility(false);
        ChangeReceivingEmailVisibility(false);
        TestEmailVisibility(false);
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
            boolean checkRow = MySQL.doesRowExist(selectionString);
            boolean doesRowHaveItems = MySQL.doesRowHaveItems(selectionString);
            if (checkRow && doesRowHaveItems) {
                int costOfItem = MySQL.costOfItem(selectionString);
                if (dollarAvailable >= costOfItem) {
                    selectLabel.setForeground(Color.white);
                } else {
                    selectLabel.setForeground(Color.red);
                }
            } else {
                selectLabel.setForeground(Color.red);
            }
        }
    }

    public void clearSelect(){
        letterAdded = false;
        selectionString = "";
        selectLabel.setForeground(Color.white);
        selectLabel.setText(selectionString);
        adminSwitch = 0;
    }

    public void addDollarBill() {
            dollarAvailable++;
            moneyCounterLabel.setText("$" + dollarAvailable);
        if (selectionString.length() == 2) {
            boolean checkRow = MySQL.doesRowExist(selectionString);
            boolean doesRowHaveItems = MySQL.doesRowHaveItems(selectionString);
            if (checkRow && doesRowHaveItems) {
                int costOfItem = MySQL.costOfItem(selectionString);
                if (dollarAvailable >= costOfItem) {
                    selectLabel.setForeground(Color.white);
                } else {
                    selectLabel.setForeground(Color.red);
                }
            } else {
                selectLabel.setForeground(Color.red);
            }
        }
    }



    ActionListener checkArd = evt -> {
        try {
            if (!ardAccess.isOpen()){
                ardAccess = SerialPort.getCommPort("/dev/ttyACM_DEVICE1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    Timer arduinoCheck = new Timer(10000, checkArd);

    // This is used to change back to normal screen if password isn't typed in and correct.
    ActionListener passwordDelay = evt -> {
        adminLoginVisibility(false);
        mainScreenVisibility(true);
        clearSelect();
        adminPass = "";
        adminPassCodeLabelSet();
    };
    Timer passCounter = new Timer(15000, passwordDelay);

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
        adminButton.setVisible(isVisible);
    }

    public void adminLoginVisibility(boolean isVisible) {
        adminOneButton.setVisible(isVisible);
        adminTwoButton.setVisible(isVisible);
        adminThreeButton.setVisible(isVisible);
        adminFourButton.setVisible(isVisible);
        adminFiveButton.setVisible(isVisible);
        adminSixButton.setVisible(isVisible);
        adminSevenButton.setVisible(isVisible);
        adminEightButton.setVisible(isVisible);
        adminNineButton.setVisible(isVisible);
        adminClearButton.setVisible(isVisible);
        adminZeroButton.setVisible(isVisible);
        adminEnterButton.setVisible(isVisible);
        adminPasscodeLabel.setVisible(isVisible);
        adminBackButton.setVisible(isVisible);
    }

    public void adminControlPanelVisibility(boolean isVisible) {
        adminOptionOne.setVisible(isVisible);
        adminOptionTwo.setVisible(isVisible);
        adminOptionThree.setVisible(isVisible);
        adminOptionFour.setVisible(isVisible);
        adminOptionFive.setVisible(isVisible);
        adminControlBack.setVisible(isVisible);
        adminPageForward.setVisible(isVisible);
        adminPageBack.setVisible(isVisible);
        adminPageLabel.setVisible(isVisible);
        if (isVisible) {
            adminPageLoad();
        }
    }

    public void ChangeOfCostVisibility(boolean isVisible) {
        changeOfCostRowBack.setVisible(isVisible);
        changeOfCostRowForward.setVisible(isVisible);
        changeOfCostBackButton.setVisible(isVisible);
        changeOfCostPriceForward.setVisible(isVisible);
        changeOfCostPriceBack.setVisible(isVisible);
        changeOfCostRowLabel.setVisible(isVisible);
        changeOfCostPriceLabel.setVisible(isVisible);
        changeOfCostSave.setVisible(isVisible);
    }

    public void UpdateAllItemVisibility(boolean isVisible) {
        updateAllItemNumBack.setVisible(isVisible);
        updateAllItemNumForward.setVisible(isVisible);
        updateAllItemBackButton.setVisible(isVisible);
        updateAllItemNumLabel.setVisible(isVisible);
        updateAllItemSave.setVisible(isVisible);
    }

    public void UpdateItemAmountVisibility(boolean isVisible) {
        updateItemNumBackButton.setVisible(isVisible);
        updateItemNumRowBack.setVisible(isVisible);
        updateItemNumRowForward.setVisible(isVisible);
        updateItemRowLabel.setVisible(isVisible);
        updateItemNumLabel.setVisible(isVisible);
        updateItemNumBack.setVisible(isVisible);
        updateItemNumForward.setVisible(isVisible);
        updateItemNumSave.setVisible(isVisible);
    }

    public void UpdateItemNameVisibility(boolean isVisible) {
        updateItemNameTextField.setVisible(isVisible);
        updateItemNameRowLabel.setVisible(isVisible);
        updateItemNameBackButton.setVisible(isVisible);
        updateItemNameRowBack.setVisible(isVisible);
        updateItemNameRowForward.setVisible(isVisible);
        updateItemNameSave.setVisible(isVisible);
    }

    public void ChangeRelayLineVisibility(boolean isVisible) {
        changeRelayLineTest.setVisible(isVisible);
        changeRelayLineBackButton.setVisible(isVisible);
        changeRelayLineRowBack.setVisible(isVisible);
        changeRelayLineRowForward.setVisible(isVisible);
        changeRelayLineSave.setVisible(isVisible);
        changeRelayLineRowLabel.setVisible(isVisible);
        changeRelayLineNumLabel.setVisible(isVisible);
        changeRelayLineNumBack.setVisible(isVisible);
        changeRelayLineNumForward.setVisible(isVisible);
    }

    public void ChangeLocationNameVisibility(boolean isVisible) {
        changeLocationTextField.setVisible(isVisible);
        changeLocationSaveButton.setVisible(isVisible);
        changeLocationBackButton.setVisible(isVisible);
    }

    public void ChangeSendingEmailVisibility(boolean isVisible) {
        changeSendingEmailBackButton.setVisible(isVisible);
        changeSendingEmailSaveButton.setVisible(isVisible);
        changeSendingEmailTextField.setVisible(isVisible);
    }

    public void ChangeSendingPassVisibility(boolean isVisible) {
        changeSendingPassTextField.setVisible(isVisible);
        changeSendingPasswordSaveButton.setVisible(isVisible);
        changeSendingPasswordBackButton.setVisible(isVisible);
    }

    public void ChangeReceivingEmailVisibility(boolean isVisible) {
        changeReceivingEmailTextField.setVisible(isVisible);
        changeReceivingEmailSaveButton.setVisible(isVisible);
        changeReceivingEmailBackButton.setVisible(isVisible);
    }

    public void TestEmailVisibility(boolean isVisible) {
        sendTestEmailLabel.setVisible(isVisible);
        sendTestEmailNoButton.setVisible(isVisible);
        sendTestEmailYesButton.setVisible(isVisible);
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

    public void adminPassCodeLabelSet() {
        adminPasscodeLabel.setText("");
        StringBuilder adminLabelSet = new StringBuilder();
        int adminCount = adminPass.length();
        for (int i = 0; i < adminCount; i++) {
            adminLabelSet.append("*");
        }
        adminPasscodeLabel.setText(adminLabelSet.toString());
    }

    public void adminPassAdd(String passNum) {
        if (adminPass.length() != 6) {
            adminPass += passNum;
            adminPassCodeLabelSet();
        }
    }

    public void adminMenuSelect(String menuSelect) {
        switch (menuSelect) {
            case "Change Cost of Item":
                ChangeOfCostVisibility(true);
                adminControlPanelVisibility(false);
                changeOfCostInt = 0;
                loadPageInfoForPriceCost();
                break;
            case "Update All Item Amount":
                UpdateAllItemVisibility(true);
                adminControlPanelVisibility(false);
                itemAllUpdateInt = 0;
                updateAllItemNumLabel.setText(String.valueOf(itemAllUpdateInt));
                break;
            case "Update Item Amount":
                UpdateItemAmountVisibility(true);
                adminControlPanelVisibility(false);
                itemAmountIntRow = 0;
                loadPageForItemAmount();
                break;
            case "Update Item Name":
                UpdateItemNameVisibility(true);
                adminControlPanelVisibility(false);
                itemNameRowInt = 0;
                loadPageForItemName();
                break;
            case "Change Admin Password":
                adminControlPanelVisibility(false);
                adminLoginVisibility(true);
                MySQL.dropAdminPass();
                adminPass = "";
                adminPassCodeLabelSet();
                break;
            case "Change Relay Line":
                ChangeRelayLineVisibility(true);
                adminControlPanelVisibility(false);
                loadPageForRelayLine();
                break;
            case "Change Machine Location":
                ChangeLocationNameVisibility(true);
                adminControlPanelVisibility(false);
                changeLocationTextField.setText(MySQL.getLocationForEmail());
                break;
            case "Change Sending Email":
                adminControlPanelVisibility(false);
                ChangeSendingEmailVisibility(true);
                changeSendingEmailTextField.setText(MySQL.getSenderEmail());
                break;
            case "Change Sending Password":
                adminControlPanelVisibility(false);
                ChangeSendingPassVisibility(true);
                changeSendingPassTextField.setText("");
                break;
            case "Change Email Receiver":
                adminControlPanelVisibility(false);
                ChangeReceivingEmailVisibility(true);
                changeReceivingEmailTextField.setText(MySQL.getReceiverEmail());
                break;
            case "Send Test Email":
                adminControlPanelVisibility(false);
                TestEmailVisibility(true);
                break;
        }
    }

    public void loadPageInfoForPriceCost() {
        changeOfCostRowLabel.setText(row.get(changeOfCostInt));
        changeOfCostPriceLabel.setText("$" + MySQL.costOfItem(row.get(changeOfCostInt)));
    }

    public void loadPageForItemAmount() {
        updateItemRowLabel.setText(row.get(itemAmountIntRow));
        updateItemNumLabel.setText(MySQL.amountOfItemsInRow(row.get(itemAmountIntRow)) + "");
    }

    public void loadPageForItemName() {
        updateItemNameRowLabel.setText(row.get(itemNameRowInt));
        updateItemNameTextField.setText(MySQL.getItemNameInRow(row.get(itemNameRowInt)));
    }

    public void loadPageForRelayLine() {
        changeRelayLineRowLabel.setText(row.get(relayLineNumInt));
        changeRelayLineNumLabel.setText(String.valueOf(MySQL.getRelayLineNum(row.get(relayLineNumInt))));
    }

    public void adminPageLoad() {
        for (JButton button: adminMenuButtons) {
            button.setVisible(false);
        }
        for (int i = 0; i < adminMenuButtons.size(); i++){
            if (adminMenuOptions.length > (i + (onPageNum * 5))) {
                adminMenuButtons.get(i).setVisible(true);
                adminMenuButtons.get(i).setText(adminMenuOptions[i + (onPageNum * 5)]);
            }
        }
        adminPageLabel.setText((onPageNum + 1) + " of " + maxPageNum);
    }


    public static void main(String[] args) {
        Main bob = new Main();
        bob.setVisible(true);


    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // MAIN MENU SCREEN

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
                int costOfItem = MySQL.costOfItem(selectionString);
                    if (dollarAvailable >= costOfItem) {
                        dollarAvailable -= costOfItem;
                        moneyCounterLabel.setText("$" + dollarAvailable);
                        MySQL.activateMotorForRow(selectionString);
                        MySQL.removeBoughtItem(selectionString);
                        if (!MySQL.doesRowHaveItems(selectionString)) {
                            MySQL.listAllItemsThatAreEmpty();
                        }
                        clearSelect();
                        mainScreenVisibility(false);
                        vendingPendingVisibility();
                    }
                }
            }
        }

        //END OF MAIN MENU SCREEN


        // ADMIN LOGIN PAGE WITH BUTTON ON MAIN MENU

        if (e.getSource() == adminButton) {
            if (selectionString.equals("A7")) {
                adminSwitch++;
                if (adminSwitch == 5) {
                    adminSwitch = 0;
                    mainScreenVisibility(false);
                    adminLoginVisibility(true);
                    passCounter.setRepeats(false);
                    passCounter.start();
                }
            }
        }
        if (e.getSource() == adminBackButton) {
            adminLoginVisibility(false);
            mainScreenVisibility(true);
            clearSelect();
            adminPass = "";
            adminPassCodeLabelSet();
            passCounter.stop();
        }

        if (e.getSource() == adminOneButton) {
            adminPassAdd("1");
        }
        if (e.getSource() == adminTwoButton) {
            adminPassAdd("2");
        }
        if (e.getSource() == adminThreeButton) {
            adminPassAdd("3");
        }
        if (e.getSource() == adminFourButton) {
            adminPassAdd("4");
        }
        if (e.getSource() == adminFiveButton) {
            adminPassAdd("5");
        }
        if (e.getSource() == adminSixButton) {
            adminPassAdd("6");
        }
        if (e.getSource() == adminSevenButton) {
            adminPassAdd("7");
        }
        if (e.getSource() == adminEightButton) {
            adminPassAdd("8");
        }
        if (e.getSource() == adminNineButton) {
            adminPassAdd("9");
        }
        if (e.getSource() == adminZeroButton) {
            adminPassAdd("0");
        }

        if (e.getSource() == adminEnterButton) {
            if (adminPass.length() >= 4) {
                if (!MySQL.doesAdminPassExist()){
                    MySQL.createAdminPass(adminPass);
                } else {
                    if (MySQL.isAdminPassCorrect(adminPass)) {
                        adminLoginVisibility(false);
                        adminControlPanelVisibility(true);
                        passCounter.stop();
                        adminPageLoad();
                    }
                }
                adminPass = "";
                adminPassCodeLabelSet();
            }

        }

        if (e.getSource() == adminClearButton) {
            adminPass = "";
            adminPassCodeLabelSet();
        }


        // END OF ADMIN LOGIN PAGE

        // ADMIN CONTROL PANEL

        if (e.getSource() == adminControlBack) {
            adminControlPanelVisibility(false);
            mainScreenVisibility(true);
            clearSelect();
            adminPass = "";
            adminPassCodeLabelSet();
        }

        if (e.getSource() == adminOptionOne) {
            adminMenuSelect(adminOptionOne.getText());
        }

        if (e.getSource() == adminOptionTwo) {
            adminMenuSelect(adminOptionTwo.getText());
        }

        if (e.getSource() == adminOptionThree) {
            adminMenuSelect(adminOptionThree.getText());
        }

        if (e.getSource() == adminOptionFour) {
            adminMenuSelect(adminOptionFour.getText());
        }

        if (e.getSource() == adminOptionFive) {
            adminMenuSelect(adminOptionFive.getText());
        }

        if (e.getSource() == adminPageBack) {
            if (onPageNum > 0) {
                onPageNum--;
            }
            adminPageLoad();
        }

        if (e.getSource() == adminPageForward) {
            if (onPageNum < maxPageNum - 1) {
                onPageNum++;
            }
            adminPageLoad();
        }

        // END OF ADMIN CONTROL PANEL

        // CHANGE OF COST

        if (e.getSource() == changeOfCostBackButton) {
            ChangeOfCostVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == changeOfCostRowBack) {
            if (changeOfCostInt > 0) {
                changeOfCostInt--;
            }
            loadPageInfoForPriceCost();
        }

        if (e.getSource() == changeOfCostRowForward) {
            if (changeOfCostInt < row.size() - 1) {
                changeOfCostInt++;
            }
            loadPageInfoForPriceCost();
        }

        if (e.getSource() == changeOfCostSave) {
            MySQL.costOfItemChange(changeOfCostRowLabel.getText(), Integer.parseInt(changeOfCostPriceLabel.getText().replace("$", "")));
        }

        if (e.getSource() == changeOfCostPriceBack) {
            if (changeOfCostPriceLabel.getText().equals("$2")) {
                changeOfCostPriceLabel.setText("$1");
            }
        }

        if (e.getSource() == changeOfCostPriceForward) {
            if (changeOfCostPriceLabel.getText().equals("$1")) {
                changeOfCostPriceLabel.setText("$2");
            }
        }

        // END OF CHANGE OF COST

        // UPDATE ALL ITEMS AMOUNT
        if (e.getSource() == updateAllItemBackButton) {
            UpdateAllItemVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == updateAllItemNumBack) {
            if (itemAllUpdateInt > 0) {
                itemAllUpdateInt--;
                updateAllItemNumLabel.setText(String.valueOf(itemAllUpdateInt));
            }
        }

        if (e.getSource() == updateAllItemNumForward) {
            itemAllUpdateInt++;
            updateAllItemNumLabel.setText(String.valueOf(itemAllUpdateInt));
        }

        if(e.getSource() == updateAllItemSave) {
            MySQL.updateAllItemAmount(itemAllUpdateInt);
        }

        // END OF UPDATE ALL ITEM AMOUNT

        // UPDATE ITEM AMOUNT

        if (e.getSource() == updateItemNumBackButton) {
            UpdateItemAmountVisibility(false);
            adminControlPanelVisibility(true);
        }

        if(e.getSource() == updateItemNumRowBack) {
            if (itemAmountIntRow > 0) {
                itemAmountIntRow--;
                loadPageForItemAmount();
            }
        }

        if(e.getSource() == updateItemNumRowForward) {
            if (itemAmountIntRow < row.size() - 1) {
                itemAmountIntRow++;
                loadPageForItemAmount();
            }
        }

        if (e.getSource() == updateItemNumSave) {
            MySQL.updateItemAmount(updateItemRowLabel.getText(), Integer.parseInt(updateItemNumLabel.getText()));
        }

        if (e.getSource() == updateItemNumForward) {
            updateItemNumLabel.setText(String.valueOf(Integer.parseInt(updateItemNumLabel.getText()) + 1));
        }

        if (e.getSource() == updateItemNumBack) {
            if (Integer.parseInt(updateItemNumLabel.getText()) > 0) {
                updateItemNumLabel.setText(String.valueOf(Integer.parseInt(updateItemNumLabel.getText()) - 1));
            }
        }

        // END OF ITEM AMOUNT

        // ITEM NAME

        if (e.getSource() == updateItemNameBackButton) {
            UpdateItemNameVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == updateItemNameRowBack) {
            if (itemNameRowInt > 0) {
                itemNameRowInt--;
                loadPageForItemName();
            }
        }

        if(e.getSource() == updateItemNameRowForward) {
            if (itemNameRowInt < row.size() - 1) {
                itemNameRowInt++;
                loadPageForItemName();
            }
        }

        if (e.getSource() == updateItemNameSave) {
            if (updateItemNameTextField.getText().length() > 0) {
                MySQL.updateItemNameInRow(updateItemNameRowLabel.getText(), updateItemNameTextField.getText());
            }
        }

        // END OF UPDATE ITEM NAME

        // CHANGE RELAY LINE
        if (e.getSource() == changeRelayLineBackButton) {
            ChangeRelayLineVisibility(false);
            adminControlPanelVisibility(true);
        }

        if(e.getSource() == changeRelayLineRowBack) {
            if (relayLineNumInt > 0) {
                relayLineNumInt--;
                loadPageForRelayLine();
            }
        }

        if (e.getSource() == changeRelayLineRowForward) {
            if (relayLineNumInt < row.size() - 1) {
                relayLineNumInt++;
                loadPageForRelayLine();
            }
        }

        if (e.getSource() == changeRelayLineNumBack) {
            if (Integer.parseInt(changeRelayLineNumLabel.getText()) > 0) {
                changeRelayLineNumLabel.setText(String.valueOf(Integer.parseInt(changeRelayLineNumLabel.getText()) - 1));
            }
        }

        if (e.getSource() == changeRelayLineNumForward) {
            changeRelayLineNumLabel.setText(String.valueOf(Integer.parseInt(changeRelayLineNumLabel.getText()) + 1));
        }

        if (e.getSource() == changeRelayLineSave) {
            MySQL.changeRelayLineNum(changeRelayLineRowLabel.getText(), Integer.parseInt(changeRelayLineNumLabel.getText()));
        }

        if (e.getSource() == changeRelayLineTest) {
            changeRelayLineTest.setEnabled(false);
            arduinoWrite(changeRelayLineNumLabel.getText());
            ActionListener task = evt -> changeRelayLineTest.setEnabled(true);
            Timer countdown = new Timer(5000 ,task);
            countdown.setRepeats(false);
            countdown.start();
        }

        // END OF RELAY LINE

        // CHANGE MACHINE LOCATION

        if (e.getSource() == changeLocationBackButton) {
            ChangeLocationNameVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == changeLocationSaveButton) {
            MySQL.setOptions("locationOfMachine", changeLocationTextField.getText());
        }

        // END OF CHANGE MACHINE LOCATION

        // CHANGE SENDING EMAIL

        if (e.getSource() == changeSendingEmailBackButton) {
            ChangeSendingEmailVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == changeSendingEmailSaveButton) {
            MySQL.setOptions("senderEmail", changeSendingEmailTextField.getText());
        }

        // END OF SENDING EMAIL

        // Change of Sending Pass

        if (e.getSource() == changeSendingPasswordBackButton) {
            ChangeSendingPassVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == changeSendingPasswordSaveButton) {
            MySQL.setOptions("senderPassword", changeSendingPassTextField.getText());
        }

        // END OF SENDING PASS

        // Change of Receiving Email

        if (e.getSource() == changeReceivingEmailBackButton) {
            ChangeReceivingEmailVisibility(false);
            adminControlPanelVisibility(true);
        }

        if (e.getSource() == changeReceivingEmailSaveButton) {
            MySQL.setOptions("receiverEmail", changeReceivingEmailTextField.getText());
        }

        // END OF Receiving EMail

        // Send Test Email

        if (e.getSource() == sendTestEmailNoButton) {
            adminControlPanelVisibility(true);
            TestEmailVisibility(false);
        }

        if (e.getSource() == sendTestEmailYesButton) {
            adminControlPanelVisibility(true);
            TestEmailVisibility(false);
            Mail.sendMail(MySQL.getReceiverEmail(), "This is a Test Email.");
        }


    }
}
