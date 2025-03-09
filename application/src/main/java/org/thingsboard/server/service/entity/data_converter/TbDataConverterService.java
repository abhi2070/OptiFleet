package org.thingsboard.server.service.entity.data_converter;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DataConverterId;
import org.thingsboard.server.common.data.id.TenantId;

public interface TbDataConverterService {
    DataConverter save(DataConverter dataConverter, User user) throws Exception;

    void delete(DataConverter dataConverter, User user);

    DataConverter assignDataConverterToCustomer(TenantId tenantId, DataConverterId dataConverterId, Customer customer, User user) throws ThingsboardException;

    DataConverter unassignDataConverterToCustomer(TenantId tenantId, DataConverterId dataConverterId, Customer customer, User user) throws ThingsboardException;

    DataConverter assignDataConverterToPublicCustomer(TenantId tenantId, DataConverterId dataConverterId, User user) throws ThingsboardException;

    DataConverter assignDataConverterToEdge(TenantId tenantId, DataConverterId dataConverterId, Edge edge, User user) throws ThingsboardException;

    DataConverter unassignDataConverterFromEdge(TenantId tenantId, DataConverter dataConverter, Edge edge, User user) throws ThingsboardException;

//    DataConverter testDecoder(TenantId tenantId, DataConverterId dataConverterId, String testPayload);

}

