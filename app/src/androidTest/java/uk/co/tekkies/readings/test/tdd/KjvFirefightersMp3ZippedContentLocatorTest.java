package uk.co.tekkies.readings.test.tdd;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

import uk.co.tekkies.readings.model.content.KjvFirefightersMp3ZippedContentLocator;

public class KjvFirefightersMp3ZippedContentLocatorTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        InputStream inputStream;
//        try {
//            inputStream = context.getAssets().open("sample.mp3");
//        } catch (IOException e) {
//        }
    }

    public void testConfirmKeyFileFound() {
        KjvFirefightersMp3ZippedContentLocator locator=new KjvFirefightersMp3ZippedContentLocator();
        assertTrue(locator.searchConfirmKeyFileFound("/sdcard/Download"));
    }
}
