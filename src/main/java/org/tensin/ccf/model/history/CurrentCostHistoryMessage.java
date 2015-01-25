package org.tensin.ccf.model.history;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCostMessage;

/**
 * The Class CurrentCostHistory.
 */
@Root(name = "msg")
public class CurrentCostHistoryMessage extends AbstractCurrentCostMessage {

    /** The history. */
    @Element(required = false, name = "hist")
    @BeanField
    private CurrentCostHistoryEntry history = new CurrentCostHistoryEntry();

    /**
     * Gets the history.
     *
     * @return the history
     */
    public CurrentCostHistoryEntry getHistory() {
        return history;
    }

    /**
     * Sets the history.
     *
     * @param history
     *            the new history
     */
    public void setHistory(final CurrentCostHistoryEntry history) {
        this.history = history;
    }
}
