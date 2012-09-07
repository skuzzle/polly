package org.jibble.pircbot;

import java.io.BufferedWriter;

/**
 * A Thread which is responsible for sending messages to the IRC server.
 * Messages are obtained from the outgoing message queue and sent
 * immediately if possible.  If there is a flood of messages, then to
 * avoid getting kicked from a channel, we put a small delay between
 * each one.
 *
 * @author PircBot-PPF project
 * @version 1.0.0
 */
public class OutputThread extends Thread {
    
    
    /**
     * Constructs an OutputThread for the underlying PircBot.  All messages
     * sent to the IRC server are sent by this OutputThread to avoid hammering
     * the server.  Messages are sent immediately if possible.  If there are
     * multiple messages queued, then there is a delay imposed.
     * 
     * @param bot The underlying PircBot instance.
     * @param outQueue The Queue from which we will obtain our messages.
     */
    OutputThread(PircBot bot, Queue outQueue) {
        _bot = bot;
        _outQueue = outQueue;
        this.setName(this.getClass() + "-Thread");
    }
    
    
    /**
     * A static method to write a line to a BufferedOutputStream and then pass
     * the line to the log method of the supplied PircBot instance.
     * 
     * @param bot The underlying PircBot instance.
     * @param bwriter The BufferedOutputStream to write to.
     * @param line The line to be written. "\r\n" is appended to the end.
     */
    static void sendRawLine(PircBot bot, BufferedWriter bwriter, String line) {
        if (line.length() > bot.getMaxLineLength() - 2) {
            line = line.substring(0, bot.getMaxLineLength() - 2);
        }
        synchronized(bwriter) {
            try {
                bwriter.write(line + "\r\n");
                bwriter.flush();
                bot.log(">>>" + line);
            }
            catch (Exception e) {
                // Silent response - just lose the line.
            }
        }
    }
    
    
    /**
     * This method starts the Thread consuming from the outgoing message
     * Queue and sending lines to the server.
     */
    public void run() {
        try {
            boolean running = true;
            while (running) {
                // Small delay to prevent spamming of the channel
                Thread.sleep(_bot.getMessageDelay());
                
                String line = (String) _outQueue.next();
                if (line != null) {
                    _bot.sendRawLine(line);
                }
                else {
                    running = false;
                }
            }
        }
        catch (InterruptedException e) {
            // Just let the method return naturally...
        }
    }
    
    private PircBot _bot = null;
    private Queue _outQueue = null;
    
}