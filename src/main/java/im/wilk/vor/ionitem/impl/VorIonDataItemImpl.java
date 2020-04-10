package im.wilk.vor.ionitem.impl;

import com.amazon.ion.IonBool;
import com.amazon.ion.IonDecimal;
import com.amazon.ion.IonInt;
import com.amazon.ion.IonText;
import com.amazon.ion.IonTimestamp;
import com.amazon.ion.IonType;
import com.amazon.ion.IonValue;
import com.amazon.ion.Timestamp;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.node.VorPathNode;
import im.wilk.vor.ionitem.model.VorIonDataItem;
import im.wilk.vor.ionitem.model.VorIonConfig;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class VorIonDataItemImpl extends VorIonItemBaseImpl implements VorIonDataItem<VorIonItem> {

    private IonValue ionValue;
    private final VorIonConfig config;
    private final VorIonItemImpl root;


    public VorIonDataItemImpl(IonValue ionValue,
                              VorIonConfig config,
                              VorIonItemImpl root) {
        this.ionValue = ionValue != null ? ionValue : config.getIonSystem().newNull();
        this.config = config;
        this.root = root;
    }

    @Override
    public Optional<String> optionalString() {
        return Optional.ofNullable(getS());
    }

    @Override
    public Optional<Boolean> optionalBoolean() {
        if (!exists() || !IonType.BOOL.equals(ionValue.getType())) {
            return Optional.empty();
        }
        return Optional.of(getB());
    }

    @Override
    public Optional<Long> optionalLong() {
        return Optional.ofNullable(getL());
    }

    @Override
    public Optional<BigDecimal> optionalBigDecimal() {
        return Optional.ofNullable(getBd());
    }

    @Override
    public Optional<Instant> optionalTimestamp() {
        return Optional.ofNullable(getTs());
    }

    @Override
    public String getS() {
        if (!exists()) {
            return null;
        }

        if (IonType.isText(ionValue.getType())) {
            return ((IonText) ionValue).stringValue();
        }
        return ionValue.toString();
    }

    @Override
    public Boolean getB() {
        if (!IonType.BOOL.equals(ionValue.getType())) {
            return false;
        }

        return ((IonBool) ionValue).booleanValue();
    }

    @Override
    public Long getL() {
        if (!IonType.INT.equals(ionValue.getType())) {
            return null;
        }

        return ((IonInt) ionValue).longValue();
    }

    @Override
    public BigDecimal getBd() {
        if (!IonType.DECIMAL.equals(ionValue.getType())) {
            return null;
        }

        return ((IonDecimal) ionValue).bigDecimalValue();
    }

    @Override
    public Instant getTs() {
        if (!IonType.TIMESTAMP.equals(ionValue.getType())) {
            return null;
        }

        return Instant.ofEpochMilli(((IonTimestamp) ionValue).getMillis());
    }

    @Override
    public void set(String value) {
        if (IonType.isText(ionValue.getType())) {
            ((IonText) ionValue).setValue(value);
        } else {
            ionValue = config.getIonSystem().newString(value);
            root.setIonNode(ionValue);
            root.propagateChange(null);
        }
    }

    @Override
    public void set(Boolean value) {
        if (IonType.BOOL.equals(ionValue.getType())) {
            ((IonBool) ionValue).setValue(value);
        } else {
            ionValue = config.getIonSystem().newBool(value);
            root.setIonNode(ionValue);
            root.propagateChange(null);
        }
    }

    @Override
    public void set(Long value) {
        if (IonType.INT.equals(ionValue.getType())) {
            ((IonInt) ionValue).setValue(value);
        } else {
            ionValue = config.getIonSystem().newInt(value);
            root.setIonNode(ionValue);
            root.propagateChange(null);
        }
    }

    @Override
    public void set(BigDecimal value) {
        if (IonType.DECIMAL.equals(ionValue.getType())) {
            ((IonDecimal) ionValue).setValue(value);
        } else {
            ionValue = config.getIonSystem().newDecimal(value);
            root.setIonNode(ionValue);
            root.propagateChange(null);
        }
    }

    @Override
    public void set(Instant value) {
        if (IonType.TIMESTAMP.equals(ionValue.getType())) {
            ((IonTimestamp) ionValue).setMillis(value.toEpochMilli());
        } else {
            ionValue = config.getIonSystem().newTimestamp(Timestamp.forMillis(value.toEpochMilli(), 0));
            root.setIonNode(ionValue);
            root.propagateChange(null);
        }
    }

    private boolean exists() {
        return !ionValue.isNullValue();
    }

    @Override
    void setValue(VorIonItemImpl other) {
        ionValue = other.asIon();
        root.setIonNode(ionValue);
        root.propagateChange(null);
    }

    @Override
    String describe() {
        return "Data: " + root.getFullPath() + " = " + ionValue;
    }

    @Override
    public String toString() {
        return "VorIonDataItemImpl{" +
                describe() +
                '}';
    }
}
