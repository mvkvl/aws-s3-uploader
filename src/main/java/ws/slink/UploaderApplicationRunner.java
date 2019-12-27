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
        System.out.println("Usage: ");
        System.out.println("  java -jar aws-s3-uploader.jar --key=<access key> --secret=<access secret> --bucket=<s3-bucket> --root=<root-directory-name> [--region=<aws region>] [--clean] [--dir=<path/to/directory>]");
        System.out.println("\t--dir\t\tDirectory to upload to s3 recursively");
        System.out.println("\t--clean\t\tRemove all existing files before upload");
        System.out.println("\t--key\t\tAWS access key");
        System.out.println("\t--secret\t\tAWS access secret");
        System.out.println("\t--bucket\tAWS bucket");
        System.out.println("\t--region\tAWS region");
        System.out.println("\t--root\t\troot directory in the bucket (deafult is 'resources')");
    }

}
