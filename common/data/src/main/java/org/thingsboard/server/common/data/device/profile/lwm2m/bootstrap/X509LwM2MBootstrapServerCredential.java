
package org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap;

import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MSecurityMode;

public class X509LwM2MBootstrapServerCredential extends AbstractLwM2MBootstrapServerCredential {

    private static final long serialVersionUID = -3740860424558547405L;

    @Override
    public LwM2MSecurityMode getSecurityMode() {
        return LwM2MSecurityMode.X509;
    }
}
