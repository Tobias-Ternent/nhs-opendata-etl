package nhs.opendata.etl.repo;

import nhs.opendata.etl.model.EmergencyAdmission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by tobias on 2017-07-15.
 */
public interface EmergencyAdmissionsRepo extends MongoRepository<EmergencyAdmission, String> {


  List<EmergencyAdmission> findAll();

  public List<EmergencyAdmission> findByYear(String year);

  //todo add index to query fields
  // todo multiple collections in 1 repo, or just have multiple repos?

}
