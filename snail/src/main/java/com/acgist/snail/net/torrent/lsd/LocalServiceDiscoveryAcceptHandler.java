package com.acgist.snail.net.torrent.lsd;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.acgist.snail.net.UdpAcceptHandler;
import com.acgist.snail.net.UdpMessageHandler;

/**
 * <p>本地发现接收器</p>
 * 
 * @author acgist
 */
public final class LocalServiceDiscoveryAcceptHandler extends UdpAcceptHandler {

	private static final LocalServiceDiscoveryAcceptHandler INSTANCE = new LocalServiceDiscoveryAcceptHandler();
	
	public static final LocalServiceDiscoveryAcceptHandler getInstance() {
		return INSTANCE;
	}
	
	/**
	 * <p>禁止创建实例</p>
	 */
	private LocalServiceDiscoveryAcceptHandler() {
	}

	/**
	 * <p>本地发现消息代理</p>
	 */
	private final LocalServiceDiscoveryMessageHandler localServiceDiscoveryMessageHandler = new LocalServiceDiscoveryMessageHandler();
	
	@Override
	public UdpMessageHandler messageHandler(ByteBuffer buffer, InetSocketAddress socketAddress) {
		return this.localServiceDiscoveryMessageHandler;
	}

}
