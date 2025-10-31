package com.raghav.youtubedownloader;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend access; restrict in prod
public class DownloadController {
    private final DownloadService service;

    public DownloadController(DownloadService service) {
        this.service = service;
    }

    @PostMapping("/info")
    public Map<String, Object> getInfo(@RequestBody Map<String, String> body) throws Exception {
        return service.getVideoInfo(body.get("url"));
    }

    @GetMapping("/download")
    public void download(@RequestParam String url, @RequestParam String formatId,
                         @RequestParam String title, @RequestParam String ext,
                         @RequestParam(defaultValue = "false") boolean audio,
                         HttpServletResponse response) throws Exception {
        String filename = service.buildFilename(title, audio ? "mp3" : ext);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        service.downloadStream(url, formatId, audio, response.getOutputStream());
    }
}