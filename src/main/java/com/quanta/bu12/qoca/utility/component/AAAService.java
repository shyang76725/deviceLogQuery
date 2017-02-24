package com.quanta.bu12.qoca.utility.component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quanta.bu12.qoca.utility.Property;


public class AAAService {
	private static Logger logger = LoggerFactory.getLogger(AAAService.class);
	// private final String adminUsername = "amadmin";
	private String adminUsername;
	// private final String adminPassword = "password";
	private String adminPassword;
	private final String cookieNameForToken = "iPlanetDirectoryPro";
	private String userUUID = null;
	private String openAMLocation;

	private static final char[] passwordPool = { 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0' };

	public String genRandomPassword() {
		String password = null;

		StringBuilder sb = new StringBuilder("Qoca15");
		for (int i = 0; i < 6; i++) {
			sb.append(passwordPool[(int) (Math.random() * passwordPool.length)]);
		}

		password = sb.toString();

		return password;
	}

	/**
	 * Construct AAAService with openAM's URL, The URL should contain: 1.
	 * protocal 2. host 3. port 4. file
	 * 
	 * @param openAMUrl
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public AAAService(String openAMUrl) throws FileNotFoundException,
			IOException {
		this();
		this.openAMLocation = openAMUrl;
	}

	/**
	 * Construct AAAService without openAM's URL, the URL will be set according
	 * to the property file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AAAService() throws FileNotFoundException, IOException {
		openAMLocation = Property.getOPENAM_INTERNAL_URI();
		adminUsername = Property.getOPENAM_ADMIN_USERNAME();
		adminPassword = Property.getOPENAM_ADMIN_PASSWORD();
	}

	public String getUserUUID() {
		return userUUID;
	}

	private void setHttpTokenInCookie(HttpURLConnection httpConn, String token) {
		String cookie = cookieNameForToken + "=" + token;
		httpConn.setRequestProperty("Cookie", cookie);
	}

	private HttpURLConnection setHttpConnection(String strUrl) throws Exception {
		HttpURLConnection httpConn;
//		if (strUrl.substring(0, 5).equals("https")) {
//			httpConn = HttpsConnectionFactory.getConnectionWithoutAuth(strUrl);
//		} else {
			URL url = new URL(strUrl);
			httpConn = (HttpURLConnection) url.openConnection();
//		}
		return httpConn;
	}

	private BufferedReader getHttpInputReader(HttpURLConnection httpConn)
			throws IOException {
		int responseCode = httpConn.getResponseCode();
		logger.debug("response code: {}", httpConn.getResponseCode());
		InputStream in = (responseCode >= 400) ? httpConn.getErrorStream()
				: httpConn.getInputStream();
		InputStreamReader ireader = new InputStreamReader(in);
		BufferedReader bufReader = new BufferedReader(ireader);

		return bufReader;
	}

	/**
	 * Set POST request and get the output writer
	 * 
	 * @param httpConn
	 * @return
	 * @throws IOException
	 */
	private OutputStreamWriter setPostHttpConnection(
			HttpURLConnection httpConn, String contentType) throws IOException {
		httpConn.setRequestMethod("POST");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
		if (!contentType.equals(""))
			httpConn.setRequestProperty("Content-Type", contentType);
		// Important! Disable auto redirect
		httpConn.setInstanceFollowRedirects(false);
		httpConn.connect();

		OutputStream out = httpConn.getOutputStream();
		OutputStreamWriter owriter = new OutputStreamWriter(out);
		return owriter;
	}

	private OutputStreamWriter setPutHttpConnection(HttpURLConnection httpConn,
			String contentType) throws IOException {
		httpConn.setRequestMethod("PUT");
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
		if (!contentType.equals(""))
			httpConn.setRequestProperty("Content-Type", contentType);
		// Important! Disable auto redirect
		httpConn.setInstanceFollowRedirects(false);
		httpConn.connect();

		OutputStream out = httpConn.getOutputStream();
		OutputStreamWriter owriter = new OutputStreamWriter(out);
		return owriter;
	}

	private void setDeleteHttpConnection(HttpURLConnection httpConn)
			throws IOException {
		httpConn.setRequestMethod("DELETE");
		httpConn.setDoInput(true);
		httpConn.setInstanceFollowRedirects(false);

		httpConn.connect();

	}

	private void setGetHttpConnection(HttpURLConnection httpConn)
			throws IOException {
		httpConn.setRequestMethod("GET");
		httpConn.setDoInput(true);
		// Important! Disable auto redirect
		httpConn.setInstanceFollowRedirects(false);

		httpConn.connect();
	}

	private void setHttpLoginAAARequestProperty(HttpURLConnection httpConn,
			String userName, String password) throws IOException {
		httpConn.setRequestProperty("X-OpenAM-Username", userName);
		httpConn.setRequestProperty("X-OpenAM-Password", password);
	}

	/**
	 * * Create AAA Account and set member userUUID
	 * 
	 * @param adminToken
	 * @param userName
	 * @param password
	 * @return error code
	 */
	private String sendCreateAAAAccountCommand(String adminToken,
			String userName, String password, String mail, String name,
			String gender, String birthday, String phone, String postalAddress,
			String employeeNumber, String iplanet_am_user_account_life) {
		String errorCode = Integer.toString(0);

		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation
					+ "/json/users/?_action=create");
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter = this.setPostHttpConnection(httpConn,
					"application/json");
			JSONObject accountJsonObj = new JSONObject();

			accountJsonObj.put("username", userName);
			accountJsonObj.put("userpassword", password);
			if(!mail.equalsIgnoreCase(""))
				accountJsonObj.put("mail", mail);
			if(!name.equalsIgnoreCase(""))
				accountJsonObj.put("givenName", name);
			if(!gender.equalsIgnoreCase(""))
				accountJsonObj.put("sunidentityserverpplegalidentitygender", gender);
			if(!birthday.equalsIgnoreCase(""))
				accountJsonObj.put("sunidentityserverppdemographicsbirthday",birthday);
			if(!phone.equalsIgnoreCase(""))
				accountJsonObj.put("telephonenumber", phone);
			if(!postalAddress.equalsIgnoreCase(""))
				accountJsonObj.put("postalAddress", postalAddress);
			if(!employeeNumber.equalsIgnoreCase(""))
				accountJsonObj.put("employeeNumber", employeeNumber);
			if(!iplanet_am_user_account_life.equalsIgnoreCase(""))
				accountJsonObj.put("iplanet-am-user-account-life", iplanet_am_user_account_life);

			logger.info("JSON: {}", accountJsonObj.toString());
			owriter.write(accountJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			String responseStr = sb.toString();
			String responseContentType = httpConn.getContentType();
			if (responseContentType.contains("json")) {
				this.userUUID = this.getUUIDByUserIdentity(sb.toString());
			}
			if (this.userUUID == null || this.userUUID.equals("")) {
				errorCode = Integer.toString(13102);
			}
			if (responseStr.contains("ldap exception")
					&& responseStr.contains("68")) {
				errorCode = Integer
						.toString(13104);
			}
			if (responseStr.contains("ldap exception 19")
					|| responseStr.contains("Minimum password length")) {
				errorCode = Integer
						.toString(13103);
			}

			bufReader.close();
		} catch (IOException e) {
			logger.error("IOException: ", e);
			errorCode = Integer.toString(11111);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
			errorCode = Integer.toString(11111);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorCode = Integer.toString(11111);
		}

		return errorCode;
	}

