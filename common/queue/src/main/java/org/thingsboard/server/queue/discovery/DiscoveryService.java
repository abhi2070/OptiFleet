
package org.thingsboard.server.queue.discovery;

import org.thingsboard.server.gen.transport.TransportProtos;

import java.util.List;

public interface DiscoveryService {

    List<TransportProtos.ServiceInfo> getOtherServers();

    boolean isMonolith();

}
