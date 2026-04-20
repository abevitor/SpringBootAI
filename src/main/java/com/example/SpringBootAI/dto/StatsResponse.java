package com.example.SpringBootAI.dto;

import java.util.List;
import java.util.Map;

public class StatsResponse {

    private String username;
    private long totalPrompts;
    private String longestPrompt;
    private List<Map<String, Object>> usageByDay;

    public StatsResponse(String username, long totalPrompts,
                          String longestPrompt, List<Map<String, Object>> usageByDay) {

                        this.username = username;
                        this.totalPrompts = totalPrompts;
                        this.longestPrompt = longestPrompt;
                        this.usageByDay = usageByDay;
                          }

    public String getUsername() { return username; }
    public long getTotalPrompts() { return totalPrompts; }
    public String getLongestPrompt() { return longestPrompt; }
    public List<Map<String, Object>> getUsageByDay() { return usageByDay; }
    
    
}
