package fi.haagahelia.liftlog.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface WorkoutRepository extends CrudRepository<Workout, Long> {
    List<Workout> findByUser(User user);
    List<Workout> findByName(String name);
}