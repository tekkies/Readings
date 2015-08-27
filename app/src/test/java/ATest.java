import junit.framework.Assert;
import junit.framework.TestCase;

public class ATest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testSum() throws Exception {
        Assert.assertEquals(3, Sum(1, 2));
    }

    private int Sum(int a, int b) {
        return a+b;
    }
}