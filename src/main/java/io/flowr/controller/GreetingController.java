package io.flowr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GreetingController {
    private static final String template = "Hello, %s!";
    private static final String defaultName = "World";

    @GetMapping("/greeting")
    public String greeting(String name) {
        return String.format(template, name != null ? name : defaultName);
    }
}
