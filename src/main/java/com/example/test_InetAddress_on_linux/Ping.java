package com.example.test_InetAddress_on_linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
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
  

  @GetMapping("/ping-with-inetaddress")
  public HostStatus pingWithIsReachable(@RequestParam String ip) throws IOException {
      return executePingAndGetStatus(ip);
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

  @GetMapping("/ping-with-process")
  public HostStatus pingWithPath(@RequestParam String ip) throws IOException {
      return pingHost(ip);
  }
  
   public static HostStatus pingHost(String host) throws IOException {
        Objects.requireNonNull(host, "Host cannot be null");

        String os = System.getProperty("os.name").toLowerCase();
        String pingCommand = getPingCommand(os, host);

        try {
            // add timeout to avoid hanging
            ProcessBuilder processBuilder = new ProcessBuilder(pingCommand.split(" "));
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // stop process after 5 seconds
            // process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            return evaluatePingOutput(reader, process);

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while pinging host: " + host, e);
            return HostStatus.ERROR;
        }
    }

    private static HostStatus evaluatePingOutput(BufferedReader reader, Process process) throws IOException, InterruptedException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Request timed out")) {
                return HostStatus.UNREACHABLE;
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0 ? HostStatus.REACHABLE : HostStatus.UNREACHABLE;
    }

    private static String getPingCommand(String os, String host) {
        if (os.contains("win")) {
            return String.format("ping -n 1 %s", host);
        } else {
            return String.format("ping -c 1 %s", host);
        }
    }
}
