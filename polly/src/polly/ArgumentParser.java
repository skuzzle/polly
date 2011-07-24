package polly;


public class ArgumentParser {

    public static void parse(String[] args, PollyConfiguration config) 
            throws ParameterException {
        int i = 0;
        while (i < args.length) {
            String param = args[i];
            ++i;
            if (param.equals("-s") || param.equals("-server")) {
                checkParam(args, i, 1);
                config.setServer(args[i]);
            } else if (param.equals("-nick") || param.equals("-n")) {
                checkParam(args, i, 1);
                config.setNickName(args[i]);
                ++i;
            } else if (param.equals("-ident") || param.equals("-i")) {
                checkParam(args, i, 1);
                config.setIdent(args[i]);
                ++i;
            } else if (param.equals("-port") || param.equals("-p")) {
                checkParam(args, i, 1);
                config.setPort(Integer.parseInt(args[i]));
                ++i;
            } else if (param.equals("-irclog") || param.equals("-il")) {
                checkParam(args, i, 1);
                if (!args[i].equals("on") && !args[i].equals("off")) {
                    throw new ParameterException("Ungültiger Parameterwert: " + args[i]);
                }
                config.setIrcLogging(args[i].equals("on"));
                ++i;
            } else if (param.equals("-join") || param.equals("-j")) {
                checkParam(args, i, 1);
                config.setChannels(args[i]);
                ++i;
            } else if (param.equals("-telnet")) {
                checkParam(args, i, 1);
                if (!args[i].equals("on") && !args[i].equals("off")) {
                    throw new ParameterException("Ungültiger Parameterwert: " + args[i]);
                }
                config.setEnableTelnet(args[i].equals("on"));
                ++i;
            } else if (param.equals("-telnetport")) {
                checkParam(args, i, 1);
                config.setTelnetPort(Integer.parseInt(args[i]));
                ++i;
            } else if (param.equals("-help") || param.equals("-?")) {
                throw new ParameterException("Showing Polly Commandline Help");
            } else {
                throw new ParameterException("Unbekannter Befehl " + param);
            }
        }
    }
    
    
    
    private static void checkParam(String[] args, int current, int expected) 
            throws ParameterException {
        if (args.length < current + expected) {
            throw new ParameterException();
        }
    }
    
}
