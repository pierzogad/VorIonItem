package im.wilk.vor.ionitem.model;

import java.util.Map;

public interface VorIonStructItem<T extends VorIonStructItem<T>> extends VorIonItemBase<T> {
    boolean isEmptyStruct();
    Map<String, T> fields();
}