	private String sendUpdateAAAAccountCommand(String adminToken,
			String userName, String mail, String name,
			String gender, String birthday, String phone,
			String postalAddress, String employeeNumber, String iplanet_am_user_account_life) {
		String errorCode = Integer.toString(0);

		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation + "/json/users/"
					+ userName);
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter = this.setPutHttpConnection(httpConn,
					"application/json");
			JSONObject accountJsonObj = new JSONObject();

			if(!mail.equalsIgnoreCase(""))
				accountJsonObj.put("mail", mail);
			if(!name.equalsIgnoreCase(""))
				accountJsonObj.put("givenName", name);
			if(!gender.equalsIgnoreCase(""))
				accountJsonObj.put("sunidentityserverpplegalidentitygender", gender);
			if(!birthday.equalsIgnoreCase(""))
				accountJsonObj.put("sunidentityserverppdemographicsbirthday",birthday);
			if(!phone.equalsIgnoreCase(""))
				accountJsonObj.put("telephonenumber", phone);
			if(!postalAddress.equalsIgnoreCase(""))
				accountJsonObj.put("postalAddress", postalAddress);
			if(!employeeNumber.equalsIgnoreCase(""))
				accountJsonObj.put("employeeNumber", employeeNumber);
			if(!iplanet_am_user_account_life.equalsIgnoreCase(""))
				accountJsonObj.put("iplanet-am-user-account-life", iplanet_am_user_account_life);

