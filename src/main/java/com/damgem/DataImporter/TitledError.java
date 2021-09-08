package com.damgem.DataImporter;

public class TitledError extends Exception {
    public String title, description;

    private static String cleanString(String input) {
        // Although IntelliJ marks this code as unnecessary JavaFX will not display these symbols correctly without
        // replacing them with their unicode representations
        return input
               .replace("ä", "\u00E4")
               .replace("Ä", "\u00C4")
               .replace("ö", "\u00F6")
               .replace("Ö", "\u00D6")
               .replace("ü", "\u00FC")
               .replace("Ü", "\u00DC")
               .replace("ß", "\u00DF");
    }

    public TitledError() {}
    public TitledError(String title, String msg) {
        super(title + ": " + msg);
        this.title = cleanString(title);
        this.description = cleanString(msg);
    }
}