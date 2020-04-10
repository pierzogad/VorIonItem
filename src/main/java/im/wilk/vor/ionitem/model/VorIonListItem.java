package im.wilk.vor.ionitem.model;

import java.util.List;

public interface VorIonListItem<T extends VorIonListItem<T>> extends VorIonItemBase<T> {
    boolean isEmptyList();
    int listSize();
    List<T> list();
    void remove(int idx);
}
