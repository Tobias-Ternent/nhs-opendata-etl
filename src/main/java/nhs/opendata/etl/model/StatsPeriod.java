package nhs.opendata.etl.model;

/**
 * Created by tobias on 2017-07-16.
 */
public class StatsPeriod {

    private String year;
    private String name;

    public StatsPeriod() {
    }

    public StatsPeriod(String year, String name) {
        this.year = year;
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}