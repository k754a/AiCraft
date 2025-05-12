package net.kallens.aiminecraft;

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
        StringBuilder currentWord = new StringBuilder();

        // Read process output and handle it word by word
        try (InputStreamReader reader = new InputStreamReader(process.getInputStream())) {
            int c;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                output.append(ch);
                String cleanOutput = currentWord.toString().replaceAll("\u001B\\[[;?0-9]*[a-zA-Z]", "");
                cleanOutput = cleanOutput.replaceAll("[^\\x20-\\x7E]", "");
//                cleanOutput = cleanOutput.replaceAll("25", "");
                source.getPlayer().displayClientMessage(Component.literal("ollama: " + cleanOutput), true);




//deepseek-r1:7b
                // If we hit a space or punctuation, consider it a word boundary
                if (Character.isWhitespace(ch) ) {
                    if (currentWord.length() > 0) {
                        currentWord.setLength(0);
                    }
                } else {
                    // Add character to the current word
                    currentWord.append(ch);
                }
            }
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final cleaned-up output after processing

        String cleanOutput = output.toString().replaceAll("\u001B\\[[;?0-9]*[a-zA-Z]", "");


        cleanOutput = cleanOutput.substring(cleanOutput.indexOf("<") + 1);
        cleanOutput = cleanOutput.substring(cleanOutput.indexOf("-") + 1);


        //remove deepseak output
        cleanOutput = cleanOutput.replaceAll("(?s)think>.*?</think>", "");

        return cleanOutput.trim();

    }
}
