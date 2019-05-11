import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MicrosoftAcademicGraph {
	String key;
	OkHttpClient client;
	String evaluateAPI;
	
	public MicrosoftAcademicGraph(OkHttpClient client) {
		key = "6c1a27a0b4844df6971833b3e615938c";
		this.client = client;
		evaluateAPI = "https://api.labs.cognitive.microsoft.com/academic/v1.0/evaluate";
	}
	
	public String evaluate(String expr, String attributes) throws IOException {		
		HttpUrl.Builder urlBuilder = HttpUrl.parse(evaluateAPI).newBuilder();
		urlBuilder.addQueryParameter("expr", expr);
		urlBuilder.addQueryParameter("attributes", attributes);
		String url = urlBuilder.build().toString();
		
		Request request = new Request.Builder()
				.header("Ocp-Apim-Subscription-Key", key)
                .url(url)
                .build();
		
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
}

class MicrosoftAcademicGraphResponse {
	String expr;
	class Entity {
		String Ti;
		String Y;
		String DOI;
	}
	Entity[] entities;
}

