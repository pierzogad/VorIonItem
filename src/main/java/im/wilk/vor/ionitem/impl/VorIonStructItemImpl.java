package im.wilk.vor.ionitem.impl;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonType;
import com.amazon.ion.IonValue;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.node.VorPathNode;
import im.wilk.vor.ionitem.model.VorIonConfig;
import im.wilk.vor.ionitem.model.VorIonStructItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VorIonStructItemImpl extends VorIonItemBaseImpl implements VorIonStructItem<VorIonItem> {

    private IonStruct ionStruct;
    private final VorIonConfig config;
    private final VorIonItemImpl root;
    private final Map<String, VorIonItemImpl> itemMap;

    public VorIonStructItemImpl(IonStruct ionStruct,
                                VorIonConfig config,
                                VorIonItemImpl root) {
        this.ionStruct = ionStruct != null ? ionStruct : (IonStruct) config.getIonSystem().newNull(IonType.STRUCT);
        this.config = config;
        this.root = root;
        itemMap = new HashMap<>();
    }

    @Override
    public boolean isEmptyStruct() {
        return !exists() || ionStruct.isEmpty();
    }

    @Override
    public Map<String, VorIonItem> fields() {
        if (!exists()) {
            return Collections.emptyMap();
        }
        Map<String, VorIonItem> result = new HashMap<>();
        ionStruct.forEach(ionValue -> result.put(ionValue.getFieldName(),
                get(VorPathNode.forName(ionValue.getFieldName()))));
        return Collections.unmodifiableMap(result);
    }

    private boolean exists() {
        return !ionStruct.isNullValue();
    }

    public VorIonItemImpl get(VorPathNode node) {
        String name = node.getNodeName();
        itemMap.computeIfAbsent(name, key ->
             new VorIonItemImpl(ionStruct.get(key), config, root, node));
        VorIonItemImpl vorIonItem = itemMap.get(name);
        vorIonItem.nodePath.syncAlias(node);
        return vorIonItem;
    }

    public boolean updateStruct(String name, IonValue ionValue) {
        if (ionStruct.isNullValue()) {
            ionStruct = config.getIonSystem().newEmptyStruct();
            root.setIonNode(ionStruct);
        }
        boolean modified = false;
        if (ionValue.getContainer() != ionStruct) {
            ionStruct.put(name, ionValue);
            modified = true;
        }
        return modified;
    }

    @Override
    void setValue(VorIonItemImpl other) {
        ionStruct = (IonStruct) other.asIon();
        itemMap.clear();
        root.setIonNode(ionStruct);
        root.propagateChange(null);
    }

    public void remove(String name) {
        itemMap.remove(name);
        ionStruct.remove(name);
    }

    @Override
    String describe() {
        return "Struct: " + root.getFullPath();
    }

    @Override
    public String toString() {
        return "VorIonStructItemImpl{" +
                "ionStruct=" + (ionStruct.isNullValue() ? "(empty)" : "(present)") +
                describe() +
                '}';
    }
}
