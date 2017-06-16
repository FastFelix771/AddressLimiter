package de.unitygaming.addresslimiter;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressLimiter {

	private final Map<InetAddress, AtomicInteger> limits = Collections.synchronizedMap(new HashMap<InetAddress, AtomicInteger>());
	
	@Getter
	private final int limit;


	public final void increment(InetAddress address) {
		if (!limits.containsKey(address)) limits.put(address, new AtomicInteger(0));

		limits.get(address).incrementAndGet();
	}

	public final void decrement(InetAddress address) {
		if (!limits.containsKey(address)) limits.put(address, new AtomicInteger(0));

		limits.get(address).decrementAndGet();

		if (limits.get(address).intValue() <= 0) limits.remove(address);
	}

	public final boolean check(InetAddress address) {
		if (!limits.containsKey(address)) return true;
		return limits.get(address).intValue() < limit;
	}

}
