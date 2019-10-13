package cz.muni.csirt.aida.mining.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import cz.muni.csirt.aida.idea.Idea;

/**
 * Item in Sequential Rules terminology
 */
public class Item {

    private String nodeName;
    private String category;
    private Integer port;

    public Item(String nodeName, String category, Integer port) {
        this.nodeName = nodeName;
        this.category = category;
        this.port = port;
    }

    public Item(Idea idea) {
        nodeName = idea.getNode().get(0).getName();
        category = idea.getCategory().get(0);
        try {
            port = idea.getTarget().get(0).getPort().get(0);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            port = null;
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getCategory() {
        return category;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(nodeName, item.nodeName) &&
                Objects.equals(category, item.category) &&
                Objects.equals(port, item.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeName, category, port);
    }

    /**
     * Represent {@link Item} as String with underscore separated attributes.
     * {@link #fromString(String)} is reverse operation. See it for more information.
     *
     * @return string
     */
    @Override
    public String toString() {
        String port = "None";
        if (this.port != null) {
            port = this.port.toString();
        }

        return String.join("_", nodeName, category, port);
    }

    /**
     * Parse {@link Item} in the form of "{category}_{node}_{port}".
     * Port set as "None" will results with {@code null} being set to {@code port} attribute.
     *
     * {@link #toString()} is reverse operation.
     *
     * Example of item:
     *      cz.cesnet.nemea.hoststats_Recon.Scanning_None
     *
     * @param item string representation of an item
     * @return Item
     */
    public static Item fromString(String item) {
        List<String> parts = splitFromEnd(item, "_", 3);

        Integer port = null;
        if (!"None".equals(parts.get(2))) {
            port = Integer.valueOf(parts.get(2));
        }

        return new Item(parts.get(0), parts.get(1), port);
    }

    private static List<String> splitFromEnd(String input, String delimiter, int limit) {
        List<String> parts = new LinkedList<>();
        for (int i = 0; i < limit-1; i++) {
            int position = input.lastIndexOf(delimiter);
            if (position == -1) {
                break;
            }

            String item = input.substring(position + delimiter.length());
            parts.add(0, item);
            input = input.substring(0, position);
        }
        parts.add(0, input);
        return parts;
    }

}
