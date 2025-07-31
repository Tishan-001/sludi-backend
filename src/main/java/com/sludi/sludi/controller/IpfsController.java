package com.sludi.sludi.controller;

import com.sludi.sludi.service.IpfsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ipfs")
public class IpfsController {
    private final IpfsService  ipfsService;

    public IpfsController(IpfsService ipfsService) {
        this.ipfsService = ipfsService;
    }

    @PostMapping("/upload")
    public String uploadToIpfs(@RequestParam("file")MultipartFile file) throws Exception{
        return ipfsService.uploadFile(file.getBytes(), file.getOriginalFilename());
    }

    @GetMapping("/download/{cid}")
    public ResponseEntity<ByteArrayResource> downloadFile (@PathVariable("cid")String cid) throws Exception{
        byte[] fileData = ipfsService.loadFile(cid);
        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + cid +"\"")
                .contentLength(fileData.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
