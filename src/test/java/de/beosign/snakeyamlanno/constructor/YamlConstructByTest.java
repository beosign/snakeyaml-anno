package de.beosign.snakeyamlanno.constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Tests the YamlConstructBy functionality.
 * 
 * @author florian
 */
public class YamlConstructByTest {

    private AnnotationAwareConstructor annotationAwareConstructor;

    @BeforeEach
    public void before() {
        annotationAwareConstructor = new AnnotationAwareConstructor(TestAnnotationAwareConstructor.class);
    }

    @Test
    public void testAnnotationOnly() {
        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(TestConstructByClass.class);
        assertEquals(TestConstructByClassConstructor.class, constructBy.value());

        // test annotation inheritance
        constructBy = annotationAwareConstructor.getConstructBy(ATestConstructByClass.class);
        assertEquals(TestConstructByClassConstructor.class, constructBy.value());

        // test annotation inheritance, but annotation on subclass overrides annotation from superclass
        constructBy = annotationAwareConstructor.getConstructBy(BTestConstructByClass.class);
        assertEquals(BTestConstructByClassConstructor.class, constructBy.value());
    }

    @Test
    public void testMapOnly() {
        annotationAwareConstructor.getConstructByMap().put(Number.class, YamlConstructBy.Factory.of(NumberCustomConstructor.class));
        annotationAwareConstructor.getConstructByMap().put(BigInteger.class, YamlConstructBy.Factory.of(BigIntegerCustomConstructor.class));

        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(Number.class);
        assertEquals(NumberCustomConstructor.class, constructBy.value());

        constructBy = annotationAwareConstructor.getConstructBy(BigInteger.class);
        assertEquals(BigIntegerCustomConstructor.class, constructBy.value());

        constructBy = annotationAwareConstructor.getConstructBy(MyBigInteger.class);
        assertEquals(BigIntegerCustomConstructor.class, constructBy.value());

        constructBy = annotationAwareConstructor.getConstructBy(Double.class);
        assertEquals(NumberCustomConstructor.class, constructBy.value());

        constructBy = annotationAwareConstructor.getConstructBy(Object.class);
        assertNull(constructBy);
    }

    @Test
    public void testMapAndAnnotationExactType() {
        annotationAwareConstructor.getConstructByMap().put(TestConstructByClass.class, YamlConstructBy.Factory.of(BTestConstructByClassConstructor.class));

        // Map wins
        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(TestConstructByClass.class);
        assertEquals(BTestConstructByClassConstructor.class, constructBy.value());
        assertEquals(YamlConstructBy.class, constructBy.annotationType());
    }

    @Test
    public void testMapAndAnnotationRelatedTypesSameSupertype() {
        annotationAwareConstructor.getConstructByMap().put(TestConstructByClass.class, YamlConstructBy.Factory.of(BTestConstructByClassConstructor.class));

        // Map and annotation have a match for same supertype, maps wins
        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(ATestConstructByClass.class);
        assertEquals(BTestConstructByClassConstructor.class, constructBy.value());
    }

    @Test
    public void testMapAndAnnotationRelatedTypesMapMoreSpecificSupertype() {
        annotationAwareConstructor.getConstructByMap().put(TestConstructByClass.class, YamlConstructBy.Factory.of(BTestConstructByClassConstructor.class));

        // Map and annotation have a match for a supertype, but maps' is more specific => map wins
        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(AATestConstructByClass.class);
        assertEquals(BTestConstructByClassConstructor.class, constructBy.value());
    }

    @Test
    public void testMapAndAnnotationRelatedTypesAnnotationMoreSpecificSupertype() {
        annotationAwareConstructor.getConstructByMap().put(TestConstructByClass.class, YamlConstructBy.Factory.of(NumberCustomConstructor.class));

        // Map and annotation have a match for a supertype, but annotations' is more specific => annotation wins
        YamlConstructBy constructBy = annotationAwareConstructor.getConstructBy(BBTestConstructByClass.class);
        assertEquals(BTestConstructByClassConstructor.class, constructBy.value());
    }

    // CHECKSTYLE:OFF
    private static class TestAnnotationAwareConstructor extends AnnotationAwareConstructor {
        private Map<Class<?>, YamlConstructBy> constructByMap;

        public TestAnnotationAwareConstructor(Class<? extends Object> theRoot, Map<Class<?>, YamlConstructBy> constructByMap) {
            super(theRoot);
            this.constructByMap = constructByMap;
        }

        @Override
        public Map<Class<?>, YamlConstructBy> getConstructByMap() {
            return constructByMap;
        }

    }

    @YamlConstructBy(TestConstructByClassConstructor.class)
    public static class TestConstructByClass {
    }

    public static class ATestConstructByClass extends TestConstructByClass {
    }

    public static class AATestConstructByClass extends ATestConstructByClass {
    }

    @YamlConstructBy(BTestConstructByClassConstructor.class)
    public static class BTestConstructByClass extends TestConstructByClass {
    }

    public static class BBTestConstructByClass extends BTestConstructByClass {
    }

    public static class MyBigInteger extends BigInteger {
        private static final long serialVersionUID = 1L;

        public MyBigInteger(String val) {
            super(val);
        }
    }

    public static class TestConstructByClassConstructor implements CustomConstructor<TestConstructByClass> {

        @Override
        public TestConstructByClass construct(Node node, Function<? super Node, ? extends TestConstructByClass> defaultConstructor) throws YAMLException {
            return null;
        }

    }

    public static class BTestConstructByClassConstructor implements CustomConstructor<BTestConstructByClass> {
        @Override
        public BTestConstructByClass construct(Node node, Function<? super Node, ? extends BTestConstructByClass> defaultConstructor) throws YAMLException {
            return null;
        }
    }

    public static class NumberCustomConstructor implements CustomConstructor<Number> {
        @Override
        public Number construct(Node node, Function<? super Node, ? extends Number> defaultConstructor) throws YAMLException {
            return null;
        }
    }

    public static class BigIntegerCustomConstructor implements CustomConstructor<BigInteger> {
        @Override
        public BigInteger construct(Node node, Function<? super Node, ? extends BigInteger> defaultConstructor) throws YAMLException {
            return null;
        }
    }

    public static class MyBigIntegerCustomConstructor implements CustomConstructor<MyBigInteger> {
        @Override
        public MyBigInteger construct(Node node, Function<? super Node, ? extends MyBigInteger> defaultConstructor) throws YAMLException {
            return null;
        }
    }
}
