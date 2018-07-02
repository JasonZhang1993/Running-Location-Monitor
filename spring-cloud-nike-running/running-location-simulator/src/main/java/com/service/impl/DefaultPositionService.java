package com.service.impl;

import com.model.CurrentPosition;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DefaultPositionService implements PositionService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPositionService.class);

    @Autowired
    private RestTemplate restTemplate;

//    @Value("${com.Jason.running.location.distribution}")
//    private String runningLocationDistribution;

    @HystrixCommand(fallbackMethod = "processPositionInfoFallback")
    @Override
    public void processPositionInfo(long id, CurrentPosition currentPosition, boolean sendPositionToDistributionService) {
        String runningLocationDistribution = "http://running-location-distribution";
        if (sendPositionToDistributionService) {
            log.info(String.format("Thread %d Simulator is calling distribution REST API", Thread.currentThread().getId()));
            this.restTemplate.postForLocation(runningLocationDistribution + "/api/locations", currentPosition);
        }
    }

    public void processPositionInfoFallback(long id, CurrentPosition currentPosition, boolean sendPositionsToDistributionService) {
        log.error("Hystrix Fallback Method. Unable to send message to distribution.");
    }
}