package de.unitygaming.addresslimiter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Test;

import de.unitygaming.addresslimiter.AddressLimiter;

public class AddressLimiterTest {

	@Test
	public void testCounter() {
		int limit = 3;
		AddressLimiter limiter = new AddressLimiter(limit);
		InetAddress address = new InetSocketAddress(0).getAddress();
		
		assertNotNull(limiter);
		assertEquals(limiter.getLimit(), limit);
		
		/*  (2 / 3) - below limit */
		limiter.increment(address);
		limiter.increment(address);
		assertTrue(limiter.check(address));
		
		/* (3 / 3) - limit reached */
		limiter.increment(address);
		assertFalse(limiter.check(address));
		
		/* (4 / 3) - above reached */
		limiter.increment(address);
		assertFalse(limiter.check(address));
		
		/* (0 / 3) - below limit */
		limiter.decrement(address);
		limiter.decrement(address);
		limiter.decrement(address);
		limiter.decrement(address);
		assertTrue(limiter.check(address));
		
		/* (-1 / 3) - below limit */
		limiter.decrement(address);
		assertTrue(limiter.check(address));
	}

}
