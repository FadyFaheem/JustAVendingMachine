package com.fadyfaheem;

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
    }

    public static void createSettingsTable() {

    }

    public static void createSalesTable() { // Only Creates it if it doesn't exist
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `vendingMachine`.`salesMade` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
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

    public static void changeItemUsingRowName(String row, int amountOfItemsInRow, int relayLineNum, int itemCost, String itemName) {
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


