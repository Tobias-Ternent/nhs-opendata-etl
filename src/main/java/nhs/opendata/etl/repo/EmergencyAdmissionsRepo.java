package nhs.opendata.etl.repo;

import nhs.opendata.etl.model.EmergencyAdmission;
import nhs.opendata.etl.model.EmergencyAdmissionType;
import nhs.opendata.etl.model.StatsPeriod;
import nhs.opendata.etl.model.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by tobias on 2017-07-15.
 */
public interface EmergencyAdmissionsRepo extends MongoRepository<EmergencyAdmission, String> {


  List<EmergencyAdmission> findAll();

  public List<EmergencyAdmission> findByBasis(String basis);

  public List<EmergencyAdmission> findByStatsPeriod(StatsPeriod statsPeriod);

  public List<EmergencyAdmission> findByProvider(Provider provider);

  public List<EmergencyAdmission> findByBasisAndProviderAndStatsPeriod(
      String basis, Provider provider, StatsPeriod statsPeriod);

  public List<EmergencyAdmission> findByBasisAndProviderAndStatsPeriodAndEmergencyAdmissionTypes(
      String basis, Provider provider, StatsPeriod statsPeriod, List<EmergencyAdmissionType> emergencyAdmissionTypes);

  // todo add index to query fields
  // todo multiple collections in 1 repo, or just have multiple repos?
  // todo override default generated query, i.e. for period or provider?
}
