# **College Registration Form**
## **Overview**

The College Registration Form is a desktop application designed to manage college student registrations with a focus on user experience and data integrity. It includes features such as field validation, UI state management, profile picture uploads, menu actions, and CSV data import/export. The application is implemented using JavaFX and integrates thread safety, user preferences, and database interactions.


## **Features**
### **1. UI State Management**

- **Edit Button:** Disabled unless a record is selected from the table view.
- **Delete Button:** Disabled unless a record is selected from the table view.
- **Add Button:** Enabled only if all form fields contain valid information.
- **Menu Items:** Grayed out unless the required selection or conditions are met.

### **2. Form Enhancement**

- **Advanced Field Validation:** Regex patterns ensure the integrity of all user input fields.
- **Major Field Dropdown:** Replaced the text input with a dropdown menu of predefined values (e.g., CS, CPIS, English), implemented using an enum.

### **3. User Feedback**

- **Status Bar:** Displays a status message at the bottom of the window to notify users of successful actions like data addition or updates.

### **4. Menu Items**

- **Import CSV File:** Allows importing registration data from a CSV file.
- **Export CSV File:** Enables exporting current data to a CSV file.

### **5. Thread Safety**

- **UserSession Class:** Redesigned to be thread-safe, ensuring consistent performance across multi-threaded environments.

### **6. User Session and Preferences**

- **Sign-up Page:**
  - Enables user account creation.
  - Stores username and password securely in a preference file for future use.
  - Refactored the UserSession class to support user preferences.
 
### **7. Improved User Interface**

- **Light and Dark Modes:**
  - Styled the application for optimal appearance in both themes using CSS.
  - Status messages adapt seamlessly to the selected theme.
- **Profile Picture Upload Feedback:**
  - Added a loading bar under the profile picture upload section to indicate progress.
- **Menu Bar Keyboard Shortcuts:**
  - Users can access menu actions using keyboard shortcuts for better navigation.
 
### **8. Database Interaction Improvement**

- **Account Creation Feedback:**
  - Displays a confirmation message upon successful account creation.
  - Automatically populates the profile picture URL in the text field upon upload.
 
## **Getting Started**

### **The Application**
- Java Development Kit (JDK 20 or higher).
- JavaFX SDK.
- An (IDE), used IntelliJ IDEA.

## **Usage**

### **Keyboard Shortcuts**
- **Menu Actions:** Use predefined keyboard shortcuts for quick navigation.

### **Profile Picture Upload**
- The loading bar provides real-time feedback during the upload process.

### **CSV Import/Export**
- Import or export data to and from CSV files via the menu.

## **Technologies Used**
- **JavaFX:** For the user interface.
- **Regex:** Advanced input validation.
- **JDBC:** Database interaction.
- **CSS:** UI theming and styling.
- **Threading:** Ensuring thread safety.

