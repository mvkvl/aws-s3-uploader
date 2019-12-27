package ws.slink.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class CommandLineArguments {

    @Autowired
    public CommandLineArguments(ApplicationArguments args, AppConfig appConfig) {
        if (args.containsOption("dir")) {
            appConfig.setDir(new File(args.getOptionValues("dir").get(0)).getAbsolutePath());
        }
        if (args.containsOption("key")) {
            appConfig.setKey(args.getOptionValues("key").get(0));
        }
        if (args.containsOption("secret")) {
            appConfig.setSecret(args.getOptionValues("secret").get(0));
        }
        if (args.containsOption("region")) {
            appConfig.setRegion(args.getOptionValues("region").get(0));
        }
        if (args.containsOption("bucket")) {
            appConfig.setBucket(args.getOptionValues("bucket").get(0));
        }
        if (args.containsOption("root")) {
            appConfig.setRoot(args.getOptionValues("root").get(0));
        }
        if (args.containsOption("clean")) {
            appConfig.setClean(true);
        }
    }




}
