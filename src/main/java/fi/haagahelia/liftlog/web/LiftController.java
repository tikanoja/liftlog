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

import fi.haagahelia.liftlog.domain.Lift;
import fi.haagahelia.liftlog.domain.LiftRepository;
import fi.haagahelia.liftlog.domain.User;
import fi.haagahelia.liftlog.domain.UserRepository;
import fi.haagahelia.liftlog.domain.Workout;
import fi.haagahelia.liftlog.domain.WorkoutRepository;
import jakarta.validation.Valid;

@Controller
public class LiftController {

    @Autowired
    private LiftRepository liftRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/workout/{workoutId}/editlift/{liftId}")
    public String editLiftPage(@PathVariable("workoutId") Long workoutId,
                               @PathVariable("liftId") Long liftId,
                               Model model) {
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            return "redirect:/index"; // redir if the workout doesn't exist
        }

        // if liftId is 0, we are adding a new lift
        if (liftId == 0) {
            model.addAttribute("liftForm", new Lift());
        } else {
            Lift lift = liftRepository.findById(liftId).orElse(null);
            if (lift != null && lift.getWorkout().getId().equals(workoutId)) {
                model.addAttribute("liftForm", lift);
            } else {
                return "redirect:/editworkout/" + workoutId; // redir if the lift doesn't exist
            }
        }

        model.addAttribute("workoutId", workoutId);
        return "editlift";
    }

    @PostMapping("/workout/{workoutId}/savelift")
    public String saveLift(@PathVariable("workoutId") Long workoutId,
                           @Valid @ModelAttribute("liftForm") Lift liftForm,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editlift";
        }

        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            return "redirect:/index";
        }

        // check if we are updating or adding a new lift
        if (liftForm.getId() != null) {
            Lift existingLift = liftRepository.findById(liftForm.getId()).orElse(null);
            if (existingLift != null && existingLift.getWorkout().getId().equals(workoutId)) {
                // updating existing lift
                existingLift.setName(liftForm.getName());
                existingLift.setSets(liftForm.getSets());
                existingLift.setReps(liftForm.getReps());
                existingLift.setWeight(liftForm.getWeight());
                existingLift.setIncrement(liftForm.getIncrement());
                liftRepository.save(existingLift);
            }
        } else {
            // adding a new lift
            liftForm.setWorkout(workout);
            liftRepository.save(liftForm);
        }

        return "redirect:/editworkout/" + workoutId;
    }

    @PostMapping("/workout/{workoutId}/deletelift/{liftId}")
    public String deleteLift(@PathVariable("workoutId") Long workoutId,
                            @PathVariable("liftId") Long liftId,
                            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername());
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        
        if (workout != null && workout.getUser().equals(user)) {
            Lift lift = liftRepository.findById(liftId).orElse(null);
            
            if (lift != null && lift.getWorkout().getId().equals(workoutId)) {
                liftRepository.delete(lift);
            }
        }
        
        return "redirect:/editworkout/" + workoutId;
    }

}