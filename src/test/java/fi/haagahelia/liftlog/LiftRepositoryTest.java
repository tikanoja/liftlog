package fi.haagahelia.liftlog;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import fi.haagahelia.liftlog.domain.Lift;
import fi.haagahelia.liftlog.domain.LiftRepository;
import fi.haagahelia.liftlog.domain.User;
import fi.haagahelia.liftlog.domain.UserRepository;
import fi.haagahelia.liftlog.domain.Workout;
import fi.haagahelia.liftlog.domain.WorkoutRepository;

@SpringBootTest(classes = LiftlogApplication.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LiftRepositoryTest {
    
    @Autowired
    private LiftRepository liftRepository;
    
    @Autowired
    private WorkoutRepository workoutRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void findByNameShouldReturnLift() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            Workout workout = new Workout("Lift Test Workout", user);
            workoutRepository.save(workout);
            
            List<Lift> existingLifts = liftRepository.findByName("Test Bench Press");
            for (Lift l : existingLifts) {
                liftRepository.delete(l);
            }
            
            Lift lift = new Lift("Test Bench Press", 3, 5, 60.0f, 2.5f, workout);
            liftRepository.save(lift);
            
            List<Lift> lifts = liftRepository.findByName("Test Bench Press");
            assertThat(lifts).hasSize(1);
            
            liftRepository.delete(lift);
            workoutRepository.delete(workout);
        }
    }
    
    @Test
    public void createNewLift() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            Workout workout = new Workout("Create Lift Test", user);
            workoutRepository.save(workout);
            
            Lift lift = new Lift("Created Test Squat", 3, 5, 100.0f, 5.0f, workout);
            liftRepository.save(lift);
            
            assertThat(lift.getId()).isNotNull();
            
            liftRepository.delete(lift);
            workoutRepository.delete(workout);
        }
    }
    
    @Test
    public void deleteLift() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            Workout workout = new Workout("Delete Lift Test", user);
            workoutRepository.save(workout);
            
            List<Lift> existingLifts = liftRepository.findByName("Delete Test Deadlift");
            for (Lift l : existingLifts) {
                liftRepository.delete(l);
            }
            
            Lift lift = new Lift("Delete Test Deadlift", 1, 5, 120.0f, 5.0f, workout);
            liftRepository.save(lift);
            
            List<Lift> lifts = liftRepository.findByName("Delete Test Deadlift");
            assertThat(lifts).isNotEmpty();
            
            liftRepository.delete(lifts.get(0));
            
            List<Lift> newLifts = liftRepository.findByName("Delete Test Deadlift");
            assertThat(newLifts).isEmpty();
            
            workoutRepository.delete(workout);
        }
    }
}