package gh.dso.datastructures;

import gh.dso.datastructures.stack.MyStack;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

class MyStackTest {

    @Test
    void pushAndPop_normalCase_LIFOOrder() {
        MyStack<Integer> stack = new MyStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void peek_doesNotRemoveTop() {
        MyStack<Integer> stack = new MyStack<>();
        stack.push(10);
        stack.push(20);

        assertEquals(20, stack.peek());
        assertEquals(2, stack.size());
    }

    @Test
    void emptyStack_boundaryCase() {
        MyStack<Integer> stack = new MyStack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void singleElement_boundaryCase() {
        MyStack<String> stack = new MyStack<>();
        stack.push("only");
        assertEquals("only", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void pop_onEmptyStack_invalidCase_throws() {
        MyStack<Integer> stack = new MyStack<>();
        assertThrows(EmptyStackException.class, stack::pop);
    }

    @Test
    void peek_onEmptyStack_invalidCase_throws() {
        MyStack<Integer> stack = new MyStack<>();
        assertThrows(EmptyStackException.class, stack::peek);
    }

    @Test
    void resize_trace_growsWhenCapacityExceeded() {
        MyStack<Integer> stack = new MyStack<>(2);
        assertEquals(2, stack.capacity());
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.capacity()); // still fits
        stack.push(3); // triggers resize (doubling)
        assertEquals(4, stack.capacity());
        assertEquals(3, stack.size());
    }

    @Test
    void clear_emptiesStack() {
        MyStack<Integer> stack = new MyStack<>();
        stack.push(1);
        stack.push(2);
        stack.clear();
        assertTrue(stack.isEmpty());
    }
}
