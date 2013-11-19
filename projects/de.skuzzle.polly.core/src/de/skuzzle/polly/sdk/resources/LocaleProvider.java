package de.skuzzle.polly.sdk.resources;

import java.util.Locale;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import de.skuzzle.polly.sdk.Configuration;


public class LocaleProvider {

    private final static Logger logger = Logger.getLogger(LocaleProvider.class.getName());
    

    public final static void initLocale(Configuration pollyCfg) throws ConfigurationException {
        final String localeName = pollyCfg.readString(Configuration.LOCALE);
        
        if (localeName == null) {
            throw new ConfigurationException("No locale set in polly configuration"); //$NON-NLS-1$
        }
        logger.info("Using locale: '" + localeName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        
        final Locale locale = new Locale(localeName);
        synchronized (Resources.MUTEX) {
            Resources.pollyLocale = locale;
        }
    }
}
