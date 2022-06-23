package com.fadyfaheem;

import java.sql.*;

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

    public static void createSalesTable() { // Only Creates it if it doesn't exist
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `vendingMachine`.`salesMade` (" +
                "  `id` INT NOT NULL," +
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
                "  PRIMARY KEY (`row`))";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFullNewItem(String row, int amountOfItemsInRow, int relayLineNum, int itemCost) {
        String sql = "INSERT IGNORE INTO `vendingMachine`.`machineRows` (`row`, `amountOfItemsInRow`, `relayLineNum`, `itemCost`)" +
                " VALUES ('" + row + "', '" + amountOfItemsInRow + "', '" + relayLineNum + "', '"+ itemCost +"')";
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

            if (rowName != "") {
                return true;
            } else {
                return false;
            }
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

            if (ArduinoConnection.ardAccess != null && ArduinoConnection.ardAccess.isOpen()) {
                ArduinoConnection.arduinoWrite(relay + "");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


