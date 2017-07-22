package nhs.opendata.etl;

import com.google.common.base.Splitter;
import nhs.opendata.etl.model.EmergencyAdmission;
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
      String defaultYear = ""; // todo get info from non-data lines instead
      String defaultPeriodName = ""; // todo get info from non-data lines instead
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
          /*emergencyAdmission = new EmergencyAdmission();
          emergencyAdmission.setBasis(cleanedLineParts.get(0));
          emergencyAdmission.setYear(cleanedLineParts.get(1));
          if (StringUtils.isEmpty(defaultYear)) {
            if (!StringUtils.isEmpty(cleanedLineParts.get(1))) {
              defaultYear = cleanedLineParts.get(1);
            }
          }
          if (StringUtils.isEmpty(emergencyAdmission.getYear())) {
            emergencyAdmission.setYear(defaultYear);
          }
          emergencyAdmission.setPeriodName(cleanedLineParts.get(2));
          if (StringUtils.isEmpty(defaultPeriodName)) {
            if (!StringUtils.isEmpty(cleanedLineParts.get(2))) {
              defaultPeriodName = cleanedLineParts.get(2);
            }
          }
          if (StringUtils.isEmpty(emergencyAdmission.getPeriodName())) {
            emergencyAdmission.setPeriodName(defaultPeriodName);
          }
          emergencyAdmission.setProviderParentName(cleanedLineParts.get(3));
          emergencyAdmission.setProviderOrgCode(cleanedLineParts.get(4));
          emergencyAdmission.setProviderOrgName(cleanedLineParts.get(5));
          emergencyAdmission.setEmergencyAdmType1(Integer.parseInt(cleanedLineParts.get(6))); // check int OK
          emergencyAdmission.setEmergencyAdmType2(Integer.parseInt(cleanedLineParts.get(7))); // check int OK
          emergencyAdmission.setEmergencyAdmType3(Integer.parseInt(cleanedLineParts.get(8))); // check int OK
          emergencyAdmission.setEmergencyAdmOther(Integer.parseInt(cleanedLineParts.get(9))); // check int OK
          emergencyAdmissionsRepo.save(emergencyAdmission);
          System.out.println("Finished loading in data: " + line);*/
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

	/*
	TODO
	local unitTests
	connection details
	get data files
	all departments
	all time points

	post-implementation thoughts
	migrate to python 3?
	 */
}