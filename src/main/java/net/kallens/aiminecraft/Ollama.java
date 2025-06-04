package net.kallens.aiminecraft;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.*;

import static net.kallens.aiminecraft.ClientEvents.createfolders;

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

        float animation = 0;
        // Read process output and handle it word by word
        try (InputStreamReader reader = new InputStreamReader(process.getInputStream())) {
            int c;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                output.append(ch);
                String cleanOutput = currentWord.toString().replaceAll("\u001B\\[[;?0-9]*[a-zA-Z]", "");
                cleanOutput = cleanOutput.replaceAll("[^\\x20-\\x7E]", "");
                cleanOutput = cleanOutput.replaceAll(".2026", "Thinking");
                cleanOutput = cleanOutput.replaceAll("[\\[]","");;


                boolean containsWord = cleanOutput.contains("Thinking");

                if(containsWord)
                {//⡀⡄⡆⡇⣇⣧⣷⣿
                    if(animation <= 28 && animation > 26)
                    {
                        cleanOutput = "Thinking ⠀";
                        animation = 0;

                    }
                    if(animation <= 26 && animation > 24)
                    {
                        cleanOutput = "Thinking ⠀";


                    }
                    if(animation <= 24 && animation > 22)
                    {
                        cleanOutput = "Thinking ⡀";

                    }
                    if(animation <= 22 && animation > 20)
                    {
                        cleanOutput = "Thinking ⡄";
                    }
                    if(animation <= 20 && animation > 18)
                    {
                        cleanOutput = "Thinking ⡇";
                    }
                    if(animation <= 18 && animation > 16)
                    {
                        cleanOutput = "Thinking ⣇";

                    }
                    if(animation <= 16 && animation > 14)
                    {
                       // animation = 0;
                        cleanOutput = "Thinking ⣧";
                    }
                    if(animation <= 14 && animation > 12)
                    {
                      //  animation = 0;
                        cleanOutput = "Thinking ⣿";
                    }
                    if(animation <= 12 && animation > 10)
                    {
                       // animation = 0;
                        cleanOutput = "Thinking ⣷";
                    }
                    if(animation <= 10 && animation > 8)
                    {
                      //  animation = 0;
                        cleanOutput = "Thinking ⣧";
                    }
                    if(animation <= 8 && animation > 6)
                    {
                        cleanOutput = "Thinking ⣇";

                    }
                    if(animation <= 6 && animation > 4)
                    {
                        cleanOutput = "Thinking ⡇";
                    }
                    if(animation <= 4 && animation > 2)
                    {
                        cleanOutput = "Thinking ⡄";
                    }
                    if(animation <= 2 && animation > 0)
                    {
                        cleanOutput = "Thinking ⡀";

                    }
                    animation++;
                }


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
