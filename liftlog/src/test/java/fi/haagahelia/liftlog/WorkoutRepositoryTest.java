package fi.haagahelia.liftlog;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import fi.haagahelia.liftlog.domain.User;
import fi.haagahelia.liftlog.domain.UserRepository;
import fi.haagahelia.liftlog.domain.Workout;
import fi.haagahelia.liftlog.domain.WorkoutRepository;

@SpringBootTest(classes = LiftlogApplication.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class WorkoutRepositoryTest {
    
    @Autowired
    private WorkoutRepository workoutRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void findByNameShouldReturnWorkout() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            List<Workout> existingWorkouts = workoutRepository.findByName("Test Workout");
            for (Workout w : existingWorkouts) {
                workoutRepository.delete(w);
            }
            
            Workout workout = new Workout("Test Workout", user);
            workoutRepository.save(workout);
            
            List<Workout> workouts = workoutRepository.findByName("Test Workout");
            assertThat(workouts).hasSize(1);
            
            workoutRepository.delete(workout);
        }
    }
    
    @Test
    public void createNewWorkout() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            Workout workout = new Workout("Created Test Workout", user);
            workoutRepository.save(workout);
            assertThat(workout.getId()).isNotNull();
            
            workoutRepository.delete(workout);
        }
    }
    
    @Test
    public void deleteWorkout() {
        User user = userRepository.findAll().iterator().next();
        if (user != null) {
            List<Workout> existingWorkouts = workoutRepository.findByName("Delete Test Workout");
            for (Workout w : existingWorkouts) {
                workoutRepository.delete(w);
            }
            
            Workout workout = new Workout("Delete Test Workout", user);
            workoutRepository.save(workout);
            
            List<Workout> workouts = workoutRepository.findByName("Delete Test Workout");
            assertThat(workouts).isNotEmpty();
            
            workoutRepository.delete(workouts.get(0));
            
            List<Workout> newWorkouts = workoutRepository.findByName("Delete Test Workout");
            assertThat(newWorkouts).isEmpty();
        }
    }
}