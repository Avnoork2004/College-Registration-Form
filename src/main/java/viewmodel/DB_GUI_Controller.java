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

import java.io.*;
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

    //added 2 menu items import/export csv files
    @FXML
    private MenuItem exportCSV;

    @FXML
    private MenuItem importCSV;




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

    //imports csv file
    @FXML
    protected void importCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                String line;
                int lineNumber = 0;

                // Skip the header
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 7) {
                        // Assuming the CSV columns match Person class fields
                        Person person = new Person(Integer.parseInt(data[0]), data[1], data[2], data[3], data[4], data[5], data[6]);
                        cnUtil.insertUser(person); // Insert into DB if needed
                        this.data.add(person); // Add to the observable list (table view)
                    } else {
                        statusLabel.setText("Invalid CSV format.");
                        break;
                    }
                    lineNumber++;
                }

                // Update UI with success message
                statusLabel.setText("Data imported successfully.");
            } catch (Exception e) {
                statusLabel.setText("Error importing data.");
                e.printStackTrace();
            }
        }
    }


    //exports csv file
    @FXML
    protected void exportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Writing the header to the CSV
                writer.append("ID,First Name,Last Name,Department,Major,Email,Image URL\n");

                // Writing each person data to the CSV
                for (Person person : data) {
                    writer.append(person.getId() + ",");
                    writer.append(person.getFirstName() + ",");
                    writer.append(person.getLastName() + ",");
                    writer.append(person.getDepartment() + ",");
                    writer.append(person.getMajor() + ",");
                    writer.append(person.getEmail() + ",");
                    writer.append(person.getImageURL() + "\n");
                }

                // Show success message
                statusLabel.setText("Data exported successfully.");
            } catch (Exception e) {
                statusLabel.setText("Error exporting data.");
                e.printStackTrace();
            }
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

        //logic added to clear the form if u click clear from the menu item
        first_name.clear();
        last_name.clear();
        department.clear();
        majorComboBox.setValue(null);
        email.clear();
        imageURL.clear();
        tv.getSelectionModel().clearSelection();
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



    //new code for progress bar, uploading profile pic
    @FXML
    protected void showImage() {
        // Open file chooser to select an image file
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            // Display the selected image in the ImageView
            img_view.setImage(new Image(file.toURI().toString()));

            // Create the upload task
            Task<Void> uploadTask = createUploadTask(file, progressBar);

            // Bind progress bar to the upload task's progress property
            progressBar.progressProperty().bind(uploadTask.progressProperty());

            // Start the upload task in a separate thread
            new Thread(uploadTask).start();
        }
    }

    //gets the url for the profile pic from the connection string and puts it into url text field
    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    // Generate a unique name for the image in Azure Blob Storage
                    String blobName = file.getName();

                    // Create a file input stream for the file to be uploaded
                    FileInputStream fileInputStream = new FileInputStream(file);
                    long fileSize = file.length();

                    // Create a BlobClient to upload the file
                    BlobClient blobClient = store.getContainerClient().getBlobClient(blobName);

                    // Set the file upload with progress reporting
                    blobClient.upload(fileInputStream, fileSize, true);

                    // Report progress (100% when done)
                    updateProgress(1, 1); // Update progress to 100%

                    // Get the URL of the uploaded image from Azure Blob Storage
                    String uploadedImageUrl = store.getContainerClient().getBlobClient(blobName).getBlobUrl();

                    // Set the uploaded image URL to the text field
                    javafx.application.Platform.runLater(() -> {
                        imageURL.setText(uploadedImageUrl);
                        statusLabel.setText("Image uploaded successfully.");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("Error uploading image.");
                    });
                }
                return null;
            }
        };
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
}