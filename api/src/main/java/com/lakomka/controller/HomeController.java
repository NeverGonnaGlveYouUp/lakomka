package com.lakomka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin("http://localhost:8080")
public class HomeController {

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

}