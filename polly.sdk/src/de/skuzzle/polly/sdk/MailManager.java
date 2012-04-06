package de.skuzzle.polly.sdk;

import de.skuzzle.polly.sdk.exceptions.EMailException;


/**
 * This class enables plugins to send a simple mail to a given address.
 * 
 * @author Simon
 * @since 0.9
 */
public interface MailManager {

    /**
     * Sends a mail to the given recipient using pollys default mail settings given in
     * the mail.cfg.
     * 
     * @param recipient The recipient of the mail.
     * @param subject The subject of the mail.
     * @param message The message.
     * @throws EMailException If sending the mail fails for any reason.
     */
    public abstract void sendMail(String recipient, String subject, String message) 
            throws EMailException;
}