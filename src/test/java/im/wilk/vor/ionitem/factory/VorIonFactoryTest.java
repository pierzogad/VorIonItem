package im.wilk.vor.ionitem.factory;

import com.amazon.ion.IonSystem;
import com.amazon.ion.IonValue;
import com.amazon.ion.system.IonSystemBuilder;
import im.wilk.vor.ionitem.VorIonItem;
import im.wilk.vor.ionitem.model.VorIonConfig;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VorIonFactoryTest {

    private IonSystem ionSystem = IonSystemBuilder.standard().build();

    private VorIonConfig config = mock(VorIonConfig.class);
    private VorIonFactory factory = new VorIonFactory(config);

    @Test
    public void whenCallingFrom_validObjectIsCreated() {
        IonValue ionValue = ionSystem.singleValue("{sample:'data'}");
        VorIonItem vorIonItem = factory.from(ionValue);
        assertThat(vorIonItem, is(notNullValue()));
        assertThat(vorIonItem.asIon(), equalTo(ionValue));
    }

    @Test
    public void whenCallingEmpty_validObjectIsCreated() {

        when(config.getIonSystem()).thenReturn(ionSystem);

        VorIonItem vorIonItem = factory.empty();

        assertThat(vorIonItem, is(notNullValue()));
        assertThat(vorIonItem.asIon().isNullValue(), is(true));
    }
}