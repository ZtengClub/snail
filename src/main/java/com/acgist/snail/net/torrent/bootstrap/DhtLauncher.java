package com.acgist.snail.net.torrent.bootstrap;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.net.torrent.dht.DhtClient;
import com.acgist.snail.net.torrent.dht.bootstrap.NodeManager;
import com.acgist.snail.pojo.bean.InfoHash;
import com.acgist.snail.pojo.session.NodeSession;
import com.acgist.snail.pojo.session.TorrentSession;
import com.acgist.snail.utils.CollectionUtils;
import com.acgist.snail.utils.NetUtils;

/**
 * <p>DHT任务：定时查询Peer</p>
 * <p>使用系统最近的可用节点和{@link #nodes}查询Peer</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public final class DhtLauncher implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DhtLauncher.class);
	
	/**
	 * <p>种子信息</p>
	 */
	private final InfoHash infoHash;
	/**
	 * <p>客户端节点</p>
	 * <p>如果连接的Peer支持DHT：将Peer放入到队列</p>
	 */
	private final List<InetSocketAddress> nodes = new ArrayList<>();
	
	private DhtLauncher(TorrentSession torrentSession) {
		this.infoHash = torrentSession.infoHash();
	}
	
	public static final DhtLauncher newInstance(TorrentSession torrentSession) {
		return new DhtLauncher(torrentSession);
	}
	
	@Override
	public void run() {
		LOGGER.debug("执行DHT定时任务");
		synchronized (this.nodes) {
			try {
				joinSystemNodes();
				final var list = pick();
				findPeers(list);
			} catch (Exception e) {
				LOGGER.error("执行DHT定时任务异常", e);
			}
		}
	}
	
	/**
	 * <p>Peer客户端加入DHT节点</p>
	 * 
	 * @param host 地址
	 * @param port 端口
	 */
	public void put(String host, Integer port) {
		synchronized (this.nodes) {
			this.nodes.add(NetUtils.buildSocketAddress(host, port));
		}
	}

	/**
	 * <p>将临时节点加入系统中</p>
	 */
	private void joinSystemNodes() {
		this.nodes.forEach(address -> {
			NodeManager.getInstance().newNodeSession(address.getHostString(), address.getPort());
		});
	}
	
	/**
	 * <p>挑选DHT节点</p>
	 * 
	 * @return 临时节点和系统节点
	 */
	private List<InetSocketAddress> pick() {
		final List<InetSocketAddress> list = new ArrayList<>();
		// 临时节点
		if(CollectionUtils.isNotEmpty(this.nodes)) {
			list.addAll(this.nodes);
			this.nodes.clear(); // 清空临时节点
		}
		// 系统节点
		final var nodes = NodeManager.getInstance().findNode(this.infoHash.infoHash());
		if(CollectionUtils.isNotEmpty(nodes)) {
			for (NodeSession node : nodes) {
				list.add(NetUtils.buildSocketAddress(node.getHost(), node.getPort()));
			}
		}
		return list;
	}
	
	/**
	 * <p>使用DHT节点查询Peer</p>
	 */
	private void findPeers(List<InetSocketAddress> list) {
		if(CollectionUtils.isEmpty(list)) {
			return;
		}
		for (InetSocketAddress socketAddress : list) {
			final DhtClient client = DhtClient.newInstance(socketAddress);
			client.getPeers(this.infoHash.infoHash());
		}
	}
	
}
