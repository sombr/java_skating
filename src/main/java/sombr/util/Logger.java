package sombr.util;

public class Logger {
    public static final boolean SHOW_DEBUG = true;

    public static void DEBUG( Object... ss ) {
        if ( SHOW_DEBUG ) {
            StringBuilder sb = new StringBuilder(":");

            for ( Object o : ss ) {
                sb.append(" ");
                sb.append(o);
            }

            System.out.println(sb.toString());
        }
    }
}
