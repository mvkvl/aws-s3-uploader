# AWS S3 uploader
Uploads passed directory hierarchy to S3 bucket, optionally removing existing data before upload.

## Running application
To run uploader application all the necessary arguments should be passed to program via command line 
or environment variables.

argument      | env&nbsp;variable |   default   | description
--------------|------------------ | :---------: | ---------------
--key         | s3u_key           |             | AWS access key
--secret      | s3u_secret        |             | AWS access secret
--bucket      | s3u_bucket        |             | AWS bucket
--region      | s3u_region        | US_EAST_1   | AWS region
--root        | s3u_root          | resources   | Root 'directory' in the bucket (special value 'root' to perform action on bucket itself)
--dir         | s3u_dir           |             | Directory to upload to s3 recursively; if not set, no upload action will be taken
--clean       | s3u_clean         | false       | Remove all existing files from given root; if no --dir is set, only cleanup will be performed

### Examples

- cleanup the whole bucket
```
java -jar aws-s3-uploader.jar --key=AWSKEY --secret=AWSSECRET --bucket=AWSBUCKET --root=root --clean
```

- cleanup default "directory" ('resources') in the bucket
```
java -jar aws-s3-uploader.jar --key=AWSKEY --secret=AWSSECRET --bucket=AWSBUCKET --clean
```

- upload local directory 'local-files' to AWS bucket under 'uploaded-files' subdirectory
```
java -jar aws-s3-uploader.jar --key=AWSKEY --secret=AWSSECRET --bucket=AWSBUCKET --root=uploaded-files --dir=local-files
```

- cleanup bucket's "subdirectory" and upload local directory 'local-files' to AWS bucket under 'uploaded-files' subdirectory
```
java -jar aws-s3-uploader.jar --key=AWSKEY --secret=AWSSECRET --bucket=AWSBUCKET --root=uploaded-files --dir=local-files --clean
```
