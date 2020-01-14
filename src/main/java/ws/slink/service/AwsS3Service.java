package ws.slink.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.slink.config.AppConfig;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AwsS3Service {

    private final @NonNull AppConfig appConfig;

    private AmazonS3 s3client;

    public boolean connect() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    appConfig.getKey(),
                    appConfig.getSecret()
            );
            s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.valueOf(appConfig.getRegion()))
                    .build();
            return true;
        } catch (Exception e) {
            log.error("can't connect to AWS: {}", e.getMessage());
            s3client = null;
            return false;
        }
    }

    public List<String> list(String bucket) {
        try {
            return s3client.listObjects(bucket).getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("error querying s3 files: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void clean(String bucket, String root) {
        Stream<String> keyStream = (StringUtils.isBlank(root))
                                 ? list(bucket).stream()
                                 : list(bucket).stream().filter(s -> s.startsWith(root));
        keyStream.forEach(key -> {
            try {
                log.info("removing object {}", key);
                s3client.deleteObject(bucket, key);
            } catch (Exception e) {
                log.warn("error removing object '{}': {}", key, e.getMessage());
            }
        });
    }

    public boolean upload(String bucket, String fileKey, String fileName) {
        try {
            PutObjectResult result = s3client.putObject(bucket, fileKey, new File(fileName));
            return true;
        } catch (Exception e) {
            log.warn("error uploading file '{}': {}", fileName, e.getMessage());
        }
        return false;
    }

}

