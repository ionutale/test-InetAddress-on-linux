package com.example.test_InetAddress_on_linux;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping(path = "ping")
public class Ping {
  

  @GetMapping()
  public String getMethodName(@RequestParam String ip) {
      return executePingAndGetStatus(ip).toString();
  }
  
  public HostStatus executePingAndGetStatus(String ip) {
		try {
	          return InetAddress.getByName(ip).isReachable(1000) ? 
	        		  HostStatus.REACHABLE : 
	        			  HostStatus.UNREACHABLE;
	     } catch (IOException e) {
	         log.error("Encountered error when ping ip {}", ip, e);
	         return HostStatus.ERROR;
	     }
	}
}
