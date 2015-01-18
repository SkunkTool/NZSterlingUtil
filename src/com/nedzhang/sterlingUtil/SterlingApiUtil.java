package com.nedzhang.sterlingUtil;

import org.w3c.dom.Document;

import com.yantra.interop.client.InteropEnvStub;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;

public class SterlingApiUtil {

	// private static String PROD_ID;
	// private static String USER_ID;
	// private static String SERVLET_URL;

	private static boolean initialized = false;
	private static Object initializeLock = new Object();

	private static YIFApi localApi = null;

	private static void initialize() throws YIFClientCreationException {
		// PROD_ID = ApplicationProperty.get("STERLING_INTEROP_PROG_ID");
		// USER_ID = ApplicationProperty.get("STERLING_INTEROP_USER_ID");
		// SERVLET_URL = ApplicationProperty.get("STERLING_INTEROP_URL");

		initialized = true;

		localApi = YIFClientFactory.getInstance().getLocalApi();

	}

	public static Document invoke(final String userID, final String password,
			final String programID, final String apiName,
			final boolean isService, final Document input,
			final Document outputTemplate) throws Exception  {


			return invokeInternal(userID, password, programID, apiName,
					isService, input, outputTemplate);


	}

	private static Document invokeInternal(final String userID,
			final String password, final String programID,
			final String apiName, final boolean isService,
			final Document input, final Document outputTemplate)
			throws Exception {

		// Check if the class is initialized
		if (!initialized) {
			synchronized (initializeLock) {
				if (!initialized) {
					initialize();
				}
			}
		}

		// Check if all arguments are valid
		// if ((PROD_ID == null) || (PROD_ID.length() == 0)) {
		// throw new IllegalArgumentException("PROD_ID is null or empty");
		// }
		//
		// if ((USER_ID == null) || (USER_ID.length() == 0)) {
		// throw new IllegalArgumentException("USER_ID is null or empty");
		// }

		// if (SERVLET_URL == null || SERVLET_URL.length() == 0) {
		// throw new IllegalArgumentException("SERVLET_URL is null or empty");
		// }

		if ((apiName == null) || (apiName.length() == 0)) {
			throw new IllegalArgumentException("apiName is null or empty");
		}

		// System.setProperty("api.security.enabled", "N");

		final InteropEnvStub interopEnv = InteropEnvStubUtil.getInteropEnvStub(
				userID, password, programID);

		if (outputTemplate != null) {
			interopEnv.setApiTemplate(apiName, outputTemplate);
		}

		if (isService) {
			return localApi.executeFlow(interopEnv, apiName, input);
		} else {

			return localApi.invoke(interopEnv, apiName, input);
		}

	}

	// public static Document invoke(String apiName, boolean isService, Document
	// input, Document template)
	// throws //HttpException,
	// IOException, IllegalArgumentException, ParserConfigurationException,
	// SAXException, TransformerException, SterlingApiException
	// {
	// // Check if the class is initialized
	// if (! initialized)
	// {
	// synchronized (initializeLock) {
	// if (! initialized)
	// {
	// initialize();
	// }
	// }
	// }
	//
	// // Check if all arguments are valid
	// if (PROD_ID == null || PROD_ID.length() == 0){
	// throw new IllegalArgumentException("PROD_ID is null or empty");
	// }
	//
	// if (USER_ID == null || USER_ID.length() == 0){
	// throw new IllegalArgumentException("USER_ID is null or empty");
	// }
	//
	// if (SERVLET_URL == null || SERVLET_URL.length() == 0){
	// throw new IllegalArgumentException("SERVLET_URL is null or empty");
	// }
	//
	// if (apiName == null || apiName.length() == 0){
	// throw new IllegalArgumentException("apiName is null or empty");
	// }
	//
	//
	// Map<String, String> params = new HashMap<String, String>();
	//
	// params.put("YFSEnvironment.progId", PROD_ID);
	// params.put("YFSEnvironment.userId", USER_ID);
	//
	// params.put("InteropApiName", apiName);
	// params.put("IsFlow", isService ? "Y" : "N");
	//
	// if (input != null)
	// {
	// params.put("InteropApiData", XmlUtil.getXmlString(input));
	// }
	//
	// if (template != null)
	// {
	// params.put("TemplateData", XmlUtil.getXmlString(template));
	// }
	//
	// String response = null;
	//
	// // HttpUtil.post(
	// // new PostMethod(SERVLET_URL),
	// // null, params,-1, null, 0, null, null);
	//
	// if (response != null && response.length() > 0)
	// {
	// Document responseDoc = XmlUtil.getDocument(response);
	//
	// // Check if the response is an error
	// Node errorNode = XPathAPI.selectSingleNode(responseDoc, "/Errors");
	//
	// if (errorNode != null)
	// {
	// throw new SterlingApiException(response);
	// }
	//
	// return responseDoc;
	//
	// }
	// else
	// {
	// return null;
	// }
	// }

}
