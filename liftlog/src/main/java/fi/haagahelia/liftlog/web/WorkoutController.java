package fi.haagahelia.liftlog.web;

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
}