package ws.slink.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ws.slink.config.AppConfig;
import ws.slink.model.ProcessingResult;
import ws.slink.service.AwsS3Service;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileProcessor {

    private final AppConfig appConfig;
    private final @NonNull AwsS3Service awsS3Service;

    public ProcessingResult process(String inputFilename) {
        ProcessingResult result = new ProcessingResult();
        if (StringUtils.isNotBlank(inputFilename))
            result.merge(upload(new File(inputFilename)));
        return result;
    }
    public ProcessingResult upload(File file) {
        String fileName = file.getAbsolutePath().replace(appConfig.getDir(), "");
        fileName = (fileName.startsWith(String.valueOf(File.separatorChar))) ? fileName.substring(1) : fileName;
        String fileKey = appConfig.getRoot() + File.separatorChar + fileName;
        try {
            log.debug("Uploading: fileName ['" + file.getAbsolutePath() + "']; fileKey '" + fileKey + "']");
            awsS3Service.upload(appConfig.getBucket(), fileKey, file.getAbsolutePath());
            return ProcessingResult.SUCCESS;
        } catch (Exception e) {
            log.error("error uploading file '{}': {}", fileKey, e.getMessage());
            return ProcessingResult.FAILURE;
        }

    }
}



//        return new ProcessingResult();
/*
if (!confluence.canPublish()) {
    System.err.println("can't publish document '" + document.inputFilename() + "' to confluence: not all confluence parameters are set (url, login, password)");
    return ProcessingResult.FAILURE;
} else {
    if (!document.canPublish()) {
        System.err.println("can't publish document '" + document.inputFilename() + "' to confluence: not all document parameters are set (title, spaceKey)");
        return ProcessingResult.FAILURE;
    } else {
        // delete page
        confluence.getPageId(document.space(), document.title()).ifPresent(id -> confluence.deletePage(id, document.title()));
        // delete old page in case of renaming
        if (StringUtils.isNotBlank(document.oldTitle()))
            confluence.getPageId(document.space(), document.oldTitle()).ifPresent(id -> confluence.deletePage(id, document.oldTitle()));
        // publish to confluence
        if (confluence.publishPage(document.space(), document.title(), document.parent(), convertedDocument)) {
            log.info(
                String.format(
                    "Published document to confluence: %s/display/%s/%s"
                    ,appConfig.getUrl()
                    ,document.space()
                    ,document.title().replaceAll(" ", "+")
                )
            );
            if (confluence.tagPage(document.space(), document.title(), document.tags())
            ) {
                log.info(
                    String.format(
                        "Labeled document with tags: %s"
                        ,document.tags()
                    )
                );
            }
            return ProcessingResult.SUCCESS;
        } else {
            log.info(
                String.format(
                    "Could not publish document '%s' to confluence server"
                    ,document.title()
                )
            );
            if (appConfig.isDebug())
                System.out.println(convertedDocument);
            return ProcessingResult.FAILURE;
        }
    }
}
 */
