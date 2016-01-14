package sombr.skating.calc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class SingleTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SingleTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SingleTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSingle()
    {
        Single a = Single.fromCSV( Arrays.asList("Вася С","Катя Т"), Arrays.asList("Катя Ы","Ася Б","Валя М"), "10  , 22, 34 ,42 ,1, 223 " );
        a.solve();
        assertTrue( true );
    }

    // RULE 5
    public void testRule5() {
        Single data = Single.fromCSV(
                Arrays.asList("10","16","24","31","45","48"),
                Arrays.asList("A","B","C","D","E"),
                "3,6,2,4,1,5\n"+
                "3,6,2,4,5,1\n"+
                "3,6,5,2,1,4\n"+
                "2,6,4,3,1,5\n"+
                "3,5,1,4,2,6"
        );
        data.solve();
        assertTrue( true );
    }
}
