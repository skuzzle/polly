package de.skuzzle.polly.sdk.httpv2;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;


public interface HttpHook {

    public HttpAnswer run(HttpEvent e, HttpAnswer answer);
}
