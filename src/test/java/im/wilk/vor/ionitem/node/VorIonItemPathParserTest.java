package im.wilk.vor.ionitem.node;

import im.wilk.vor.ionitem.model.PathParserConfig;
import im.wilk.vor.ionitem.util.matchers.VorPathNodeMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;


class VorIonItemPathParserTest {

    @Test
    public void givenValidPath_itIsParsed() {
        VorPathParser pathParser = new VorPathParser(PathParserConfig.standard());

        List<VorPathNode> nodes = pathParser.toNodes("one/two/three=three_alias:91=nine_one:7=seven/four");

        assertThat(nodes, hasSize(6));
        MatcherAssert.assertThat(nodes.get(0), VorPathNodeMatcher.isNodeWith("one", null, null));
        MatcherAssert.assertThat(nodes.get(1), VorPathNodeMatcher.isNodeWith("two", null, null));
        MatcherAssert.assertThat(nodes.get(2), VorPathNodeMatcher.isNodeWith("three", null, "three_alias"));
        MatcherAssert.assertThat(nodes.get(3), VorPathNodeMatcher.isNodeWith(null, 91, "nine_one"));
        MatcherAssert.assertThat(nodes.get(4), VorPathNodeMatcher.isNodeWith(null, 7, "seven"));
        MatcherAssert.assertThat(nodes.get(5), VorPathNodeMatcher.isNodeWith("four", null, null));
    }

    @Test
    public void givenRepeatedSeparator_itIsIgnoredParsed() {
        VorPathParser pathParser = new VorPathParser(PathParserConfig.standard());

        List<VorPathNode> nodes = pathParser.toNodes("//one///two::34::////three===alias");

        assertThat(nodes, hasSize(4));
        MatcherAssert.assertThat(nodes.get(0), VorPathNodeMatcher.isNodeWith("one", null, null));
        MatcherAssert.assertThat(nodes.get(1), VorPathNodeMatcher.isNodeWith("two", null, null));
        MatcherAssert.assertThat(nodes.get(2), VorPathNodeMatcher.isNodeWith(null, 34, null));
        MatcherAssert.assertThat(nodes.get(3), VorPathNodeMatcher.isNodeWith("three", null, "alias"));
    }

    @Test
    public void givenNewIndex_ItIsModified() {
        VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
        List<VorPathNode> nodes = pathParser.toNodes("one.two.three[2].four");
        assertThat(nodes, hasSize(5));
        VorPathNode indexNode = nodes.get(3);
        MatcherAssert.assertThat(indexNode, VorPathNodeMatcher.isNodeWith(null, 2, null));
        indexNode.setNodeIndex(4);
        MatcherAssert.assertThat(indexNode, VorPathNodeMatcher.isNodeWith(null, 4, null));
    }

    @Test
    public void givenDifferentConfigs_TheyAreInterChangeable() {
        String fileStyle = "one/two/three:2/four=alias";
        String sqlStyle = "one.two.three[2].four=alias";

        VorPathParser sqlParser = new VorPathParser(PathParserConfig.ionSqlStyle());
        VorPathParser fileParser = new VorPathParser(PathParserConfig.fileSystemStyleWithColon());

        List<VorPathNode> nodes = sqlParser.toNodes(sqlStyle);
        assertThat(nodes, hasSize(5));

        String builtFilePath = fileParser.buildPath(nodes);
        String builtSqlPath = sqlParser.buildPath(nodes);

        assertThat(builtFilePath, equalTo(fileStyle));
        assertThat(builtSqlPath, equalTo(sqlStyle));
    }

    @Test
    public void illegalIndex_WillThrow() {


        PathParsingException parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.standard());
                    pathParser.toNodes("a:12X");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index can contain digits only in a:12X"));

        parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.standard());
                    pathParser.toNodes("a:1 2");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index can contain digits only in a:1 2"));
    }

    @Test
    public void withClosingIndex_doubleOpenWillFail() {

        PathParsingException parsingException = Assertions.assertThrows(PathParsingException.class,
            () -> {
                VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                pathParser.toNodes("a[2[3]");
            }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index opening separator without closing previous one in a[2[3]"));

        parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("a[2=alias");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index opened but not closed in a[2=alias"));

        parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("a[2.]next");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index opened but not closed in a[2.]next"));
    }

    @Test
    public void withClosingIndex_ClosingWithoutOpenWillFail() {

        PathParsingException parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("abc]");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index closing separator without opening one in abc]"));

        parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("abc[10]]");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index closing separator without opening one in abc[10]]"));

        parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("abc[10]=def]");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Index closing separator without opening one in abc[10]=def]"));
    }

    @Test
    public void strayTestAfterClosingIndex_withThrow() {
        PathParsingException parsingException = Assertions.assertThrows(PathParsingException.class,
                () -> {
                    VorPathParser pathParser = new VorPathParser(PathParserConfig.ionSqlStyle());
                    pathParser.toNodes("abc[10]def");
                }
        );
        assertThat(parsingException.getMessage(),
                containsString("Unexpected character past index closing separator in abc[10]def"));

    }
}