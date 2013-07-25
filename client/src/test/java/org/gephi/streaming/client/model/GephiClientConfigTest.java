package org.gephi.streaming.client.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GephiClientConfigTest {
	
	@Test
	public void testConstructorUri() {
		GephiClientConfig config = new GephiClientConfig("http://localhost");
		assertThat(config.getUrl(), equalTo("http://localhost?operation=updateGraph"));
		config.setUrl("http://localhost?");
		assertThat(config.getUrl(), equalTo("http://localhost?operation=updateGraph"));
		config.withUrl("http://localhost?a=b");
		assertThat(config.getUrl(), equalTo("http://localhost?a=b&operation=updateGraph"));
		config.withUrl("http://localhost?a=b&");
		assertThat(config.getUrl(), equalTo("http://localhost?a=b&operation=updateGraph"));
		config.withUrl("http://localhost?a=b&c=d&e=f");
		assertThat(config.getUrl(), equalTo("http://localhost?a=b&c=d&e=f&operation=updateGraph"));
	}

}
