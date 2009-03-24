package org.springframework.flex.config;

import java.util.Arrays;
import java.util.Iterator;

import org.springframework.flex.config.BeanIds;
import org.springframework.flex.config.xml.RemotingDestinationBeanDefinitionParserTests.TestAdapter;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingExclude;
import org.springframework.flex.remoting.RemotingInclude;

import flex.messaging.MessageBroker;
import flex.messaging.services.RemotingService;
import flex.messaging.services.remoting.adapters.JavaAdapter;
import flex.messaging.services.remoting.adapters.RemotingMethod;


public class RemotingAnnotationPostProcessorTests extends
		AbstractFlexConfigurationTests {

	private MessageBroker broker;
	
	public void testExportAnnotatedXmlConfiguredBeanWithDefaults() {
		broker = (MessageBroker) getApplicationContext().getBean(BeanIds.MESSAGE_BROKER, MessageBroker.class);
		assertNotNull("MessageBroker bean not found for default ID", broker);
		RemotingService rs = (RemotingService) broker.getService("remoting-service");
		assertNotNull("Could not find the remoting service", rs);
		flex.messaging.services.remoting.RemotingDestination rd = (flex.messaging.services.remoting.RemotingDestination ) rs.getDestination("annotatedRemoteBean1");
		assertNotNull("Destination not found", rd);
	}
	
	@SuppressWarnings("unchecked")
	public void testExportBeanWithCustomSettings() {
		broker = (MessageBroker) getApplicationContext().getBean("remoteServiceBroker", MessageBroker.class);
		assertNotNull("MessageBroker bean not found for custom id", broker);
		RemotingService rs = (RemotingService) broker.getService("remoting-service");
		assertNotNull("Could not find the remoting service", rs);
		flex.messaging.services.remoting.RemotingDestination rd = (flex.messaging.services.remoting.RemotingDestination) rs.getDestination("exportedAnnotatedRemoteBean2");
		assertNotNull("Destination not found", rd);
		String[] channels = new String[] {"my-amf", "my-secure-amf"};
		assertEquals("Channels not set",Arrays.asList(channels), rd.getChannels());
		
		assertTrue("Custom adapter not set", rd.getAdapter() instanceof TestAdapter);
		
		String[] includeNames = new String[]{ "foo", "bar" };
		String[] excludeNames = new String[]{ "zoo", "baz" };
		
		assertTrue("No included methods found",((JavaAdapter)rd.getAdapter()).getIncludeMethodIterator().hasNext());
		Iterator includes = ((JavaAdapter)rd.getAdapter()).getIncludeMethodIterator();
		while(includes.hasNext()) {
			RemotingMethod include = (RemotingMethod) includes.next();
			assertTrue(Arrays.asList(includeNames).contains(include.getName()));
			assertFalse(Arrays.asList(excludeNames).contains(include.getName()));
		}
		
		assertTrue("No excluded methods found",((JavaAdapter)rd.getAdapter()).getExcludeMethodIterator().hasNext());
		Iterator excludes = ((JavaAdapter)rd.getAdapter()).getExcludeMethodIterator();
		while(includes.hasNext()) {
			RemotingMethod exclude = (RemotingMethod) excludes.next();
			assertTrue(Arrays.asList(excludeNames).contains(exclude.getName()));
			assertFalse(Arrays.asList(includeNames).contains(exclude.getName()));
		}
	}
	
	public void testExportAnnotatedBeanWithDefaults() {
		broker = (MessageBroker) getApplicationContext().getBean(BeanIds.MESSAGE_BROKER, MessageBroker.class);
		assertNotNull("MessageBroker bean not found for default ID", broker);
		RemotingService rs = (RemotingService) broker.getService("remoting-service");
		assertNotNull("Could not find the remoting service", rs);
		flex.messaging.services.remoting.RemotingDestination rd = (flex.messaging.services.remoting.RemotingDestination) rs.getDestination("annotatedRemoteBean");
		assertNotNull("Destination not found", rd);
	}
	
	@RemotingDestination
	public static class MyService1 {}
	
	@RemotingDestination(value="exportedAnnotatedRemoteBean2", messageBroker="remoteServiceBroker", channels={"my-amf", "my-secure-amf"}, serviceAdapter="customAdapter1")
	public static class MyService2 {
		
		@RemotingInclude
		public void foo(){}
		
		@RemotingInclude
		public void bar(){}
		
		@RemotingExclude
		public void zoo(){}
		
		@RemotingExclude
		public void baz(){}
	}
}