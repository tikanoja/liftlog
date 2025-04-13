package fi.haagahelia.liftlog.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fi.haagahelia.liftlog.domain.Lift;
import fi.haagahelia.liftlog.domain.LiftRepository;
import fi.haagahelia.liftlog.domain.User;
import fi.haagahelia.liftlog.domain.UserRepository;
import fi.haagahelia.liftlog.domain.Workout;
import fi.haagahelia.liftlog.domain.WorkoutForm;
import fi.haagahelia.liftlog.domain.WorkoutRepository;
import jakarta.validation.Valid;

@Controller
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LiftRepository liftRepository;

    // Display the "New Workout" form
    @GetMapping("/newworkout")
    public String newWorkoutPage(Model model) {
        model.addAttribute("workoutForm", new WorkoutForm());
        return "newworkout";
    }

    // Handle creating a new workout
    @PostMapping("/createworkout")
    public String createWorkout(@Valid @ModelAttribute("workoutForm") WorkoutForm workoutForm,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails currentUser) {
        if (bindingResult.hasErrors()) {
            return "newworkout";
        }

        User user = userRepository.findByUsername(currentUser.getUsername());
        if (user != null) {
            Workout workout = new Workout(workoutForm.getName(), user);
            workoutRepository.save(workout);
        }

        return "redirect:/index";
    }

    // Handle deleting a workout
    @PostMapping("/deleteworkout/{id}")
    public String deleteWorkout(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(id).orElse(null);

        if (workout != null && workout.getUser().equals(user)) {
            workoutRepository.delete(workout);
        }

        return "redirect:/index";
    }

    // Display the "Edit Workout" page, including all lifts for the workout
    @GetMapping("/editworkout/{id}")
    public String editWorkoutPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(id).orElse(null);
    
        // Ensure the workout belongs to the logged-in user
        if (workout != null && workout.getUser().equals(user)) {
            model.addAttribute("workout", workout);
            model.addAttribute("lifts", liftRepository.findByWorkout(workout)); // Pass lifts to the model
            return "editworkout";
        }
    
        return "redirect:/index"; // Redirect if the workout is not found or doesn't belong to the user
    }

    @PostMapping("/startworkout/{id}")
    public String startWorkout(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(id).orElse(null);
    
        if (workout != null && workout.getUser().equals(user)) {
            // Set all other workouts to inactive first
            List<Workout> userWorkouts = workoutRepository.findByUser(user);
            for (Workout w : userWorkouts) {
                if (w.isActive()) {
                    w.setActive(false);
                    workoutRepository.save(w);
                }
            }
    
            // Set this workout to active
            workout.setActive(true);

            for (Lift lift : workout.getLifts()) {
                lift.setSuccess(false);
            }

            workoutRepository.save(workout);
            
            // Redirect to the workout page instead of index
            return "redirect:/workout/" + id;
        }
    
        return "redirect:/index";
    }

    @PostMapping("/workout/{workoutId}/lift/{liftId}/success")
    public String markLiftSuccess(@PathVariable("workoutId") Long workoutId,
                                @PathVariable("liftId") Long liftId,
                                @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        
        // Ensure the workout belongs to the user
        if (workout != null && workout.getUser().equals(user)) {
            Lift lift = liftRepository.findById(liftId).orElse(null);
            
            // Ensure the lift exists and belongs to the workout
            if (lift != null && lift.getWorkout().getId().equals(workoutId)) {
                lift.setSuccess(true);
                liftRepository.save(lift);
            }
        }
        
        return "redirect:/workout/" + workoutId;
    }

    @GetMapping("/workout/{id}")
    public String showWorkout(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(id).orElse(null);

        // Ensure the workout belongs to the logged-in user and is active
        if (workout != null && workout.getUser().equals(user)) {
            model.addAttribute("workout", workout);
            model.addAttribute("lifts", liftRepository.findByWorkout(workout));
            return "workout";
        }

        return "redirect:/index"; // Redirect if the workout is not found or doesn't belong to the user
    }

    @PostMapping("/finishworkout/{id}")
    public String finishWorkout(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(id).orElse(null);
    
        if (workout != null && workout.getUser().equals(user) && workout.isActive()) {
            // Process each lift and adjust weights
            for (Lift lift : workout.getLifts()) {
                float currentWeight = lift.getWeight();
                float increment = lift.getIncrement();
                
                if (lift.isSuccess()) {
                    // Success: Increase weight by increment
                    lift.setWeight(currentWeight + increment);
                } else {
                    // Failure: Decrease weight by increment * 2
                    // Ensure weight doesn't go below 0
                    float newWeight = currentWeight - (increment * 2);
                    lift.setWeight(Math.max(0, newWeight));
                }
            }
            
            // Mark workout as inactive
            workout.setActive(false);
            workoutRepository.save(workout);
        }
    
        return "redirect:/index";
    }

}