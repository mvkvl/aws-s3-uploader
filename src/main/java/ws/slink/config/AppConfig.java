package ws.slink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


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
 *
 */

@Data
@Component
@ConfigurationProperties(prefix = "s3u")
public class AppConfig {

    private String dir;
    private String key;
    private String secret;
    private String region;
    private String bucket;
    private String root = "resources";
    private boolean clean = false;

}
