package org.gephi.streaming.client;

import org.gephi.streaming.client.model.Edge;
import org.gephi.streaming.client.model.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:integrationTest-config.xml"})
public class GephiStreamingApacheClientIT {
	
	@Autowired
	private GephiStreamingClient client;
	
	
	@Test
	public void testCreateStarFormation() {
		// nodes
		client.addNode(new Node("A", "A Node", 5L));
		client.addNode(new Node("B", "B Node", 6L));
		client.addNode(new Node("C", "C Node", 7L));
		client.addNode(new Node("D", "D Node", 8L));
		client.addNode(new Node("E", "E Node", 9L));
		
		// edges
		client.addEdge(new Edge("A-B", "A", "B", false, 1L));
		client.addEdge(new Edge("A-C", "A", "C", false, 2L));
		client.addEdge(new Edge("A-D", "A", "D", false, 3L));
		client.addEdge(new Edge("A-E", "A", "E", false, 4L));
		
		client.addEdge(new Edge("B-A", "B", "A", false, 1L));
		client.addEdge(new Edge("B-C", "B", "C", false, 2L));
		client.addEdge(new Edge("B-D", "B", "D", false, 3L));
		client.addEdge(new Edge("B-E", "B", "E", false, 4L));
		
		client.addEdge(new Edge("C-A", "C", "A", false, 1L));
		client.addEdge(new Edge("C-B", "C", "B", false, 2L));
		client.addEdge(new Edge("C-D", "C", "D", false, 3L));
		client.addEdge(new Edge("C-E", "C", "E", false, 4L));
		
		client.addEdge(new Edge("D-A", "D", "A", false, 1L));
		client.addEdge(new Edge("D-B", "D", "B", false, 2L));
		client.addEdge(new Edge("D-C", "D", "C", false, 3L));
		client.addEdge(new Edge("D-E", "D", "E", false, 4L));
		
		client.addEdge(new Edge("E-A", "E", "A", false, 1L));
		client.addEdge(new Edge("E-B", "E", "B", false, 2L));
		client.addEdge(new Edge("E-C", "E", "C", false, 3L));
		client.addEdge(new Edge("E-D", "E", "D", false, 4L));
		
		// update
		client.changeEdge(new Edge("A-E", "A", "E", false, 3L));
		client.changeEdge(new Edge("B-E", "B", "E", false, 3L));
		client.changeEdge(new Edge("C-E", "C", "E", false, 3L));
		client.changeEdge(new Edge("D-E", "D", "E", false, 3L));
		client.changeEdge(new Edge("E-D", "E", "D", false, 3L));

		client.changeNode(new Node("A", "A Node", 9L));
		client.changeNode(new Node("B", "B Node", 8L));
		client.changeNode(new Node("C", "C Node", 7L));
		client.changeNode(new Node("D", "D Node", 6L));
		client.changeNode(new Node("E", "E Node", 5L));
	}

}
