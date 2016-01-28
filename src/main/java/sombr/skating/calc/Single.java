package sombr.skating.calc;

import static sombr.util.Logger.DEBUG;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.List;
import java.util.Collection;

import java.lang.IllegalArgumentException;

import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class Single {
    class PartialComputationResult implements Comparable<PartialComputationResult> {
        public final int count;
        public final int sum;

        PartialComputationResult( int count, int sum ) {
            this.count = count;
            this.sum = sum;
        }

        PartialComputationResult( Collection<Integer> marks ) {
            this( marks.size(), marks.stream().reduce( (acc,x) -> acc+x ).orElse(0) );
        }

        public String toString() {
            return count + " (" + sum + ")";
        }

        @Override
        public boolean equals( Object o ) {
            if ( o == this )
                return true;
            if (! (o instanceof PartialComputationResult))
                return false;

            PartialComputationResult that = (PartialComputationResult) o;
            return (this.count == that.count) && (this.sum == that.sum);
        }

        @Override
        public int hashCode() {
            return this.count*31 + this.sum;
        }

        @Override
        public int compareTo( PartialComputationResult that ) {
            if ( that == null )
                throw new IllegalArgumentException("Can't compare to NULL");
            return this.count == that.count ?
                       this.sum == that.sum ? 0 : this.sum - that.sum :
                       that.count - this.count;
        }
    }

    private final ArrayTable<String,String,Integer> marks;
    private final ArrayTable<String,String,PartialComputationResult> computation;

    private final int MAX_POSSIBLE_PLACE;
    private final int JUDGES_COUNT;

    private static final Pattern rlines = Pattern.compile("\\s*\n\\s*");
    private static final Pattern rvals  = Pattern.compile("\\s*,\\s*");
    private static final Pattern rnum   = Pattern.compile("^\\s*(\\d+)\\s*$");

    private Single( ArrayTable<String,String,Integer> marks ) {
        this.marks = marks;
        this.MAX_POSSIBLE_PLACE = marks.rowKeyList().size();
        this.JUDGES_COUNT = marks.columnKeyList().size();

        this.computation = ArrayTable.create(
            marks.rowKeyList(),
            Stream.concat(
                IntStream.rangeClosed(1, MAX_POSSIBLE_PLACE).mapToObj( x -> "1-"+x ),
                Stream.of("RESULT")
            ).collect(Collectors.toList())
        );

    }

    public Map<String, Integer> solve() {
        computation.eraseAll(); // clear previous computation
        TreeMap<Integer, Set<String>> res = new TreeMap<Integer, Set<String>>();

        IntStream.rangeClosed( 1, MAX_POSSIBLE_PLACE ).forEach( col -> {
            DEBUG("Checking column:", col);
            Multimap<PartialComputationResult, String> inMajority = TreeMultimap.create();

            for ( String competitor : marks.rowKeyList() ) {
                PartialComputationResult pres = new PartialComputationResult(
                    marks.row( competitor ).values().stream().filter( x -> x <= col ).collect(Collectors.toList())
                );

                computation.put( competitor, "1-"+col, pres );

                if ( pres.count > JUDGES_COUNT / 2 ) // we have majority
                    inMajority.put( pres, competitor );
            }

            DEBUG("Majority:", inMajority);
            if ( inMajority.size() >= 1 ) {
                for ( Map.Entry<PartialComputationResult, Collection<String>> pair : inMajority.asMap().entrySet() ) {
                    Collection<String> competitors = pair.getValue();
                    if ( res.containsKey( pair.getKey() ) )
                        continue;

                    if ( competitors.size() == 1 ) { // just one Major!
                        res.put( competitors.iterator().next(), unassigned_place.inc() );
                    }
                };
            } else {
                DEBUG("No Majority for col: 1-" + col + ". Continue further");
            }
        });

        System.out.println(":>>\n" + computation);
        System.out.println("RES:>>\n" + res);

        return null;
    }

    public static Single fromArray( String[] competitors, String[] judges, int[] marks ) {
        if ( competitors == null || judges == null || marks == null )
            throw new IllegalArgumentException("null args are not accepted");
        return fromList( Arrays.asList(competitors), Arrays.asList(judges), marks );
    }

    public static Single fromList( List<String> competitors, List<String> judges, int[] marks ) {
        if ( competitors == null || judges == null || marks == null )
            throw new IllegalArgumentException("null args are not accepted");
        ArrayTable<String,String,Integer> res = ArrayTable.create( competitors, judges );
        int i = 0;
        for ( String competitor : competitors )
            for ( String judge : judges )
                res.put( competitor, judge, marks[i++] );
        return new Single( res );
    }

    public static Single fromCSV( List<String> competitors, List<String> judges, String marks ) {
        if ( competitors == null || judges == null || marks == null )
            throw new IllegalArgumentException("null args are not accepted");
        return fromList(
            competitors,
            judges,
            rlines.splitAsStream(marks).flatMapToInt(
                line -> rvals.splitAsStream(line).flatMapToInt( x -> {
                    Matcher m = rnum.matcher(x);
                    if (!m.matches())
                        throw new IllegalArgumentException("Wrong mark: " + x);
                    return IntStream.of(Integer.parseInt(m.group(1)));
                } )
            ).toArray()
        );
    }
}
