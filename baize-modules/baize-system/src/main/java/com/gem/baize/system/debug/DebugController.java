package com.gem.baize.system.debug;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("test global exception handler");
    }
}
