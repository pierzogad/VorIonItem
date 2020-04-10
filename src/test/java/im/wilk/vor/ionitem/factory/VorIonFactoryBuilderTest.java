package im.wilk.vor.ionitem.factory;

import com.amazon.ion.IonSystem;
import im.wilk.vor.ionitem.model.PathParserConfig;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

class VorIonFactoryBuilderTest {

    private IonSystem ionSystem = mock(IonSystem.class);

    @Test
    public void whenUsingStaticConstructors_validSettingsAreUsed() {
        VorIonFactoryBuilder builder = VorIonFactoryBuilder.standard();

        assertThat(builder.getIonSystem(), is(nullValue()));
        assertThat(builder.isLenientLists(), is(true));
        assertThat(builder.isLenientStructure(), is(true));
        assertThat(builder.getParserConfig(), is(notNullValue()));
        assertThat(builder.getParserConfig(), equalTo(PathParserConfig.standard()));

        builder = VorIonFactoryBuilder.standard(ionSystem);

        assertThat(builder.getIonSystem(), equalTo(ionSystem));
        assertThat(builder.isLenientLists(), is(true));
        assertThat(builder.isLenientStructure(), is(true));
        assertThat(builder.getParserConfig(), is(notNullValue()));
        assertThat(builder.getParserConfig(), equalTo(PathParserConfig.standard()));

        // build non-lenient VorIonItems
        builder = VorIonFactoryBuilder.strict();

        assertThat(builder.getIonSystem(), is(nullValue()));
        assertThat(builder.isLenientLists(), is(false));
        assertThat(builder.isLenientStructure(), is(false));
        assertThat(builder.getParserConfig(), is(notNullValue()));
        assertThat(builder.getParserConfig(), equalTo(PathParserConfig.standard()));

        // build non-lenient VorIonItems
        builder = VorIonFactoryBuilder.strict(ionSystem);

        assertThat(builder.getIonSystem(), equalTo(ionSystem));
        assertThat(builder.isLenientLists(), is(false));
        assertThat(builder.isLenientStructure(), is(false));
        assertThat(builder.getParserConfig(), is(notNullValue()));
        assertThat(builder.getParserConfig(), equalTo(PathParserConfig.standard()));
    }


    @Test
    public void whenCreatingFactory_validInstanceIsCreated() {
        VorIonFactory factory = VorIonFactoryBuilder.standard().build();
        assertThat(factory.getVorIonConfig().getIonSystem(), is(notNullValue()));

        factory = VorIonFactoryBuilder.standard(ionSystem).build();
        assertThat(factory.getVorIonConfig().getIonSystem(), equalTo(ionSystem));
    }

}