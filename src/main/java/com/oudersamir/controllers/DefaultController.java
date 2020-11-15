package com.oudersamir.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
	private static final Logger logger=LoggerFactory.getLogger(DefaultController.class);
	@GetMapping("/")
public ResponseEntity<String> run(){
	logger.info("Demarrage de l'API");
	return new ResponseEntity<String>("Response de serveur : "+HttpStatus.OK.name(), HttpStatus.OK);
}
}
