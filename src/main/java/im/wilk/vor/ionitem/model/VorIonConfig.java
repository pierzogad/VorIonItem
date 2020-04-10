package im.wilk.vor.ionitem.model;

import com.amazon.ion.IonSystem;
import im.wilk.vor.ionitem.node.VorPathParser;
import lombok.Data;

@Data
public class VorIonConfig {
    private final IonSystem ionSystem;
    private final PathParserConfig parserConfig;
    private final boolean lenientStructure;
    private final boolean lenientLists;
    private final VorPathParser pathParser;

    public VorIonConfig(IonSystem ionSystem,
                        PathParserConfig parserConfig,
                        boolean lenientStructure,
                        boolean lenientLists) {
        this.ionSystem = ionSystem;
        this.parserConfig = parserConfig;
        this.lenientStructure = lenientStructure;
        this.lenientLists = lenientLists;
        pathParser = new VorPathParser(parserConfig);
    }
}
