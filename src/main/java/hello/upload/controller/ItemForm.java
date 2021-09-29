package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
// 상품 저장용 폼.
public class ItemForm {
    private Long itemId;
    private String itmeName;
    private MultipartFile attachFile; // 단일이미지 업로드용
    private List<MultipartFile> imageFiles; // 이미지 다중업로드용

}

