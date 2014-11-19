/**
 * Complex.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ku.ske.calculator;

public class Complex  implements java.io.Serializable {
    private double imag;

    private double real;

    public Complex() {
    }

    public Complex(
           double imag,
           double real) {
           this.imag = imag;
           this.real = real;
    }


    /**
     * Gets the imag value for this Complex.
     * 
     * @return imag
     */
    public double getImag() {
        return imag;
    }


    /**
     * Sets the imag value for this Complex.
     * 
     * @param imag
     */
    public void setImag(double imag) {
        this.imag = imag;
    }


    /**
     * Gets the real value for this Complex.
     * 
     * @return real
     */
    public double getReal() {
        return real;
    }


    /**
     * Sets the real value for this Complex.
     * 
     * @param real
     */
    public void setReal(double real) {
        this.real = real;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Complex)) return false;
        Complex other = (Complex) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.imag == other.getImag() &&
            this.real == other.getReal();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Double(getImag()).hashCode();
        _hashCode += new Double(getReal()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Complex.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://calculator.ske.ku/", "complex"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imag");
        elemField.setXmlName(new javax.xml.namespace.QName("", "imag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("real");
        elemField.setXmlName(new javax.xml.namespace.QName("", "real"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
