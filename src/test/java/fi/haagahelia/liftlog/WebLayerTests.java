package fi.haagahelia.liftlog;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebLayerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void shouldReturnLoginPage() throws Exception {
        this.mockMvc.perform(get("/login"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Login")));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldShowIndexPageWhenAuthenticated() throws Exception {
        this.mockMvc.perform(get("/index"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Welcome")));
    }
}