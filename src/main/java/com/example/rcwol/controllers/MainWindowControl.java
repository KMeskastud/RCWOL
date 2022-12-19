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
import java.net.UnknownHostException; //perteklinis kodas
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

    private static final String ipRange = "192.168.137.255"; //neatitinka static pavadinimo standarto

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
        this.folders = folders; //dublikatas, folders jau turi reiksme
        return folders;
    }

    public void addFolder() throws IOException, SQLException { //sqlexception nereikalinga, perteklius
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
            setComputersList(selectedFolder.getId());
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////
    //COMPUTERS//
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setComputersList(int folderID) throws SQLException { // folderID nereikalingas, perteklinis
        this.computersList.getItems().clear();
        for (Computer computer : this.getComputers()) {
            if(computer.getFolderId() == selectedFolder.getId()) {
                this.computersList.getItems().add(computer.getName());
            }
        }
    }

    public ArrayList<Computer> getComputers() throws SQLException {
        computers = DbQuerys.getAllComputers();
        this.computers = computers; // computers jau turi reiksme, perteklinis veiksmas
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
        setComputersList(selectedFolder.getId());
    }

    public void updateComputer() throws IOException, SQLException { //ioexception nereikalinga, perteklinis
        DbQuerys.updateComputer(selectedComputer.getId(), pcNameTF.getText().toString(), pcMACTF.getText().toString(), pcIPTF.getText().toString()); //nereikia toString(), nes metodas getText() grazina ir taip string //netikrinamos reiksmes
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
        WOL.wakeComputer(ipRange, selectedComputer.getMAC());
        pingComputer(10000, selectedComputer.getIP());
    }

    public void wakeFolderComputers() {
        for(Computer computer : computers) {
            if(computer.getFolderId() == selectedFolder.getId())
                WOL.wakeComputer(ipRange, computer.getMAC());

        }
    }

    public void wakeAllComputers() {
        for(Computer computer : computers) {
            WOL.wakeComputer(ipRange, computer.getMAC());

        }
    }

    public void checkComputerStatus() throws IOException {
        int timeOut = 3000;
        boolean status = pingComputer(timeOut, selectedComputer.getIP());
        if (status == true) // galima parasyti tiesiog status
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
