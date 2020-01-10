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
        String fileKey = StringUtils.isBlank(appConfig.getRoot())
                       ? fileName
                       : appConfig.getRoot() + "/" + fileName;
        try {
            log.debug("Uploading: fileName='" + file.getAbsolutePath() + "'; fileKey='" + fileKey + "'");
            if (awsS3Service.upload(appConfig.getBucket(), fileKey, file.getAbsolutePath()))
                return ProcessingResult.SUCCESS;
        } catch (Exception e) {
            log.error("error uploading file '{}': {}", fileKey, e.getMessage());
        }
        return ProcessingResult.FAILURE;
    }
}
