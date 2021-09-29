package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}") // application.properties의 속성을 가져올 수 있다.
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}",request);

        String itemName = request.getParameter("itemName");
        log.info("itemName={}",itemName);

        // 이부분의 Parts가 multipart/form-data 전송에서 파트별로 나누어진 데이터를 받아주는 역할을 한다.
        Collection<Part> parts = request.getParts();
        log.info("parts={}",parts);

        // multipart/form-data가 예제에서는 두부분이기 때문에, ====part====로그는 두번 찍히게 되고
        // part는 두개가 된다.
        for (Part part : parts) {
            log.info("====part====");
            log.info("name={}",part.getName());
            Collection<String> headerNames = part.getHeaderNames();// part도 헤더와 body로 구분된다.
            for (String headerName : headerNames) {
                log.info("header {}: {}",headerName, part.getHeader(headerName));
            }
            // 편의 메서드를 제공해준다
            // content-disposition : fileName
            // 헤더에서 파일이름을 꺼내주는 역할을 해주는 편의 메서드를 제공해준다.
            // 그것이 바로 part의 getSubmittedFileName()이다.
            log.info("submittedFilename={}",part.getSubmittedFileName());
            log.info("size={}",part.getSize());

            //데이터 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);// 가져온 Stream 데이터를 UTF-8로 읽어준다.
            log.info("body={}",body);

            // 설정해주었던 경로에 파일 저장하기
            // 만약 SubmittedFileName()이 있다면 -> 파일이름이 있다면 -> 파일이 넘어왔다면 조건문 실행
            if(StringUtils.hasText(part.getSubmittedFileName())){
                String fullPath = fileDir + part.getSubmittedFileName(); // 디렉토리명에 파일명을 합친 String을 하나 만들어준다.
                log.info("파일저장 fullPath={}",fullPath);
                part.write(fullPath); // part.write에 디렉토리명에 파일명을 넣어주면 파일이 설정된 폴더에 저장된다.
            }
        }

        return "upload-form";
    }
}
