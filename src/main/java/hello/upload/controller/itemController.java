package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.UploadFile;
import hello.upload.domain.itemRepository;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class itemController {
    private final itemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form){
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItme(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        // 파일은 스토리지만 저장하고, DB에는 파일저장 경로만 저장한다.
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());

        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // 데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItmeName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId",item.getId());
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model){
        Item item = itemRepository.findById(id);
        model.addAttribute("item",item);
        return "item-view";
    }

    // 웹페이지에서 이미지경로를 가지고 이미지를 출력해주는 컨트롤러가 필요하다!!
    // 해당 기능이없으면 엑박뜸
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // "file:/users/../UUID파일명" 그니까 받아온 url경로에 파일에 직접 접근하여 출력해준다.
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    // 첨부파일 저장기능
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName(); // 사용자가 저장했을 때 나와야하는 이름

        //UrlResource를 이용해서 서버저장된 파일에 접근한다.
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        log.info("uploadFileName={}",resource);


        // contentDispostion라는걸 아래와 같은 형식으로 만들어준다.
        //이건 규약이니까 따라하자.
        // header에 만들어준 contentDispostion을 넣어주고
        // body에는 아까 UrlResource로 접근해서 가져온 파일을 넣어준다.
        // 파일내용이 꺠질 수 있기 떄문에, 인코딩시켜서 인코딩된 파일 내용을 contentDispostion에 넣어준다.
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDispostion = "attachment; filename=\"" + encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispostion)
                .body(resource);
    }
}
