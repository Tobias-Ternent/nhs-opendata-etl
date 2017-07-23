package nhs.opendata.etl;

import com.google.common.base.Splitter;
import nhs.opendata.etl.model.EmergencyAdmission;
import nhs.opendata.etl.model.EmergencyAdmissionType;
import nhs.opendata.etl.model.StatsPeriod;
import nhs.opendata.etl.model.Provider;
import nhs.opendata.etl.repo.EmergencyAdmissionsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication
public class NhsOpendataEtlApplication implements CommandLineRunner {

  private final Logger log = LoggerFactory.getLogger(NhsOpendataEtlApplication.class);
  private File[] curatedAeAdmissionFiles;
  private File[] curatedAeAttendanceFiles;

  @Autowired
  EmergencyAdmissionsRepo emergencyAdmissionsRepo;

  public static void main(String[] args) {
    SpringApplication.run(NhsOpendataEtlApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // downloadAllFiles
    // parseAndLoadAllAreas
    getCuratedFiles();
    parseAndLoadEmergencyData();
  }

  private void getCuratedFiles() {
    File curatedAeDir = new File("C:\\test\\stub\\curated\\ae");
    curatedAeAdmissionFiles = curatedAeDir.listFiles((dir, name) -> name.contains("admissions"));
  }

  private void parseAndLoadEmergencyData() {
    parseAndLoadEmergencyAdmissions();
    //todo parseAndLoadEmergencyAttendances();
  }

  private void parseAndLoadEmergencyAdmissions() {
    emergencyAdmissionsRepo.deleteAll(); // clean slate
    if (curatedAeAdmissionFiles==null || curatedAeAdmissionFiles.length<1) {
      log.error("No A&E admissions files to load.");
    } else {
      for (File curatedAeAdmissionFile : curatedAeAdmissionFiles) {
        try (BufferedReader br = new BufferedReader(new FileReader(curatedAeAdmissionFile))) {
          String line;
          int lineNumber = 0;
          List<String> lineParts;
          boolean foundHeader = false;
          EmergencyAdmission emergencyAdmission;
          List<EmergencyAdmissionType> emergencyAdmissionTypes;
          while ((line = br.readLine()) != null) {
            lineNumber++;
            // todo use commons-csv instead?
            lineParts = Splitter.on(Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).splitToList(line);
            List<String> cleanedLineParts = new ArrayList<>(lineParts.size());
            for (int i = 0; i < lineParts.size(); i++) {
              if (lineParts.size() > 0) {
                String linePart = lineParts.get(i);
                if (linePart != null && linePart.length() > 0 && linePart.charAt(0) == '\"') {
                  linePart = linePart.substring(1, linePart.length() - 1);
                }
                cleanedLineParts.add(i, linePart);
              }
            }
            if (!foundHeader) { // non-data lines
              if (cleanedLineParts.size() > 0) {
                String firstCellofLine = cleanedLineParts.get(0);
                if (firstCellofLine.equalsIgnoreCase("Basis")) {
                  foundHeader = true; // header line
                  // todo check headers are OK in relation to model class?
                }
              }
            } else { // data lines
              if (cleanedLineParts.size() != 10) {
                throw new IOException("Data line isn't exactly in 10 parts: " + lineNumber + " : " + line);
              }
              emergencyAdmission = new EmergencyAdmission();
              emergencyAdmission.setBasis(cleanedLineParts.get(0));
              emergencyAdmission.setStatsPeriod(new StatsPeriod(cleanedLineParts.get(1), cleanedLineParts.get(2)));
              Provider provider = new Provider();
              provider.setParentName(cleanedLineParts.get(3));
              provider.setOrgCode(cleanedLineParts.get(4));
              provider.setOrgName(cleanedLineParts.get(5));
              emergencyAdmission.setProvider(provider);
              emergencyAdmissionTypes = new ArrayList<>();
              emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.ONE, cleanedLineParts.get(6)));
              emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.TWO, cleanedLineParts.get(7)));
              emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.THREE, cleanedLineParts.get(8)));
              emergencyAdmissionTypes.add(new EmergencyAdmissionType(EmergencyAdmissionType.EmergencyAdmissionTypeName.OTHER, cleanedLineParts.get(9)));
              emergencyAdmission.setEmergencyAdmissionTypes(emergencyAdmissionTypes);
              List<EmergencyAdmission> existingEmergencyAdmissions = emergencyAdmissionsRepo.
                  findByBasisAndProviderAndStatsPeriodAndEmergencyAdmissionTypes(
                      emergencyAdmission.getBasis(),
                      emergencyAdmission.getProvider(),
                      emergencyAdmission.getStatsPeriod(),
                      emergencyAdmission.getEmergencyAdmissionTypes());
              if (existingEmergencyAdmissions.size() < 1) {
                emergencyAdmissionsRepo.save(emergencyAdmission);
                log.debug("Inserted new record, " + lineNumber + ": " + line);
              } else {
                log.debug("Record already exists, skipping.");
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        log.info("Finished processing A&E admissions data.");
      }
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