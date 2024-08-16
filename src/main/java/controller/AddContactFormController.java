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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddContactFormController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbTitle;

    @FXML
    private DatePicker dateDOB;

    @FXML
    private Label lblContactID;

    @FXML
    private Label lblInvalidEmail;

    @FXML
    private Label lblInvalidDOB;

    @FXML
    private Label lblInvalidNIC;

    @FXML
    private Label lblInvalidPhoneNumber;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTitle();
        setInvalidLabels();
        addListeners();
        autoGenerateID();
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (isValidContact()) {
            addContactToDatabase();
            clearFields();
            autoGenerateID();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnHomePageOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/home_page_form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Home Page");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load home page.");
        }
    }

    private void addContactToDatabase() {
        String contactID = lblContactID.getText();
        String title = cmbTitle.getValue();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String nic = txtNIC.getText();
        LocalDate dob = dateDOB.getValue();
        String phoneNumber = txtPhoneNumber.getText();

        String query = "INSERT INTO contacts (id, title, firstName, lastName, email, address, nic, dob, phoneNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, contactID);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, nic);
            preparedStatement.setDate(8, java.sql.Date.valueOf(dob));
            preparedStatement.setString(9, phoneNumber);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Contact added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Contact addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add contact to the database.");
        }
    }

    private void clearFields() {
        cmbTitle.setValue(null);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtNIC.setText("");
        dateDOB.setValue(null);
        txtPhoneNumber.setText("");

        lblInvalidEmail.setVisible(false);
        lblInvalidNIC.setVisible(false);
        lblInvalidDOB.setVisible(false);
        lblInvalidPhoneNumber.setVisible(false);

        txtEmail.setStyle(null);
        txtNIC.setStyle(null);
        dateDOB.setStyle(null);
        txtPhoneNumber.setStyle(null);
    }

    private void setTitle() {
        ObservableList<String> titles = FXCollections.observableArrayList(
                "Mr", "Mrs", "Miss", "Dr", "Prof", "Hon"
        );
        cmbTitle.setItems(titles);
    }

    private void setInvalidLabels() {
        lblInvalidEmail.setVisible(false);
        lblInvalidNIC.setVisible(false);
        lblInvalidDOB.setVisible(false);
        lblInvalidPhoneNumber.setVisible(false);
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

        if (isContactIDExisting(contactID)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Contact ID already exists!");
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

    private boolean isContactIDExisting(String contactID) {
        String query = "SELECT id FROM contacts WHERE id = ?";
        boolean exists = false;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, contactID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                exists = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to check if Contact ID exists.");
        }

        return exists;
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

    private void addListeners() {
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEmail(newValue)) {
                lblInvalidEmail.setVisible(false);
                txtEmail.setStyle(null);
            } else {
                lblInvalidEmail.setVisible(true);
                txtEmail.setStyle("-fx-border-color: red;");
            }
        });

        txtNIC.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidSriLankanNIC(newValue)) {
                lblInvalidNIC.setVisible(false);
                txtNIC.setStyle(null);
            } else {
                lblInvalidNIC.setVisible(true);
                txtNIC.setStyle("-fx-border-color: red;");
            }
        });

        dateDOB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isAfter(LocalDate.now())) {
                lblInvalidDOB.setVisible(false);
                dateDOB.setStyle(null);
            } else {
                lblInvalidDOB.setVisible(true);
                dateDOB.setStyle("-fx-border-color: red;");
            }
        });

        txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidSriLankanPhoneNumber(newValue)) {
                lblInvalidPhoneNumber.setVisible(false);
                txtPhoneNumber.setStyle(null);
            } else {
                lblInvalidPhoneNumber.setVisible(true);
                txtPhoneNumber.setStyle("-fx-border-color: red;");
            }
        });
    }

    public void autoGenerateID() {
        String query = "SELECT id FROM contacts ORDER BY id DESC LIMIT 1";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                String lastId = resultSet.getString("id");
                int noOfContacts = Integer.parseInt(lastId.substring(1)) + 1;
                String newId = String.format("C%03d", noOfContacts);
                lblContactID.setText(newId);
            } else {
                lblContactID.setText("C001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to auto-generate contact ID.");
        }
    }
}
