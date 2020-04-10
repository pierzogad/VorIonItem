package im.wilk.vor.ionitem.impl;

import com.amazon.ion.IonList;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonType;
import com.amazon.ion.IonValue;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.node.VorPathNode;
import im.wilk.vor.ionitem.model.VorIonConfig;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class VorIonItemDelegates implements VorIonItem {

    private IonValue ionNode;
    protected final VorIonConfig config;
    protected final VorPathNode nodePath;
    protected final VorIonItemImpl root;


    private VorIonListItemImpl listDelegate;
    private VorIonDataItemImpl dataDelegate;
    private VorIonStructItemImpl structDelegate;

    public VorIonItemDelegates(IonValue inputNode,
                               VorIonConfig config,
                               VorIonItemImpl root,
                               VorPathNode nodePath) {
        this.nodePath = nodePath;
        this.ionNode = (inputNode != null) ? inputNode : config.getIonSystem().newNull();
        this.config = config;
        this.root = root;
    }

    protected ItemType getType(IonValue ionValue) {
        ItemType result;
        if (IonType.LIST.equals(ionValue.getType())) {
            result = ItemType.LIST;
        } else if (IonType.STRUCT.equals(ionValue.getType())) {
            result = ItemType.STRUCT;
        } else if (ionValue == null || ionValue.isNullValue()) {
            result = ItemType.ANY;
        } else {
            result = ItemType.DATA;
        }
        return result;
    }

    @Override
    public ItemType getItemType() {
        return getType(ionNode);
    }

    protected VorIonListItemImpl getListDelegate() {
        if (listDelegate == null) {
            IonList ionList = (IonType.LIST.equals(ionNode.getType())) ? (IonList) ionNode : null;
            listDelegate = new VorIonListItemImpl(ionList, config, getThis());
        }
        validateAccess(IonType.LIST);
        return listDelegate;
    }

    protected VorIonDataItemImpl getDataDelegate() {
        if (dataDelegate == null) {
            IonValue ionValue = IonType.isContainer(ionNode.getType()) ? null : ionNode;
            dataDelegate = new VorIonDataItemImpl(ionValue, config, getThis());
        }
        validateAccess(null);
        return dataDelegate;
    }

    protected VorIonStructItemImpl getStructDelegate() {
        if (structDelegate == null) {
            IonStruct ionStruct = (IonType.STRUCT.equals(ionNode.getType())) ? (IonStruct) ionNode : null;
            structDelegate = new VorIonStructItemImpl(ionStruct, config, getThis());
        }
        validateAccess(IonType.STRUCT);
        return structDelegate;
    }

    protected void validateAccess(IonType expectedType) {
        if (!config.isLenientStructure()) {
            if (!ionNode.isNullValue()) {
                if (expectedType == null && IonType.isContainer(ionNode.getType())) {
                    String error = String.format("%s has type %s and cannot be treated as Data ionNode",
                            getFullPath(), ionNode.getType().toString());
                    throw new IllegalStateException(error);
                }
                if (expectedType != null && !expectedType.equals(ionNode.getType())) {
                    String error = String.format("%s has type %s and cannot be treated as %s ionNode",
                            getFullPath(), ionNode.getType().toString(), expectedType.toString());
                    throw new IllegalStateException(error);
                }
            }
        }
    }

    protected void resetDelegates() {
        dataDelegate = null;
        structDelegate = null;
        listDelegate = null;
    }

    protected abstract VorIonItemImpl getThis();

    @Override
    public boolean isEmptyStruct() {
        return getStructDelegate().isEmptyStruct();
    }

    @Override
    public Map<String, VorIonItem> fields() {
        return getStructDelegate().fields();
    }

    @Override
    public int listSize() {
        return getListDelegate().listSize();
    }

    @Override
    public List<VorIonItem> list() {
        return getListDelegate().list();
    }

    @Override
    public boolean isEmptyList() {
        return getListDelegate().isEmptyList();
    }

    @Override
    public Optional<String> optionalString() {
        return getDataDelegate().optionalString();
    }

    @Override
    public Optional<Boolean> optionalBoolean() {
        return getDataDelegate().optionalBoolean();
    }

    @Override
    public Optional<Long> optionalLong() {
        return getDataDelegate().optionalLong();
    }

    @Override
    public String getS() {
        return getDataDelegate().getS();
    }

    @Override
    public Boolean getB() {
        return getDataDelegate().getB();
    }

    @Override
    public Long getL() {
        return getDataDelegate().getL();
    }

    @Override
    public void set(String value) {
        getDataDelegate().set(value);
    }

    @Override
    public void set(Boolean value) {
        getDataDelegate().set(value);
    }

    @Override
    public Optional<BigDecimal> optionalBigDecimal() {
        return getDataDelegate().optionalBigDecimal();
    }

    @Override
    public Optional<Instant> optionalTimestamp() {
        return getDataDelegate().optionalTimestamp();
    }

    @Override
    public BigDecimal getBd() {
        return getDataDelegate().getBd();
    }

    @Override
    public Instant getTs() {
        return getDataDelegate().getTs();
    }

    @Override
    public void set(Long value) {
        getDataDelegate().set(value);
    }

    @Override
    public void set(BigDecimal value) {
        getDataDelegate().set(value);
    }

    @Override
    public void set(Instant value) {
        getDataDelegate().set(value);
    }

    @Override
    public void remove(int index) {
        getListDelegate().remove(index);
    }

    protected IonValue getIonNode() {
        return ionNode;
    }

    public void setIonNode(IonValue newNode) {
        ionNode = newNode;
    }

    @Override
    public String toString() {
        return "Delegates{" +
                "arrayDelegate=" + listDelegate +
                ", dataDelegate=" + dataDelegate +
                ", structDelegate=" + structDelegate +
                '}';
    }
}
