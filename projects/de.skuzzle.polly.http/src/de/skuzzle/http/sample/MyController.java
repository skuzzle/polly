package de.skuzzle.http.sample;

import java.io.FileNotFoundException;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.Post;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


public class MyController extends Controller {
    

    @Override
    protected Controller createInstance() {
        return new MyController();
    }

    
    @Post("/login")
    public HttpAnswer login(
        @Param("name") String name, 
        @Param("pw") String password) throws FileNotFoundException {
        
        this.getEvent().getSession().attach("user", name);
        return HttpAnswers.createTemplateAnswer("index.tmpl", 
            "header", "Hello",
            "title", "Polly Webinterface v2",
            "user", name,
            "sessionId", this.getEvent().getSession().getId());
    }
    
    
    @Get("/")
    public HttpAnswer index() throws FileNotFoundException {
        return HttpAnswers.createTemplateAnswer("index.tmpl", 
            "header", "Hello",
            "title", "Polly Webinterface v2",
            "sessionId", this.getEvent().getSession().getId());
    }
}
