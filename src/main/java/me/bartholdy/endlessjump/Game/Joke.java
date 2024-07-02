package me.bartholdy.endlessjump.Game;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/*
        The Joke API
        https://github.com/15Dkatz/official_joke_api
 */
@Data
@AllArgsConstructor
public class Joke {

    String type;
    String setup;
    String punchline;
    String id;

    private static final ComponentLogger LOGGER = ComponentLogger.logger(Joke.class);

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static Joke getRandomJoke() {
        String json = null;
        try {
            json = readUrl("https://official-joke-api.appspot.com/jokes/programming/random");
        } catch (Exception e) {
            LOGGER.error("Could not read url: No joke available!");
            return new Joke(
                    "demo",
                    "You know what?",
                    "Ligma, the joke website just went down.",
                    "0");
        }

        Gson gson = new Gson();
        return gson.fromJson(gson.fromJson(json, JsonArray.class).get(0), Joke.class);
    }
}
