package fi.haagahelia.liftlog.domain;

import jakarta.validation.constraints.NotEmpty;

public class WorkoutForm {

    @NotEmpty(message = "Workout name is required")
    private String name = "";

    // Getters and setters
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}