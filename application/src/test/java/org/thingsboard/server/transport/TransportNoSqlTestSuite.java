
package org.thingsboard.server.transport;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;
import org.thingsboard.server.dao.AbstractNoSqlContainer;

@RunWith(ClasspathSuite.class)
@ClasspathSuite.ClassnameFilters({
        "org.thingsboard.server.transport.*.telemetry.timeseries.nosql.*Test",
})
public class TransportNoSqlTestSuite extends AbstractNoSqlContainer {

}
