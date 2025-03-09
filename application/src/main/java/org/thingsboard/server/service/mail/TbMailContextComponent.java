
package org.thingsboard.server.service.mail;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@Data
@Lazy
public class TbMailContextComponent {

    @Autowired
    private AdminSettingsService adminSettingsService;
}