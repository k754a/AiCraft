package net.kallens.aiminecraft;

import java.io.*;

public class Ollama {

    public static String ollama(String prompt, String modelName) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("ollama", "run", modelName);
        builder.redirectErrorStream(true);

        Process process = builder.start();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(prompt);
            writer.newLine();
            writer.flush();
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String cleanOutput = output.toString().replaceAll("\u001B\\[[;?0-9]*[a-zA-Z]", "");
        return cleanOutput.trim();


    }
}
