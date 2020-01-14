package ws.slink.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import ws.slink.config.AppConfig;
import ws.slink.model.ProcessingResult;
import ws.slink.service.AwsS3Service;

import java.io.File;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DependsOn({"commandLineArguments"})
public class Processor {

    private final @NonNull AppConfig appConfig;
    private final @NonNull DirectoryProcessor directoryProcessor;
    private final @NonNull AwsS3Service awsS3Service;

    public String process() {

        if (awsS3Service.connect()) {

            appConfig.printParams();

            long timeA = Instant.now().toEpochMilli();

            // cleanup bucket
            if (appConfig.isClean()) {
                log.info("Cleaning up {}/{}", appConfig.getBucket(), appConfig.getRoot());
                awsS3Service.clean(appConfig.getBucket(), appConfig.getRoot());
            }

            long timeB = Instant.now().toEpochMilli();

            // process documentation sources
            ProcessingResult result = new ProcessingResult();
            if (StringUtils.isNotBlank(appConfig.getDir()))
                result.merge(directoryProcessor.process(new File(appConfig.getDir()).getAbsolutePath()));

            long timeC = Instant.now().toEpochMilli();

            return new StringBuilder()
                    .append("-------------------------------------------------------------").append("\n")
                    .append("total time taken   : " + DurationFormatUtils.formatDuration(timeC - timeA, "HH:mm:ss")).append("\n")
                    .append("clean up time      : " + DurationFormatUtils.formatDuration(timeB - timeA, "HH:mm:ss")).append("\n")
                    .append("uploading time     : " + DurationFormatUtils.formatDuration(timeC - timeB, "HH:mm:ss")).append("\n")
                    .append("successful uploads : " + result.successful().get()).append("\n")
                    .append("upload failures    : " + result.failed().get()).append("\n")
                    .toString();
        } else {
            log.error("could not connect to AWS");
            return "ERROR: could not connect to AWS";
        }
    }

}
