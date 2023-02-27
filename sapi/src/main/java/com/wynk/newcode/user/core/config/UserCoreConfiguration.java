package com.wynk.newcode.user.core.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Configuration
public class UserCoreConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(UserCoreConfiguration.class.getCanonicalName());
    @Value("${cassandra.contactpoints}")
    private String              cassandraIPs;
    @Value("${cassandra.port}")
    private int                 cassandraPort;
    @Value("${cassandra.user.keyspace}")
    private String              cassandraKeySpace;
    @Value("${cassandra.username}")
    private String              cassandraUsername;
    @Value("${cassandra.password}")
    private String              cassandraPassword;

    @Bean
    public CassandraOperations cassandraOperations() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 2, 8);
        poolingOptions.setConnectionsPerHost(HostDistance.REMOTE, 2, 8);

        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(5000);
        socketOptions.setKeepAlive(true);
        socketOptions.setReuseAddress(true);
        socketOptions.setSoLinger(60);
        socketOptions.setTcpNoDelay(true);
        socketOptions.setReadTimeoutMillis(60000);
        String[] ips = cassandraIPs.split(",");
        String cassandra_user = System.getenv("cassandra_user");
        String cassandra_password = System.getenv("cassandra_password");
        if(StringUtils.isNotBlank(cassandra_user) && StringUtils.isNotBlank(cassandra_password)) {
            cassandraUsername = cassandra_user;
            cassandraPassword = cassandra_password;
        }
        Cluster cluster = Cluster.builder().addContactPoints(ips).withPoolingOptions(poolingOptions).withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                .withReconnectionPolicy(new ConstantReconnectionPolicy(10000)).withPort(cassandraPort).withCredentials(cassandraUsername, cassandraPassword).build();
        com.datastax.driver.core.Session session = cluster.connect(cassandraKeySpace);

        CassandraOperations cassandraOps = new CassandraTemplate(session);
        return cassandraOps;
    }

}
