package bugs.stackoverflow.belisarius;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import bugs.stackoverflow.belisarius.models.Chatroom;
import bugs.stackoverflow.belisarius.models.Higgs;
import bugs.stackoverflow.belisarius.services.*;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.LoginUtils;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import io.swagger.client.ApiException;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class Application {

	public static void main(String[] args) throws ApiException {

/*		try {
			TrustManager trustManager = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {

				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {

				}

			};

			HostnameVerifier nullVerifier = new HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			};

			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[]{trustManager}, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(nullVerifier);
		} catch (KeyManagementException e) {

		} catch (NoSuchAlgorithmException e) {

		}*/

		StackExchangeClient client = LoginUtils.getClient();

		DatabaseUtils.createRoomTable();
		DatabaseUtils.createVandalisedPostTable();
		DatabaseUtils.createReasonTable();
		DatabaseUtils.createBlacklistedWordTable();
		DatabaseUtils.createBlacklistedWordCaughtTable();
		DatabaseUtils.createOffensiveWordTable();
		DatabaseUtils.createOffensiveWordCaughtTable();
		DatabaseUtils.createReasonCaughtTable();
		DatabaseUtils.createFeedbackTable();
		DatabaseUtils.createHiggsTable();

/*
		Higgs higgs = DatabaseUtils.getHiggs(3); //Hippo
        if(higgs != null){
        	HiggsService.initInstance(higgs.getUrl(), higgs.getKey());
		}
*/

		List<Chatroom> rooms = DatabaseUtils.getRooms();
		MonitorService monitorService = new MonitorService(client, rooms);
		monitorService.startMonitor();
		monitorService.runMonitor();
	}

}