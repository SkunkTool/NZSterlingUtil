package com.nedzhang.sterlingUtil;

import java.net.InetAddress;

import com.nedzhang.util.SlidingWindowCacheManager;
import com.yantra.interop.client.InteropEnvStub;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.util.YFSContextManager;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycp.sm.server.YCPSecurityManager;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.util.YFCException;

//import com.yantra.interop.services.security.util.UserTokenHelper;

public final class InteropEnvStubUtil {

	private static final SlidingWindowCacheManager<String, InteropEnvStub> envCache 
		= new SlidingWindowCacheManager<String, InteropEnvStub>();

	private static String computerName;

	/****
	 * Static constructor to load computer name
	 */
	{
		try {
			computerName = InetAddress.getLocalHost().toString();
		} catch (final Throwable e) {
			// Unknown computer name
			computerName = "UNKNOWN";
		}
		
		try {
			computerName = InetAddress.getLocalHost().getHostName();
		} catch (final Throwable e) {
			// DO nothing. Use the computerName set by previous
			// method.
			
		}
	}

	/**
	 * Private constructor to prevent instantiate this class.
	 */
	private InteropEnvStubUtil() {

	}


	public static YFSContext getContextForEnv(final InteropEnvStub env)
			throws Exception {

		return YFSContextManager.getInstance().getContextFor(env);
	}

	public static YIFApi getYifApi() throws YIFClientCreationException {
		return YIFClientFactory.getInstance().getApi();
	}

	public static InteropEnvStub getInteropEnvStub(final String userID,
			final String password, final String programID) throws YFCException,
			Exception {
		return getInteropEnvStub(userID, password, programID, computerName);
	}

	public static InteropEnvStub getInteropEnvStub(final String userID,
			final String password, final String programID,
			final String systemName) throws YFCException, Exception {

		final String cacheKey = userID + "/&\\" + password + "@" + programID;

		if (envCache.containsKey(cacheKey)) {
			return getInteropEnvFromCache(cacheKey);
		} else {

			synchronized (cacheKey) {

				if (envCache.containsKey(cacheKey)) {
					return getInteropEnvFromCache(cacheKey);
				} else {

					final InteropEnvStub interopenvstub = new InteropEnvStub(
							userID, programID);
					interopenvstub.setSystemName(systemName);
					interopenvstub
							.setResourceId("nedzhang Skunk tool resource");
					interopenvstub.setLocaleCode(null);
					interopenvstub.setVersion("");

					final String tokenID = getUserTokenThroughAuth(
							interopenvstub, password);

					interopenvstub.setTokenID(tokenID);

//					interopenvstub.clearApiTemplates();

					envCache.put(cacheKey, interopenvstub);
					return interopenvstub;
				}

			}
		}
	}

	private static InteropEnvStub getInteropEnvFromCache(final String cacheKey) {
		final InteropEnvStub interopenvstub = envCache.get(cacheKey);
		synchronized (interopenvstub) {
			interopenvstub.clearApiTemplates();
		}
		return interopenvstub;
	}

	private static String getUserTokenThroughAuth(
			final InteropEnvStub interopenvstub, final String password)
			throws Exception {
		interopenvstub.setPassword(password);

		// TODO: check if there is an easier way to either
		// -- Authenticate the user and automatically put token in env
		// -- get Token without have to parse the xml

		final YFCDocument authDoc = YCPSecurityManager.authenticate(
				YFSContextManager.getInstance().getContextFor(interopenvstub),
				interopenvstub.getUserId(), interopenvstub.getPassword());

		final String tokenID = authDoc.getDocumentElement().getAttribute(
				"UserToken");
		
		return tokenID;
	}
}
