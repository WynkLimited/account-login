package com.wynk.music.constants;

import com.sun.management.UnixOperatingSystemMXBean;
import com.wynk.common.StatsdService;
import com.wynk.config.StatsdConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.management.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicMonitoringManager {

    private static volatile MusicMonitoringManager m_monitoringManager;

    private static final int MEGA_BYTES = 1048576;

    private StatsdConfig statsdConfig;

    private Timer timer = new Timer();
    private StatsdService m_statsdClient;
    private String m_prefix;
    private RuntimeMXBean runtimeBean;



    public static MusicMonitoringManager getInstance(StatsdConfig config)
    {
        if (m_monitoringManager == null)
        {
            synchronized(MusicMonitoringManager.class)
            {
                if (m_monitoringManager == null)
                {
                    m_monitoringManager = new MusicMonitoringManager(config);
                }
            }
        }

        return m_monitoringManager;
    }


    MusicMonitoringManager(StatsdConfig config) {
        this.statsdConfig = config;
        m_prefix = getUniqueInstanceId();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        try {
            m_statsdClient = new StatsdService(statsdConfig.getStatsdServerHost(),
                    statsdConfig.getStatsdServerPort());
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize Statsd client. Error : " + e.getMessage(), e);
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                m_statsdClient.timing(m_prefix + ".jvm.uptime", (int) (runtimeBean.getUptime() / 60000L));

                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage usage = memoryBean.getHeapMemoryUsage();
                m_statsdClient.gauge(m_prefix + ".jvm.mem.initial", Long.valueOf(usage.getInit() / MEGA_BYTES).intValue());
                m_statsdClient.gauge(m_prefix + ".jvm.mem.used", Long.valueOf(usage.getUsed() / MEGA_BYTES).intValue());
                m_statsdClient.gauge(m_prefix + ".jvm.mem.max", Long.valueOf(usage.getMax() / MEGA_BYTES).intValue());
                m_statsdClient.gauge(m_prefix + ".jvm.mem.commited", Long.valueOf(usage.getCommitted() / MEGA_BYTES).intValue());

                ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
                m_statsdClient.gauge(m_prefix + ".jvm.thread", threadBean.getThreadCount());
                OperatingSystemMXBean osStats = ManagementFactory.getOperatingSystemMXBean();
                if (osStats instanceof UnixOperatingSystemMXBean) {
                    UnixOperatingSystemMXBean unixOsStats = (UnixOperatingSystemMXBean) osStats;
                    m_statsdClient.gauge(m_prefix + ".jvm.fd", unixOsStats.getOpenFileDescriptorCount());
                }
                m_statsdClient.gauge(m_prefix + ".jvm.load", (int) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());


            }
        }, 30000, 5000);

    }

    public static String getUniqueInstanceId()
    {
        String instanceId = System.getProperty("music.instanceid");
        if (StringUtils.isEmpty(instanceId))
        {
            //process id will change with each deployment and
            //we will have only 1 instance running on one server
            //instanceId = getHostName()+"_"+getProcessId();
            instanceId = getHostName();
            if(StringUtils.isEmpty(instanceId))
                throw new RuntimeException("invalid music.instanceid");
        }

        return instanceId;
    }

    public static String getHostName() {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                return null;
            }
        }
    }

    public void error(String error)
    {
        m_statsdClient.increment(m_prefix + ".errors." + error, 1, 1.0);
    }

    public void increment(String key)
    {
        m_statsdClient.increment(key);
    }
}
