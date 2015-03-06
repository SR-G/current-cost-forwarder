package org.tensin.ccf.model;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.VisitorStrategy;

public abstract class AbstractReaderTestCase {

    /**
     * Builds the serializer.
     *
     * @return the serializer
     */
    protected Serializer buildSerializer() {
        final Strategy strategy = new VisitorStrategy(new CurrentCostVisitor());
        final Serializer serializer = new Persister(strategy);
        return serializer;

    }
}