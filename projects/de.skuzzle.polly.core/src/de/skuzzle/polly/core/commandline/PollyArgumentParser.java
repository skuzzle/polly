package de.skuzzle.polly.core.commandline;

import de.skuzzle.polly.sdk.Configuration;


public class PollyArgumentParser extends AbstractArgumentParser {

    private Configuration config;
    
    
    public PollyArgumentParser(Configuration config) {
        this.config = config;
        this.createArguments();
    }
    
    
    
    private void createArguments() {
        this.addArgument(
            new BooleanConfigArgument("-update", this.config,  //$NON-NLS-1$
                Configuration.AUTO_UPDATE) {
                    @Override
                    public boolean filter() {
                        return true;
                    }
            });
        this.addArgument(
            new BooleanConfigArgument("-u", this.config,  //$NON-NLS-1$
                Configuration.AUTO_UPDATE) {
                    @Override
                    public boolean filter() {
                        return true;
                    }
            });
        
        this.addArgument(new ConfigArgument("-server", this.config,  //$NON-NLS-1$
            Configuration.SERVER));
        this.addArgument(new ConfigArgument("-s", this.config,  //$NON-NLS-1$
            Configuration.SERVER));
        
        this.addArgument(new ConfigArgument("-nick", this.config,  //$NON-NLS-1$
            Configuration.NICKNAME));
        this.addArgument(new ConfigArgument("-n", this.config,  //$NON-NLS-1$
            Configuration.NICKNAME));

        this.addArgument(new ConfigArgument("-ident", this.config,  //$NON-NLS-1$
            Configuration.IDENT));
        this.addArgument(new ConfigArgument("-i", this.config,  //$NON-NLS-1$
            Configuration.IDENT));

        this.addArgument(new IntegerConfigArgument("-ports", this.config,  //$NON-NLS-1$
            Configuration.PORT));
        this.addArgument(new IntegerConfigArgument("-p", this.config,  //$NON-NLS-1$
            Configuration.PORT));

        this.addArgument(new BooleanConfigArgument("-irclog", this.config,  //$NON-NLS-1$
            Configuration.IRC_LOGGING));
        this.addArgument(new BooleanConfigArgument("-il", this.config,  //$NON-NLS-1$
            Configuration.IRC_LOGGING));

        this.addArgument(new ConfigArgument("-join", this.config,  //$NON-NLS-1$
            Configuration.CHANNELS));
        this.addArgument(new ConfigArgument("-j", this.config,  //$NON-NLS-1$
            Configuration.CHANNELS));
        
        this.addArgument(new BooleanConfigArgument("-telnet", this.config,  //$NON-NLS-1$
            Configuration.ENABLE_TELNET));
        this.addArgument(new IntegerConfigArgument("-telnetport", this.config,  //$NON-NLS-1$
            Configuration.TELNET_PORT));

        this.addArgument(new ConfigArgument("-help", this.config, "") { //$NON-NLS-1$ //$NON-NLS-2$
            @Override
            public int getParameters() {
                return 0;
            }
            @Override
            public void execute(String... parameter) throws ParameterException {
                throw new ParameterException(MSG.showingHelp); 
            } 
        });
        
        this.addArgument(new ConfigArgument("-?", this.config, "") { //$NON-NLS-1$ //$NON-NLS-2$
            @Override
            public int getParameters() {
                return 0;
            }
            @Override
            public void execute(String... parameter) throws ParameterException {
                throw new ParameterException(MSG.showingHelp); 
            } 
        });
        
        this.addArgument(new ReturnInfoArgument());
    }
}
