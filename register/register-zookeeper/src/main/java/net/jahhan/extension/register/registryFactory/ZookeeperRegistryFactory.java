package net.jahhan.extension.register.registryFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.jahhan.common.extension.annotation.Extension;
import net.jahhan.common.extension.context.Node;
import net.jahhan.register.api.Registry;
import net.jahhan.spi.register.RegistryFactory;
import net.jahhan.zookeeper.ZookeeperRegistry;

@Extension("zookeeper")
@Singleton
public class ZookeeperRegistryFactory implements RegistryFactory {
	@Inject
	private ZookeeperRegistry zookeeperRegistry;

	@Override
	public Registry getRegistry(Node node) {
		zookeeperRegistry.init(node);
		return zookeeperRegistry;
	}

}