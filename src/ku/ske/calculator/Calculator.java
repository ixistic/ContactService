/**
 * Calculator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ku.ske.calculator;

public interface Calculator extends javax.xml.rpc.Service {
    public java.lang.String getComplexCalculatorPortAddress();

    public ku.ske.calculator.ComplexCalculator getComplexCalculatorPort() throws javax.xml.rpc.ServiceException;

    public ku.ske.calculator.ComplexCalculator getComplexCalculatorPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
