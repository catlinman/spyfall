package com.catlinman.spyfall;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

// Static language management class.
public class Locale {
    // These variables are relevant to reading the data file.
    private static final String SEPARATOR      = ",";
    private static final String LOCALEFILENAME = "ui.csv";

    private static String lang;                  // Game language static string.
    private static HashMap<String, String> data; // String map containing localization data.

    // Constant application supported languages array.
    private static final String[] LANGUAGES = {
        "en",
        "de"
    };

    // Wrapper constructor.
    Locale(String l) {
        if (!l.equals(lang))
            initialize(l);
    }

    /**
     * Main initialization method of the Locale class. Loads data from the supplied
     * language key and stores it for further use. Acts as a wrapper for easier
     * interaction with the stored data.
     * @param String l Language key to use for loading of data. If empty defaults to the first available key.
     */
    public static void initialize(String l) {
        // Make sure that the supplied language is supported.
        boolean langfound = false;

        // Make sure the language key exists.
        if(l != null) {
            for (String s : LANGUAGES)
                if (l.equals(s)) {
                    langfound = true;
                    break;
                }
        }

        // Set to the first language key if the supplied key doesn't exist.
        if (langfound == false) l = LANGUAGES[0];

        if (Debug.DATA) System.out.println("Data: Loading locale data file for the language key of " + l + ".");

        // Reserve variables for our handling of the locale file location.
        URI localeURI; String localePath;

        // Fetch the resource location from the localized input file. We originally receive a URL which we convert to a URI.
        try {
            localeURI = Program.class.getClassLoader().getResource(String.format("%s_%s", l, LOCALEFILENAME)).toURI();
            localePath = Paths.get(localeURI).toString();

        } catch(URISyntaxException e) {
            System.out.println(e);

            return;
        }

        String content = ""; // Used as temporary storage for the input data.

        // Read the file contents with the right system encoding and combine it to a single string.
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(localePath));
            content = new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {}

        // Split each line into a new string.
        String[] lines = content.split("\n");

        // Initialize a new HashMap.
        data = new HashMap<String, String>();

        if (Debug.DATA) System.out.println("Data: " + l.toUpperCase() + " is loading.");

        // Iterate over each line. Split each at the separator. Clean up the string and insert it into the location data array.
        for (int i = 0; i < lines.length - 1; i++) {
            String[] fields = lines[i + 1].split(SEPARATOR, 2); // Make sure to skip the first header line.

            try {
                data.put(fields[0], fields[1].trim());

                if (Debug.DATA) System.out.println(i + ". " + fields[0] + " = " + fields[1].trim());

            } catch (IndexOutOfBoundsException e) {
                if (Debug.DATA) System.out.println(
                    "Data: Error in " + l.toUpperCase() + " UI CSV line " + i
                    + " does not contain the right amount of fields (expected two fields)."
                );
            }
        }

        // Set the static language key.
        lang = l;
    } /* loadData */

    /**
     * Gets a data value from the data hashmap using an input key.
     * @param String key The CSV key to lookup with.
     * @return data string.
     */
    public static String get(String key) {
        // Make sure that missing locale default to the key for easier debugging.
        String out = data.get(key);

        return out != null ? out : key;
    }

    /**
     * Returns the language key of the current data data.
     * @return Current language key.
     */
    public static String getCurrent() {
        return lang;
    }

    /**
     * Fetches the final language key array containing supported language keys.
     * @return Array of supported language keys.
     */
    public static String[] getSupported() {
        return LANGUAGES;
    }

}
