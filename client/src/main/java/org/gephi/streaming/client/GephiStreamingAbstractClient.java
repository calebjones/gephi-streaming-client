package org.gephi.streaming.client;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.gephi.streaming.client.exception.GephiStreamingException;
import org.gephi.streaming.client.model.Edge;
import org.gephi.streaming.client.model.GephiClientConfig;
import org.gephi.streaming.client.model.Node;


/**
 * Abstract base class for clients implementing {@link GephiStreamingClient}. Creates appropriate 
 * command strings and delegates to {@link #doSendCommand(String)} to make client implementation 
 * simpler.
 * 
 * @author Caleb Jones &lt;<a href="mailto:calebjones@gmail.com">calebjones@gmail.com</a>&gt;
 */
public abstract class GephiStreamingAbstractClient implements GephiStreamingClient {
	
	/**
	 * Enum of node command types.
	 */
	public static enum NodeCommand {
		ADD("an"),
		CHANGE("cn");
		
		private String key;
		
		private NodeCommand(String key) {
			this.key = key;
		}
		
		public String toString() {
			return key;
		}
		
		public static NodeCommand fromString(String key) {
			for (NodeCommand cmd : NodeCommand.values()) {
				if (cmd.toString().equalsIgnoreCase(key)) {
					return cmd;
				}
			}
			
			throw new IllegalArgumentException(format("invalid key '%s' for NodeCommand", key));
		}
	}
	
	/**
	 * Enum of edge command types.
	 */
	public static enum EdgeCommand {
		ADD("ae"),
		CHANGE("ce");
		
		private String key;
		
		private EdgeCommand(String key) {
			this.key = key;
		}
		
		public String toString() {
			return key;
		}
		
		public static EdgeCommand fromString(String key) {
			for (EdgeCommand cmd : EdgeCommand.values()) {
				if (cmd.toString().equalsIgnoreCase(key)) {
					return cmd;
				}
			}
			
			throw new IllegalArgumentException(format("invalid key '%s' for EdgeCommand", key));
		}
	}

	
	private ObjectMapper jacksonMapper = new ObjectMapper();
	
	private GephiClientConfig clientConfig;
	
	protected String userAgent;
	
	public GephiStreamingAbstractClient() {
		generateUserAgent();
	}
	
	public GephiStreamingAbstractClient(GephiClientConfig clientConfig) {
		this.clientConfig = clientConfig;
		generateUserAgent();
	}
	
	public GephiStreamingAbstractClient(GephiClientConfig clientConfig, ObjectMapper jacksonMapper) {
		this.clientConfig = clientConfig;
		this.jacksonMapper = jacksonMapper;
		generateUserAgent();
	}
	
	@Override
	public Node getNode(String nodeId) {
		throw new RuntimeException("not yet implemented");
	}
	
	@Override
	public Edge getEdge(String edgeId) {
		throw new RuntimeException("not yet implemented");
	}
	
	@Override
	public void addNode(Node node) {
		Map<String, Object> command = generateNodeCommand(node, NodeCommand.ADD);
		sendCommand(command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeNode(Node node) {
		Map<String, Object> command = generateNodeCommand(node, NodeCommand.CHANGE);
		sendCommand(command);
	}

	@Override
	public void deleteNode(String nodeId) {
		Map<String, Object> command = generateDeleteNodeCommand(nodeId);
		sendCommand(command);
	}

	@Override
	public void addEdge(Edge edge) {
		Map<String, Object> command = generateEdgeCommand(edge, EdgeCommand.ADD);
		sendCommand(command);
	}

	@Override
	public void changeEdge(Edge edge) {
		Map<String, Object> command = generateEdgeCommand(edge, EdgeCommand.CHANGE);
		sendCommand(command);
	}

	@Override
	public void deleteEdge(String edgeId) {
		Map<String, Object> command = generateDeleteEdgeCommand(edgeId);
		sendCommand(command);
	}
	
	protected void sendCommand(Map<String, Object> command) {
		try {
			String body = jacksonMapper.writeValueAsString(command);
			doSendCommand(body);
		}
		catch (GephiStreamingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GephiStreamingException(e);
		}
	}
	
	/**
	 * 
	 * @param command
	 * @throws GephiStreamingException
	 */
	protected abstract void doSendCommand(String command) throws GephiStreamingException;
	
	
	protected Map<String, Object> generateDeleteNodeCommand(String nodeId) {
		Map<String, Object> inner = new HashMap<String, Object>();
		inner.put(nodeId, new HashMap<String, Object>(0));
		
		Map<String,Object> nodeCommand = new HashMap<String, Object>();
		nodeCommand.put("dn", inner);
		
		return nodeCommand;
	}
	
	
	protected Map<String, Object> generateDeleteEdgeCommand(String edgeId) {
		Map<String, Object> inner = new HashMap<String, Object>();
		inner.put(edgeId, new HashMap<String, Object>(0));
		
		Map<String,Object> edgeCommand = new HashMap<String, Object>();
		edgeCommand.put("de", inner);
		
		return edgeCommand;
	}

	
	protected Map<String, Object> generateEdgeCommand(Edge edge, EdgeCommand command) {
		Map<String, Object> inner = new HashMap<String, Object>();
		inner.put(edge.getId(), generateEdgeMap(edge));
		
		Map<String,Object> edgeCommand = new HashMap<String, Object>();
		edgeCommand.put(command.toString(), inner);
		
		return edgeCommand;
	}
	
	protected Map<String, Object> generateNodeCommand(Node node, NodeCommand command) {
		Map<String, Object> inner = new HashMap<String, Object>();
		inner.put(node.getId(), generateNodeMap(node));
		
		Map<String,Object> nodeCommand = new HashMap<String, Object>();
		nodeCommand.put(command.toString(), inner);
		
		return nodeCommand;
	}
	
	protected Map<String, Object> generateEdgeMap(Edge edge) {
		Map<String, Object> edgeMap = edge.toMap();
		edgeMap.remove(Edge.ID_KEY);
		return edgeMap;
	}
	
	protected Map<String, Object> generateNodeMap(Node node) {
		Map<String, Object> nodeMap = node.toMap();
		nodeMap.remove(Node.ID_KEY);
		return nodeMap;
	}
	
	protected void generateUserAgent() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("GephiStreamingClient");
		
		// TODO: read version/os/java info
		
		this.userAgent = sb.toString();
	}
	
	
	public GephiClientConfig getClientConfig() {
		return this.clientConfig;
	}
	
	public void setClientConfig(GephiClientConfig config) {
		this.clientConfig = config;
	}

	public ObjectMapper getJacksonMapper() {
		return jacksonMapper;
	}

	public void setJacksonMapper(ObjectMapper jacksonMapper) {
		this.jacksonMapper = jacksonMapper;
	}
	
}
