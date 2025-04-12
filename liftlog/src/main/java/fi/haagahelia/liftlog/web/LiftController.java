package fi.haagahelia.liftlog.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fi.haagahelia.liftlog.domain.Lift;
import fi.haagahelia.liftlog.domain.LiftRepository;
import fi.haagahelia.liftlog.domain.Workout;
import fi.haagahelia.liftlog.domain.WorkoutRepository;
import jakarta.validation.Valid;

@Controller
public class LiftController {

    @Autowired
    private LiftRepository liftRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    // Display the "Edit Lift" page (for both adding and editing)
    @GetMapping("/workout/{workoutId}/editlift/{liftId}")
    public String editLiftPage(@PathVariable("workoutId") Long workoutId,
                               @PathVariable("liftId") Long liftId,
                               Model model) {
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            return "redirect:/index"; // Redirect if the workout doesn't exist
        }

        // If liftId is 0, this is for adding a new lift
        if (liftId == 0) {
            model.addAttribute("liftForm", new Lift());
        } else {
            Lift lift = liftRepository.findById(liftId).orElse(null);
            if (lift != null && lift.getWorkout().getId().equals(workoutId)) {
                model.addAttribute("liftForm", lift);
            } else {
                return "redirect:/editworkout/" + workoutId; // Redirect if the lift doesn't exist or doesn't belong to the workout
            }
        }

        model.addAttribute("workoutId", workoutId);
        return "editlift";
    }

    // Handle saving a lift (both adding and editing)
    @PostMapping("/workout/{workoutId}/savelift")
    public String saveLift(@PathVariable("workoutId") Long workoutId,
                           @Valid @ModelAttribute("liftForm") Lift liftForm,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editlift";
        }

        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            return "redirect:/index"; // Redirect if the workout doesn't exist
        }

        // Check if this is an update or a new lift
        if (liftForm.getId() != null) {
            Lift existingLift = liftRepository.findById(liftForm.getId()).orElse(null);
            if (existingLift != null && existingLift.getWorkout().getId().equals(workoutId)) {
                // Update the existing lift
                existingLift.setName(liftForm.getName());
                existingLift.setSets(liftForm.getSets());
                existingLift.setReps(liftForm.getReps());
                existingLift.setWeight(liftForm.getWeight());
                existingLift.setIncrement(liftForm.getIncrement());
                liftRepository.save(existingLift);
            }
        } else {
            // Add a new lift
            liftForm.setWorkout(workout);
            liftRepository.save(liftForm);
        }

        return "redirect:/editworkout/" + workoutId;
    }
}