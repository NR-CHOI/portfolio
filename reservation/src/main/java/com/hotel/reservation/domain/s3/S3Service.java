package com.hotel.reservation.domain.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.hotel.reservation.domain.roomInfoImage.RoomInfoImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Service {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String getFullPath(String filename) {
        return null;
    }

    //RoomInfoImage 객체생성
    public List<RoomInfoImage> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<RoomInfoImage> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    //S3에 저장
    public RoomInfoImage storeFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            InputStream inputStream = multipartFile.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, storeFileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 실패");
        }

        return new RoomInfoImage(storeFileName, amazonS3.getUrl(bucketName, storeFileName).toString());
    }

    //업로드될 이미지파일 이름만들기(uuid.확장자)
    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //확장자 추출
    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }

    //S3에 있는 이미지 파일 삭제
    public void deleteFile(String storeFileName){
        try{
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, storeFileName));
        }catch (AmazonS3Exception e){
            //에러처리
            log.info("S3에서의 파일삭제가 실패하였습니다.");
        }
    }
}
