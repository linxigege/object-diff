package xyz.arbres.objdiff.common.date;

import java.time.ZonedDateTime;

/**
 * DefaultDateProvider
 *
 * @author carlos
 * @date 2021-12-07
 */
public class DefaultDateProvider implements DateProvider {

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
