
package org.thingsboard.server.service.edge.rpc.constructor.customer;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.gen.edge.v1.CustomerUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface CustomerMsgConstructor extends MsgConstructor {

    CustomerUpdateMsg constructCustomerUpdatedMsg(UpdateMsgType msgType, Customer customer);

    CustomerUpdateMsg constructCustomerDeleteMsg(CustomerId customerId);
}
