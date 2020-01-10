package ws.slink.config;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;


/**
 * accepts following configuration parameters from any spring-supported configuration source
 * (properties file, command line arguments, environment variables):
 *
 *   s3u.clean   - if bucket should be cleaned up before upload
 *   s3u.dir     - input directory for processing
 *   s3u.key     - AWS access key
 *   s3u.secret  - AWS access secret
 *   s3u.region  - AWS region
 *   s3u.bucket  - AWS bucket to upload files to
 *   s3u.root    - root 'directory' in bucket to upload files to
 */

@Data
@Component
@ConfigurationProperties(prefix = "s3u")
public class AppConfig {

    private String dir;
    private String key;
    private String secret;
    private String region = "US_EAST_1";
    private String bucket;
    private String root = "resources";
    private boolean clean = false;

    @PostConstruct
    private void updateSourceDirectory() {
        if (StringUtils.isNotBlank(dir))
            setDir(new File(getDir()).getAbsolutePath());
    }

    public void printParams() {
        System.out.println("Source directory: " + dir);
//        System.out.println("AWS key         : " + key);
//        System.out.println("AWS secret      : " + secret);
        System.out.println("AWS region      : " + region);
        System.out.println("AWS bucket      : " + bucket);
        System.out.println("AWS root        : " + root);
        System.out.println("Cleanup flag    : " + clean);
    }
}
