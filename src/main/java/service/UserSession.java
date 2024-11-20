package service;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {

    private static UserSession instance;

    private String userName;

    private String password;
    private String privileges;

    // Private initialize user session
    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }

    // Thread safe get instance of UserSession
    public static synchronized UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            instance = new UserSession(userName, password, privileges);
        }
        return instance;
    }

    // Overloaded method with default privileges
    public static synchronized UserSession getInstance(String userName, String password) {
        if (instance == null) {
            instance = new UserSession(userName, password, "NONE");
        }
        return instance;
    }

    // Thread safe userName get
    public synchronized String getUserName() {
        return this.userName;
    }

    // Thread safe password get
    public synchronized String getPassword() {
        return this.password;
    }

    // Thread safe privileges get
    public synchronized String getPrivileges() {
        return this.privileges;
    }

    // Thread safe clean user session
    public synchronized void cleanUserSession() {
        this.userName = ""; // or null
        this.password = "";
        this.privileges = ""; // or null
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges='" + this.privileges + '\'' +
                '}';
    }
}
