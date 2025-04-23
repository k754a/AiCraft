package net.kallens.aiminecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Ollama {

    public static String ollama(String Prompt, String ModelName) throws IOException{
        String modelName = ModelName;
        String prompttext = Prompt;


        URL url = new URL("http://localhost:11434/api/generate");

        HttpURLConnection comm = (HttpURLConnection) url.openConnection();

        comm.setRequestMethod("POST");
        comm.setRequestProperty("Content-Type", "application/json; utf-8");
        comm.setRequestProperty("Accept", "application/json");
        comm.setDoOutput(true);

        String jsonInputString = String.format(
                "{\"model\": \"%s\", \"prompt\":\"%s\", \"stream\": false}", modelName, prompttext
        );

        try(OutputStream os = comm.getOutputStream())
        {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = comm.getResponseCode();
        System.out.println("Response Code: "+ code);

        BufferedReader in = new BufferedReader(new InputStreamReader(comm.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
        {
            response.append(line);
        }
        in.close();

        System.out.println("Response Body: " + response.toString());

        JsonObject jsonResponse = JsonParser.parseString(response.toString())
                .getAsJsonObject();
        String responsetext = jsonResponse
                .get("response")
                .getAsString();


        comm.disconnect();

        return responsetext;





    }
}
