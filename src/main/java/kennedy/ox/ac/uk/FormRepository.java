package kennedy.ox.ac.uk;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by vinod on 08/09/2016.
 */
public interface FormRepository extends MongoRepository<Form, String> {

    public Form findById(String id);


}
