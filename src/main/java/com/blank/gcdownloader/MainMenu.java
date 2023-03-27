package com.blank.gcdownloader;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MainMenu implements ApplicationRunner {

    private final GcDownloader downloader;
    private final ImageFilter imageFilter;
    private final IdChanger idChanger;

    public MainMenu(GcDownloader downloader, ImageFilter imageFilter, IdChanger idChanger) {
        this.downloader = downloader;
        this.imageFilter = imageFilter;
        this.idChanger = idChanger;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String name = "pharmacies";
        downloader.downloadBucket("ANCMiddleware", name);
        imageFilter.filterImages(name + ".zip");
        idChanger.renameIds("974.xlsx", name + "_filtered", 0, 2);
    }
}
