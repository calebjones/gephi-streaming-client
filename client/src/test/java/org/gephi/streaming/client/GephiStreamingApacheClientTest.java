package org.gephi.streaming.client;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.gephi.streaming.client.exception.GephiStreamingException;
import org.gephi.streaming.client.model.GephiClientConfig;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultHttpClient.class)
public class GephiStreamingApacheClientTest {
	
	@Test
	public void testConstructorMalformedUrl() {
		try {
			new GephiStreamingApacheClient(new GephiClientConfig().withUrl("foo"), null);
			fail("expected exception");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(GephiStreamingException.class));
			assertThat(e.getCause(), instanceOf(MalformedURLException.class));
		}
	}
	
	@Test
	public void testConstructorNoAuth() {
		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(new GephiClientConfig().withUrl("http://localhost"), httpClient);
		assertThat(client.userAgent, notNullValue());
		verifyNoMoreInteractions(httpClient);
	}
	
	@Test
	public void testConstructorWithAuth() {
		GephiClientConfig config = new GephiClientConfig()
			.withUrl("http://localhost")
			.withUsername("erdös")
			.withPassword("renyi");

		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		CredentialsProvider provider = mock(CredentialsProvider.class);
		
		doReturn(provider).when(httpClient).getCredentialsProvider();
		ArgumentCaptor<UsernamePasswordCredentials> credsArg = 
				ArgumentCaptor.forClass(UsernamePasswordCredentials.class);
		doNothing().when(provider).setCredentials(Matchers.eq(AuthScope.ANY), credsArg.capture());
		
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(config, httpClient);
		
		assertThat(credsArg.getValue().getUserName(), equalTo(config.getUsername()));
		assertThat(credsArg.getValue().getPassword(), equalTo(config.getPassword()));
		
		verify(provider).setCredentials(Matchers.eq(AuthScope.ANY), credsArg.capture());
		verifyNoMoreInteractions(provider);
	}
	
	@Test
	public void testAddHeaders() {

		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(new GephiClientConfig().withUrl("http://localhost"), null);
		
		GephiStreamingApacheClient spyClient = spy(client);
		//doNothing().when(spyClient).addAuth(Matchers.any(HttpPost.class), Matchers.any(HttpContext.class));
		
		HttpPost post = mock(HttpPost.class);
		doNothing().when(post).addHeader(Matchers.anyString(), Matchers.anyString());
		HttpContext context = mock(HttpContext.class);
		
		
		spyClient.addHeaders(post, context);
		
		verify(post).addHeader("User-agent", client.userAgent);
		//verify(spyClient).addAuth(Matchers.any(HttpPost.class), Matchers.any(HttpContext.class));
	}
	
	@Test
	public void testDoSendCommand() throws IOException, URISyntaxException {
		GephiClientConfig config = new GephiClientConfig()
			.withUrl("http://localhost")
			.withUsername("erdös")
			.withPassword("renyi");
		
		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		CredentialsProvider provider = new BasicCredentialsProvider();
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		
		ArgumentCaptor<HttpHost> hostArg = ArgumentCaptor.forClass(HttpHost.class);
		ArgumentCaptor<HttpPost> postArg = ArgumentCaptor.forClass(HttpPost.class);
		ArgumentCaptor<HttpContext> contextArg = ArgumentCaptor.forClass(HttpContext.class);

		doReturn(provider).when(httpClient).getCredentialsProvider();
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();
		doReturn(statusLine).when(response).getStatusLine();
		doReturn(response).when(httpClient).execute(hostArg.capture(), postArg.capture(), contextArg.capture());
		
		
		String body = "{}";
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(config, httpClient);
		client.doSendCommand(body);
		
		assertThat(hostArg.getValue().getHostName(), equalTo(new URI(config.getUrl()).getHost()));
		
		assertThat(postArg.getValue().getEntity(), instanceOf(StringEntity.class));
		assertThat(postArg.getValue().getEntity().getContentType().getValue(), 
						equalTo(ContentType.APPLICATION_JSON.toString()));
		StringWriter writer = new StringWriter();
		IOUtils.copy(postArg.getValue().getEntity().getContent(), writer, "UTF-8");
		assertThat(writer.toString(), equalTo(body));
		assertThat(postArg.getValue(), hasUserAgentHeader());
		
		assertThat(contextArg.getValue(), notNullValue());
	}
	
	@Test
	public void testDoSendCommandWithNoAuth() throws IOException, URISyntaxException {
		GephiClientConfig config = new GephiClientConfig("http://localhost");
		
		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		
		ArgumentCaptor<HttpHost> hostArg = ArgumentCaptor.forClass(HttpHost.class);
		ArgumentCaptor<HttpPost> postArg = ArgumentCaptor.forClass(HttpPost.class);
		ArgumentCaptor<HttpContext> contextArg = ArgumentCaptor.forClass(HttpContext.class);
		
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();
		doReturn(statusLine).when(response).getStatusLine();
		doReturn(response).when(httpClient).execute(hostArg.capture(), postArg.capture(), contextArg.capture());
		
		String body = "{}";
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(config, httpClient);
		client.doSendCommand(body);
		
		assertThat(hostArg.getValue().getHostName(), equalTo(new URI(config.getUrl()).getHost()));
		
		assertThat(postArg.getValue().getEntity(), instanceOf(StringEntity.class));
		assertThat(postArg.getValue().getEntity().getContentType().getValue(), 
						equalTo(ContentType.APPLICATION_JSON.toString()));
		StringWriter writer = new StringWriter();
		IOUtils.copy(postArg.getValue().getEntity().getContent(), writer, "UTF-8");
		assertThat(writer.toString(), equalTo(body));
		assertThat(postArg.getValue(), hasUserAgentHeader());
		
		assertThat(contextArg.getValue(), notNullValue());
	}

	@Test
	public void testDoSendCommandWithExecutException() throws IOException, URISyntaxException {
		GephiClientConfig config = new GephiClientConfig("http://localhost");
		
		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		doThrow(new IOException()).when(httpClient).execute(Matchers.any(HttpHost.class), 
													Matchers.any(HttpPost.class), 
													Matchers.any(HttpContext.class));
		
		String body = "{}";
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(config, httpClient);
		
		try {
			client.doSendCommand(body);
			fail("expected exception");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(GephiStreamingException.class));
			assertThat(e.getCause(), instanceOf(IOException.class));
		}
	}

	@Test
	public void testDoSendCommandBadStatusCode() throws IOException, URISyntaxException {
		GephiClientConfig config = new GephiClientConfig("http://localhost");
		
		DefaultHttpClient httpClient = PowerMockito.mock(DefaultHttpClient.class);
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		
		doReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR).when(statusLine).getStatusCode();
		doReturn(statusLine).when(response).getStatusLine();
		doReturn(response).when(httpClient).execute(Matchers.any(HttpHost.class), 
													Matchers.any(HttpPost.class), 
													Matchers.any(HttpContext.class));
		
		String body = "{}";
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(config, httpClient);
		
		try {
			client.doSendCommand(body);
			fail("expected exception");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(GephiStreamingException.class));
			assertThat(e.getMessage(), containsString("" + HttpStatus.SC_INTERNAL_SERVER_ERROR));
		}
	}
	
	protected TypeSafeMatcher<HttpPost> hasUserAgentHeader() {
		return new TypeSafeMatcher<HttpPost>() {

			@Override
			public void describeTo(Description desc) {
				desc.appendText("no 'User-agent header found");
			}

			@Override
			protected boolean matchesSafely(HttpPost item) {
				for (Header header : item.getAllHeaders()) {
					if (header.getName().equals("User-agent")) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	
	/*
	 * 	@Override
	protected void doSendCommand(String command) throws GephiStreamingException {
		HttpContext context = new BasicHttpContext(null);
		
		HttpPost post = new HttpPost(getClientConfig().getUrl());
		post.setEntity(new StringEntity(command, ContentType.APPLICATION_JSON));
		
		addHeaders(post, context);
		
		try {
			HttpResponse response = client.execute(host, post, context);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new GephiStreamingException(format("got status code '%s' from streaming server", 
						response.getStatusLine().getStatusCode()));
			}
		}
		catch (IOException e) {
			throw new GephiStreamingException(e);
		}
	}
	 */
	
	/*@Test
	public void testAddAuthNoCreds() {
		GephiStreamingApacheClient client = 
				new GephiStreamingApacheClient(new GephiClientConfig().withUrl("http://localhost"), null);
		
		HttpPost post = mock(HttpPost.class);
		HttpContext context = mock(HttpContext.class);
		//client.addAuth(post, context);
		verifyNoMoreInteractions(post, context);
	}*/
	
	/*@Test
	public void testAddAuthWithCreds() {
		GephiClientConfig config = new GephiClientConfig()
			.withUrl("http://localhost")
			.withUsername("erdös")
			.withPassword("renyi");
		
		GephiStreamingApacheClient client = new GephiStreamingApacheClient(config, null);
		
		HttpPost post = mock(HttpPost.class);
		ArgumentCaptor<Header> headerArg = ArgumentCaptor.forClass(Header.class);
		doNothing().when(post).addHeader(headerArg.capture());
		
		HttpContext context = mock(HttpContext.class);
		
		client.addAuth(post, context);
		
		assertThat(headerArg.getValue().getName(), equalTo("Auth"));
		assertThat(headerArg.getValue().getValue(), equalTo("abcd"));
		
		verify(post).addHeader(headerArg.getValue());
		verifyNoMoreInteractions(post, context);
	}*/
	
	/*
	 	protected void addAuth(HttpPost post, HttpContext context) throws GephiStreamingException {
		if (null == creds && 
			(getClientConfig().getUsername() != null ||
			 getClientConfig().getPassword() != null)) {
			creds = new UsernamePasswordCredentials(getClientConfig().getUsername(), 
					getClientConfig().getPassword());
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
	}
	 */

}
