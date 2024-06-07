package net.risedata.jdbc.commons.listener;

public interface VariableRedirect {
  /**
   * 修改的变量	
   * @param newValue
   */
  public void Redirect(Object newValue,Object... args);
}
