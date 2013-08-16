package de.skuzzle.http.sample;

import java.io.FileNotFoundException;
import java.util.List;

import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.Get;
import de.skuzzle.polly.http.api.Param;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;


public class MyController extends Controller {
    

    @Override
    protected Controller createInstance() {
        return new MyController();
    }

    
    
    @Get("/testIt")
    public HttpAnswer test(
        @Param("name") String name, 
        @Param(value = "list", typeHint = String.class) List<String> list) {
     
        return HttpAnswers.createStringAnswer("Name was " + name + ", value: " + list.toString() + 
            ", session id:" + this.getEvent().getSession().getId());
    }
    
    
    
    @Get("/sum")
    public HttpAnswer sum(
        @Param(value = "values", typeHint = Integer.class) List<Integer> numbers) {
        
        int sum = 0;
        for (Integer i : numbers) {
            sum += i;
        }
        return HttpAnswers.createStringAnswer("Sum is: " + sum);
    }
    
    
    
    @Get("/")
    public HttpAnswer index() throws FileNotFoundException {
        return HttpAnswers.createTemplateAnswer("index.html", "header", "Hello");
    }
}
