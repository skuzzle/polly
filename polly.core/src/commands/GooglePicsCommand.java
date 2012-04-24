package commands;

import de.skuzzle.polly.sdk.MyPolly;


public class GooglePicsCommand extends SearchEngineCommand {

    public GooglePicsCommand(MyPolly myPolly) {
        super(myPolly, "pix");
    }

    
    @Override
    protected String getSearchLink(String key) {
        key = key.replace(" ", "+");
        return "https://www.google.com/search?q=" + key + "&tbm=isch";
    }

}
