package fi.haagahelia.liftlog;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.haagahelia.liftlog.web.IndexController;
import fi.haagahelia.liftlog.web.WorkoutController;

@SpringBootTest
class LiftlogApplicationTests {

    @Autowired
    private WorkoutController workoutController;
    
    @Autowired
    private IndexController indexController;

    @Test
    void contextLoads() {
        assertThat(workoutController).isNotNull();
        assertThat(indexController).isNotNull();
    }
}