package uk.co.tekkies.readings.test.tdd;

import junit.framework.TestCase;

import uk.co.tekkies.readings.model.content.KjvFirefightersMp3ZippedContentLocator;

/**
 * Created by ajoiner on 03/11/2014.
 */
public class KjvFirefightersMp3ZippedContentLocatorTest extends TestCase {

    public void testConfirmKeyFileFound() {
        KjvFirefightersMp3ZippedContentLocator locator=new KjvFirefightersMp3ZippedContentLocator();
        assertTrue(locator.searchConfirmKeyFileFound("/sdcard/Download"));
    }
}
