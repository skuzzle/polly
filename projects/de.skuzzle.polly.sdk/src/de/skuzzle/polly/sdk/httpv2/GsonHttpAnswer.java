package de.skuzzle.polly.sdk.httpv2;

import de.skuzzle.polly.http.api.answers.AbstractHttpAnswer;


public class GsonHttpAnswer extends AbstractHttpAnswer {
    
    private final Object value;

    public GsonHttpAnswer(int responseCode, Object value) {
        super(responseCode);
        this.addHeader("Content-Type", "charset=utf-8"); //$NON-NLS-1$ //$NON-NLS-2$
        this.value = value;
    }
    
    
    
    public Object getValue() {
        return this.value;
    }
}
