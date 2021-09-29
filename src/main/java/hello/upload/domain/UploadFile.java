package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    // 파일명을 두가지만들어준다. 그 이유는, 여러명의 사용자가 업로드를 진행했을 때.
    // 파일명이 겹칠 수 있기 때문이다.
    // 따라서 서버에 저장될 파일명은 UUID같은걸 이용해서 파일명이 겹치지 않도록 따로 만들어주어야 한다.
    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 서버 내부에서 관리하는 파일명

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
