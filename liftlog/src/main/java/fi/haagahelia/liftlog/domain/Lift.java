package fi.haagahelia.liftlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "lift")
public class Lift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotEmpty(message = "Lift name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 1, message = "Sets must be at least 1")
    @Column(name = "sets", nullable = false)
    private int sets;

    @Min(value = 1, message = "Reps must be at least 1")
    @Column(name = "reps", nullable = false)
    private int reps;

    @Min(value = 0, message = "Weight must be at least 0")
    @Column(name = "weight", nullable = false)
    private float weight;

    @Column(name = "increment", nullable = false)
    private float increment;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    public Lift() {
    }

    public Lift(String name, int sets, int reps, float weight, float increment, Workout workout) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.increment = increment;
        this.workout = workout;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }
}