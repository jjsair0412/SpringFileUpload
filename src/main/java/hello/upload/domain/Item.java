package hello.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles; // 여러개의 파일들이 들어오면 , List로 받아주면 다들어올수 있다.
}
