package viewmodel;

import javafx.scene.control.ComboBox;
import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB_GUI_Controller implements Initializable {

    //added edit btn
    @FXML
    private Button editBtn;

    //added delete Btn
    @FXML
    private Button deleteBtn;

    //added add btn
    @FXML
    private Button addBtn;

    //added a status message
    @FXML
    private Label statusLabel;

    //added progress bar
    @FXML
    ProgressBar progressBar;

    StorageUploader store = new StorageUploader();

    //added major dropdown
    @FXML
    private ComboBox<Major> majorComboBox;

    //menu items under edit
    @FXML
    private MenuItem editItem;

    @FXML
    private MenuItem deleteItem;

    @FXML
    private MenuItem ClearItem;

    @FXML
    private MenuItem CopyItem;



    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);


            //new code
            // Disables the "Edit" button at first
            editBtn.setDisable(true);
            //Disables "delete" button at first
            deleteBtn.setDisable(true);

            // Disables the menu items initially
            editItem.setDisable(true);
            deleteItem.setDisable(true);
            ClearItem.setDisable(true);
            CopyItem.setDisable(true);

            // Adds a listener to the TableView to monitor the changes
            tv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Person>() {
                @Override
                public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) {
                    // Enables "Edit" button if a record is selected, otherwise it disables again
                    editBtn.setDisable(newValue == null);
                    // Enables "Delete" button if a record is selected, otherwise disables again
                    deleteBtn.setDisable(newValue == null);

                    // Enable ClearItem if a row is selected, otherwise disable
                    ClearItem.setDisable(newValue == null);

                    // Enable or disable menu items based on selection
                    boolean isSelected = newValue != null;
                    editItem.setDisable(!isSelected);
                    deleteItem.setDisable(!isSelected);


                }
            });

            // Disables ClearItem until the form is filled
            first_name.textProperty().addListener((observable, oldValue, newValue) -> validateClearItem());
            last_name.textProperty().addListener((observable, oldValue, newValue) -> validateClearItem());
            department.textProperty().addListener((observable, oldValue, newValue) -> validateClearItem());
            majorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateClearItem());
            email.textProperty().addListener((observable, oldValue, newValue) -> validateClearItem());
            imageURL.textProperty().addListener((observable, oldValue, newValue) -> validateClearItem());


            // Disables the "Add" button at first
            addBtn.setDisable(true);

            // Populate ComboBox with Major enum values
            majorComboBox.setItems(FXCollections.observableArrayList(Major.values()));

            // Default selection for ComboBox
            majorComboBox.getSelectionModel().selectFirst();

            // Adds listeners to the text fields
            first_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            last_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            department.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            majorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
            //major.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            email.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            imageURL.textProperty().addListener((observable, oldValue, newValue) -> validateForm());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Disables ClearItem based on form validation
    private void validateClearItem() {
        // Check if all fields are empty or if nothing is selected
        boolean isFormFilled = !first_name.getText().isEmpty() ||
                !last_name.getText().isEmpty() ||
                !department.getText().isEmpty() ||
                majorComboBox.getValue() != null ||
                !email.getText().isEmpty() ||
                !imageURL.getText().isEmpty();

        // Enable or disable the ClearItem based on the form state or selection
        ClearItem.setDisable(!isFormFilled && tv.getSelectionModel().getSelectedItem() == null);
    }



    //validates the feilds
    private void validateForm() {
        // Validates First Name with (letters & spaces)
        boolean isFirstNameValid = isValidName(first_name.getText());

        // Validates Last Name with (letters & spaces)
        boolean isLastNameValid = isValidName(last_name.getText());

        // Validates Department with (letters, spaces, & hyphens)
        boolean isDepartmentValid = isValidDepartment(department.getText());

        // Validates Major ( letters, spaces, & hyphens)
        //boolean isMajorValid = isValidDepartment(major.getText());
        boolean isMajorValid = majorComboBox.getValue() != null; // Ensure a major is selected

        // Validates Email
        boolean isEmailValid = isValidEmail(email.getText());

        // Validates Image URL ( valid image URL format)
        boolean isImageURLValid = isValidImageURL(imageURL.getText());

        // Enables Add button if ALL fields are valid
        addBtn.setDisable(!(isFirstNameValid && isLastNameValid && isDepartmentValid &&
                isMajorValid && isEmailValid && isImageURLValid));
    }

    //added regex patterns to every feild
    // Regex to validate names (alphabet & spaces)
    private boolean isValidName(String name) {
        String nameRegex = "^[A-Za-z\\s]+$"; // letters & spaces
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // Regex to validate department & major ( letters, spaces, & hyphens)
    private boolean isValidDepartment(String field) {
        String departmentRegex = "^[A-Za-z\\s-]+$"; // Only letters, spaces, and hyphens
        Pattern pattern = Pattern.compile(departmentRegex);
        Matcher matcher = pattern.matcher(field);
        return matcher.matches();
    }

    // Regex to validate email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; // email validation
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Regex to validate image URL (only common image extensions like https://www.pinterest.com/pin/.jpg)
    private boolean isValidImageURL(String url) {
        String imageURLRegex = "^(http|https)://.*\\.(jpg|jpeg|png|gif|bmp|webp)$"; // URL with image extension
        Pattern pattern = Pattern.compile(imageURLRegex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }



    @FXML
    protected void addNewRecord() {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    majorComboBox.getValue().toString(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();

            //new code
            //status label to show success message
            try {
                statusLabel.setText("Record added successfully.");
            } catch (Exception e) { // exception
                statusLabel.setText("Record added successfully.");
            }

}

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        majorComboBox.setValue(null); // Clear the ComboBox selection
        //major.setText("");
        email.setText("");
        imageURL.setText("");
        addBtn.setDisable(true); // Disable add button after form clears
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                majorComboBox.getValue().toString(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);

        //new code
        try {
            // Updates status label to show success message
            statusLabel.setText("Record updated successfully.");
        } catch (Exception e) {  //exception
            statusLabel.setText("Record added successfully.");
        }

    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));

            //new code
            Task<Void> uploadTask = createUploadTask(file, progressBar);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            new Thread(uploadTask).start();
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        //major.setText(p.getMajor());
        majorComboBox.setValue(Major.valueOf(p.getMajor())); // Set ComboBox value
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Major, Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

    //new code for progress bar, uploading profile pic
    //part2
    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100);
                    }
                }

                return null;
            }
        };
    }
}