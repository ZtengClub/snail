package com.acgist.snail.context.initializer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.acgist.snail.context.NodeContext;
import com.acgist.snail.utils.Performance;

public class DhtInitializerTest extends Performance {

	@Test
	public void testDhtInitializer() {
		DhtInitializer.newInstance().sync();
		assertTrue(NodeContext.getInstance().nodes().size() > 0);
	}
	
}
