package fi.haagahelia.liftlog.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LiftRepository extends CrudRepository<Lift, Long> {
    List<Lift> findByWorkout(Workout workout);
    List<Lift> findByName(String name);
}