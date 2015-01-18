package com.nedzhang.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/***
 * The SlidingWindowCacheManager class caches objects and remove idle objects
 * after period of time.
 * 
 * @author nzhang
 * 
 * @param <KeyType>
 * @param <ValueType>
 */
public class SlidingWindowCacheManager<KeyType, ValueType> {

	// /***
	// * Logger for the class
	// */
	// private static Log logger = LogFactory
	// .getLog(SlidingWindowCacheManager.class);

	/***
	 * The map to store the cached objects
	 */
	private final Map<KeyType, Pair<ValueType, Long>> cacheMap;

	/***
	 * The object for synchronize lock on the cacheMap object
	 */
	private final Object cacheMapLockObject;

	/***
	 * The manager removes objects idled longer than this duration from cache
	 */
	private final int cacheDurationInMilisecond;

	/***
	 * The frequency that manager checks for idle objects
	 */
	private final int checkFrequencyInMilisecond;

	/***
	 * The runner for idle object removal
	 */
	private final cacheCollectionRunner idleObjectRemovalRunner;

	/***
	 * The thread for idle object removal
	 */
	private final Thread idleObjectRemovalThread;

	/***
	 * Construct a SlidingWindowCacheManager object that checks for idle objects
	 * every 30 seconds and remove a cached object after it has been idling for
	 * more than 10 minutes (600 seconds).
	 */
	public SlidingWindowCacheManager() {
		this(600, 30);
	}

	/***
	 * Construct a SlidingWindowCacheManager object with specified cache
	 * duration and check frequency.
	 * 
	 * @param cacheDurationInSecond
	 *            This manager removes a cached object if it is idled for more
	 *            than specified seconds.
	 * 
	 * @param checkFrequencyInSecond
	 *            This manager checks for idle objects every specified seconds.
	 */
	public SlidingWindowCacheManager(final int cacheDurationInSecond,
			final int checkFrequencyInSecond) {

		this.cacheDurationInMilisecond = cacheDurationInSecond * 1000;
		this.checkFrequencyInMilisecond = checkFrequencyInSecond * 1000;

		this.cacheMap = new HashMap<KeyType, Pair<ValueType, Long>>();
		this.cacheMapLockObject = new Object();

		this.idleObjectRemovalRunner = new cacheCollectionRunner(this,
				this.checkFrequencyInMilisecond);

		this.idleObjectRemovalThread = new Thread(this.idleObjectRemovalRunner);

		this.idleObjectRemovalThread.start();
	}

	private long getCurrentTimeInMilisecond() {
		final Date currentTime = new Date();
		return currentTime.getTime();
	}

	/***
	 * Returns true if this manager contains an object for the specified key.
	 * 
	 * @param key
	 *            whose presence in this manager is to be tested.
	 * @return true if this map contains a mapping for the specified key.
	 */
	public boolean containsKey(final KeyType key) {
		synchronized (this.cacheMapLockObject) {
			return this.cacheMap.containsKey(key);
		}
	}

	/***
	 * 
	 * Returns the value to which this map maps the specified key. Returns null
	 * if the map contains no mapping for this key. A return value of null does
	 * not necessarily indicate that the map contains no mapping for the key;
	 * it's also possible that the map explicitly maps the key to null. The
	 * containsKey operation may be used to distinguish these two cases.
	 * 
	 * @param key
	 *            key whose associated value is to be returned
	 * @return the value to which this map maps the specified key, or null if
	 *         the map contains no mapping for this key.
	 */
	public ValueType get(final KeyType key) {
		if (containsKey(key)) {
			synchronized (this.cacheMapLockObject) {

				final Pair<ValueType, Long> valuePair = this.cacheMap.get(key);

				if (valuePair == null) {
					this.remove(key);
				} else {
					valuePair.setValue2(getCurrentTimeInMilisecond());
				}

				// if (logger.isTraceEnabled()) {
				// logger.trace(String.format("Return \"%s\" with key \"%s\"",
				// valuePair == null ? null : valuePair.getValue1(),
				// key));
				// }
				return valuePair == null ? null : valuePair.getValue1();
			}

		} else {
			return null;
		}
	}

