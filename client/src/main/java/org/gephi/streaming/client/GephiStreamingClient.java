package org.gephi.streaming.client;

import org.gephi.streaming.client.annotations.Idempotent;
import org.gephi.streaming.client.annotations.ThreadSafe;
import org.gephi.streaming.client.model.Edge;
import org.gephi.streaming.client.model.Node;

/**
 * Client for <a href="http://wiki.gephi.org/index.php/Graph_Streaming">the Gephi Streaming API</a>.
 * 
 * @author Caleb Jones &lt;<a href="mailto:calebjones@gmail.com">calebjones@gmail.com</a>&gt;
 */
@ThreadSafe
public interface GephiStreamingClient {
	
	public Node getNode(String nodeId);
	
	public Edge getEdge(String edgeId);
	
	/**
	 * Adds {@link Node} to graph
	 * @param node {@link Node} to add
	 */
	@Idempotent
	public void addNode(Node node);
	
	/**
	 * Changes {@link Node} already in graph
	 * @param node {@link Node} to change (replaces information)
	 */
	@Idempotent
	public void changeNode(Node node);
	
	/**
	 * Deletes {@link Node} from graph with specified id
	 * @param nodeId {@link Node} id
	 */
	@Idempotent
	public void deleteNode(String nodeId);
	
	/**
	 * Adds {@link Edge} between two {@link Node}s in graph
	 * @param edge {@link Edge} to add
	 */
	@Idempotent
	public void addEdge(Edge edge);
	
	/**
	 * Changes {@link Edge} between two nodes in graph
	 * @param edge {@link Edge} to change (replaces information)
	 */
	@Idempotent
	public void changeEdge(Edge edge);
	
	/**
	 * Deletes {@link Edge} from graph with specified id
	 * @param edgeId {@link Edge} id
	 */
	@Idempotent
	public void deleteEdge(String edgeId);

}
