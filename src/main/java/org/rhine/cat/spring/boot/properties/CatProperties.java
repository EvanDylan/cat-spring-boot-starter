package org.rhine.cat.spring.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "public.cat")
public class CatProperties {

    /**
     * 服务器列表,格式为ip:httpPort:port,如127.0.0.1:8080:2080
     */

    private String servers;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