	public ValueType put(final KeyType key, final ValueType value) {
		final Pair<ValueType, Long> valuePair = new Pair<ValueType, Long>(
				value, getCurrentTimeInMilisecond());

		synchronized (this.cacheMapLockObject) {
			// if (logger.isTraceEnabled()) {
			// logger.trace(String.format("Store \"%s\" with key \"%s\"",
			// value, key));
			// }
			this.cacheMap.put(key, valuePair);
		}

		return value;
	}

	/***
	 * Removes the mapping for this key from this manager if it is present
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the manager
	 * @return previous value associated with specified key, or null if there
	 *         was no mapping for key.
	 */
	public ValueType remove(final KeyType key) {
		final ValueType currentValue = this.get(key);

		synchronized (this.cacheMapLockObject) {
			// if (logger.isTraceEnabled()) {
			// logger
			// .trace(String.format("Remove entry with key \"%s\"",
			// key));
			// }
			this.cacheMap.remove(key);
		}

		return currentValue;
	}

	/***
	 * Removes all objects from this manager
	 */
	public void clear() {
		synchronized (this.cacheMapLockObject) {
			this.cacheMap.clear();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (this.idleObjectRemovalThread != null) {
				// Tell runner to stop
				this.idleObjectRemovalRunner.stop();
				// interrupt the runner thread
				this.idleObjectRemovalThread.interrupt();
				// wait for thread to finish
				this.idleObjectRemovalThread.join();

			}
		} finally {
			super.finalize();
		}
	}

	/***
	 * Remove items that has not been accessed in the last
	 * cacheDurationInMilisecond.
	 */
	private void removeIdelObject() {
		final long cutOffTime = getCurrentTimeInMilisecond()
				- this.cacheDurationInMilisecond;

		synchronized (this.cacheMapLockObject) {

			final Set<KeyType> keyset = this.cacheMap.keySet();

			if ((keyset != null) && (keyset.size() > 0)) {
				final Object[] keys = keyset.toArray();

				// Loop through the cacheMap and remove items if needed
				for (int i = 0; i < keys.length; i++) {

					final Pair<ValueType, Long> cachedObj = this.cacheMap
							.get(keys[i]);
					// Compare the values in the cachedObj with cutOffTime
					if (cachedObj.getValue2() < cutOffTime) {
						// if (logger.isTraceEnabled()) {
						// logger
						// .trace(String.format(
						// "Remove idle object. key: \"%s\"",
						// keys[i]));
						// }
						this.cacheMap.remove(keys[i]);
					}
				}
			}
		}
	}

	/***
	 * 
	 * Runnable class to remove items that has not been accessed in the last
	 * interval.
	 * 
	 * @author nzhang
	 * 
	 */
	private class cacheCollectionRunner implements Runnable {

		private boolean keepRunning;


		private final SlidingWindowCacheManager<KeyType, ValueType> manager;

		private final int checkFrequencyInMilisecond;
		
		private final int sleepDuration;

		
		public cacheCollectionRunner(final SlidingWindowCacheManager<KeyType, ValueType> manager,
				final int checkFrequencyInMilisecond) {
			this.manager = manager;
			this.keepRunning = true;
			this.checkFrequencyInMilisecond = checkFrequencyInMilisecond;
			
			if (checkFrequencyInMilisecond <= 1000) {
				sleepDuration = checkFrequencyInMilisecond;
			} else if (checkFrequencyInMilisecond <= 30000) { 
				sleepDuration = 1000;
			} else {
				sleepDuration = 4000;
			}
		}

		public void stop() {
			this.keepRunning = false;
		}

		@Override
		public void run() {

			do {

				// if (logger.isDebugEnabled()) {
				// String logMessage = String
				// .format(
				// "Removal Runner execute on Runner: %s[%d]. Manager : %s[%d]",
				// this.getClass(), this.hashCode(), manager
				// .getClass(), manager.hashCode());
				// logger.debug(logMessage);
				// }
				// Remove idling objects
				this.manager.removeIdelObject();
				try {
					for (int waitTime = 0; keepRunning && waitTime < checkFrequencyInMilisecond; waitTime += this.sleepDuration) {
						Thread.sleep(this.sleepDuration);
					}
				} catch (final InterruptedException e) {
					// Do nothing if interrupted. Just check the
					// keepRunning flag and exit if needed.
				}
			} while (this.keepRunning);

		}

	}
}
