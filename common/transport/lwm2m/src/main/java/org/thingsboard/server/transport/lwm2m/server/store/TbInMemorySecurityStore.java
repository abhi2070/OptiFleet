
package org.thingsboard.server.transport.lwm2m.server.store;

import org.eclipse.leshan.core.SecurityMode;
import org.eclipse.leshan.server.security.NonUniqueSecurityInfoException;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.thingsboard.server.transport.lwm2m.secure.TbLwM2MSecurityInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TbInMemorySecurityStore implements TbEditableSecurityStore {
    // lock for the two maps
    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();

    // by client end-point
    protected Map<String, TbLwM2MSecurityInfo> securityByEp = new HashMap<>();

    // by PSK identity
    protected Map<String, TbLwM2MSecurityInfo> securityByIdentity = new HashMap<>();

    public TbInMemorySecurityStore() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityInfo getByEndpoint(String endpoint) {
        readLock.lock();
        try {
            TbLwM2MSecurityInfo securityInfo = securityByEp.get(endpoint);
            if (securityInfo != null ) {
                if (SecurityMode.NO_SEC.equals(securityInfo.getSecurityMode())) {
                    return SecurityInfo.newPreSharedKeyInfo(SecurityMode.NO_SEC.toString(), SecurityMode.NO_SEC.toString(),
                            SecurityMode.NO_SEC.toString().getBytes());
                } else {
                    return securityInfo.getSecurityInfo();
                }
            }
            else {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityInfo getByIdentity(String identity) {
        readLock.lock();
        try {
            TbLwM2MSecurityInfo securityInfo = securityByIdentity.get(identity);
            if (securityInfo != null) {
                return securityInfo.getSecurityInfo();
            } else {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(TbLwM2MSecurityInfo tbSecurityInfo) throws NonUniqueSecurityInfoException {
        writeLock.lock();
        try {
            String identity = null;
            if (tbSecurityInfo.getSecurityInfo() != null) {
                identity = tbSecurityInfo.getSecurityInfo().getIdentity();
                if (identity != null) {
                    TbLwM2MSecurityInfo infoByIdentity = securityByIdentity.get(identity);
                    if (infoByIdentity != null && !tbSecurityInfo.getSecurityInfo().getEndpoint().equals(infoByIdentity.getEndpoint())) {
                        throw new NonUniqueSecurityInfoException("PSK Identity " + identity + " is already used");
                    }
                    securityByIdentity.put(tbSecurityInfo.getSecurityInfo().getIdentity(), tbSecurityInfo);
                }
            }

            TbLwM2MSecurityInfo previous = securityByEp.put(tbSecurityInfo.getEndpoint(), tbSecurityInfo);
            if (previous != null && previous.getSecurityInfo() != null) {
                String previousIdentity = previous.getSecurityInfo().getIdentity();
                if (previousIdentity != null && !previousIdentity.equals(identity)) {
                    securityByIdentity.remove(previousIdentity);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void remove(String endpoint) {
        writeLock.lock();
        try {
            TbLwM2MSecurityInfo securityInfo = securityByEp.remove(endpoint);
            if (securityInfo != null && securityInfo.getSecurityInfo() != null && securityInfo.getSecurityInfo().getIdentity() != null) {
                securityByIdentity.remove(securityInfo.getSecurityInfo().getIdentity());
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public TbLwM2MSecurityInfo getTbLwM2MSecurityInfoByEndpoint(String endpoint) {
        readLock.lock();
        try {
            return securityByEp.get(endpoint);
        } finally {
            readLock.unlock();
        }
    }

}
