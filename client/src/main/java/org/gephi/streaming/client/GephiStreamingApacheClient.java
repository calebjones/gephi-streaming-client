package org.gephi.streaming.client;

import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.gephi.streaming.client.exception.GephiStreamingException;
import org.gephi.streaming.client.model.GephiClientConfig;

public class GephiStreamingApacheClient extends GephiStreamingAbstractClient {
	
	private DefaultHttpClient client;
	
	private HttpHost host;
	
	//private UsernamePasswordCredentials creds;
	
	public GephiStreamingApacheClient(GephiClientConfig config, DefaultHttpClient client) throws GephiStreamingException {
		super(config);
		this.client = client;
		
		if (getClientConfig().getUsername() != null ||
			getClientConfig().getPassword() != null) {
				UsernamePasswordCredentials creds = 
						new UsernamePasswordCredentials(getClientConfig().getUsername(), 
														getClientConfig().getPassword());
				client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
		}
		
		try {
			URL url = new URL(config.getUrl());
			host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		}
		catch (MalformedURLException e) {
			throw new GephiStreamingException(e);
		}
	}
	
	@Override
	protected void doSendCommand(String command) throws GephiStreamingException {
		HttpContext context = new BasicHttpContext(null);
		
		HttpPost post = new HttpPost(getClientConfig().getUrl());
		post.setEntity(new StringEntity(command, ContentType.APPLICATION_JSON));
		
		addHeaders(post, context);
		
		HttpResponse response;
		try {
			response = client.execute(host, post, context);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new GephiStreamingException(format("got status code '%s' from streaming server", 
						response.getStatusLine().getStatusCode()));
			}
		}
		catch (IOException e) {
			throw new GephiStreamingException(e);
		}
		finally {
			if (post != null) {
				post.reset();
			}
		}
	}
	
	protected void addHeaders(HttpPost post, HttpContext context) {
		post.addHeader("User-agent", userAgent);
		//addAuth(post, context);
	}
	
	/*protected void addAuth(HttpPost post, HttpContext context) throws GephiStreamingException {
		if (null == creds && 
			(getClientConfig().getUsername() != null ||
			 getClientConfig().getPassword() != null)) {
			creds = new UsernamePasswordCredentials(getClientConfig().getUsername(), 
					getClientConfig().getPassword());
			client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
		}
		
		if (creds != null) {
			try {
				DigestScheme digestor = new DigestScheme();
				Header authHeader = digestor.authenticate(creds, post, context);
				post.addHeader(authHeader);
			}
			catch (AuthenticationException e) {
				throw new GephiStreamingException(e);
			}
		}
	}*/
	
}
