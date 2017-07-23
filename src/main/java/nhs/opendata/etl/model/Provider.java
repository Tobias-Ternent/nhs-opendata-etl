package nhs.opendata.etl.model;

/**
 * Created by tobias on 2017-07-16.
 */
public class Provider {

  private String orgCode;
  private String orgName;
  private String parentName;

  public Provider(){
  }

  public Provider(String orgCode, String orgName, String parentName) {
    this.orgCode = orgCode;
    this.orgName = orgName;
    this.parentName = parentName;
  }

  public String getOrgCode() {
    return orgCode;
  }

  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!Provider.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final Provider other = (Provider) obj;
    if ((this.orgName == null) ? (other.orgName != null) : !this.orgName.equals(other.orgName)) {
      return false;
    }
    if ((this.orgCode == null) ? (other.orgCode!= null) : !this.orgCode.equals(other.orgCode)) {
      return false;
    }
    if ((this.parentName == null) ? (other.parentName != null) : !this.parentName.equals(other.parentName)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + (this.orgName != null ? this.orgName.hashCode() : 0);
    hash = 53 * hash + (this.orgCode != null ? this.orgCode.hashCode() : 0);
    hash = 53 * hash + (this.parentName != null ? this.parentName.hashCode() : 0);
    return hash;
  }
}
