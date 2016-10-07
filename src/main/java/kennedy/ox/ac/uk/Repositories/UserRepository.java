package kennedy.ox.ac.uk.Repositories;

import kennedy.ox.ac.uk.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

}