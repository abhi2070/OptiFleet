
package org.thingsboard.server.common.data;

public interface FstStatsService {

    void incrementEncode(Class<?> clazz);

    void incrementDecode(Class<?> clazz);

    void recordEncodeTime(Class<?> clazz, long startTime);

    void recordDecodeTime(Class<?> clazz, long startTime);

}
