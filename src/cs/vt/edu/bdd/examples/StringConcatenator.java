/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.vt.edu.bdd.examples;

/**
 *
 * @author akshaymaloo
 */
public class StringConcatenator {
    
    private String valueStr1;
    private String valueStr2;
    
    public void setvalueStr1(String valueStr1) {
        this.valueStr1 = valueStr1;
    }
    
    public void setvaString2(String valueStr2) {
        this.valueStr2 = valueStr2;
    }
    
    public String getConcatenatedValue() {
        return valueStr1 + valueStr2;
    }
    
}
