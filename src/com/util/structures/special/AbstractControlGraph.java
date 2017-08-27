package com.util.structures.special;

/**
 * Created by John Crockett.
 */

public abstract class AbstractControlGraph<T extends AbstractControlGraph.CGNode> {

    private T current;

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public static class CGNode<T> {
        private T itemPointedAt;

        public T getItemPointedAt() {
            return itemPointedAt;
        }

        /**
         * @param itemPointedAt
         */
        public void setItemPointedAt(T itemPointedAt) {
            this.itemPointedAt = itemPointedAt;
        }
    }
}
