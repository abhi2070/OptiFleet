
package org.thingsboard.server.service.subscription;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TbEntityUpdatesInfo {

    volatile long attributesUpdateTs;
    volatile long timeSeriesUpdateTs;

    public TbEntityUpdatesInfo(long ts) {
        this.attributesUpdateTs = ts;
        this.timeSeriesUpdateTs = ts;
    }
}
