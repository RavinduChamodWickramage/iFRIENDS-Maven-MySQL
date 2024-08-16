package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Contact;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SearchContactFormController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbTitle;

    @FXML
    private DatePicker dateDOB;

    @FXML
    private Label lblContactID;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtFirstName;

    @FXML
    private JFXTextField txtLastName;

    @FXML
    private JFXTextField txtNIC;

    @FXML
    private JFXTextField txtPhoneNumber;

    @FXML
    private TextField txtSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTitle();
        lblContactID.setText("");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnHomePageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/home_page_form.fxml"))));
            stage.setTitle("Home Page");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();

            Stage disposeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            disposeStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String searchTerm = txtSearch.getText().trim();

        if (searchTerm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Term Missing", "Please enter a search term.");
            return;
        }

        String query = "SELECT * FROM contacts WHERE firstName LIKE ? OR lastName LIKE ? OR CONCAT(firstName, ' ', lastName) LIKE ? OR phoneNumber = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchTerm + "%");
            preparedStatement.setString(2, "%" + searchTerm + "%");
            preparedStatement.setString(3, "%" + searchTerm + "%");
            preparedStatement.setString(4, searchTerm);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Contact contact = new Contact(
                            resultSet.getString("id"),
                            resultSet.getString("title"),
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("nic"),
                            resultSet.getDate("dob").toLocalDate(),
                            resultSet.getString("phoneNumber")
                    );

                    populateFields(contact);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "No Contact Found", "No contact matching the search criteria was found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
        }
    }

    private void populateFields(Contact contact) {
        lblContactID.setText(contact.getId());
        cmbTitle.setValue(contact.getTitle());
        txtFirstName.setText(contact.getFirstName());
        txtLastName.setText(contact.getLastName());
        txtEmail.setText(contact.getEmail());
        txtAddress.setText(contact.getAddress());
        txtNIC.setText(contact.getNic());
        dateDOB.setValue(contact.getDob());
        txtPhoneNumber.setText(contact.getPhoneNumber());
    }

    private void clearFields() {
        lblContactID.setText("");
        cmbTitle.setValue(null);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtNIC.setText("");
        dateDOB.setValue(null);
        txtPhoneNumber.setText("");
    }

    private void setTitle() {
        ObservableList<String> titles = FXCollections.observableArrayList(
                "Mr", "Mrs", "Miss", "Dr", "Prof", "Hon"
        );
        cmbTitle.setItems(titles);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
