package im.wilk.vor.ionitem.impl;

import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.model.VorIonItemBase;

public abstract class VorIonItemBaseImpl implements VorIonItemBase<VorIonItem> {

    abstract void setValue(VorIonItemImpl other);
    abstract String describe();
}
