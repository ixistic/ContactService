package ku.ske.calculator;

public class ComplexCalculatorProxy implements ku.ske.calculator.ComplexCalculator {
  private String _endpoint = null;
  private ku.ske.calculator.ComplexCalculator complexCalculator = null;
  
  public ComplexCalculatorProxy() {
    _initComplexCalculatorProxy();
  }
  
  public ComplexCalculatorProxy(String endpoint) {
    _endpoint = endpoint;
    _initComplexCalculatorProxy();
  }
  
  private void _initComplexCalculatorProxy() {
    try {
      complexCalculator = (new ku.ske.calculator.CalculatorLocator()).getComplexCalculatorPort();
      if (complexCalculator != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)complexCalculator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)complexCalculator)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (complexCalculator != null)
      ((javax.xml.rpc.Stub)complexCalculator)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ku.ske.calculator.ComplexCalculator getComplexCalculator() {
    if (complexCalculator == null)
      _initComplexCalculatorProxy();
    return complexCalculator;
  }
  
  public ku.ske.calculator.Complex add(ku.ske.calculator.Complex arg0, ku.ske.calculator.Complex arg1) throws java.rmi.RemoteException{
    if (complexCalculator == null)
      _initComplexCalculatorProxy();
    return complexCalculator.add(arg0, arg1);
  }
  
  public ku.ske.calculator.Complex multiply(ku.ske.calculator.Complex arg0, ku.ske.calculator.Complex arg1) throws java.rmi.RemoteException{
    if (complexCalculator == null)
      _initComplexCalculatorProxy();
    return complexCalculator.multiply(arg0, arg1);
  }
  
  
}