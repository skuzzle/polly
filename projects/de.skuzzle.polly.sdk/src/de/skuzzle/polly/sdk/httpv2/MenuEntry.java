package de.skuzzle.polly.sdk.httpv2;

public class MenuEntry {

    private final String name;
    private final String url;



    public MenuEntry(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }



    public String getName() {
        return this.name;
    }



    public String getUrl() {
        return this.url;
    }

}
