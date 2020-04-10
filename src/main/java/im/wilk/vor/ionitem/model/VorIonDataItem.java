package im.wilk.vor.ionitem.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface VorIonDataItem<T extends VorIonDataItem<T>> extends VorIonItemBase<T> {
    Optional<String> optionalString();
    Optional<Boolean> optionalBoolean();
    Optional<Long> optionalLong();
    Optional<BigDecimal> optionalBigDecimal();
    Optional<Instant> optionalTimestamp();

    String getS();
    Boolean getB();
    Long getL();
    BigDecimal getBd();
    Instant getTs();

    void set(String value);
    void set(Boolean value);
    void set(Long value);
    void set(BigDecimal value);
    void set(Instant value);
}
