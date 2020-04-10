package im.wilk.vor.ionitem.impl;

import com.amazon.ion.IonList;
import com.amazon.ion.IonType;
import com.amazon.ion.IonValue;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.node.VorPathNode;
import im.wilk.vor.ionitem.model.VorIonListItem;
import im.wilk.vor.ionitem.model.VorIonConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VorIonListItemImpl extends VorIonItemBaseImpl implements VorIonListItem<VorIonItem> {

    private IonList ionList;
    private final VorIonConfig config;
    private final VorIonItemImpl root;
    private final List<VorIonItemImpl> itemList;

    public VorIonListItemImpl(IonList ionList,
                              VorIonConfig config,
                              VorIonItemImpl root) {
        this.ionList = ionList != null ? ionList : (IonList) config.getIonSystem().newNull(IonType.LIST);
        this.config = config;
        this.root = root;
        itemList = new ArrayList<>(listSize());
    }

    @Override
    public boolean isEmptyList() {
        return !exists() || ionList.isEmpty();
    }

    @Override
    public int listSize() {
        return exists() ? ionList.size() : 0;
    }

    @Override
    public List<VorIonItem> list() {
        List<VorIonItem> items = new ArrayList<>();
        for (int i = 0; i < listSize(); i++) {
            items.add(get(VorPathNode.forIndex(i)));
        }
        return Collections.unmodifiableList(items);
    }

    public void addToList(VorIonItem item) {
        setInList(listSize(), item);
    }

    public void setInList(int idx, VorIonItem item) {

        VorIonItemImpl ionItem = root.makeCopy(item, VorPathNode.forIndex(idx));
        while (itemList.size() <= idx) {
            itemList.add(null);
        }
        itemList.set(idx, ionItem);

        root.propagateChange(ionItem);
    }

    private boolean exists() {
        return !ionList.isNullValue();
    }

    public VorIonItemImpl get(VorPathNode node) {
        int idx = node.getNodeIndex();

        if (idx >= itemList.size()) {
            if (!config.isLenientLists()) {
                if ((!exists() && idx > 0) || idx > ionList.size()) {
                    throw new IndexOutOfBoundsException("Attempt to get item with index " + idx
                            + " from " + describe());
                }
            }
            while (idx >= itemList.size()) {
                itemList.add(null);
            }
        }

        VorIonItemImpl ionItem = itemList.get(idx);
        if (ionItem == null) {
            IonValue ionValue = null;
            if (exists() && idx < listSize()) {
                ionValue = ionList.get(idx);
            }
            ionItem = new VorIonItemImpl(ionValue, config, root, node);
            itemList.set(idx, ionItem);
        }
        ionItem.nodePath.syncAlias(node);
        return ionItem;
    }

    public boolean updateList(Integer index, IonValue ionValue) {
        boolean updated = false;
        if (ionList.isNullValue()) {
            ionList = config.getIonSystem().newEmptyList();
            root.setIonNode(ionList);
            updated = true;
        }
        if (!config.isLenientLists()) {
            if (index > ionList.size()) {
                throw new IndexOutOfBoundsException("Attempt to add item with index " + index + " to " + describe());
            }
        }
        while (ionList.size() <= index) {
            ionList.add(config.getIonSystem().newNull());
        }
        ionList.set(index, ionValue);
        return updated;
    }

    @Override
    void setValue(VorIonItemImpl other) {
        this.ionList = (IonList) other.asIon();
        itemList.clear();
        root.setIonNode(ionList);
        root.propagateChange(null);
    }

    @Override
    String describe() {
        return "List: " + root.getFullPath() + " (" + listSize() + " items)";
    }

    @Override
    public void remove(int index) {
        if (!exists()) {
            return;
        }
        if (index < ionList.size()) {
            ionList.remove(index);
        }
        if (index < itemList.size()) {
            itemList.remove(index);
            for (int idx = index; idx < itemList.size(); idx++) {
                VorIonItemImpl vorIonItem = itemList.get(idx);
                if (vorIonItem != null) {
                    vorIonItem.getPathNode().setNodeIndex(idx);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "VorIonListItemImpl{" +
                "ionList=" + (ionList.isNullValue() ? "(empty)" : "(present)") +
                describe() +
                '}';
    }
}
