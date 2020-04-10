package im.wilk.vor.ionitem.util.matchers;

import im.wilk.vor.ionitem.node.VorPathNode;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class VorPathNodeMatcher {

    public static NodeMatcher isNodeWith(String name, Integer index, String alias) {
        return new NodeMatcher(name, index, alias);
    }

    private static class NodeMatcher extends TypeSafeMatcher<VorPathNode> {

        private final String name;
        private final Integer index;
        private final String alias;

        private NodeMatcher(String name, Integer index, String alias) {
            this.name = name;
            this.index = index;
            this.alias = alias;
        }

        @Override
        protected boolean matchesSafely(VorPathNode tested) {
            return equalWithNulls(tested.getNodeName(), name)
                    && equalWithNulls(tested.getNodeIndex(), index)
                    && equalWithNulls(tested.getNodeAlias(), alias);
        }

        private boolean equalWithNulls(Object found, Object expected) {
            if (found == null || expected == null) {
                return found == null && expected == null;
            }
            return found.equals(expected);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("name=%s, index=%s, alias=%s", name, index, alias));
        }
    }
}
