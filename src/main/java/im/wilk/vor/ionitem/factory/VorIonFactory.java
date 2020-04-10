package im.wilk.vor.ionitem.factory;

import com.amazon.ion.IonValue;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.impl.VorIonItemImpl;
import im.wilk.vor.ionitem.model.VorIonConfig;
import im.wilk.vor.ionitem.node.VorPathNode;
import lombok.Getter;

/**
 * VorIonFactory is the only mechanism to create instance of @see:VorIonItem .
 *
 * VorIonFactory itself must be created using VorIonFactoryBuilder that
 * allows setting path parser format ("a/b/c:n" vs "a.b.c[n]"), IonSystem to be used,
 * and VorIonItem behaviour (leniency)
 */
@Getter
public class VorIonFactory {
    private final VorIonConfig vorIonConfig;

    /*package local - it should be created with VorIonFactoryBuilder */
    VorIonFactory(VorIonConfig vorIonConfig) {
        this.vorIonConfig = vorIonConfig;
    }

    /**
     * Create VorIonItem based on existing IonValue data.
     *
     * @param value - ionValue.
     *              Data passed here will:
     *              * not be modified by read-only access
     *                 (attempt to read from item that does not exist will not modify Ion)
     *              * WILL be modified data when using set(), remove(), add()..
     * @return VorIonItem referencing to root of ionValue data  hierarchy.
     */
    public VorIonItem from(IonValue value) {
        return new VorIonItemImpl(value, vorIonConfig, null, VorPathNode.forName(""));
    }

    /**
     * Create VorIonItem containing no data.
     *
     *  Data can be modified data when using set(), remove(), add()..
     *
     *  asIon() function will return data in Ion format.
     *
     * @return VorIonItem containing no data.
     */
    public VorIonItem empty() {
        return new VorIonItemImpl(null, vorIonConfig, null, VorPathNode.forName(""));
    }
}
