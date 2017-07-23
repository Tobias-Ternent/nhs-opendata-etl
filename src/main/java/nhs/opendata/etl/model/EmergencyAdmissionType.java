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

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!EmergencyAdmissionType.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final EmergencyAdmissionType other = (EmergencyAdmissionType) obj;
    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
      return false;
    }
    if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }
}