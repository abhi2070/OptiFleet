
package org.thingsboard.server.common.data.util;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;

}
