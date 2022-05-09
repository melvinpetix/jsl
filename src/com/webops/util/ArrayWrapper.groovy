package com.webops.util

/**
 * A utility class to give semi-list functionality which is compatible with jenkins pipeline script serialization requirements (CPS).
 */
class ArrayWrapper implements Serializable {

    private Object[] array

    /**
     * Create a new ArrayWrapper instance and optionally populate it with values from the given list
     * @param list (optionally) a list with initial values for this instance
     */
    ArrayWrapper(List list = []) {
        array = list.toArray()
    }

    /**
     * Add an element to this list
     * @param element the element to add
     */
    void add(element) {
        Object[] newArray = new Object[array.length+1]
        System.arraycopy(array, 0, newArray, 0, array.length)
        newArray[array.length] = element
        array = newArray
    }

    /**
     * Set the element at a given position in the list
     * @param index
     * @param element the element to set
     * @return the old value if existed, or null
     */
    def set(int index, element) {
        def old = array[index]
        array[index] = element
        old
    }

    /**
     * Get the element at a given index
     * @param index
     */
    def get(int index) {
        array[index]
    }

    /**
     * Get the size of the list
     */
    int size() {
        array.length
    }

    @NonCPS
    String toString() {
        Arrays.asList(array).toString()
    }

}
