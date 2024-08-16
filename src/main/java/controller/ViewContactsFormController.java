package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Contact;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewContactsFormController implements Initializable {

    @FXML
    private TableColumn<Contact, String> colAddress;

    @FXML
    private TableColumn<Contact, String> colDOB;

    @FXML
    private TableColumn<Contact, String> colEmail;

    @FXML
    private TableColumn<Contact, String> colFirstName;

    @FXML
    private TableColumn<Contact, String> colID;

    @FXML
    private TableColumn<Contact, String> colLastName;

    @FXML
    private TableColumn<Contact, String> colNIC;

    @FXML
    private TableColumn<Contact, String> colPhoneNumber;

    @FXML
    private TableColumn<Contact, String> colTitle;

    @FXML
    private TableView<Contact> tblContacts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDataToColumns();
        loadTable();
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

    private void loadTable() {
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList<Contact> data = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM contacts");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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
                data.add(contact);
            }
            tblContacts.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDataToColumns() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }
}
