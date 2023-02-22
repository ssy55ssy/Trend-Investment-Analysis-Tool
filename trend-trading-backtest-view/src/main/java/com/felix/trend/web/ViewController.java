package com.felix.trend.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RefreshScope
public class ViewController {

    @Value("${version}")
    String version;

    @GetMapping("/")
    public String view(Model model){
        model.addAttribute("version",version);
        return "view";
    }
}
