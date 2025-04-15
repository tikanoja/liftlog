package fi.haagahelia.liftlog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping({"", "/"})
    public String redirectToIndex() {
        return "redirect:/index";
    }
}