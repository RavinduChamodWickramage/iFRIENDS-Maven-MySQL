package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageFormController {

    @FXML
    void btnAddContactOnAction(ActionEvent event) {
        openNewWindow("add_contact_form.fxml", "Add Contact", event);
    }

    @FXML
    void btnDeleteContactOnAction(ActionEvent event) {
        openNewWindow("delete_contact_form.fxml", "Delete Contact", event);
    }

    @FXML
    void btnSearchContactOnAction(ActionEvent event) {
        openNewWindow("search_contact_form.fxml", "Search Contact", event);
    }

    @FXML
    void btnUpdateContactOnAction(ActionEvent event) {
        openNewWindow("update_contact_form.fxml", "Update Contact", event);
    }

    @FXML
    void btnViewContactsOnAction(ActionEvent event) {
        openNewWindow("view_contacts_form.fxml", "View Contacts", event);
    }

    private void openNewWindow(String fxmlFileName, String title, ActionEvent event) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFileName));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();

            Stage disposeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            disposeStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
