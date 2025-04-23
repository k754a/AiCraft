package net.kallens.aiminecraft;

import java.io.*;

public class Ollama {

    public static String ollama(String prompt, String modelName) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("ollama", "run", modelName);
        builder.redirectErrorStream(true); // merge stdout + stderr for max chaos

        Process process = builder.start();

        // shove the prompt into the model like it owes you money
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(prompt);
            writer.newLine();
            writer.flush();
        }

        // catch whatever the model screams back
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // wait for it to die peacefully
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace(); // lol chaos mode
        }

        return output.toString().trim();
    }
}
