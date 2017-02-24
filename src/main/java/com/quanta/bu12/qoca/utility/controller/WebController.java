package com.quanta.bu12.qoca.utility.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String root(){
		return "redirect:/deviceLog.html";
	}
}
