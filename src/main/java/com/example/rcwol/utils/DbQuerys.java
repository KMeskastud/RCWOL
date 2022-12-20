package com.example.rcwol.utils;

import com.example.rcwol.controllers.MainWindowControl;
import com.example.rcwol.ds.Computer;
import com.example.rcwol.ds.Folder;

import java.sql.*;
import java.util.ArrayList;

public class DbQuerys {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static void addFolder(Folder folder) throws SQLException {
        if(!folder.getName().equals("") && folder.getName().length() <= 30) {
            connection = DbUtils.connectToDb();
            String insertString = "INSERT INTO folder(`name`) VALUES (?)";
            preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setString(1, folder.getName());
            preparedStatement.execute();
            DbUtils.disconnectFromDb(connection, preparedStatement);
            MainWindowControl.alertMessage("Folder created");
        }
        else
            MainWindowControl.alertMessage("Folder not created, bad input");
    }

    public static void removeFolder(Folder folder) throws SQLException {
        connection = DbUtils.connectToDb();

        //delete computers that folder contains
        String query1 = "DELETE FROM computer WHERE folder_id = '" + folder.getId() + "'";
        preparedStatement = connection.prepareStatement(query1);
        preparedStatement.execute();

        //delete folder
        String query2 = "DELETE FROM folder WHERE id = '" + folder.getId() + "'";
        preparedStatement = connection.prepareStatement(query2);
        preparedStatement.execute();

        DbUtils.disconnectFromDb(connection, preparedStatement);
    }

    public static ArrayList<Folder> getAllFolders() throws SQLException {
        ArrayList<Folder> folders = new ArrayList<>();
        connection = DbUtils.connectToDb();
        statement = connection.createStatement();
        String query1 = "SELECT * FROM folder";
        ResultSet rs1 = statement.executeQuery(query1);
        while (rs1.next()) {
            folders.add(new Folder(rs1.getInt("id"), rs1.getString("name")));
        }

        DbUtils.disconnectFromDb(connection, statement);
        return folders;
    }

    public static void addComputer(Computer computer) throws SQLException {
        if(!computer.getName().equals("") && !computer.getIP().equals("") && !computer.getMAC().equals("") && computer.getName().length() <= 30)
        {
            connection = DbUtils.connectToDb();
            String insertString = "INSERT INTO computer(`folder_id`, `name`, `mac`, `ip`) VALUES (?,?,?,?)";
            preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setInt(1, computer.getFolderId());
            preparedStatement.setString(2, computer.getName());
            preparedStatement.setString(3, computer.getMAC());
            preparedStatement.setString(4, computer.getIP());
            preparedStatement.execute();

            DbUtils.disconnectFromDb(connection, preparedStatement);
            MainWindowControl.alertMessage("Computer created");
        }
        else
            MainWindowControl.alertMessage("Computer not created, bad input");
    }

    public static void removeComputer(Computer computer) throws SQLException {
        connection = DbUtils.connectToDb();
        String query1 = "DELETE FROM computer WHERE id = '" + computer.getId() + "'";
        preparedStatement = connection.prepareStatement(query1);
        preparedStatement.execute();
        DbUtils.disconnectFromDb(connection, preparedStatement);
    }

    public static void updateComputer(int id, String name, String mac, String ip) throws SQLException {

        if(!name.equals("") && !ip.equals("") && !ip.equals("") && name.length() <= 30)
        {
            connection = DbUtils.connectToDb();
            String insertString = "UPDATE computer SET name = '" + name + "', mac = '" + mac + "', ip = '" + ip + "' where id = '" + id + "'";
            preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.execute();
            DbUtils.disconnectFromDb(connection, preparedStatement);
            MainWindowControl.alertMessage("Computer updated");
        }
        else
            MainWindowControl.alertMessage("Computer not updated, bad input");
    }

    public static ArrayList<Computer> getAllComputers() throws SQLException {
        ArrayList<Computer> computers = new ArrayList<>();
        connection = DbUtils.connectToDb();
        statement = connection.createStatement();
        String query1 = "SELECT * FROM computer";
        ResultSet rs1 = statement.executeQuery(query1);
        while (rs1.next()) {
            computers.add(new Computer(rs1.getInt("id"), rs1.getInt("folder_id"), rs1.getString("name"), rs1.getString("mac"), rs1.getString("ip")));
        }

        DbUtils.disconnectFromDb(connection, statement);
        return computers;
    }
}
