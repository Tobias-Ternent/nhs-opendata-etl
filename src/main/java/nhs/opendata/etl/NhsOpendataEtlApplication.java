package nhs.opendata.etl;

import com.google.common.base.Splitter;
import nhs.opendata.etl.model.EmergencyAdmission;
import nhs.opendata.etl.model.EmergencyAdmissionType;
import nhs.opendata.etl.model.NhsPeriod;
import nhs.opendata.etl.model.Provider;
import nhs.opendata.etl.repo.EmergencyAdmissionsRepo;
import nhs.opendata.etl.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication
public class NhsOpendataEtlApplication implements CommandLineRunner {

  @Autowired
  EmergencyAdmissionsRepo emergencyAdmissionsRepo;

  public static void main(String[] args) {
    SpringApplication.run(NhsOpendataEtlApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // downloadAllFiles
    // parseAndLoadAllAreas
    parseAndLoadEmergencyAdmissions();
		/*for each speciality
		get CSV files
		for each file, parse headers/data rows
		connect to mongo
		load each row into MongoDB:
		if row already exists, check if they are the same
		if not the same, update/delete old row and just have the new information
		close mongo, exit app
*/
  }

  private void parseAndLoadEmergencyAdmissions() {
    emergencyAdmissionsRepo.deleteAll(); // clean slate
    // todo for each file in map for this department
    // todo all historical files
    String fileName = "C:\\test\\stub\\rawMay-csv-adm-fmkwe.csv"; //todo stub file
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      int lineNumber=0;
      List<String> lineParts;
      boolean foundHeader = false;
      EmergencyAdmission emergencyAdmission;
      NhsPeriod nhsPeriod;
      String defaultYear = ""; // todo get info from non-data lines instead
      String defaultPeriodName = ""; // todo get info from non-data lines instead
      List<EmergencyAdmissionType> emergencyAdmissionTypes;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        // todo use commons-csv instead?
        lineParts = Splitter.on(Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).splitToList(line);
        List<String> cleanedLineParts = new ArrayList<>(lineParts.size());
        for (int i=0; i<lineParts.size(); i++) {
          if (lineParts.size()>0) {
            String linePart = lineParts.get(i);
            if (linePart!=null && linePart.length()>0 && linePart.charAt(0)=='\"') {
              linePart = linePart.substring(1, linePart.length()-1);
            }
            cleanedLineParts.add(i, linePart);
          }
        }
        if (!foundHeader) { // non-data lines
          if (cleanedLineParts.size()>0) {
            String firstCellofLine = cleanedLineParts.get(0);
            if (firstCellofLine.equalsIgnoreCase("Basis")) {
              foundHeader = true; // header line
              // todo check headers are OK in relation to model class?
            }
          }
        } else { // data lines
          if (cleanedLineParts.size()!=10) {
            throw new IOException("Data line isn't exactly in 10 parts: " + lineNumber + " : " + line);
          }
          emergencyAdmission = new EmergencyAdmission();
          emergencyAdmission.setBasis(cleanedLineParts.get(0));
          nhsPeriod = new NhsPeriod();
          nhsPeriod.setYear(cleanedLineParts.get(1));
          if (StringUtils.isEmpty(defaultYear)) {
            if (!StringUtils.isEmpty(cleanedLineParts.get(1))) {
              defaultYear = cleanedLineParts.get(1);
            }
          }
          if (StringUtils.isEmpty(nhsPeriod.getYear())) {
            nhsPeriod.setYear(defaultYear);
          }
          nhsPeriod.setName(cleanedLineParts.get(2));
          if (StringUtils.isEmpty(defaultPeriodName)) {
            if (!StringUtils.isEmpty(cleanedLineParts.get(2))) {
              defaultPeriodName = cleanedLineParts.get(2);
            }
          }

          if (StringUtils.isEmpty(nhsPeriod.getName())) {
            nhsPeriod.setName(defaultPeriodName);
          }
          emergencyAdmission.setNhsPeriod(nhsPeriod);
          Provider provider = new Provider();
          provider.setParentName(cleanedLineParts.get(3));
          provider.setOrgCode(cleanedLineParts.get(4));
          provider.setOrgName(cleanedLineParts.get(5));
          emergencyAdmissionTypes = new ArrayList<>();
          emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.ONE, cleanedLineParts.get(6)));
          emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.TWO, cleanedLineParts.get(7)));
          emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.THREE, cleanedLineParts.get(8)));
          emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.OTHER, cleanedLineParts.get(9)));
          emergencyAdmission.setEmergencyAdmissionTypes(emergencyAdmissionTypes);
          emergencyAdmissionsRepo.save(emergencyAdmission);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

	/*
	TODO logging
	TODO local unitTests
	TODO connection details
	TODO get data files
	TODO all departments
	TODO all time points

	TODO post-implementation thoughts
	*/
}