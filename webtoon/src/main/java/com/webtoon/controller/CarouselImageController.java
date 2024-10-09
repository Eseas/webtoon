package com.webtoon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CarouselImageController {
    @GetMapping("/carousel/images")
    public List<String> getCarouselImages() {
        String folderPath = "src/main/resources/static/carousel";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<String> imageList = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                    imageList.add(file.getName());
                }
            }
        }

        return imageList;
    }
}
