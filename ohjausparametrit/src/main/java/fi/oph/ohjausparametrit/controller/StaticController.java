package fi.oph.ohjausparametrit.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@RequestMapping("/")
public class StaticController {
  @GetMapping({"/swagger", "/swagger/**"})
  public String swagger() {
    return "redirect:/swagger-ui/index.html";
  }
}
