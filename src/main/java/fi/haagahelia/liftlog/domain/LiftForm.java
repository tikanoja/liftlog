package fi.haagahelia.liftlog.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class LiftForm {

    @NotEmpty(message = "Lift name is required")
    private String name;

    @Min(value = 1, message = "Sets must be at least 1")
    private int sets;

    @Min(value = 1, message = "Reps must be at least 1")
    private int reps;

    @Min(value = 0, message = "Weight must be at least 0")
    private float weight;

    private float increment;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getIncrement() {
        return increment;
    }

    public void setIncrement(float increment) {
        this.increment = increment;
    }
}