package org.gephi.streaming.client;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.gephi.streaming.client.GephiStreamingAbstractClient.EdgeCommand;
import org.gephi.streaming.client.GephiStreamingAbstractClient.NodeCommand;
import org.gephi.streaming.client.exception.GephiStreamingException;
import org.gephi.streaming.client.model.Edge;
import org.gephi.streaming.client.model.GephiClientConfig;
import org.gephi.streaming.client.model.Node;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GephiStreamingAbstractClientTest {
	
	TestClient testClient;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Before
	public void before() {
		testClient = new TestClient();
	}
	
	@Test
	public void testEmptyConstructor() {
		assertThat(testClient.userAgent, notNullValue());
	}
	
	@Test
	public void testConfigConstructor() {
		GephiClientConfig config = new GephiClientConfig("http://localhost:8080");
		TestClient client = new TestClient(config);
		assertThat(client.userAgent, notNullValue());
		assertThat(client.getClientConfig(), equalTo(config));
	}
	
	@Test
	public void testFullConstructor() {
		ObjectMapper mapper = new ObjectMapper();
		GephiClientConfig config = new GephiClientConfig()
			.withUrl("http://localhost:8080")
		    .withUsername("username")
		    .withPassword("password");
		
		TestClient client = new TestClient(config, mapper);
		assertThat(client.userAgent, notNullValue());
		assertThat(client.getClientConfig(), equalTo(config));
		assertThat(client.getJacksonMapper(), equalTo(mapper));
		
	}
	
	@Test
	public void testCommandError() {
		String errorMessage =  "io error";
		TestClient client = spy(testClient);
		doThrow(new RuntimeException(errorMessage)).when(client).doSendCommand(Matchers.any(String.class));
		
		try {
			client.addNode(new Node().withId("A"));
			fail("expected exception");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(GephiStreamingException.class));
			assertThat(e.getCause(), instanceOf(RuntimeException.class));
			assertThat(e.getCause().getMessage(), equalTo(errorMessage));
		}
	}

	
	@Test
	public void testCommandGephiStreamingExceptionError() {
		String errorMessage =  "io error";
		TestClient client = spy(testClient);
		doThrow(new GephiStreamingException(errorMessage)).when(client).doSendCommand(Matchers.any(String.class));
		
		try {
			client.addNode(new Node().withId("A"));
			fail("expected exception");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(GephiStreamingException.class));
			assertThat(e.getCause(), nullValue());
			assertThat(e.getMessage(), equalTo(errorMessage));
		}
	}
	
	@Test
	public void testNodeCommandEnum() {
		for (NodeCommand cmd : NodeCommand.values()) {
			assertThat(cmd, equalTo(NodeCommand.fromString(cmd.toString())));
			assertThat(cmd, equalTo(NodeCommand.fromString(cmd.toString().toUpperCase())));
		}
	}
	
	@Test
	public void testNodeCommandEnumInvalid() {
		try {
			NodeCommand.fromString("asdf__not_a_command__qwerty");
			fail("expected IllegalArgumentException");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
		}
	}
	
	@Test
	public void testEdgeCommandEnum() {
		for (EdgeCommand cmd : EdgeCommand.values()) {
			assertThat(cmd, equalTo(EdgeCommand.fromString(cmd.toString())));
			assertThat(cmd, equalTo(EdgeCommand.fromString(cmd.toString().toUpperCase())));
		}
	}
	
	@Test
	public void testEdgeCommandEnumInvalid() {
		try {
			EdgeCommand.fromString("asdf__not_a_command__qwerty");
			fail("expected IllegalArgumentException");
		}
		catch (Exception e) {
			assertThat(e, instanceOf(IllegalArgumentException.class));
		}
	}
	
	@Test
	public void testAddNodeCommand() throws Exception {
		Node node = generateTestNode("A");
		testClient.addNode(node);
		assertThat(testClient.getCommand(), isCommand(testClient.generateNodeCommand(node, NodeCommand.ADD)));
	}
	
	@Test
	public void testChangeNodeCommand() throws Exception {
		Node node = generateTestNode("A");
		testClient.changeNode(node);
		assertThat(testClient.getCommand(), isCommand(testClient.generateNodeCommand(node, NodeCommand.CHANGE)));
	}
	
	@Test
	public void testDeleteNodeCommand() throws Exception {
		String id = "A";
		testClient.deleteNode(id);
		assertThat(testClient.getCommand(), isCommand(testClient.generateDeleteNodeCommand(id)));
	}
	
	@Test
	public void testAddEdgeCommand() throws Exception {
		Edge edge = generateTestEdge("A");
		testClient.addEdge(edge);
		assertThat(testClient.getCommand(), isCommand(testClient.generateEdgeCommand(edge, EdgeCommand.ADD)));
	}
	
	@Test
	public void testChangeEdgeCommand() throws Exception {
		Edge edge = generateTestEdge("A");
		testClient.changeEdge(edge);
		assertThat(testClient.getCommand(), isCommand(testClient.generateEdgeCommand(edge, EdgeCommand.CHANGE)));
	}
	
	@Test
	public void testDeleteEdgeCommand() throws Exception {
		String id = "A";
		testClient.deleteEdge(id);
		assertThat(testClient.getCommand(), isCommand(testClient.generateDeleteEdgeCommand(id)));
	}
	
	public Node generateTestNode(String id) {
		return new Node()
			.withId(id)
			.withLabel(id + "_label")
			.withSize(1L)
			.withAttribute("foo", "bar")
			.withAttribute("biz", 1);
	}
	
	public Edge generateTestEdge(String id) {
		return new Edge()
			.withId(id)
			.withDirected(false)
			.withSource("A")
			.withTarget("B")
			.withWeight(1L)
			.withAttribute("foo", "bar")
			.withAttribute("biz", 1);
	}
	
	class TestClient extends GephiStreamingAbstractClient {
		
		private String command;
		
		public TestClient() {
			super();
		}
		
		public TestClient(GephiClientConfig config) {
			super(config);
		}
		
		public TestClient(GephiClientConfig config, ObjectMapper mapper) {
			super(config, mapper);
		}

		@Override
		protected void doSendCommand(String command)
				throws GephiStreamingException {
			this.command = command;
			logger.debug(command);
		}
		
		public String getCommand() {
			return command;
		}
	}
	
	public TypeSafeMatcher<String> isCommand(final Map<String, Object> expected) {
		return new TypeSafeMatcher<String>() {
			
			ObjectMapper mapper = new ObjectMapper();

			@Override
			public void describeTo(Description arg0) {
				// TODO Auto-generated method stub
				
			}

			@SuppressWarnings("unchecked")
			@Override
			protected boolean matchesSafely(String command) {
				try {
					Map<String, Object> cmd = mapper.readValue(command, HashMap.class);
					
					return mapsHaveSameKeysAndValues(cmd, expected);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected boolean mapsHaveSameKeysAndValues(Map<String, Object> a, Map<String, Object> b) {
				if (a != null ^ b != null) {
					return false;
				}
				
				if (null == a && null == b) {
					return true;
				}
				
				if (a.size() != b.size()) {
					return false;
				}
				
				for(Entry<String, Object> aEntry : a.entrySet()) {
					if (!b.containsKey(aEntry.getKey())) {
						return false;
					}
					
					Object aVal = aEntry.getValue();
					Object bVal = b.get(aEntry.getKey());
					
					if (aVal == null ^ null == bVal) {
						return false;
					}
					
					if (aVal == null && null == bVal) {
						return true;
					}
					
					if (aVal instanceof Map && bVal instanceof Map) {
						return mapsHaveSameKeysAndValues((Map)aVal, (Map)bVal);
					}
					else {
						if (aVal instanceof Long ^ bVal instanceof Long) {
							Long aLong = null;
							if (aVal instanceof Long) {
								aLong = (Long) aVal;
							}
							else if (aVal instanceof Integer) {
								aLong = ((Integer)aVal).longValue();
							}
							
							Long bLong = null;
							if (bVal instanceof Long) {
								bLong = (Long) bVal;
							}
							else if (bVal instanceof Integer) {
								bLong = ((Integer)bVal).longValue();
							}
							
							if (aLong != null) {
								return aLong.equals(bLong);
							}
							else {
								return false;
							}
						}
						return aVal.equals(bVal);
					}
				}
				
				return true;
			}
			
		};
	}

}
