module ZeroPointLaunch.Core {
    requires cn.hutool;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.apache.commons.compress;

    exports com.github.wohaopa.zeropointlanuch.api;
    exports com.github.wohaopa.zeropointlanuch.core;
    exports com.github.wohaopa.zeropointlanuch.core.download;
    exports com.github.wohaopa.zeropointlanuch.core.auth;
    exports com.github.wohaopa.zeropointlanuch.core.tasks;
    exports com.github.wohaopa.zeropointlanuch.core.launch;
}