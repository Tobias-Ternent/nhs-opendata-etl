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
  private boolean cleanSlate = false;

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
    log.info("Starting to load Emergency Admissions data");
    if (cleanSlate) {
      emergencyAdmissionsRepo.deleteAll();
    }
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
                  log.debug("Found header line: " + line);
                  if (!cleanedLineParts.get(1).equalsIgnoreCase("Year") ||
                      !cleanedLineParts.get(2).equalsIgnoreCase("Period Name") ||
                      !cleanedLineParts.get(3).equalsIgnoreCase("Provider Parent Name") ||
                      !cleanedLineParts.get(4).equalsIgnoreCase("Provider Org Code") ||
                      !cleanedLineParts.get(5).equalsIgnoreCase("Provider Org Name") ||
                      !cleanedLineParts.get(6).equalsIgnoreCase("Emergency Adm Type 1") ||
                      !cleanedLineParts.get(7).equalsIgnoreCase("Emergency Adm Type 2") ||
                      !cleanedLineParts.get(8).equalsIgnoreCase("Emergency Adm Type 3") ||
                      !cleanedLineParts.get(9).equalsIgnoreCase("Emergency Adm Other")
                      ) {
                    log.error("Incompatible header lines: " + line);
                  }
                } else {
                  log.error("Missing header line: " + line);
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
                  findByBasisAndProviderAndStatsPeriod(
                      emergencyAdmission.getBasis(),
                      emergencyAdmission.getProvider(),
                      emergencyAdmission.getStatsPeriod());
              if (existingEmergencyAdmissions.size() < 1) {
                emergencyAdmissionsRepo.save(emergencyAdmission);
                log.debug("Inserted new record, " + lineNumber + ": " + line);
              } else {
                if (existingEmergencyAdmissions.size()>1) {
                  log.error("More than 1 document found for: " + emergencyAdmission.getProvider().getOrgName() + " " +
                      emergencyAdmission.getStatsPeriod().getYear() + " " + emergencyAdmission.getStatsPeriod().getName());
                } else { // exactly 1 document
                  EmergencyAdmission existingEmergencyAdmission = existingEmergencyAdmissions.get(0);
                  boolean matches = true;
                  for (EmergencyAdmissionType emergencyAdmissionType : existingEmergencyAdmission.getEmergencyAdmissionTypes()) {
                    if (!emergencyAdmissionTypes.contains(emergencyAdmissionType)) {
                      matches = false;
                      log.debug(("Mismatch between admission types."));
                      break;
                    }
                  }
                  if (!matches) {
                    emergencyAdmissionsRepo.delete(existingEmergencyAdmission.getId());
                    emergencyAdmissionsRepo.save(emergencyAdmission);
                    log.debug("Removed old entry, and replaced it with new entry for: " + emergencyAdmission.getProvider().getOrgName() + " " +
                        emergencyAdmission.getStatsPeriod().getYear() + " " + emergencyAdmission.getStatsPeriod().getName());
                  }
                }
                log.debug("Record already exists, skipping.");
              }
            }
          }
        } catch (IOException e) {
          log.error("", e);
        }
        log.info("Finished processing A&E admissions data.");
      }
    }
  }

	/*
	TODO local unitTests
	TODO connection details
	TODO get data files
	TODO all departments
	TODO all time points

	TODO post-implementation thoughts
	*/
}