			logger.info("JSON: {}", accountJsonObj.toString());
			owriter.write(accountJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				Integer.toString(0);
			} else {
				if (sb.toString().contains("ldap exception 19")) {
					errorCode = Integer
							.toString(13101);
				}
				errorCode = Integer.toString(11111);
			}

			bufReader.close();
		} catch (IOException e) {
			logger.error("IOException: ", e);
			errorCode = Integer.toString(11111);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
			errorCode = Integer.toString(11111);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorCode = Integer.toString(11111);
		}

		return errorCode;
	}
	
	/**
	 * Delete AAA Account by given username
	 * 
	 * @param adminToken
	 * @param username
	 * @return errorCode
	 */
	private String sendDeleteAAAAccountCommand(String adminToken,
			String username) {
		String errorCode = Integer.toString(0);

		HttpURLConnection httpConn = null;

		try {
			boolean success = true;
			httpConn = this.setHttpConnection(openAMLocation + "/json/users/"
					+ username);
			this.setHttpTokenInCookie(httpConn, adminToken);

			this.setDeleteHttpConnection(httpConn);

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			String responseContentType = httpConn.getContentType();
			if (responseContentType.contains("json")) {
				success = this.getSuccessInJsonResponse(sb.toString());
				logger.info("success: {}", success);
			} else {
				logger.info("no json");
				success = false;
			}

			if (!success) {
				errorCode = Integer.toString(11111);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorCode = Integer.toString(11111);
		}

		return errorCode;
	}

	private String sendCreateAAAGroupCommand(String adminToken, String groupName) {
		String errorCode = Integer.toString(0);

		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation
					+ "/json/groups?_action=create");
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter;
			owriter = this.setPostHttpConnection(httpConn, "application/json");
			JSONObject groupJsonObj = new JSONObject();
			groupJsonObj.put("username", groupName);
			groupJsonObj.put("realm", "/");
			logger.info("JSON: {}", groupJsonObj.toString());
			owriter.write(groupJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			String responseStr = sb.toString();
			if (httpConn.getResponseCode() != 200
					&& httpConn.getResponseCode() != 201) {
				errorCode = Integer.toString(11111);
				logger.error("create group fail");
				if (responseStr.contains("ldap exception")
						&& responseStr.contains("68")) {
					errorCode = Integer
							.toString(13104);
				}
			}

			bufReader.close();
		} catch (IOException e) {
			logger.error("IOException: ", e);
			errorCode = Integer.toString(11111);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
			errorCode = Integer.toString(11111);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorCode = Integer.toString(11111);
		}

		return errorCode;
	}

	/**
	 * Send reset attribute command
	 * 
	 * @param adminToken
	 * @param userName
	 * @param password
	 * @return errorCode
	 */
	private String sendResetAttributeCommand(String adminToken,
			String userName, String attribute, String value) {
		String errorCode = Integer.toString(0);
		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation + "/json/users/"
					+ userName);
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter = this.setPutHttpConnection(httpConn,
					"application/json");
			JSONObject passwordJsonObj = new JSONObject();
			passwordJsonObj.put(attribute, value);
			logger.info("JSON: {}", passwordJsonObj.toString());
			owriter.write(passwordJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				Integer.toString(0);
			} else {
				if (sb.toString().contains("ldap exception 19")) {
					errorCode = Integer
							.toString(13101);
				}
				errorCode = Integer.toString(11111);
			}

			bufReader.close();
		} catch (Exception e) {
			errorCode = Integer.toString(11111);
			logger.error("Exception: ", e);
		}

		return errorCode;
	}

	/**
	 * Send reset password command
	 * 
	 * @param adminToken
	 * @param userName
	 * @param password
	 * @return errorCode
	 */
	private String sendResetPasswordCommand(String adminToken, String userName,
			String password) {
		String errorCode = Integer.toString(0);
		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation + "/json/users/"
					+ userName);
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter = this.setPutHttpConnection(httpConn,
					"application/json");
			JSONObject passwordJsonObj = new JSONObject();
			passwordJsonObj.put("userPassword", password);
			logger.info("JSON: {}", passwordJsonObj.toString());
			owriter.write(passwordJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				Integer.toString(0);
			} else {
				if (sb.toString().contains("ldap exception 19")) {
					errorCode = Integer
							.toString(13101);
				}
				errorCode = Integer.toString(11111);
			}

			bufReader.close();
		} catch (Exception e) {
			errorCode = Integer.toString(11111);
			logger.error("Exception: ", e);
		}

		return errorCode;
	}

	/**
	 * Login admin and send create AAA account command. The member useruuid will
	 * be set as the uuid of the user. If fail, the useruuid will be null.
	 * 
	 * @param userName
	 * @param password
	 * @param mail
	 * @return errorCode
	 */
	public String createAAAAccount(String userName, String password,
			String mail, String name, String gender, String birthday,
			String phone, String postalAddress, String employeeNumber, String iplanet_am_user_account_life) {

		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);
		String errorCode = this.sendCreateAAAAccountCommand(adminToken,
				userName, password, mail, name, gender, birthday, phone, postalAddress, employeeNumber, iplanet_am_user_account_life);
		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}

		return errorCode;
	}

	public String updateAAAAccount(String userName, String mail, String name, String gender, String birthday, String phone, String postalAddress, String employeeNumber, String iplanet_am_user_account_life) {

		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);
		String errorCode = this.sendUpdateAAAAccountCommand(adminToken,
				userName, mail, name, gender, birthday, phone, postalAddress, employeeNumber, iplanet_am_user_account_life);
		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}

		return errorCode;
	}
	/**
	 * Login admin and send delete AAA account command
	 * 
	 * @param username
	 * @return ErrorCode
	 */
	public String deleteAAAAccount(String username) {
		String errorCode = Integer.toString(0);
		String adminToken = this.loginAAA(adminUsername, adminPassword);

		errorCode = this.sendDeleteAAAAccountCommand(adminToken, username);

		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}

		return errorCode;
	}

	/**
	 * Create an account for CMP admin and assign it to specific cmp admin group
	 * 
	 * @param userName
	 * @param password
	 * @param mail
	 * @return errorCode
	 */
	public String createCMPAdminAccount(String userName, String password,
			String mail, String name, String gender, String birthday,
			String phone, String cmpPrefix, String postalAddress, String employeeNumber, String iplanet_am_user_account_life) {
		String errorCode;
		errorCode = this.createAAAAccount(userName, password, mail, name,
				gender, birthday, phone, postalAddress, employeeNumber, iplanet_am_user_account_life);
		if (errorCode.equals(Integer.toString(0))) {
			boolean success = this
					.assignGroup(userName, cmpPrefix + "cmpadmin");
			if (!success) {
				errorCode = Integer.toString(13102);
			}
		}

		return errorCode;
	}

	/**
	 * Login admin and send create AAA group command.
	 * 
	 * @param groupName
	 * @return errorCode
	 */
	public String createAAAGroup(String groupName) {

		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);
		String errorCode = this
				.sendCreateAAAGroupCommand(adminToken, groupName);
		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}

		return errorCode;
	}

	/**
	 * Create cmpadmin, nurse, doctor, patient, family group with input prefix
	 * 
	 * @param prefix
	 * @return errorCode
	 */
	public String createAllGroupOfCMP(String prefix) {
		String errorCode = null;

		errorCode = this.createAAAGroup(prefix + "cmpadmin");
		if (errorCode.equals(Integer.toString(0))) {
			errorCode = this.createAAAGroup(prefix + "nurse");
			if (errorCode.equals(Integer.toString(0))) {
				errorCode = this.createAAAGroup(prefix + "doctor");
				if (errorCode.equals(Integer.toString(0))) {
					errorCode = this.createAAAGroup(prefix + "patient");
					if (errorCode.equals(Integer.toString(0))) {
						errorCode = this.createAAAGroup(prefix + "family");
					}
				}
			}
		}

		return errorCode;
	}

	/**
	 * Reset the password of specific account
	 * 
	 * @param userName
	 * @param password
	 * @return error code
	 */
	public String resetPassword(String userName, String password) {
		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);

		String errorCode = this.sendResetPasswordCommand(adminToken, userName,
				password);

		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}

		return errorCode;
	}

	/**
	 * get user UUID according to the username
	 * 
	 * @param userName
	 * @return uuid
	 */
	public String getUUIDByUser(String userName, String token) {
		String uuid = null;

		// String adminToken = this.loginAAA(adminUsername, adminPassword);
		try {
			String identity = this.getIdentity(userName);
			if (identity != null) {
				uuid = this.getUUIDByUserIdentity(identity);
			}
		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return uuid;
	}

	/**
	 * get user UUID according to the username
	 * 
	 * @param userName
	 * @return uuid
	 */
	public String getUUIDFromAAA(String userName) {
		String uuid = null;

		String adminToken = this.loginAAA(adminUsername, adminPassword);
		try {
			String identity = this.getIdentity(userName);
			if (identity != null) {
				uuid = this.getUUIDByUserIdentity(identity);
			}
		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			if (adminToken != null) {
				this.logoutAAA(adminToken);
			}
		}

		return uuid;
	}

	/**
	 * Use legacy API to get entryUUID attribute of the token's owner, please
	 * prevent using this
	 * 
	 * @param token
	 * @return UUID
	 * @throws Exception
	 */
	public String getUUIDFromAAALegacy(String token) throws Exception {
		String uuid = null;

		HttpURLConnection httpConn;
		httpConn = this.setHttpConnection(openAMLocation
				+ "/identity/attributes?subjectid=" + token
				+ "&attributenames=entryUUID");
		this.setGetHttpConnection(httpConn);

		BufferedReader br = this.getHttpInputReader(httpConn);
		String str;
		String uuidTag = "userdetails.attribute.value=";

		if (httpConn.getResponseCode() == 200) {
			while ((str = br.readLine()) != null) {
				logger.info("{}", str);
				int location = str.indexOf(uuidTag);
				if (location != -1) {
					uuid = str.substring(location + uuidTag.length());
				}
			}
		}

		return uuid;
	}

	public String getDnFromAAA(String adminToken, String userName)
			throws JSONException, IOException, Exception {
		String dn = null;

		String identity = this.getIdentity(userName);
		JSONObject jsonObject = new JSONObject(identity);
		if (jsonObject.has("dn")) {
			dn = jsonObject.getJSONArray("dn").getString(0);
		}
		logger.info("dn of {}: {}", userName, dn);

		return dn;

	}

	/**
	 * login and return token
	 * 
	 * @return token
	 */
	public String loginAAA(String userName, String password) {
		String token = null;

		HttpURLConnection httpConn = null;
		try {
			httpConn = this.setHttpConnection(openAMLocation
					+ "/json/authenticate");
			this.setHttpLoginAAARequestProperty(httpConn, userName, password);
			this.setPostHttpConnection(httpConn, "application/json");

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				String responseContentType = httpConn.getContentType();
				if (responseContentType.contains("json")) {
					JSONObject JsonObj = new JSONObject(sb.toString());
					if (JsonObj.has("tokenId"))
						token = JsonObj.getString("tokenId");
				}
				logger.info("token: {}", token);
			}

			bufReader.close();
		} catch (IOException e) {
			logger.error("IOException:", e);
		} catch (JSONException e) {
			logger.error("JSONException:", e);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}

		return token;
	}

	/**
	 * Login and return token by legacy api, please prevent using it!!
	 * 
	 * @param userName
	 * @param password
	 * @return token
	 */
	public String loginAAALegacy(String userName, String password) {
		String token = "";
		HttpURLConnection httpConn;

		try {
			httpConn = this.setHttpConnection(openAMLocation
					+ "/identity/authenticate");

			OutputStreamWriter owriter = this.setPostHttpConnection(httpConn,
					"");
			String data = "username=" + userName + "&password=" + password;
			owriter.write(data);
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				token = sb.toString();
				token = token.substring(token.indexOf("token.id=")
						+ "token.id=".length());
				logger.info("token = {}", token);
			}

		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return token;
	}

	/**
	 * logout a session by token
	 * 
	 * @param token
	 * @return success or not
	 */
	public boolean logoutAAA(String token) {
		boolean success = true;

		HttpURLConnection httpConn = null;

		try {
			// httpConn = this.setHttpConnection(openAMLocation +
			// "/json/sessions/?_action=logout");
			httpConn = this.setHttpConnection(openAMLocation
					+ "/identity/logout");
			this.setHttpTokenInCookie(httpConn, token);
			this.setPostHttpConnection(httpConn, "application/json");

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				String responseContentType = httpConn.getContentType();
				if (responseContentType.contains("json")) {
					JSONObject JsonObj = new JSONObject(sb.toString());
					if (JsonObj.getString("result").equals(
							"Successfully logged out")) {
						success = true;
					} else {
						success = false;
					}
				}
			} else {
				success = false;
			}

			bufReader.close();
		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return success;
	}

	/**
	 * check if user exist in AAA, if yes, return true and the UUID will be set
	 * in the private member userUUID
	 * 
	 * @param userName
	 * @return exist or not
	 */
	public boolean checkUserExist(String userName) {
		boolean exist = true;
		String adminToken = this.loginAAA(adminUsername, adminPassword);

		try {
			// read identity of the user
			String identity = this.getIdentity(userName);
			if (identity != null && !identity.equals("")) {
				// parse the UUID from user identity, the member userUUID will
				// be
				// null if the user is not exist
				this.userUUID = this.getUUIDByUserIdentity(identity);
				if (this.userUUID == null)
					exist = false;
			} else {
				exist = false;
			}
		} catch (IOException e) {
			exist = false;
			logger.error("IOException: ", e);
		} catch (Exception e) {
			exist = false;
			logger.error("Exception: ", e);
		} finally {
			if (adminToken != null) {
				this.logoutAAA(adminToken);
			}
		}

		if (exist)
			logger.info("user {}'s UUID = {} ", userName, this.userUUID);
		else
			logger.info("user {} is not exist");

		return exist;
	}

	/**
	 * Get user's identity based on userName
	 * 
	 * @return String identity
	 */
	public String getIdentity(String userName) throws Exception {
		String identity = null;

		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);
		HttpURLConnection httpConn;
		httpConn = this.setHttpConnection(openAMLocation + "/json/users/"
				+ userName);
		this.setHttpTokenInCookie(httpConn, adminToken);
		this.setGetHttpConnection(httpConn);

		BufferedReader br = this.getHttpInputReader(httpConn);
		String str;
		StringBuffer sb = new StringBuffer();

		while ((str = br.readLine()) != null) {
			sb.append(str);
		}

		if (httpConn.getResponseCode() == 200) {
			identity = sb.toString();
			logger.info("identity of {}: {}", userName, identity);
		} else {
			logger.error("read identity fail, response: {}", sb.toString());
		}
		br.close();
		this.logoutAAA(adminToken);

		return identity;

	}

	/**
	 * Get UUID in the identity with json format, and set to the member userUUID
	 * 
	 * @param identity
	 * @throws JSONException
	 */
	private String getUUIDByUserIdentity(String identity) {
		String userUUID = null;
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(identity);
			if (jsonObj.has("entryuuid"))
				userUUID = jsonObj.getJSONArray("entryuuid").getString(0);
			else if (jsonObj.has("entryUUID"))
				userUUID = jsonObj.getJSONArray("entryUUID").getString(0); // TODO
																			// Add
																			// by
																			// Alston
																			// @20150917
			else if (jsonObj.has("uid")) // TODO Add by Alston @20150917
				userUUID = jsonObj.getJSONArray("uid").getString(0); // TODO Add
																		// by
																		// Alston
																		// @20150917
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
		}

		return userUUID;
	}

	/**
	 * Get success in json format response
	 * 
	 * @param string
	 * @return success or not
	 */
	private boolean getSuccessInJsonResponse(String responseJson) {
		boolean success = true;
		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(responseJson);
			if (jsonObj.has("success")) {
				success = Boolean.valueOf(jsonObj.getString("success"));
			} else {
				success = false;
			}
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
			success = false;
		}

		return success;
	}

	/*
	 * public List<String> getAssignedGroup(String userName) throws IOException,
	 * JSONException, Exception{ ArrayList<String> groupInfo = new
	 * ArrayList<String>(); String identity = this.getIdentity(userName);
	 * JSONObject JsonObj = new JSONObject(identity); JSONArray groupArray;
	 * if(JsonObj.has("ismemberof")) { groupArray =
	 * JsonObj.getJSONArray("ismemberof"); if(groupArray != null) { for(int i =
	 * 0; i < groupArray.length(); i++) { String groupString =
	 * groupArray.getString(i); groupString =
	 * groupString.split(",")[0].split("=")[1]; groupInfo.add(groupString); } }
	 * }
	 * 
	 * logger.info("groupInfo: {}", groupInfo.toString());
	 * 
	 * return groupInfo; }
	 */

	/**
	 * Get users' groups
	 * 
	 * @param string
	 *            userName
	 * @return List<String>
	 */
	public List<String> getAssignedGroup(String userName) throws IOException,
			JSONException, Exception {
		ArrayList<String> groupInfo = new ArrayList<String>();
		String adminToken = this.loginAAA(this.adminUsername,this.adminPassword);
		HttpURLConnection httpConn;
		httpConn = this.setHttpConnection(openAMLocation
				+ "/identity/read?name=" + userName
				+ "&attributes_names=ismemberof");
		this.setHttpTokenInCookie(httpConn, adminToken);
		this.setGetHttpConnection(httpConn);
		String result = null;
		String groupString = null;

		BufferedReader br = this.getHttpInputReader(httpConn);
		String str;
		StringBuffer sb = new StringBuffer();

		while ((str = br.readLine()) != null) {
			sb.append(str);
		}

		if (httpConn.getResponseCode() == 200) {
			result = sb.toString();
			System.out.println(result);
			String[] temp = result.split("identitydetails.group=");
			for (int i = 1; i < temp.length; i++) {
				groupInfo.add(temp[i].split("identity")[0]);
				// System.out.println(temp[i]);
			}
		}
		
		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}
		logger.info("groupInfo: {}", groupInfo.toString());

		return groupInfo;
	}

	/**
	 * Assign user's group based on userName
	 * 
	 * @return true / false
	 */
	public boolean assignGroup(String userName, String groupName) {
		boolean success = true;

		String adminToken = this.loginAAA(adminUsername, adminPassword);
		try {
			List<String> assignedGroup = this.getAssignedGroup(userName);
			for (String group : assignedGroup) {
				if (group.equals(groupName)) {
					logger.info("user is already in {}", groupName);
					return true;
				}
			}
			String dn = this.getDnFromAAA(adminToken, userName);
			System.out.println("dn=" + dn);
			JSONArray userArray = this.getUsersDnInGroup(adminToken, groupName);
			if (userArray == null)
				userArray = new JSONArray();
			userArray.put(dn);
			logger.info("new user Array: {}", userArray);
			this.updateGroup(adminToken, groupName, userArray);
			assignedGroup = this.getAssignedGroup(userName);
			for (String group : assignedGroup) {
				if (group.equals(groupName)) {
					logger.info("user is in {}", groupName);
					success = true;
				}
			}
		} catch (IOException e) {
			logger.error("IOException: ", e);
			success = false;
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
			success = false;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			success = false;
		} finally {
			if (adminToken != null) {
				this.logoutAAA(adminToken);
			}
		}

		return success;
	}

	/**
	 * Remove user from group
	 * 
	 * @param userName
	 * @param groupName
	 * @return success or not
	 */
	public boolean removeGroup(String userName, String groupName) {
		boolean success = false;

		String adminToken = this.loginAAA(adminUsername, adminPassword);
		try {
			List<String> assignedGroup = this.getAssignedGroup(userName);
			for (String group : assignedGroup) {
				if (group.equals(groupName)) {
					logger.info("user is in {}", groupName);
				}
			}
			String dn = this.getDnFromAAA(adminToken, userName);
			JSONArray userArray = this.getUsersDnInGroup(adminToken, groupName);
			if (userArray == null)
				userArray = new JSONArray();
			else {
				for (int i = 0; i < userArray.length(); i++) {
					if (userArray.getString(i).equals(dn)) {
						userArray.remove(i);
						i--;
					}
				}
			}
			logger.info("new user Array: {}", userArray);
			this.updateGroup(adminToken, groupName, userArray);
		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			if (adminToken != null) {
				this.logoutAAA(adminToken);
			}
		}

		return success;
	}

	private boolean updateGroup(String adminToken, String groupName,
			JSONArray userArray) {
		boolean success = true;

		HttpURLConnection httpConn = null;

		try {
			httpConn = this.setHttpConnection(openAMLocation + "/json/groups/"
					+ groupName);
			this.setHttpTokenInCookie(httpConn, adminToken);

			OutputStreamWriter owriter = this.setPutHttpConnection(httpConn,
					"application/json");
			JSONObject accountJsonObj = new JSONObject();
			accountJsonObj.put("name", groupName);
			accountJsonObj.put("realm", "/");
			accountJsonObj.put("uniquemember", userArray);
			accountJsonObj.put("cn", groupName);
			accountJsonObj.put("description", "Update " + groupName);
			logger.info("JSON: {}", accountJsonObj.toString());
			owriter.write(accountJsonObj.toString());
			owriter.flush();
			owriter.close();

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				String responseContentType = httpConn.getContentType();
				if (responseContentType.contains("json")) {
					JSONObject jsonObj = new JSONObject(sb.toString());
					JSONArray newUserArray = null;
					if (jsonObj.has("uniquemember"))
						newUserArray = jsonObj.getJSONArray("uniquemember");
					else if (jsonObj.has("uniqueMember"))
						newUserArray = jsonObj.getJSONArray("uniqueMember");
					logger.debug("new user of group {}: {}", groupName,
							newUserArray);
				}
			} else {
				logger.error(
						"http connection response code: {}, response message: {}",
						httpConn.getResponseCode(), sb.toString());
			}

			bufReader.close();

		} catch (IOException e) {
			success = false;
			logger.error("IOException: ", e);
		} catch (JSONException e) {
			success = false;
			logger.error("JSONException: ", e);
		} catch (Exception e) {
			success = false;
			logger.error("Exception: ", e);
		}
		return success;
	}

	public JSONArray getUsersDnInGroup(String adminToken, String groupName) {
		JSONArray userArray = new JSONArray();

		HttpURLConnection httpConn = null;
		try {
			httpConn = this.setHttpConnection(openAMLocation + "/json/groups/"
					+ groupName + "?_prettyPrint=true");
			this.setHttpTokenInCookie(httpConn, adminToken);
			this.setGetHttpConnection(httpConn);

			BufferedReader bufReader = this.getHttpInputReader(httpConn);
			StringBuffer sb = new StringBuffer();
			String str = null;

			while ((str = bufReader.readLine()) != null) {
				logger.info("{}", str);
				sb.append(str);
			}

			if (httpConn.getResponseCode() == 200) {
				String responseContentType = httpConn.getContentType();
				if (responseContentType.contains("json")) {
					JSONObject jsonObj = new JSONObject(sb.toString());
					if (jsonObj.has("uniquemember"))
						userArray = jsonObj.getJSONArray("uniquemember");
					else if (jsonObj.has("uniqueMember"))
						userArray = jsonObj.getJSONArray("uniqueMember");
					logger.debug("new user of group {}: {}", groupName,
							userArray);
				}
			} else {
				logger.error(
						"http connection response code: {}, response message: {}",
						httpConn.getResponseCode(), sb.toString());
			}

			bufReader.close();

		} catch (IOException e) {
			logger.error("IOException: ", e);
		} catch (JSONException e) {
			logger.error("JSONException: ", e);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return userArray;
	}

	/**
	 * Get All users's userName
	 * 
	 * @return List<String>
	 */
	public List<String> getUserList() throws Exception {
		String result = null;
		ArrayList<String> userList = new ArrayList<String>();
		String adminToken = this.loginAAA(this.adminUsername,
				this.adminPassword);
		HttpURLConnection httpConn;
		httpConn = this.setHttpConnection(openAMLocation
				+ "/json/users?_queryID=*");
		this.setHttpTokenInCookie(httpConn, adminToken);
		this.setGetHttpConnection(httpConn);

		BufferedReader br = this.getHttpInputReader(httpConn);
		String str;
		StringBuffer sb = new StringBuffer();

		while ((str = br.readLine()) != null) {
			sb.append(str);
		}

		if (httpConn.getResponseCode() == 200) {
			result = sb.toString();
			JSONObject JsonObj = new JSONObject(result);
			JSONArray userArray;
			if (JsonObj.has("result")) {
				userArray = JsonObj.getJSONArray("result");
				if (userArray != null) {

					for (int i = 0; i < userArray.length(); i++) {
						String userAccount = userArray.getString(i);
						// System.out.println(userAccount);
						if (!userAccount.equalsIgnoreCase("quanta")
								&& !userAccount.equalsIgnoreCase("mcumanager")
								&& !userAccount.equalsIgnoreCase("rtpproxy1")
								&& !userAccount.equalsIgnoreCase("rtpproxy2")
								&& !userAccount.equalsIgnoreCase("amAdmin")
								&& !userAccount.equalsIgnoreCase("thcadmin")
								&& !userAccount.equalsIgnoreCase("cmpadmin")
								&& !userAccount.equalsIgnoreCase("anonymous"))
							userList.add(userAccount);
					}
				}
			}
		} else {
			logger.error("read User List fail, response: {}", sb.toString());
		}
		br.close();
		
		if (adminToken != null) {
			this.logoutAAA(adminToken);
		}
		return userList;

	}
}
