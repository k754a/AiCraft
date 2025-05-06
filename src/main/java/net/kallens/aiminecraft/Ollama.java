package net.kallens.aiminecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.*;

public class Ollama {

    public static String ollama(String prompt, String modelName, CommandSourceStack source) throws IOException {
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


        cleanOutput = cleanOutput.substring(cleanOutput.indexOf("<") + 1);
        cleanOutput = cleanOutput.substring(cleanOutput.indexOf("-") + 1);


        //remove deepseak output
        cleanOutput = cleanOutput.replaceAll("(?s)think>.*?</think>", "");

        return cleanOutput.trim();


    }
}
