package com.example.rcwol.controllers;

import com.example.rcwol.Start;
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

public class AddFolderControl {

    @FXML
    public TextField nameTF;
    @FXML
    public Button addButton;
    @FXML
    public Button cancelButton;

    private ArrayList<Folder> folders;

    public void addFolder(ActionEvent actionEvent) throws SQLException, IOException {
        boolean doesExist = false;
        folders.add(new Folder(0, " "));
        for(Folder folder : folders) {
            if (folder.getName().equals(this.nameTF.getText())) {
                MainWindowControl.alertMessage("Folder already exists");
                doesExist = true;
                break;
            }
        }
        if(doesExist == false)
        {
            try {
                DbQuerys.addFolder(new Folder(this.nameTF.getText()));
            } catch (Exception e) {
                System.out.println(e);
                MainWindowControl.alertMessage("Error creating folder" + e);
            }
        }
        this.goBack();
    }

    public void setFoldersArray(ArrayList<Folder> folders) {
        this.folders = folders;
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
