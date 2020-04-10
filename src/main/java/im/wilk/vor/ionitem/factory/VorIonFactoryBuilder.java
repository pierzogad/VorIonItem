package im.wilk.vor.ionitem.factory;

import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;
import im.wilk.vor.ionitem.model.VorIonConfig;
import im.wilk.vor.ionitem.model.PathParserConfig;
import lombok.Getter;

@Getter
public final class VorIonFactoryBuilder {

    private IonSystem ionSystem;
    private PathParserConfig parserConfig;
    private boolean lenientStructure;
    private boolean lenientLists;

    private VorIonFactoryBuilder() {
    }

    public static VorIonFactoryBuilder standard() {
        return new VorIonFactoryBuilder()
                .withPathParserConfig(PathParserConfig.standard())
                .withLenientStructure(true)
                .withLenientLists(true);
    }

    public static VorIonFactoryBuilder standard(IonSystem ionSystem) {
        return standard().withIonSystem(ionSystem);
    }

    public static VorIonFactoryBuilder strict(IonSystem ionSystem) {
        return strict().withIonSystem(ionSystem);
    }

    public static VorIonFactoryBuilder strict() {
        return new VorIonFactoryBuilder()
                .withPathParserConfig(PathParserConfig.standard())
                .withLenientStructure(false)
                .withLenientLists(false);

    }

    public VorIonFactoryBuilder withLenientLists(boolean lenientLists) {
        this.lenientLists = lenientLists;
        return this;
    }

    public VorIonFactoryBuilder withLenientStructure(boolean lenientStructure) {
        this.lenientStructure = lenientStructure;
        return this;
    }

    public VorIonFactoryBuilder withPathParserConfig(PathParserConfig parserConfig) {
        this.parserConfig = parserConfig;
        return this;
    }

    public VorIonFactoryBuilder withIonSystem(IonSystem ionSystem) {
        this.ionSystem = ionSystem;
        return this;
    }

    public VorIonFactory build() {
        if (ionSystem == null) {
            ionSystem = IonSystemBuilder.standard().build();
        }
        VorIonConfig vorIonConfig = new VorIonConfig(ionSystem,
                parserConfig, lenientStructure, lenientLists);

        return new VorIonFactory(vorIonConfig);
    }
}
