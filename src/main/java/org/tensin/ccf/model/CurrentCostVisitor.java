package org.tensin.ccf.model;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Visitor;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

/**
 * The Class CurrentCostVisitor.
 */
public class CurrentCostVisitor implements Visitor {

    /**
     * {@inheritDoc}
     *
     * @see org.simpleframework.xml.strategy.Visitor#read(org.simpleframework.xml.strategy.Type, org.simpleframework.xml.stream.NodeMap)
     */
    @Override
    public void read(final Type type, final NodeMap<InputNode> node) throws Exception {
        // if (type.getType().equals(org.tensin.mirror4j.model.operations.IOperation.class)) {
        // System.out.println(">>>>>>>>>>>> " + type.getType().getName());
        // Element a = type.getAnnotation(Element.class);
        // if (a != null) {
        // System.out.println("         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + a.name());
        // }
        //
    }

    /**
     * {@inheritDoc}
     *
     * @see org.simpleframework.xml.strategy.Visitor#write(org.simpleframework.xml.strategy.Type, org.simpleframework.xml.stream.NodeMap)
     */
    @Override
    public void write(final Type type, final NodeMap<OutputNode> node) throws Exception {

    }
}