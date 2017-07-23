package nhs.opendata.etl.model;

/**
 * Created by tobias on 2017-07-15.
 */
public class EmergencyAdmissionType {

  public enum EmergencyAdmissionTypeName {
    ONE,
    TWO,
    THREE,
    OTHER
  }

  private EmergencyAdmissionTypeName name;
  private String value;

  public EmergencyAdmissionType() {
  }

  public EmergencyAdmissionType(EmergencyAdmissionTypeName name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Sets new value.
   *
   * @param value New value of value.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets name.
   *
   * @return Value of name.
   */
  public EmergencyAdmissionTypeName getName() {
    return name;
  }

  /**
   * Gets value.
   *
   * @return Value of value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets new name.
   *
   * @param name New value of name.
   */
  public void setName(EmergencyAdmissionTypeName name) {
    this.name = name;
  }
}