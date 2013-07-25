package org.gephi.streaming.client;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.gephi.streaming.client.model.Edge;
import org.gephi.streaming.client.model.GephiClientConfig;
import org.gephi.streaming.client.model.Node;

public class ExampleCode {
	
	public static void main(String[] args) {
		// setup
		GephiClientConfig config = new GephiClientConfig()
		    .withUrl("http://localhost:8080/workspace0");

		DefaultHttpClient httpClient = 
		    new DefaultHttpClient(new PoolingClientConnectionManager());

		GephiStreamingClient client = 
		    new GephiStreamingApacheClient(config, httpClient);

		// create 4 nodes
		client.addNode(new Node().withId("A").withLabel("A Node").withSize(10L));
		client.addNode(new Node().withId("B").withLabel("B Node").withSize(10L));
		client.addNode(new Node().withId("C").withLabel("C Node").withSize(10L));
		client.addNode(new Node().withId("D").withLabel("D Node").withSize(10L));

		// add edges
		client.addEdge(new Edge("A-B", "A", "B", false, 1L));
		client.addEdge(new Edge("B-C", "B", "C", false, 1L));
		client.addEdge(new Edge("C-D", "C", "D", false, 1L));
		client.addEdge(new Edge("D-A", "D", "A", false, 1L));
	}

}
