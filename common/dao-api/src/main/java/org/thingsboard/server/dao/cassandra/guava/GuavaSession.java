
package org.thingsboard.server.dao.cassandra.guava;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.cql.SyncCqlSession;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.internal.core.cql.DefaultPrepareRequest;
import com.datastax.oss.driver.internal.core.cql.SinglePageResultSet;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.concurrent.ExecutionException;

public interface GuavaSession extends Session, SyncCqlSession {

    GenericType<ListenableFuture<AsyncResultSet>> ASYNC =
            new GenericType<ListenableFuture<AsyncResultSet>>() {};

    GenericType<ListenableFuture<PreparedStatement>> ASYNC_PREPARED =
            new GenericType<ListenableFuture<PreparedStatement>>() {};

    @NonNull
    default ResultSet execute(@NonNull Statement<?> statement) {
        AsyncResultSet firstPage = getSafe(this.executeAsync(statement));
        if (firstPage.hasMorePages()) {
            return new GuavaMultiPageResultSet(this, statement, firstPage);
        } else {
            return new SinglePageResultSet(firstPage);
        }
    }

    default ListenableFuture<AsyncResultSet> executeAsync(Statement<?> statement) {
        return this.execute(statement, ASYNC);
    }

    default ListenableFuture<AsyncResultSet> executeAsync(String statement) {
        return this.executeAsync(SimpleStatement.newInstance(statement));
    }

    default ListenableFuture<PreparedStatement> prepareAsync(SimpleStatement statement) {
        return this.execute(new DefaultPrepareRequest(statement), ASYNC_PREPARED);
    }

    default ListenableFuture<PreparedStatement> prepareAsync(String statement) {
        return this.prepareAsync(SimpleStatement.newInstance(statement));
    }

    static AsyncResultSet getSafe(ListenableFuture<AsyncResultSet> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
