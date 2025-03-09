
package org.thingsboard.server.dao;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.BooleanType;


public class ThingsboardPostgreSQLDialect extends org.hibernate.dialect.PostgreSQL10Dialect {
    public ThingsboardPostgreSQLDialect() {
        super();
        registerFunction("ilike", new SQLFunctionTemplate(BooleanType.INSTANCE, "(?1 ILIKE ?2)"));
    }
}
