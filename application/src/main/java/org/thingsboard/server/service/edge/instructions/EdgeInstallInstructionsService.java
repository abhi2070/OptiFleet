
package org.thingsboard.server.service.edge.instructions;

import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInstructions;

import javax.servlet.http.HttpServletRequest;

public interface EdgeInstallInstructionsService {

    EdgeInstructions getInstallInstructions(Edge edge, String installationMethod, HttpServletRequest request);

    void setAppVersion(String version);
}
