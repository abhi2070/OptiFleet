
package org.thingsboard.server.transport.lwm2m.bootstrap.store;

import org.eclipse.leshan.server.bootstrap.BootstrapConfig;
import org.eclipse.leshan.server.bootstrap.ConfigurationChecker;
import org.eclipse.leshan.server.bootstrap.InvalidConfigurationException;

import java.util.Map;

public class LwM2MConfigurationChecker extends ConfigurationChecker {

    @Override
    public void verify(BootstrapConfig config) throws InvalidConfigurationException {
        // check security configurations
        for (Map.Entry<Integer, BootstrapConfig.ServerSecurity> e : config.security.entrySet()) {
            BootstrapConfig.ServerSecurity sec = e.getValue();

            // checks security config
            switch (sec.securityMode) {
                case NO_SEC:
                    checkNoSec(sec);
                    break;
                case PSK:
                    checkPSK(sec);
                    break;
                case RPK:
                    checkRPK(sec);
                    break;
                case X509:
                    checkX509(sec);
                    break;
                case EST:
                    throw new InvalidConfigurationException("EST is not currently supported.", e);
            }

            validateMandatoryField(sec);
        }

        // does each server have a corresponding security entry?
        validateOneSecurityByServer(config);
    }

    protected void validateOneSecurityByServer(BootstrapConfig config) throws InvalidConfigurationException {
        for (Map.Entry<Integer, BootstrapConfig.ServerConfig> e : config.servers.entrySet()) {
            BootstrapConfig.ServerConfig srvCfg = e.getValue();

            // shortId checks
            if (srvCfg.shortId == 0) {
                throw new InvalidConfigurationException("short ID must not be 0");
            }

            // look for security entry
            BootstrapConfig.ServerSecurity security = getSecurityEntry(config, srvCfg.shortId);

            if (security == null) {
                throw new InvalidConfigurationException("no security entry for server instance: " + e.getKey());
            }
        }
    }

    protected static BootstrapConfig.ServerSecurity getSecurityEntry(BootstrapConfig config, int shortId) {
        for (Map.Entry<Integer, BootstrapConfig.ServerSecurity> es : config.security.entrySet()) {
            if (es.getValue().serverId == shortId) {
                return es.getValue();
            }
        }
        return null;
    }

}