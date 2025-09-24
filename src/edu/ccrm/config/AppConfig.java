package edu.ccrm.config;

import java.nio.file.Paths;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class AppConfig {
    private static volatile AppConfig instance;
    private static final Object lock = new Object();

    private String dataDirectory;
    private int maxCreditsPerSemester;
    private String applicationName;
    private String version;

    private AppConfig() {
        loadConfiguration();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private void loadConfiguration() {

        this.dataDirectory = System.getProperty("user.home") + "/ccrm_data";
        this.maxCreditsPerSemester = 20;
        this.applicationName = "Campus Course & Records Manager";
        this.version = "1.0.0";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);

                this.dataDirectory = props.getProperty("data.directory", this.dataDirectory);
                this.maxCreditsPerSemester = Integer.parseInt(
                        props.getProperty("max.credits.per.semester", "20"));
                this.applicationName = props.getProperty("application.name", this.applicationName);
                this.version = props.getProperty("application.version", this.version);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: Could not load configuration file. Using defaults.");
        }

        try {
            java.nio.file.Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    public String getDataDirectory() { return dataDirectory; }
    public int getMaxCreditsPerSemester() { return maxCreditsPerSemester; }
    public String getApplicationName() { return applicationName; }
    public String getVersion() { return version; }


    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void setMaxCreditsPerSemester(int maxCredits) {
        this.maxCreditsPerSemester = maxCredits;
    }

    @Override
    public String toString() {
        return String.format("AppConfig[app=%s v%s, dataDir=%s, maxCredits=%d]",
                applicationName, version, dataDirectory, maxCreditsPerSemester);
    }
}
