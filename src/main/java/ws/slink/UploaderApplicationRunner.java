package ws.slink;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ws.slink.config.AppConfig;
import ws.slink.parser.Processor;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UploaderApplicationRunner implements CommandLineRunner, ApplicationContextAware {

    private final @NonNull AppConfig appConfig;
    private final @NonNull Processor processor;

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void run(String... args) {
        int exitCode = 0;
        if (!checkConfiguration()) {
            printUsage();
            exitCode = 1;
        } else {
            System.out.println(processor.process());
        }
        // close up
        applicationContext.close();
        System.exit(exitCode);
    }

    private boolean checkConfiguration() {
        return !( StringUtils.isBlank(appConfig.getKey()) || StringUtils.isBlank(appConfig.getSecret())
              || (!appConfig.isClean() && StringUtils.isBlank(appConfig.getDir())))
        ;
    }

    public void printUsage() {
        System.out.println();
        System.out.println("Usage: ");
        System.out.println("  java -jar aws-s3-uploader.jar <arguments>");
        System.out.println("\t--dir\t\tDirectory to upload to s3 recursively. If not set, no upload action will be taken.");
        System.out.println("\t--clean\t\tRemove all existing files from given root. If no --dir is set, only cleanup will be performed.");
        System.out.println("\t--key\t\tAWS access key");
        System.out.println("\t--secret\tAWS access secret");
        System.out.println("\t--bucket\tAWS bucket");
        System.out.println("\t--region\tAWS region (default is 'US_EAST_1')");
        System.out.println("\t--root\t\tRoot 'directory' in the bucket (default is 'resources'; special value 'root' to perform action on bucket itself)");
        System.out.println();
        System.out.println("  Any argument can be taken from environment variables of form 's3u_<argument>'. ");
        System.out.println("  For example AWS credentials can be set via environment like this:");
        System.out.println("\n     export s3u_key=AWSKEY");
        System.out.println("     export s3u_secret=AWSSECRET\n");
        System.out.println("  In this case, there's no need to set credentials via command line arguments.");
        System.out.println();
    }

}
