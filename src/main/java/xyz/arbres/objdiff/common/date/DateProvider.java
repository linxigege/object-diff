package xyz.arbres.objdiff.common.date;

import java.time.ZonedDateTime;

/**
 * DateProvider
 *
 * @author carlos
 * @date 2021-12-07
 */
public interface DateProvider {
    /**
     * now
     *
     * @return
     */
    ZonedDateTime now();
}
