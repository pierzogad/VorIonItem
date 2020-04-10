package im.wilk.vor.ionitem.impl;

import com.amazon.ion.IonValue;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.model.VorIonConfig;
import im.wilk.vor.ionitem.node.VorPathNode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VorIonItemImpl extends VorIonItemDelegates implements VorIonItem {

    public VorIonItemImpl(IonValue node, VorIonConfig config, VorIonItemImpl root, VorPathNode nodePath) {
        super(node, config, root, nodePath);
    }

    @Override
    public VorIonItem get(int idx) {
        return fromPathNode(VorPathNode.forIndex(idx));
    }

    @Override
    public VorIonItemImpl get(String path) {
        return get(config.getPathParser().toNodes(path));
    }

    @Override
    public VorIonItemImpl get(List<VorPathNode> nodes) {
        VorIonItemImpl ret = this;
        for (VorPathNode node : nodes) {
            ret = ret.fromPathNode(node);
        }
        return ret;
    }

    @Override
    public void set(String name, VorIonItem other) {
        VorIonItemImpl vorIonItem = get(name);
        VorIonItemBaseImpl itemBase = vorIonItem.selectTypeToSet(other.getItemType());
        itemBase.setValue((VorIonItemImpl) other);

    }

    @Override
    public String getFullPath() {
        LinkedList<VorPathNode> nodes = new LinkedList<>();
        VorIonItem vorIonItem = this;

        while (vorIonItem != null) {
            nodes.push(vorIonItem.getPathNode());
            vorIonItem = vorIonItem.getContainer();
        }

        return config.getPathParser().buildPath(nodes);
    }

    @Override
    public void setNull() {
        setIonNode(config.getIonSystem().newNull());
        resetDelegates();
        if (root != null) {
            root.propagateChange(this);
        }
    }

    @Override
    public void remove() {
        if (root != null) {
            root.remove(nodePath);
        } else {
            setNull();
        }
    }

    private void remove(VorPathNode nodePath) {
        if (nodePath.getNodeName() != null) {
            getStructDelegate().remove(nodePath.getNodeName());
        } else {
            getListDelegate().remove(nodePath.getNodeIndex());
        }
    }

    private VorIonItemBaseImpl selectTypeToSet(ItemType otherType) {
        ItemType itemType = otherType;

        switch (itemType) {
            case LIST:
                return getListDelegate();

            case STRUCT:
                return getStructDelegate();

            default:
                return getDataDelegate();
        }
    }

    @Override
    public void set(int idx, VorIonItem other) {
        getListDelegate().setInList(idx, other);
    }

    @Override
    public void add(VorIonItem other) {
        getListDelegate().addToList(other);
    }

    @Override
    public void set(VorIonItem other) {
        set(other.name(), other);
    }

    @Override
    public void set(String path, String value) {
        VorIonItemImpl vorIonItem = get(path);
        vorIonItem.getDataDelegate().set(value);
    }

    @Override
    public void set(String path, Boolean value) {
        VorIonItemImpl vorIonItem = get(path);
        vorIonItem.set(value);
    }

    @Override
    public void set(String path, Long value) {
        VorIonItemImpl vorIonItem = get(path);
        vorIonItem.set(value);
    }

    @Override
    public void set(String path, BigDecimal value) {
        VorIonItemImpl vorIonItem = get(path);
        vorIonItem.set(value);
    }

    @Override
    public void set(String path, Instant value) {
        VorIonItemImpl vorIonItem = get(path);
        vorIonItem.set(value);
    }

    public VorIonItemImpl fromPathNode(VorPathNode node) {
        if (node.getNodeName() != null) {
            return getStruct(node);
        }

        return getList(node);
    }

    @Override
    public List<VorIonItem> select(String... paths) {

        List<VorIonItem> items = new ArrayList<>();
        for (String path : paths) {
            items.add(get(path));
        }

        return items;
    }

    protected VorIonItemImpl getStruct(VorPathNode node) {
        return getStructDelegate().get(node);
    }

    protected VorIonItemImpl getList(VorPathNode node) {
        return getListDelegate().get(node);
    }


    @Override
    public VorPathNode getPathNode() {
        return nodePath;
    }

    @Override
    public VorIonItem getContainer() {
        return root;
    }

    @Override
    public String name() {
        if (nodePath.getNodeAlias() != null) {
            return nodePath.getNodeAlias();
        }
        if (nodePath.getNodeName() != null) {
            return nodePath.getNodeName();
        }
        return root.name();
    }

    @Override
    public IonValue asIon() {
        return getIonNode().clone();
    }

    @Override
    public boolean exists() {
        return !getIonNode().isNullValue();
    }

    protected VorIonItemImpl getThis() {
        return this;
    }

    public void propagateChange(VorIonItemImpl modified) {
        boolean needPropagate = true;
        if (modified != null) {
            if (modified.getPathNode().getNodeName() != null) {
                needPropagate = getStructDelegate()
                        .updateStruct(modified.getPathNode().getNodeName(), modified.getIonNode());
            } else if (modified.getPathNode().getNodeIndex() != null) {
                needPropagate = getListDelegate()
                        .updateList(modified.getPathNode().getNodeIndex(), modified.getIonNode());
            }
        }
        if (needPropagate && root != null) {
            root.propagateChange(this);
        }
    }

    public VorIonItemImpl makeCopy(VorIonItem toCopy, VorPathNode pathNode) {
        return new VorIonItemImpl(toCopy.asIon(), config, this, pathNode);
    }

    @Override
    public String toString() {
        return "VorIonItemImpl{" +
                "node=" + (getIonNode().isNullValue() ? "(empty)" : "(" + getIonNode().getType() + ")") +
                ", config=" + config +
                ", nodePath=" + nodePath +
                ", " + super.toString() +
                '}';
    }
}
