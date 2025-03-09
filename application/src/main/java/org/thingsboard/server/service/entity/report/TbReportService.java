package org.thingsboard.server.service.entity.report;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

import java.util.Set;

public interface TbReportService extends SimpleTbEntityService<Report> {

    Report assignReportToCustomer(Report report, Customer customer, User user) throws ThingsboardException;

    Report updateReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException;

    Report addReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException;

    Report removeReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException;
}
