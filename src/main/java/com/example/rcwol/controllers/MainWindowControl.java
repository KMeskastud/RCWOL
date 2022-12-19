package com.example.rcwol.controllers;

import com.example.rcwol.Start;
import com.example.rcwol.ds.Computer;
import com.example.rcwol.ds.Folder;
import com.example.rcwol.utils.DbQuerys;
import com.example.rcwol.utils.WOL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainWindowControl {
    @FXML
    public TextField statusTF;
    @FXML
    public TextField pcMACTF;
    @FXML
    public TextField pcIPTF;
    @FXML
    public TextField pcNameTF;
    @FXML
    public ListView foldersList;
    @FXML
    public ListView computersList;
    @FXML
    public Button addComputerButton;
    @FXML
    public Button addFolderButton;
    private ArrayList<Folder> folders;
    private ArrayList<Computer> computers;
    private Computer selectedComputer;
    private Folder selectedFolder;

    private static final String IP_RANGE = "192.168.137.255";

    public void initialize() throws SQLException {
        setFoldersList();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    //FOLDERS//
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setFoldersList() throws SQLException {
        this.foldersList.getItems().clear();
        for (Folder folder : this.getFolders()) {
            this.foldersList.getItems().add(folder.getName());
        }
    }

    private ArrayList<Folder> getFolders() throws SQLException{
        folders = DbQuerys.getAllFolders();
        return folders;
    }

    public void addFolder() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("add-folder.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            AddFolderControl addFolderControl = fxmlLoader.getController();
            addFolderControl.setFoldersArray(folders);

            Stage stage = (Stage) this.addFolderButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
    }

    public void deleteFolder() throws SQLException {
        DbQuerys.removeFolder(selectedFolder);
        setFoldersList();
    }

    public void selectFolder(MouseEvent mouseEvent) throws SQLException {
        if (this.foldersList.getSelectionModel().getSelectedItem() != null) {
            String folderName = this.foldersList.getSelectionModel().getSelectedItem().toString();
            for (Folder folder : this.folders) {
                if(folder.getName().equals(folderName))
                    selectedFolder = folder;
            }
            setComputersList();
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////
    //COMPUTERS//
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setComputersList() throws SQLException {
        this.computersList.getItems().clear();
        for (Computer computer : this.getComputers()) {
            if(computer.getFolderId() == selectedFolder.getId()) {
                this.computersList.getItems().add(computer.getName());
            }
        }
    }

    public ArrayList<Computer> getComputers() throws SQLException {
        computers = DbQuerys.getAllComputers();
        return computers;
    }

    public void addComputer() throws IOException {
        if (selectedFolder != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("add-computer.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            AddComputerControl addComputerControl = fxmlLoader.getController();
            addComputerControl.setSelectedFolder(selectedFolder);
            addComputerControl.setComputersArray(computers);

            Stage stage = (Stage) this.addComputerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else
            MainWindowControl.alertMessage("Please select folder");

    }

    public void deleteComputer() throws SQLException {
        DbQuerys.removeComputer(selectedComputer);
        setComputersList();
    }

    public void updateComputer() throws SQLException {
        DbQuerys.updateComputer(selectedComputer.getId(), pcNameTF.getText(), pcMACTF.getText(), pcIPTF.getText());
        setComputersList();
    }

    public void selectComputer(MouseEvent mouseEvent) throws IOException {
        if (this.computersList.getSelectionModel().getSelectedItem() != null) {
            String computerName = this.computersList.getSelectionModel().getSelectedItem().toString();
            for (Computer computer : this.computers) {
                if(computer.getName().equals(computerName)) {
                    selectedComputer = computer;
                    setComputerFields();
                    checkComputerStatus();
                }
            }
        }
    }

    public void setComputerFields () {
        pcNameTF.setText(selectedComputer.getName());
        pcIPTF.setText(selectedComputer.getIP());
        pcMACTF.setText(selectedComputer.getMAC());
    }

    public void wakeComputer() throws IOException {
        WOL.wakeComputer(IP_RANGE, selectedComputer.getMAC());
        pingComputer(10000, selectedComputer.getIP());
    }

    public void wakeFolderComputers() {
        for(Computer computer : computers) {
            if(computer.getFolderId() == selectedFolder.getId())
                WOL.wakeComputer(IP_RANGE, computer.getMAC());

        }
    }

    public void wakeAllComputers() {
        for(Computer computer : computers) {
            WOL.wakeComputer(IP_RANGE, computer.getMAC());

        }
    }

    public void checkComputerStatus() throws IOException {
        int timeOut = 3000;
        boolean status = pingComputer(timeOut, selectedComputer.getIP());
        if (status)
            statusTF.setText("ON");
        else
            statusTF.setText("OFF");
    }

    private Boolean pingComputer(int timeOut, String host) throws IOException {
        return InetAddress.getByName(host).isReachable(timeOut);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    //MISC//
////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void alertMessage(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(s);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
