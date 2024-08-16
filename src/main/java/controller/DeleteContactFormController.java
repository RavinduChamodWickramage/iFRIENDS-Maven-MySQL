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
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Contact;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteContactFormController implements Initializable {

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
    void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to delete your account?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.YES) {
            String contactID = lblContactID.getText();

            if (contactID.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No contact selected to delete.");
                return;
            }

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM contacts WHERE id = ?");
                preparedStatement.setString(1, contactID);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Contact successfully deleted.");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No contact found with the given ID.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
            }

        } else {
            showAlert(Alert.AlertType.INFORMATION, "Deletion Cancelled", "The contact was not deleted.");
        }
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

    private boolean isValidContact() {
        String contactID = lblContactID.getText();
        String title = cmbTitle.getValue();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String nic = txtNIC.getText();
        LocalDate dob = dateDOB.getValue();
        String phoneNumber = txtPhoneNumber.getText();

        if (title == null || title.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || email == null || email.isEmpty() || address == null || address.isEmpty() || nic == null || nic.isEmpty() || dob == null || phoneNumber == null || phoneNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled!");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email format!");
            return false;
        }

        if (!isValidSriLankanNIC(nic)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid NIC format! Use 9 numbers followed by 'V' or 11 numbers.");
            return false;
        }

        if (dob.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date of birth cannot be in the future!");
            return false;
        }

        if (!isValidSriLankanPhoneNumber(phoneNumber)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid phone number format! Use 10 digits starting with 07.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidSriLankanNIC(String nic) {
        String nicRegexOld = "^[0-9]{9}[vVxX]$";
        String nicRegexNew = "^[0-9]{12}$";
        return nic.matches(nicRegexOld) || nic.matches(nicRegexNew);
    }

    private boolean isValidSriLankanPhoneNumber(String phoneNumber) {
        String phoneRegex = "^07[0-9]{8}$";
        return phoneNumber.matches(phoneRegex);
    }
}
