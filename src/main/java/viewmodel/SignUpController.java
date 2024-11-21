package viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class SignUpController {

    //added methods from the modified fxml file
    @FXML
    private Button goBackBtn;

    @FXML
    private Button newAccountBtn;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField emailField;

    @FXML
    private Label signupValidationMessage;

    // Regex for validation
    private final String usernameRegex = "^[a-zA-Z0-9]{5,20}$"; // Alphanumeric, 5-20 chars
    private final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"; // At least 1 upper, 1 lower, 1 digit, 8+ chars
    private final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\\.[a-zA-Z]{2,6}$"; // Standard email format

    @FXML
    public void initialize() {
        // Add focus listeners for fields
        addFocusListeners();

        // BooleanBinding to check if all fields are valid
        BooleanBinding isFormValid = Bindings.createBooleanBinding(() ->
                        !usernameField.getText().matches(usernameRegex) ||
                                !passwordField.getText().matches(passwordRegex) ||
                                !confirmPasswordField.getText().equals(passwordField.getText()) ||
                                !emailField.getText().matches(emailRegex),
                usernameField.textProperty(),
                passwordField.textProperty(),
                confirmPasswordField.textProperty(),
                emailField.textProperty()
        );

        // Disable "New Account" button if the form is invalid
        newAccountBtn.disableProperty().bind(isFormValid);

        // Add event listener to "New Account" button
        newAccountBtn.setOnAction(this::createNewAccount);
    }

    private void addFocusListeners() {
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                checkValidity(usernameField, usernameRegex, "Username");
            }
        });

        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                checkValidity(passwordField, passwordRegex, "Password");
            }
        });

        confirmPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                checkPasswordMatch();
            }
        });

        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                checkValidity(emailField, emailRegex, "Email");
            }
        });
    }

    private void checkValidity(TextField field, String regex, String fieldName) {
        if (field.getText().matches(regex)) {
            signupValidationMessage.setText(fieldName + " is valid.");
        } else {
            signupValidationMessage.setText(fieldName + " is invalid.");
        }
    }

    private void checkPasswordMatch() {
        if (confirmPasswordField.getText().equals(passwordField.getText())) {
            signupValidationMessage.setText("Passwords match.");
        } else {
            signupValidationMessage.setText("Passwords do not match.");
        }
    }


    public void createNewAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Info for the user. Message goes here");
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
