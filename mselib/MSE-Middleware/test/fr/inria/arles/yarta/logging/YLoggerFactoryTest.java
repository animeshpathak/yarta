package fr.inria.arles.yarta.logging;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

public class YLoggerFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLogger() {

		YLogger y1 = YLoggerFactory.getLogger();

		assertTrue("Why is this not a Systemlogger?",
				y1 instanceof SystemLogger);

		// getting another logger. Let's see if they are the same object or not
		// :).
		YLogger y2 = YLoggerFactory.getLogger();

		assertEquals("Why is YLoggerFactory Giving me different loggers!", y1,
				y2);
	}

	@Test
	public void testSetLogLevel() {
		String TAG = "SAMPLE TAG";
		YLogger y1 = YLoggerFactory.getLogger();

		// loglevel is set by default to debug
		y1.d(TAG, "This (debug) should print. :)");
		y1.i(TAG, "This (info) should print. :)");
		y1.w(TAG, "This (warn) should print. :)");
		y1.e(TAG, "This (error) should print. :)");

		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_INFO);
		y1.d(TAG, "This (debug) should NOT print! WTF!");
		y1.i(TAG, "This (info) should print. :)");
		y1.w(TAG, "This (warn) should print. :)");
		y1.e(TAG, "This (error) should print. :)");

		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_WARN);
		y1.d(TAG, "This (debug) should NOT print! WTF!");
		y1.i(TAG, "This (info) should NOT print! WTF!");
		y1.w(TAG, "This (warn) should print. :)");
		y1.e(TAG, "This (error) should print. :)");

		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_ERROR);
		y1.d(TAG, "This (debug) should NOT print! WTF!");
		y1.i(TAG, "This (info) should NOT print! WTF!");
		y1.w(TAG, "This (warn) should NOT print! WTF!");
		y1.e(TAG, "This (error) should print. :)");

		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);
		y1.d(TAG, "This (debug) should NOT print! WTF!");
		y1.i(TAG, "This (info) should NOT print! WTF!");
		y1.w(TAG, "This (warn) should NOT print! WTF!");
		y1.e(TAG, "This (error) should NOT print. WTF!");

		// testing if the other loggers also get affected
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_ERROR);
		YLogger y2 = YLoggerFactory.getLogger();
		y2.d(TAG, "This (debug) for y2 should NOT print! WTF!");
		y2.e(TAG, "This (error) for y2 should print. :)");
	}

}
