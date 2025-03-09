
package org.thingsboard.server.dao.device.provision;

public class ProvisionFailedException extends RuntimeException {

    private static final long serialVersionUID = 1673991117668477401L;

    public ProvisionFailedException(String errorMsg) {
        super(errorMsg);
    }

}
