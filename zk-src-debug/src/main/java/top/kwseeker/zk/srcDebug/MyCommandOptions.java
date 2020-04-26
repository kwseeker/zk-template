package top.kwseeker.zk.srcDebug;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCommandOptions {

    private Map<String,String> options = new HashMap<String,String>();
    private List<String> cmdArgs = null;
    private String command = null;
    public static final Pattern ARGS_PATTERN = Pattern.compile("\\s*([^\"\']\\S*|\"[^\"]*\"|'[^']*')\\s*");
    public static final Pattern QUOTED_PATTERN = Pattern.compile("^([\'\"])(.*)(\\1)$");

    public MyCommandOptions() {
        options.put("server", "localhost:2181");
        options.put("timeout", "30000");
    }

    public String getOption(String opt) {
        return options.get(opt);
    }

    public String getCommand( ) {
        return command;
    }

    public String getCmdArgument( int index ) {
        return cmdArgs.get(index);
    }

    public int getNumArguments( ) {
        return cmdArgs.size();
    }

    public String[] getArgArray() {
        return cmdArgs.toArray(new String[0]);
    }

    /**
     * Parses a command line that may contain one or more flags
     * before an optional command string
     * @param args command line arguments
     * @return true if parsing succeeded, false otherwise.
     */
    public boolean parseOptions(String[] args) {
        List<String> argList = Arrays.asList(args);
        Iterator<String> it = argList.iterator();

        while (it.hasNext()) {
            String opt = it.next();
            try {
                if (opt.equals("-server")) {
                    options.put("server", it.next());
                } else if (opt.equals("-timeout")) {
                    options.put("timeout", it.next());
                } else if (opt.equals("-r")) {
                    options.put("readonly", "true");
                }
            } catch (NoSuchElementException e){
                System.err.println("Error: no argument found for option "
                        + opt);
                return false;
            }

            if (!opt.startsWith("-")) {
                command = opt;
                cmdArgs = new ArrayList<String>( );
                cmdArgs.add( command );
                while (it.hasNext()) {
                    cmdArgs.add(it.next());
                }
                return true;
            }
        }
        return true;
    }

    /**
     * Breaks a string into command + arguments.
     * @param cmdstring string of form "cmd arg1 arg2..etc"
     * @return true if parsing succeeded.
     */
    public boolean parseCommand( String cmdstring ) {
        Matcher matcher = ARGS_PATTERN.matcher(cmdstring);

        List<String> args = new LinkedList<String>();
        while (matcher.find()) {
            String value = matcher.group(1);
            if (QUOTED_PATTERN.matcher(value).matches()) {
                // Strip off the surrounding quotes
                value = value.substring(1, value.length() - 1);
            }
            args.add(value);
        }
        if (args.isEmpty()){
            return false;
        }
        command = args.get(0);
        cmdArgs = args;
        return true;
    }
}

