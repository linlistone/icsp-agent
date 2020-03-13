package cn.com.yusys.icsp.agent.server.util;

import com.alibaba.fastjson.*;
import okhttp3.*;
import org.slf4j.*;

public final class OkhttpClient {
	private static final Logger logger;
	private static final OkHttpClient client;
	private static final MediaType mediaType;

	public static String httpGet(final String url) throws Exception {
		OkhttpClient.logger.info("发起的get请求为:{}", url);
		String result = null;
		final Request request = new Request.Builder().url(url).build();
		Response response = null;
		try {
			response = OkhttpClient.client.newCall(request).execute();
			if (!response.isSuccessful() && !response.isRedirect()) {
				throw new Exception(response.message());
			}
			result = response.body().string();
			OkhttpClient.logger.info("接受的get请求响应:{}", result);
		} catch (Exception e) {
			OkhttpClient.logger.error(e.getMessage());
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return result;
	}

	public static String httpPost(String url, Object data) throws Exception {
		String result = null;
		String strData = JSON.toJSONString(data);
		logger.info("发送的地址为:{}, 请求的信息为:{}", url, strData);
		RequestBody requestBody = RequestBody.create(strData,OkhttpClient.mediaType);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		Response response = null;
		try {
			response = OkhttpClient.client.newCall(request).execute();
			result = response.body().string();
			if (!response.isSuccessful() && !response.isRedirect()) {
				throw new Exception(response.message());
			}
			OkhttpClient.logger.info("发送的地址为:{}, 接受的信息为:{}", url, result);
		} catch (Exception e) {
			OkhttpClient.logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return result;
	}

	static {
		logger = LoggerFactory.getLogger(OkhttpClient.class);
		client = new OkHttpClient();
		mediaType = MediaType.parse("application/json;charset=utf-8");
	}
}
