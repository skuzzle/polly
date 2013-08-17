package de.skuzzle.polly.core.internal.httpv2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;


public class GsonHttpAnswerHandler extends HttpAnswerHandler {

    @Override
    public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out)
            throws IOException {
        
        final GsonHttpAnswer gsonAnswer = (GsonHttpAnswer) answer;
        final Gson gson = new Gson();
        final Writer w = new OutputStreamWriter(new BufferedOutputStream(out));
        w.write(gson.toJson(gsonAnswer.getValue()));
        w.flush();
    }

}
