package org.tensin.ccf.model;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * The Class AbstractReaderTestCase.
 */
public abstract class AbstractReaderTestCase {

    /**
     * Builds the serializer.
     *
     * @return the serializer
     */
    protected Serializer buildSerializer() {
        final Serializer serializer = new Persister();
        return serializer;
    }
}