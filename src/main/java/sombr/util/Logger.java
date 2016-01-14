package sombr.util;

public class Logger {
    public static final boolean SHOW_DEBUG = true;

    public static void DEBUG( String s ) {
        if ( SHOW_DEBUG )
            System.out.println(s);
    }
}
