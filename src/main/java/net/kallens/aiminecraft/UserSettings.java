package net.kallens.aiminecraft;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class UserSettings {
    private static final String SETTINGS_FILE = "settings.properties";

    private static UserSettings instance;

    public String modelName = "";
    public int chunkRadiusY = 32;
    public int maxBlocks = 6000;
    public boolean streamOutput = true;
    public boolean autoExecuteCommands = true;

    public static UserSettings get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static void reload() {
        instance = load();
    }

    private static UserSettings load() {
        UserSettings settings = new UserSettings();
        File file = getSettingsFile();
        if (!file.exists()) {
            settings.modelName = readLegacyToken();
            return settings;
        }

        Properties props = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            props.load(reader);
            settings.modelName = props.getProperty("modelName", readLegacyToken());
            settings.chunkRadiusY = parseInt(props.getProperty("chunkRadiusY"), 32);
            settings.maxBlocks = parseInt(props.getProperty("maxBlocks"), 6000);
            settings.streamOutput = parseBool(props.getProperty("streamOutput"), true);
            settings.autoExecuteCommands = parseBool(props.getProperty("autoExecuteCommands"), true);
        } catch (IOException e) {
            settings.modelName = readLegacyToken();
        }
        return settings;
    }

    public void save() throws IOException {
        File file = getSettingsFile();
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create settings directory: " + dir.getAbsolutePath());
            }
        }

        Properties props = new Properties();
        props.setProperty("modelName", modelName == null ? "" : modelName.trim());
        props.setProperty("chunkRadiusY", Integer.toString(chunkRadiusY));
        props.setProperty("maxBlocks", Integer.toString(maxBlocks));
        props.setProperty("streamOutput", Boolean.toString(streamOutput));
        props.setProperty("autoExecuteCommands", Boolean.toString(autoExecuteCommands));

        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "AI Minecraft user settings");
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static boolean parseBool(String value, boolean fallback) {
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static File getSettingsFile() {
        File dir = ClientEvents.promptsFolderPath;
        if (dir == null) {
            File gameDir = Minecraft.getInstance().gameDirectory;
            File parent = gameDir.getParentFile();
            if (parent == null) {
                parent = gameDir;
            }
            dir = new File(parent, "prompts");
        }
        return new File(dir, SETTINGS_FILE);
    }

    private static String readLegacyToken() {
        File dir = ClientEvents.promptsFolderPath;
        if (dir == null) {
            String roamingPath = System.getenv("APPDATA");
            if (roamingPath == null) {
                return "";
            }
            dir = new File(roamingPath, "prompts");
        }
        File file = new File(dir, "token.txt");
        if (!file.exists()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            return "";
        }
        return builder.toString().trim();
    }
}
