package xyz.arbres.objdiff.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ObjDiffCoreProperties
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ObjDiffCoreProperties {

    private String algorithm;
    private String commitIdGenerator;
    private String mappingStyle;
    private Boolean initialChanges;
    private Boolean terminalChanges;
    private Boolean prettyPrint;
    private Boolean typeSafeValues;
    private String packagesToScan = "";
    private PrettyPrintDateFormats prettyPrintDateFormats = new PrettyPrintDateFormats();

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public void setCommitIdGenerator(String commitIdGenerator) {
        this.commitIdGenerator = commitIdGenerator;
    }

    public String getMappingStyle() {
        return mappingStyle;
    }

    public void setMappingStyle(String mappingStyle) {
        this.mappingStyle = mappingStyle;
    }

    /**
     * Use {@link #isInitialChanges()}
     */
    @Deprecated
    public Boolean isNewObjectSnapshot() {
        return isInitialChanges();
    }

    /**
     * Use {@link #setInitialChanges(Boolean)}
     */
    @Deprecated
    public void setNewObjectSnapshot(Boolean newObjectSnapshot) {
        setInitialChanges(newObjectSnapshot);
    }

    public Boolean isInitialChanges() {
        return initialChanges;
    }

    public Boolean isTerminalChanges() {
        return terminalChanges;
    }

    public Boolean isPrettyPrint() {
        return prettyPrint;
    }

    public Boolean isTypeSafeValues() {
        return typeSafeValues;
    }

    public String getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setInitialChanges(Boolean initialChanges) {
        this.initialChanges = initialChanges;
    }

    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public void setTypeSafeValues(Boolean typeSafeValues) {
        this.typeSafeValues = typeSafeValues;
    }

    public PrettyPrintDateFormats getPrettyPrintDateFormats() {
        return prettyPrintDateFormats;
    }

    public void setTerminalChanges(Boolean terminalChanges) {
        this.terminalChanges = terminalChanges;
    }

    public static class PrettyPrintDateFormats {
        private static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy";
        private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
        private Map<Class<? extends Temporal>, String> formats = new HashMap<>();

        public PrettyPrintDateFormats() {
            setLocalDateTime(DEFAULT_DATE_FORMAT + ", " + DEFAULT_TIME_FORMAT);
            setZonedDateTime(DEFAULT_DATE_FORMAT + ", " + DEFAULT_TIME_FORMAT + "Z");
            setLocalDate(DEFAULT_DATE_FORMAT);
            setLocalTime(DEFAULT_TIME_FORMAT);
        }

        public void registerFormat(Class<? extends Temporal> forType, String format) {
            formats.put(forType, format);
        }

        public String getLocalDateTime() {
            return formats.get(LocalDateTime.class);
        }

        public void setLocalDateTime(String localDateTime) {
            registerFormat(LocalDateTime.class, localDateTime);
        }

        public String getZonedDateTime() {
            return formats.get(ZonedDateTime.class);
        }

        public void setZonedDateTime(String zonedDateTime) {
            registerFormat(ZonedDateTime.class, zonedDateTime);
        }

        public String getLocalDate() {
            return formats.get(LocalDate.class);
        }

        public void setLocalDate(String localDate) {
            registerFormat(LocalDate.class, localDate);
        }

        public String getLocalTime() {
            return formats.get(LocalTime.class);
        }

        public void setLocalTime(String localTime) {
            registerFormat(LocalTime.class, localTime);
        }

        public Map<Class<? extends Temporal>, String> getFormats() {
            return Collections.unmodifiableMap(formats);
        }
    }
}
