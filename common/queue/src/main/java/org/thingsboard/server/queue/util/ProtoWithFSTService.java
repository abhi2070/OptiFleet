
package org.thingsboard.server.queue.util;

import lombok.extern.slf4j.Slf4j;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.FSTUtils;
import org.thingsboard.server.common.data.FstStatsService;

import java.util.Optional;

@Slf4j
@Service
public class ProtoWithFSTService implements DataDecodingEncodingService {

    @Autowired
    private FstStatsService fstStatsService;

    public static final FSTConfiguration CONFIG = FSTConfiguration.createDefaultConfiguration();

    @Override
    public <T> Optional<T> decode(byte[] byteArray) {
        try {
            long startTime = System.nanoTime();
            Optional<T> optional = Optional.ofNullable(FSTUtils.decode(byteArray));
            optional.ifPresent(obj -> {
                fstStatsService.recordDecodeTime(obj.getClass(), startTime);
                fstStatsService.incrementDecode(obj.getClass());
            });
            return optional;
        } catch (IllegalArgumentException e) {
            log.error("Error during deserialization message, [{}]", e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public <T> byte[] encode(T msq) {
        long startTime = System.nanoTime();
        var bytes = FSTUtils.encode(msq);
        fstStatsService.recordEncodeTime(msq.getClass(), startTime);
        fstStatsService.incrementEncode(msq.getClass());
        return bytes;
    }


}
