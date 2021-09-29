package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}") // application.properties의 속성을 가져올 수 있다.
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    // 스프링에서는 파일업로드 기능을 위해, MultipartFile이라는 인터페이스로 멀티파트 파일을 편리하게 지원한다.
    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           // 업로드시켜주는 HTML Form에 name값에 맞춰서, 데이터타입을 MultipartFile로 해주면 된다.
                           // @ModelAttribute도 가능
                           @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        log.info("request={}",request);
        log.info("itemName={}",itemName);
        log.info("multipartFile={}",file);

        if(!file.isEmpty()){
            // MultipartFile의 getOriginalFilename()은 파일원본이름을 꺼내옴
            String fullPath = fileDir + file.getOriginalFilename(); // 파일저장경로 + 파일원본이름을 이용해서 전체경로 만들어줌
            log.info("파일 저장 fullPath={}",fullPath);
            file.transferTo(new File(fullPath)); // MultipartFile의 transferTo를 이용해 파일저장
        }
        return "upload-form";
    }
}
