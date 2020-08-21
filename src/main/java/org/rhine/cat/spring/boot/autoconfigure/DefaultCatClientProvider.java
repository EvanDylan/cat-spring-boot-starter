package org.rhine.cat.spring.boot.autoconfigure;

import com.dianping.cat.configuration.ClientConfigProvider;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;
import org.rhine.cat.spring.boot.properties.ApplicationProperties;
import org.rhine.cat.spring.boot.properties.CatProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCatClientProvider implements ClientConfigProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCatClientProvider.class);

    private static ApplicationProperties applicationProperties;

    private static CatProperties catProperties;

    private static final char SERVER_DELIMITER = ';';

    private static final String PORT_DELIMITER = ":";

    private static volatile ClientConfig CLIENT_CONFIG = null;

    private static final Object LOCK = new Object();

    @Override
    public ClientConfig getClientConfig() {
        if (CLIENT_CONFIG == null) {
            synchronized (LOCK) {
                if (CLIENT_CONFIG == null) {
                    ClientConfig clientConfig = new ClientConfig();
                    String servers = catProperties.getServers();
                    if (StringUtils.isEmpty(servers)) {
                        logger.warn("没有配置cat服务器地址");
                        return null;
                    }
                    for (String server : StringUtils.split(servers, SERVER_DELIMITER)) {
                        if (StringUtils.isEmpty(server)) {
                            continue;
                        }
                        String[] params = server.split(PORT_DELIMITER);
                        if (params.length != 3) {
                            logger.warn("cat server配置格式有误");
                            continue;
                        }
                        clientConfig.addServer(new Server(params[0]).setHttpPort(Integer.parseInt(params[1]))
                                .setPort(Integer.parseInt(params[2])));
                    }
                    clientConfig.setDomain(applicationProperties.getName());
                    CLIENT_CONFIG = clientConfig;
                }
            }
        }
        return CLIENT_CONFIG;
    }

    public static void setApplicationProperties(ApplicationProperties applicationProperties) {
        DefaultCatClientProvider.applicationProperties = applicationProperties;
    }

    public static void setCatProperties(CatProperties catProperties) {
        DefaultCatClientProvider.catProperties = catProperties;
    }
}
