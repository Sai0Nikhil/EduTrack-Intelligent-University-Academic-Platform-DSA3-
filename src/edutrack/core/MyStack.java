package edutrack.core;

/**
 * Hand-built LIFO Stack with zero java.util.* dependencies.
 */
public class MyStack<T> {
    private Object[] elements;
    private int top;
    private static final int DEFAULT_CAPACITY = 16;

    public MyStack() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.top = -1;
    }

    public void push(T item) {
        if (top + 1 >= elements.length) {
            resize();
        }
        elements[++top] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T item = (T) elements[top];
        elements[top--] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return (T) elements[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }

    private void resize() {
        Object[] newArr = new Object[elements.length * 2];
        System.arraycopy(elements, 0, newArr, 0, elements.length);
        elements = newArr;
    }
}
