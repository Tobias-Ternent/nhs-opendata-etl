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
}
