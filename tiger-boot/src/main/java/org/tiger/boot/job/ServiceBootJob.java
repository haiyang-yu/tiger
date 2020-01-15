package org.tiger.boot.job;

import org.apache.commons.lang3.StringUtils;
import org.tiger.api.listener.Listener;
import org.tiger.api.service.Server;
import org.tiger.api.zk.node.ServiceNode;
import org.tiger.common.log.TigerLog;
import org.tiger.spi.factory.zk.ServiceRegistryFactory;

import java.util.Objects;

/**
 * {@link ServiceBootJob}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 18:05 周二
 */
public class ServiceBootJob extends BaseBootJob {

    private final Server server;
    private final ServiceNode node;

    public ServiceBootJob(Server server) {
        this(server, null);
    }

    public ServiceBootJob(Server server, ServiceNode node) {
        this.server = server;
        this.node = node;
    }

    @Override
    protected void start() {
        server.start(new Listener() {
            @Override
            public void onSuccess(Object... args) {
                TigerLog.CONSOLE.info("start {} success on:{}", server.getClass().getSimpleName(), StringUtils.joinWith(",", args));
                if (Objects.nonNull(node)) {
                    ServiceRegistryFactory.create().register(node);
                }
                startNext();
            }

            @Override
            public void onFailure(Throwable cause) {
                TigerLog.CONSOLE.error("start {} failure, jvm with exit code -1", server.getClass().getSimpleName());
                System.exit(-1);
            }
        });
    }

    @Override
    protected void stop() {
        stopNext();
        if (Objects.nonNull(node)) {
            ServiceRegistryFactory.create().deregister(node);
        }
        TigerLog.CONSOLE.info("try shutdown {}...", server.getClass().getSimpleName());
        server.stop().join();
        TigerLog.CONSOLE.info("{} shutdown success.", server.getClass().getSimpleName());
    }

    @Override
    protected String getName() {
        return super.getName() + "(" + server.getClass().getSimpleName() + ")";
    }
}
