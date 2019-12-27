package ws.slink.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.slink.config.AppConfig;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AwsS3Service {

    private final AppConfig appConfig;

    private AmazonS3 s3client;

    @PostConstruct
    private void createAwsClient() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    appConfig.getKey(),
                    appConfig.getSecret()
            );
            s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    // TODO: configure region (?)
                    .withRegion(Regions.US_EAST_2)
                    .build();
        } catch (Exception e) {
            log.error("con't connect to AWS: {}", e.getMessage());
            s3client = null;
        }
    }

    public List<String> list(String bucket) {
        return s3client.listObjects(bucket).getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    public void clean(String bucket) {
        list(bucket).stream().forEach(key -> s3client.deleteObject(bucket, key));
    }

    public void upload(String bucket, String fileKey, String fileName) {
        PutObjectResult result = s3client.putObject(bucket, fileKey, new File(fileName));
    }

}

