package test;

import static org.junit.Assert.*;

import org.junit.Test;
import static lisp.Lisp.*;

public class TestLispProcedure {

    @Test
    public void testCar() {
        assertEquals(read("a"), eval(read("(car '(a b))")));
    }
    
    @Test
    public void testCdr() {
        assertEquals(read("(b)"), eval(read("(cdr '(a b))")));
    }
    
    @Test
    public void testCons() {
        assertEquals(read("(a . b)"), eval(read("(cons 'a 'b)")));
    }
    
    @Test
    public void testList() {
        assertEquals(read("(a b c)"), eval(read("(list 'a 'b 'c)")));
    }
    
    @Test
    public void testEqTrue() {
        assertEquals(read("#t"), eval(read("(eq? 'a 'a)")));
    }
    
    @Test
    public void testEqFalse() {
        assertEquals(read("#f"), eval(read("(eq? 'a 'b)")));
    }
    
    @Test
    public void testEqualTrue() {
        assertEquals(read("#t"), eval(read("(equal? '(a b) '(a b))")));
    }
    
    @Test
    public void testEqualFalseCar() {
        assertEquals(read("#f"), eval(read("(equal? '(a b) '(x b))")));
    }
    
    @Test
    public void testEqualFalseCdr() {
        assertEquals(read("#f"), eval(read("(equal? '(a b) '(a x))")));
    }
    
    @Test
    public void testEqualFalseDifferentClass() {
        assertEquals(read("#f"), eval(read("(equal? '(a b) 3)")));
    }
    
    @Test
    public void testPairTrue() {
        assertEquals(read("#t"), eval(read("(pair? '(a b))")));
    }
    
    @Test
    public void testPairFalse() {
        assertEquals(read("#f"), eval(read("(pair? 'a)")));
    }
    
    @Test
    public void testPairFalseNil() {
        assertEquals(read("#f"), eval(read("(pair? '())")));
    }
    
    @Test
    public void testDisplay() {
        assertEquals(read("#f"), eval(read("(display 'a 'b)")));
    }
  
}
