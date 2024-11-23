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
import service.UserSession;

import java.util.prefs.Preferences;


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



//creates an account and stores credentials into preference file
    public void createNewAccount(ActionEvent actionEvent) {
        // Retrieve input from fields
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();

        // Check validity again before saving (if needed)
        if (!username.matches(usernameRegex)) {
            signupValidationMessage.setText("Invalid username.");
            return;
        }
        if (!password.matches(passwordRegex)) {
            signupValidationMessage.setText("Invalid password.");
            return;
        }
        if (!confirmPasswordField.getText().equals(password)) {
            signupValidationMessage.setText("Passwords do not match.");
            return;
        }
        if (!email.matches(emailRegex)) {
            signupValidationMessage.setText("Invalid email.");
            return;
        }

        try {
            // Store data in Preferences
            Preferences userPreferences = Preferences.userRoot().node(this.getClass().getName());
            userPreferences.put("USERNAME", username);
            userPreferences.put("PASSWORD", password);
            userPreferences.put("EMAIL", email);

            // Set the current user session
            UserSession.getInstance(username, password);

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setContentText("Your account has been successfully created and stored.");
            alert.showAndWait();

            // Redirect to login screen
            goBack(actionEvent);

        } catch (Exception e) {
            e.printStackTrace();
            signupValidationMessage.setText("Error storing account information. Please try again.");
        }
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
