package com.fadyfaheem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class MySQL {

    static Connection connection;

    public static void mySQLConnect() {
        String url = "jdbc:mysql://localhost:3306/vendingMachine";
        String username = "vending";
        String password = "dWPZVB4o8WUzg1";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        createSalesTable();
        createDataTable();
        createSettingsTable();
    }

    public static void createSettingsTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `vendingMachine`.`settings` (" +
                "  `name` VARCHAR(255) NOT NULL," +
                "  `value` VARCHAR(255) NOT NULL," +
                "  PRIMARY KEY (`name`))";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSalesTable() { // Only Creates it if it doesn't exist
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `vendingMachine`.`salesMade` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
                "  `row` VARCHAR(255) NOT NULL," +
                "  `soldItemName` VARCHAR(255) NULL," +
                "  `priceOfSoldItem` INT NULL," +
                "  PRIMARY KEY (`id`))";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDataTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `vendingMachine`.`machineRows` (" +
                "  `row` VARCHAR(255) NOT NULL," +
                "  `amountOfItemsInRow` INT NOT NULL," +
                "  `relayLineNum` INT NOT NULL," +
                "  `itemCost` INT NOT NULL," +
                "  `nameOfItemSold` VARCHAR(255) NOT NULL," +
                "  PRIMARY KEY (`row`))";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFullNewItem(String row, int amountOfItemsInRow, int relayLineNum, int itemCost, String itemName) {
        String sql = "INSERT IGNORE INTO `vendingMachine`.`machineRows` (`row`, `amountOfItemsInRow`, `relayLineNum`, `itemCost`, `nameOfItemSold`)" +
                " VALUES ('" + row + "', '" + amountOfItemsInRow + "', '" + relayLineNum + "', '"+ itemCost +"', '"+ itemName +"' )";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doesRowExist(String rowStr) {
        String sql = "SELECT machineRows.row FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            String rowName = "";
            while (rs.next())
            {
               rowName = rs.getString("row");
            }
            return !rowName.equals("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doesRowHaveItems(String rowStr) {
        String sql = "SELECT machineRows.amountOfItemsInRow FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int itemInMachine = 0;
            while (rs.next()) {
                itemInMachine = rs.getInt("amountOfItemsInRow");
            }
            return itemInMachine >= 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void listAllItemsThatAreEmpty() {
        String sql = "SELECT * FROM vendingMachine.machineRows where machineRows.amountOfItemsInRow = 0";
        ArrayList<String> itemsMissing = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                itemsMissing.add(rs.getString("nameOfItemSold"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Mail.sendMail(getReceiverEmail(), ("There are Items missing within the machine. The items include: " + itemsMissing));
    }

    public static boolean areAllReqMetToSendEmail() { // Must be a bool func
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'receiverEmail'";
        String sqlTwo = "SELECT * FROM vendingMachine.settings where settings.name = 'senderEmail'";
        String sqlThree = "SELECT * FROM vendingMachine.settings where settings.name = 'senderPassword'";
        String sqlFour = "SELECT * FROM vendingMachine.settings where settings.name = 'locationOfMachine'";
        String recEmail = "";
        String sendEmail = "";
        String sendPass = "";
        String locationOfMachine = "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Statement stmtTwo = connection.createStatement();
            ResultSet rsTwo = stmtTwo.executeQuery(sqlTwo);
            Statement stmtThree = connection.createStatement();
            ResultSet rsThree = stmtThree.executeQuery(sqlThree);
            Statement stmtFour = connection.createStatement();
            ResultSet rsFour = stmtFour.executeQuery(sqlFour);
            while (rs.next()) {
                recEmail = rs.getString("value");
            }
            while (rsTwo.next()) {
                sendEmail = rsTwo.getString("value");
            }
            while (rsThree.next()) {
                sendPass = rsThree.getString("value");
            }
            while (rsFour.next()) {
                locationOfMachine = rsFour.getString("value");
            }

            return (locationOfMachine.length() >= 1) && (sendPass.length() >= 5) && (sendEmail.length() >= 5) && (recEmail.length() >= 5);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getReceiverEmail() { // Must be string func
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'receiverEmail'";
        String email = "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                email = rs.getString("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return email;
    }

    public static String getSenderEmail() { // Must be string func
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'senderEmail'";
        String email = "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                email = rs.getString("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return email;
    }

    public static String getSenderPassword() { // Must be string func
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'senderPassword'";
        String pass = "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                pass = rs.getString("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pass;
    }

    public static String getLocationForEmail() {
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'locationOfMachine'";
        String locationOfMachine = "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                locationOfMachine = rs.getString("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return locationOfMachine;
    }

    public static void setOptions(String settingName, String value) {
        String sql = "SELECT * FROM vendingMachine.settings where settings.name = '" + settingName + "'";
        int counter = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (counter == 0) {
           String sqlTwo = "INSERT IGNORE INTO `vendingMachine`.`settings` (`name`, `value`) VALUES ('" + settingName + "', '" + value + "')";
            try {
                Statement stmt = connection.createStatement();
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String updateSQL = "UPDATE `vendingMachine`.`settings` SET `value` = '" + value + "' WHERE (`name` = '" + settingName + "')";
            PreparedStatement exstmt;
            try {
                exstmt = connection.prepareStatement(updateSQL);
                exstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean doesAdminPassExist() {
        String adminHash = "";
        String sqlOne = "SELECT * FROM vendingMachine.settings where settings.name = 'adminHash'";
        try {
            Statement stmtOne = connection.createStatement();
            ResultSet rsOne = stmtOne.executeQuery(sqlOne);
            while (rsOne.next()) {
                adminHash = rsOne.getString("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return adminHash.length() > 1;

    }

    public static void dropAdminPass() {
        String sql = "DELETE FROM `vendingMachine`.`settings` WHERE (`name` = 'adminHash')";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createAdminPass(String pass) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] hash = digest.digest(pass.getBytes());
            String sql = "INSERT IGNORE INTO `vendingMachine`.`settings` (`name`, `value`) VALUES ('adminHash', '" + bytesToStringHex(hash) + "')";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (NoSuchAlgorithmException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static boolean isAdminPassCorrect(String pass) { // THIS IS A BOOLEAN

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] hash = digest.digest(pass.getBytes());
            String serverHashedPassword = "";
            String sql = "SELECT * FROM vendingMachine.settings where settings.name = 'adminHash'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                serverHashedPassword = rs.getString("value");
            }

            return bytesToStringHex(hash).equals(serverHashedPassword);

        } catch (NoSuchAlgorithmException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void removeBoughtItem(String rowStr) {
        String sql = "SELECT machineRows.amountOfItemsInRow FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int itemInMachine = 0;
            while (rs.next()) {
                itemInMachine = rs.getInt("amountOfItemsInRow");
            }
            if (itemInMachine > 0) {
                itemInMachine--;
            }
            String updateSQL = "UPDATE `vendingMachine`.`machineRows` SET `amountOfItemsInRow` = '" + itemInMachine + "' WHERE (`row` = '"+ rowStr +"')";
            PreparedStatement exstmt = connection.prepareStatement(updateSQL);
            exstmt.executeUpdate();
            createSale(rowStr);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSale(String rowStr) {
        String soldItemName = "";
        int itemPrice = 0;
        String sqlSelect = "SELECT machineRows.itemCost, machineRows.nameOfItemSold FROM vendingMachine.machineRows where machineRows.row = '" + rowStr + "'";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlSelect);
            while (rs.next()) {
                itemPrice = rs.getInt("itemCost");
                soldItemName = rs.getString("nameOfItemSold");
            }

            String sql = "INSERT INTO `vendingMachine`.`salesMade` (`row`, `soldItemName`, `priceOfSoldItem`) VALUES ('" + rowStr + "', '" + soldItemName + "', '" + itemPrice + "')";
            Statement exstmt = connection.createStatement();
            exstmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public static void updateItemAmount(String rowStr, int itemsPlaced) {
        String updateSQL = "UPDATE `vendingMachine`.`machineRows` SET `amountOfItemsInRow` = '" + itemsPlaced + "' WHERE (`row` = '"+ rowStr +"')";
        PreparedStatement exstmt;
        try {
            exstmt = connection.prepareStatement(updateSQL);
            exstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAllItemAmount(int itemAmount) {
        String sql = "SELECT machineRows.row FROM vendingMachine.machineRows";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<String> rowName = new ArrayList<>();
            while (rs.next()) {
                rowName.add(rs.getString("row"));
            }
            for (String row : rowName) {
                updateItemAmount(row, itemAmount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getItemNameInRow(String rowStr) {
        String sql = "SELECT machineRows.nameOfItemSold FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            String itemName = "";
            while (rs.next()) {
                itemName = rs.getString("nameOfItemSold");
            }
            return itemName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateItemNameInRow(String rowStr, String itemName) {
        String updateSQL = "UPDATE `vendingMachine`.`machineRows` SET `nameOfItemSold` = '" + itemName + "' WHERE (`row` = '"+ rowStr +"')";
        PreparedStatement exstmt;
        try {
            exstmt = connection.prepareStatement(updateSQL);
            exstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAllItemNameInRow(String itemName) {
        String sql = "SELECT machineRows.row FROM vendingMachine.machineRows";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<String> rowName = new ArrayList<>();
            while (rs.next()) {
                rowName.add(rs.getString("row"));
            }
            for (String row : rowName) {
                updateItemNameInRow(row, itemName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int amountOfItemsInRow(String rowStr) {
        String sql = "SELECT machineRows.amountOfItemsInRow FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int amountOfItemsInRow = 0;
            while (rs.next()) {
                amountOfItemsInRow = rs.getInt("amountOfItemsInRow");
            }
            return amountOfItemsInRow;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int costOfItem (String rowStr) {
        String sql = "SELECT machineRows.itemCost FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int dollarCost = 0;
            while (rs.next()) {
                dollarCost = rs.getInt("itemCost");
            }
            return dollarCost;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void costOfItemChange(String rowStr, int cost) {
        String updateSQL = "UPDATE `vendingMachine`.`machineRows` SET `itemCost` = '" + cost + "' WHERE (`row` = '"+ rowStr +"')";
        PreparedStatement exstmt;
        try {
            exstmt = connection.prepareStatement(updateSQL);
            exstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> rowList() {
        ArrayList<String> rows = new ArrayList<>();

        String sql = "SELECT machineRows.row FROM vendingMachine.machineRows";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                rows.add(rs.getString("row"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static void changeRelayLineNum (String rowStr, int relayLineNum) {
        String updateSQL = "UPDATE `vendingMachine`.`machineRows` SET `relayLineNum` = '" + relayLineNum + "' WHERE (`row` = '"+ rowStr +"')";
        PreparedStatement exstmt;
        try {
            exstmt = connection.prepareStatement(updateSQL);
            exstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRelayLineNum(String rowStr) {
        String sql = "SELECT machineRows.relayLineNum FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        int relayLineNum = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                relayLineNum = rs.getInt("relayLineNum");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return relayLineNum;
    }

    public static void activateMotorForRow(String rowStr) {
        String sql = "SELECT machineRows.relayLineNum FROM vendingMachine.machineRows WHERE machineRows.row = \"" + rowStr + "\"";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int relay = 0;
            while (rs.next())
            {
                relay = rs.getInt("relayLineNum");
            }

            ArduinoConnection.arduinoWrite(relay + "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


