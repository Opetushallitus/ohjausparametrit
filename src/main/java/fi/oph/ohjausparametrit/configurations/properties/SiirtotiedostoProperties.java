package fi.oph.ohjausparametrit.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "siirtotiedostot")
public class SiirtotiedostoProperties {
  private String awsRegion;
  private String s3BucketName;
  private String s3bucketTargetRoleArn;
  private int maxItemcountInFile;

  public String getAwsRegion() {
    return awsRegion;
  }

  public void setAwsRegion(String awsRegion) {
    this.awsRegion = awsRegion;
  }

  public String getS3BucketName() {
    return s3BucketName;
  }

  public void setS3BucketName(String s3BucketName) {
    this.s3BucketName = s3BucketName;
  }

  public String getS3bucketTargetRoleArn() {
    return s3bucketTargetRoleArn;
  }

  public void setS3bucketTargetRoleArn(String s3bucketTargetRoleArn) {
    this.s3bucketTargetRoleArn = s3bucketTargetRoleArn;
  }

  public int getMaxItemcountInFile() {
    return maxItemcountInFile;
  }

  public void setMaxItemcountInFile(int maxItemcountInFile) {
    this.maxItemcountInFile = maxItemcountInFile;
  }
}
