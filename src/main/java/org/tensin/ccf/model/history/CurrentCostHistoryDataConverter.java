package org.tensin.ccf.model.history;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

/**
 * The Class CurrentCostHistoryDataConverter.
 */
public class CurrentCostHistoryDataConverter implements Converter<CurrentCostHistoryData> {

    /**
     * Extract seed.
     *
     * @param key
     *            the key
     * @return the string
     */
    private String extractSeed(final String key) {
        return key.substring(1, key.length());
    }

    /**
     * Process entry.
     *
     * @param result
     *            the result
     * @param key
     *            the key
     * @param value
     *            the value
     */
    private void processEntry(final CurrentCostHistoryData result, final String key, final String value) {
        if (StringUtils.isNotEmpty(key)) {
            if (StringUtils.equalsIgnoreCase("sensor", key)) {
                result.setSensor(value);
            } else {
                if (key.startsWith("h")) {
                    final String seed = extractSeed(key);
                    final ICurrentCostHistoryItem item = new CurrentCostHistoryItemHour(seed, value);
                    result.addItem(item);
                } else if (key.startsWith("d")) {
                    final String seed = extractSeed(key);
                    final ICurrentCostHistoryItem item = new CurrentCostHistoryItemDay(seed, value);
                    result.addItem(item);
                } else if (key.startsWith("m")) {
                    final String seed = extractSeed(key);
                    final ICurrentCostHistoryItem item = new CurrentCostHistoryItemMonth(seed, value);
                    result.addItem(item);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.simpleframework.xml.convert.Converter#read(org.simpleframework.xml.stream.InputNode)
     */
    @Override
    public CurrentCostHistoryData read(final InputNode node) throws Exception {
        final CurrentCostHistoryData result = new CurrentCostHistoryData();
        InputNode next = null;
        while ((next = node.getNext()) != null) {
            final String key = next.getName();
            final String value = next.getValue();
            processEntry(result, key, value);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.simpleframework.xml.convert.Converter#write(org.simpleframework.xml.stream.OutputNode, java.lang.Object)
     */
    @Override
    public void write(final OutputNode node, final CurrentCostHistoryData value) throws Exception {
        // TODO Auto-generated method stub

    }

}
