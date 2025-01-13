package sopt.jeolloga;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
    @GetMapping("/templestay/list")
    public String review() {
        return "/templestay/list";
    }
}
