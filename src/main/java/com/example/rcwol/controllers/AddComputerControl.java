package com.example.rcwol.controllers;

import com.example.rcwol.Start;
import com.example.rcwol.ds.Computer;
import com.example.rcwol.ds.Folder;
import com.example.rcwol.utils.DbQuerys;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddComputerControl {

    @FXML
    public TextField nameTF;
    @FXML
    public TextField MACTF;
    @FXML
    public TextField IPTF;
    @FXML
    public Button addButton;
    @FXML
    public Button cancelButton;

    private Folder selectedFolder;
    private ArrayList<Computer> computers;

    public void addComputer(ActionEvent actionEvent) throws SQLException, IOException {
        boolean doesExist = false;
        for(Computer computer : computers) {
            if (computer.getName().equals(this.nameTF.getText())) {
                MainWindowControl.alertMessage("Computer already exists");
                doesExist = true;
                break;
            }
        }
        if(doesExist == false)
        {
            try {
                DbQuerys.addComputer(new Computer(selectedFolder.getId(), this.nameTF.getText(), this.MACTF.getText(), this.IPTF.getText()));
            } catch (Exception e) {
                System.out.println(e);
                MainWindowControl.alertMessage("Error creating computer" + e);
            }
        }
        this.goBack();
    }
    public void setSelectedFolder(Folder folder) {
        this.selectedFolder = folder;
    }

    public void setComputersArray(ArrayList<Computer> computers) {
        this.computers = computers;
    }

    public void goBack() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("main-window.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        MainWindowControl mainWindowControl = fxmlLoader.getController();
        Stage stage = (Stage) this.nameTF.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
