package nhs.opendata.etl.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by tobias on 2017-07-15.
 */

@Document(collection = "emergencyadmissions")
public class EmergencyAdmission {

  @Id
  private String id;
  private String basis;
  private StatsPeriod statsPeriod;
  private Provider provider;
  private List<EmergencyAdmissionType> emergencyAdmissionTypes;

  public EmergencyAdmission() {
  }

  /**
   * Sets new id.
   *
   * @param id New value of id.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets id.
   *
   * @return Value of id.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets new basis.
   *
   * @param basis New value of basis.
   */
  public void setBasis(String basis) {
    this.basis = basis;
  }

  /**
   * Gets basis.
   *
   * @return Value of basis.
   */
  public String getBasis() {
    return basis;
  }

  /**
   * Gets emergencyAdmissionTypes.
   *
   * @return Value of emergencyAdmissionTypes.
   */
  public List<EmergencyAdmissionType> getEmergencyAdmissionTypes() {
    return emergencyAdmissionTypes;
  }

  /**
   * Sets new emergencyAdmissionTypes.
   *
   * @param emergencyAdmissionTypes New value of emergencyAdmissionTypes.
   */
  public void setEmergencyAdmissionTypes(List<EmergencyAdmissionType> emergencyAdmissionTypes) {
    this.emergencyAdmissionTypes = emergencyAdmissionTypes;
  }

  public StatsPeriod getStatsPeriod() {
    return statsPeriod;
  }

  public void setStatsPeriod(StatsPeriod statsPeriod) {
    this.statsPeriod = statsPeriod;
  }

  public Provider getProvider() {
    return provider;
  }

  public void setProvider(Provider provider) {
    this.provider = provider;
  }
}
