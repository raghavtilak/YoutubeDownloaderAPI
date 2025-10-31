package com.raghav.youtubedownloader;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

@Service
public class DownloadService {
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> getVideoInfo(String url) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("yt-dlp", "--dump-json", url);
        Process p = pb.start();
        String json = new String(p.getInputStream().readAllBytes());
        p.waitFor();
        Map<String, Object> info = mapper.readValue(json, Map.class);
        // Extract relevant: title, thumbnail, formats (list of maps with format_id, ext, resolution, etc.)
        return Map.of("title", info.get("title"), "thumbnail", info.get("thumbnail"), "formats", info.get("formats"));
    }

    public void downloadStream(String url, String formatId, boolean isAudio, OutputStream out) throws Exception {
        List<String> cmd = new ArrayList<>(List.of("yt-dlp", "-f", formatId, "-o", "-", url));
        if (isAudio) {
            cmd.addAll(List.of("-x", "--audio-format", "mp3")); // Extract and convert to MP3
        }
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        try (InputStream in = p.getInputStream()) {
            in.transferTo(out);
        }
        p.waitFor();
    }

    public String buildFilename(String title, String ext) {
        return title.replaceAll("[^a-zA-Z0-9-\\s]", "") + "." + ext; // Sanitize
    }
}