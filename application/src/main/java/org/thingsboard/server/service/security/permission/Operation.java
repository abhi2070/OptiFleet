
package org.thingsboard.server.service.security.permission;

public enum Operation {

    ALL, CREATE, READ, WRITE, DELETE, SEND, ASSIGN_TO_CUSTOMER, UNASSIGN_FROM_CUSTOMER, RPC_CALL,
    READ_CREDENTIALS, WRITE_CREDENTIALS, READ_ATTRIBUTES, WRITE_ATTRIBUTES, READ_TELEMETRY, WRITE_TELEMETRY, CLAIM_DEVICES,
    ASSIGN_TO_TENANT

}
