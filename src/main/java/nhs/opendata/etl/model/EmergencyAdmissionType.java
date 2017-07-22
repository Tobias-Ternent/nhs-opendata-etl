package nhs.opendata.etl.model;

/**
 * Created by tobias on 2017-07-15.
 */
public class EmergencyAdmissionType {

  private String name;
  private String value;

  public EmergencyAdmissionType() {
  }

  public EmergencyAdmissionType(String name, String value) {
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
  public String getName() {
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
  public void setName(String name) {
    this.name = name;
  }
}
