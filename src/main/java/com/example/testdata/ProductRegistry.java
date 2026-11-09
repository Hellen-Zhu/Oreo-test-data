package com.example.testdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * The products of the suite and the capabilities they can have: what this library needs to know about the suite
 * that uses it. The suite implements it once and registers the class in
 * {@code META-INF/services/com.example.testdata.ProductRegistry} (a public class with a no-argument constructor, a
 * nested class of the suite's product enum say); {@link #get()} finds it on the classpath. Adding a product is a
 * change of the suite's registry only, never of this library.
 */
public interface ProductRegistry {

    /** The products, in registry order: the order of the {@code Examples} rows. */
    List<? extends Product> products();

    /**
     * The capabilities, as {@code @capability#} tag names, by group: the group's title ("Trade lifecycle") to its
     * capabilities in declaration order. Every capability is in exactly one group.
     */
    Map<String, List<String>> capabilityGroups();

    /** The prefix of the tag that marks a product Outline with its capability. */
    String TAG_PREFIX = "@capability#";

    /**
     * The registry of the classpath.
     *
     * @throws IllegalStateException when there is none, or more than one
     */
    static ProductRegistry get() {
        return Holder.INSTANCE;
    }

    /** The names of the products, in registry order. */
    static List<String> productNames() {
        return get().products().stream().map(Product::name).toList();
    }

    /**
     * The product named {@code name}.
     *
     * @throws IllegalArgumentException when the registry has no such product
     */
    static Product product(String name) {
        for (Product p : get().products()) {
            if (p.name().equals(name)) return p;
        }
        throw new IllegalArgumentException("'" + name + "' is not a product of the registry " + productNames());
    }

    /** Every capability tag name, group by group, in declaration order. */
    static List<String> capabilityTags() {
        List<String> tags = new ArrayList<>();
        get().capabilityGroups().values().forEach(tags::addAll);
        return tags;
    }

    /**
     * {@code tagName} when it is a capability.
     *
     * @throws IllegalArgumentException naming the known capabilities otherwise
     */
    static String capability(String tagName) {
        if (capabilityTags().contains(tagName)) return tagName;
        throw new IllegalArgumentException("'" + tagName + "' is not a capability; known: "
                + String.join(", ", capabilityTags()));
    }

    /** The names of the products that support {@code capability}, in registry order. */
    static List<String> productsWith(String capability) {
        return get().products().stream().filter(p -> p.capabilityTags().contains(capability))
                .map(Product::name).toList();
    }

    /** Loads the registry once. */
    final class Holder {
        private static final ProductRegistry INSTANCE = load();

        private Holder() {}

        private static ProductRegistry load() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) loader = ProductRegistry.class.getClassLoader();
            Iterator<ProductRegistry> found = ServiceLoader.load(ProductRegistry.class, loader).iterator();
            List<ProductRegistry> all = new ArrayList<>();
            found.forEachRemaining(all::add);
            if (all.size() != 1) {
                throw new IllegalStateException("[ProductRegistry] expected one registry on the classpath, found "
                        + all.stream().map(r -> r.getClass().getName()).collect(Collectors.toList())
                        + ": register the suite's in META-INF/services/" + ProductRegistry.class.getName());
            }
            return all.get(0);
        }
    }
}
