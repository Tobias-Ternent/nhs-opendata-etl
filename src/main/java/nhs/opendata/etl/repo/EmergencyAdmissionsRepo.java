package nhs.opendata.etl.repo;

import nhs.opendata.etl.model.EmergencyAdmission;
import nhs.opendata.etl.model.NhsPeriod;
import nhs.opendata.etl.model.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by tobias on 2017-07-15.
 */
public interface EmergencyAdmissionsRepo extends MongoRepository<EmergencyAdmission, String> {


  List<EmergencyAdmission> findAll();

  public List<EmergencyAdmission> findByBasis(String basis);

  public List<EmergencyAdmission> findByNhsPeriod(NhsPeriod nhsPeriod);

  public List<EmergencyAdmission> findByProvider(Provider provider);

  // todo add index to query fields
  // todo multiple collections in 1 repo, or just have multiple repos?
  // todo override default generated query, i.e. for period or provider?
}
