package com.mattjohnson.teai5_2.controller;

import com.mattjohnson.teai5_2.pojo.Location;
import com.mattjohnson.teai5_2.pojo.WeatherLocation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Controller
public class WeatherController {

    private String baseUrl = "https://www.metaweather.com/api/location/";
    private static final String FALLBACK_SEARCH= "warsaw";

    private WeatherLocation weatherLocation = null;


    public List<Location> getLocation(String search) {
        RestTemplate restTemplate = new RestTemplate();

        if(search.equals("")) {
            search = FALLBACK_SEARCH;
        }

        ResponseEntity<Location[]> location = restTemplate.exchange(baseUrl + "search/?query=" + search,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Location[].class);
        return Arrays.asList(location.getBody());

    }

    public WeatherLocation getWeatherLocation(String search) {
        RestTemplate restTemplate = new RestTemplate();
        Integer woeid = 523920; //default Warsaw
        try {
            woeid = getLocation(search).get(0).getWoeid();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getStackTrace();
        }


            ResponseEntity<WeatherLocation> weatherLocation = restTemplate.exchange(baseUrl + woeid,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    WeatherLocation.class);

            return weatherLocation.getBody();

    }



    @GetMapping("/weather")
    public String getWeather(Model model) {
        String fallback = "warsaw";
        weatherLocation = getWeatherLocation(fallback);
        model.addAttribute("weatherLocation", weatherLocation);
        return "weather";
    }


    @PostMapping("/check-weather")
    public String getWeatherL(Model model, @RequestParam(name = "search") String search) {
        weatherLocation = getWeatherLocation(search.toLowerCase());
        model.addAttribute("weatherLocation", weatherLocation);
        return "weather";
    }
}
