package xyz.arbres.objdiff.core.json.typeadapter.util;


import xyz.arbres.objdiff.core.json.BasicStringTypeAdapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * Serializes java.sql.Timestamp to JSON String using ISO util format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlTimestampTypeAdapter extends BasicStringTypeAdapter<Timestamp> {

    @Override
    public String serialize(Timestamp sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Timestamp deserialize(String serializedValue) {
        LocalDateTime date = UtilTypeCoreAdapters.deserializeLocalDateTime(serializedValue);
        return Timestamp.valueOf(date);
    }

    @Override
    public Class getValueType() {
        return Timestamp.class;
    }
}
